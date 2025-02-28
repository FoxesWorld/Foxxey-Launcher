package org.foxesworld.launchserver.auth.core;

import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launcher.Launcher;
import org.foxesworld.launcher.events.request.GetAvailabilityAuthRequestEvent;
import org.foxesworld.launcher.request.auth.AuthRequest;
import org.foxesworld.launcher.request.auth.details.AuthPasswordDetails;
import org.foxesworld.launcher.request.auth.password.AuthPlainPassword;
import org.foxesworld.launcher.request.secure.HardwareReportRequest;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.Reconfigurable;
import org.foxesworld.launchserver.auth.AuthException;
import org.foxesworld.launchserver.auth.core.interfaces.UserHardware;
import org.foxesworld.launchserver.auth.core.interfaces.provider.AuthSupportGetAllUsers;
import org.foxesworld.launchserver.auth.core.interfaces.provider.AuthSupportHardware;
import org.foxesworld.launchserver.auth.core.interfaces.provider.AuthSupportRegistration;
import org.foxesworld.launchserver.auth.core.interfaces.user.UserSupportHardware;
import org.foxesworld.launchserver.auth.protect.hwid.HWIDProvider;
import org.foxesworld.launchserver.manangers.AuthManager;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.auth.AuthResponse;
import org.foxesworld.utils.ProviderMap;
import org.foxesworld.utils.command.Command;
import org.foxesworld.utils.command.CommandException;
import org.foxesworld.utils.command.SubCommand;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*
All-In-One provider
 */
public abstract class AuthCoreProvider implements AutoCloseable, Reconfigurable {
    public static final ProviderMap<AuthCoreProvider> providers = new ProviderMap<>("AuthCoreProvider");
    private static final Logger logger = LogManager.getLogger();
    private static boolean registredProviders = false;

    public static void registerProviders() {
        if (!registredProviders) {
            providers.register("reject", RejectAuthCoreProvider.class);
            providers.register("mysql", MySQLCoreProvider.class);
            providers.register("json", JsonCoreProvider.class);
            registredProviders = true;
        }
    }

    public abstract User getUserByUsername(String username);

    public User getUserByLogin(String login) {
        return getUserByUsername(login);
    }

    public abstract User getUserByUUID(UUID uuid);

    public abstract UserSession getUserSessionByOAuthAccessToken(String accessToken) throws OAuthAccessTokenExpired;

    public abstract AuthManager.AuthReport refreshAccessToken(String refreshToken, AuthResponse.AuthContext context /* may be null */);

    public abstract void verifyAuth(AuthResponse.AuthContext context) throws AuthException;

    public abstract PasswordVerifyReport verifyPassword(User user, AuthRequest.AuthPasswordInterface password);

    public abstract AuthManager.AuthReport createOAuthSession(User user, AuthResponse.AuthContext context /* may be null */, PasswordVerifyReport report /* may be null */, boolean minecraftAccess) throws IOException;

    public abstract void init(LaunchServer server);

    // Auth Handler methods
    protected abstract boolean updateServerID(User user, String serverID) throws IOException;

    public List<GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> getDetails(Client client) {
        return List.of(new AuthPasswordDetails());
    }

    @Override
    public Map<String, Command> getCommands() {
        Map<String, Command> map = defaultCommandsMap();
        map.put("checkpassword", new SubCommand("[username] [json/plain password data]", "check password") {
            @Override
            public void invoke(String... args) throws Exception {
                verifyArgs(args, 2);
                User user = getUserByUsername(args[0]);
                if (user == null) throw new CommandException("User not found");
                AuthRequest.AuthPasswordInterface password;
                if (args[1].startsWith("{")) {
                    password = Launcher.gsonManager.gson.fromJson(args[1], AuthRequest.AuthPasswordInterface.class);
                } else {
                    password = new AuthPlainPassword(args[1]);
                }
                PasswordVerifyReport report = verifyPassword(user, password);
                if (report.success) {
                    logger.info("Password correct");
                } else {
                    if (report.needMoreFactor) {
                        if (report.factors.size() == 1 && report.factors.get(0) == -1) {
                            logger.info("Password not correct: Required 2FA");
                        } else {
                            logger.info("Password not correct: Required more factors: {}", report.factors.toString());
                        }
                    } else {
                        logger.info("Password incorrect");
                    }
                }
            }
        });
        map.put("getuserbyusername", new SubCommand("[username]", "get user by username") {
            @Override
            public void invoke(String... args) throws Exception {
                verifyArgs(args, 1);
                User user = getUserByUsername(args[0]);
                if (user == null) {
                    logger.info("User {} not found", args[0]);
                } else {
                    logger.info("User {}: {}", args[0], user.toString());
                }
            }
        });
        map.put("getuserbyuuid", new SubCommand("[uuid]", "get user by uuid") {
            @Override
            public void invoke(String... args) throws Exception {
                verifyArgs(args, 1);
                User user = getUserByUUID(UUID.fromString(args[0]));
                if (user == null) {
                    logger.info("User {} not found", args[0]);
                } else {
                    logger.info("User {}: {}", args[0], user.toString());
                }
            }
        });
        map.put("createsession", new SubCommand("[username] (true/false)", "create user session with/without minecraft access") {
            @Override
            public void invoke(String... args) throws Exception {
                verifyArgs(args, 1);
                User user = getUserByUsername(args[0]);
                if (user == null) {
                    logger.info("User {} not found", args[0]);
                    return;
                }
                boolean minecraftAccess = args.length > 1 && Boolean.parseBoolean(args[1]);
                AuthManager.AuthReport report = createOAuthSession(user, null, null, minecraftAccess);
                if (report == null) {
                    logger.error("Method createOAuthSession return null");
                    return;
                }
                if (report.isUsingOAuth()) {
                    logger.info("OAuth: AccessToken: {} RefreshToken: {} MinecraftAccessToken: {}", report.oauthAccessToken(), report.oauthRefreshToken(), report.minecraftAccessToken());
                    if (report.session() != null) {
                        logger.info("UserSession: id {} expire {} user {}", report.session().getID(), report.session().getExpireIn(), report.session().getUser() == null ? "null" : "found");
                        logger.info(report.session().toString());
                    }
                } else {
                    logger.info("Basic: MinecraftAccessToken: {}", report.minecraftAccessToken());
                }
            }
        });
        {
            var instance = isSupport(AuthSupportGetAllUsers.class);
            if (instance != null) {
                map.put("getallusers", new SubCommand("(limit)", "print all users information") {
                    @Override
                    public void invoke(String... args) {
                        int max = Integer.MAX_VALUE;
                        if (args.length > 0) max = Integer.parseInt(args[0]);
                        Iterable<User> users = instance.getAllUsers();
                        int counter = 0;
                        for (User u : users) {
                            logger.info("User {}", u.toString());
                            counter++;
                            if (counter == max) break;
                        }
                        logger.info("Found {} users", counter);
                    }
                });
            }
        }
        {
            var instance = isSupport(AuthSupportHardware.class);
            if (instance != null) {
                map.put("gethardwarebyid", new SubCommand("[id]", "get hardware by id") {
                    @Override
                    public void invoke(String... args) throws Exception {
                        verifyArgs(args, 1);
                        UserHardware hardware = instance.getHardwareInfoById(args[0]);
                        if (hardware == null) {
                            logger.info("UserHardware {} not found", args[0]);
                        } else {
                            logger.info("UserHardware: {}", hardware);
                        }
                    }
                });
                map.put("gethardwarebydata", new SubCommand("[json data]", "fulltext search hardware by json data(slow)") {
                    @Override
                    public void invoke(String... args) throws Exception {
                        verifyArgs(args, 1);
                        UserHardware hardware = instance.getHardwareInfoByData(Launcher.gsonManager.gson.fromJson(args[0], HardwareReportRequest.HardwareInfo.class));
                        if (hardware == null) {
                            logger.info("UserHardware {} not found", args[0]);
                        } else {
                            logger.info("UserHardware: {}", hardware);
                        }
                    }
                });
                map.put("getuserhardware", new SubCommand("[username]", "get hardware by username") {
                    @Override
                    public void invoke(String... args) throws Exception {
                        verifyArgs(args, 1);
                        User user = getUserByUUID(UUID.fromString(args[0]));
                        if (user == null) {
                            logger.info("User {} not found", args[0]);
                        }
                        UserSupportHardware hardware = instance.fetchUserHardware(user);
                        if (hardware == null) {
                            logger.error("Method fetchUserHardware return null");
                            return;
                        }
                        UserHardware userHardware = hardware.getHardware();
                        if (userHardware == null) {
                            logger.info("User {} not contains hardware info", args[0]);
                        } else {
                            logger.info("UserHardware: {}", userHardware);
                            logger.info("HardwareInfo(JSON): {}", Launcher.gsonManager.gson.toJson(userHardware.getHardwareInfo()));
                        }
                    }
                });
                map.put("findmulti", new SubCommand("[hardware id]", "get all users in one hardware id") {
                    @Override
                    public void invoke(String... args) throws Exception {
                        verifyArgs(args, 1);
                        UserHardware hardware = instance.getHardwareInfoById(args[0]);
                        if (hardware == null) {
                            logger.info("UserHardware {} not found", args[0]);
                            return;
                        }
                        Iterable<User> users = instance.getUsersByHardwareInfo(hardware);
                        for (User user : users) {
                            logger.info("User {}", user);
                        }
                    }
                });
                map.put("banhardware", new SubCommand("[hardware id]", "ban hardware by id") {
                    @Override
                    public void invoke(String... args) throws Exception {
                        verifyArgs(args, 1);
                        UserHardware hardware = instance.getHardwareInfoById(args[0]);
                        if (hardware == null) {
                            logger.info("UserHardware {} not found", args[0]);
                            return;
                        }
                        instance.banHardware(hardware);
                        logger.info("UserHardware {} banned", args[0]);
                    }
                });
                map.put("unbanhardware", new SubCommand("[hardware id]", "ban hardware by id") {
                    @Override
                    public void invoke(String... args) throws Exception {
                        verifyArgs(args, 1);
                        UserHardware hardware = instance.getHardwareInfoById(args[0]);
                        if (hardware == null) {
                            logger.info("UserHardware {} not found", args[0]);
                            return;
                        }
                        instance.unbanHardware(hardware);
                        logger.info("UserHardware {} unbanned", args[0]);
                    }
                });
                map.put("comparehardware", new SubCommand("[json data 1] [json data 2]", "compare hardware info") {
                    @Override
                    public void invoke(String... args) throws Exception {
                        verifyArgs(args, 2);
                        HardwareReportRequest.HardwareInfo hardware1 = Launcher.gsonManager.gson.fromJson(args[0], HardwareReportRequest.HardwareInfo.class);
                        HardwareReportRequest.HardwareInfo hardware2 = Launcher.gsonManager.gson.fromJson(args[1], HardwareReportRequest.HardwareInfo.class);
                        HWIDProvider.HardwareInfoCompareResult result = instance.compareHardwareInfo(hardware1, hardware2);
                        if (result == null) {
                            logger.error("Method compareHardwareInfo return null");
                            return;
                        }
                        logger.info("Compare result: {} Spoof: {} first {} second", result.compareLevel, result.firstSpoofingLevel, result.secondSpoofingLevel);
                    }
                });
            }
        }
        {
            var instance = isSupport(AuthSupportRegistration.class);
            if (instance != null) {
                map.put("register", new SubCommand("[username] [email] [plain or json password] (json args)", "Register new user") {
                    @Override
                    public void invoke(String... args) throws Exception {
                        verifyArgs(args, 2);
                        Map<String, String> map = null;
                        String username = args[0];
                        String email = args[1];
                        String plainPassword = args[2];
                        if (args.length > 3) {
                            Type typeOfMap = new TypeToken<Map<String, String>>() {
                            }.getType();
                            map = Launcher.gsonManager.gson.fromJson(args[2], typeOfMap);
                        }
                        AuthRequest.AuthPasswordInterface password;
                        if (plainPassword.startsWith("{")) {
                            password = Launcher.gsonManager.gson.fromJson(plainPassword, AuthRequest.AuthPasswordInterface.class);
                        } else {
                            password = new AuthPlainPassword(plainPassword);
                        }
                        instance.registration(username, email, password, map);
                    }
                });
            }
        }
        return map;
    }

    public User checkServer(Client client, String username, String serverID) throws IOException {
        User user = getUserByUsername(username);
        if (user == null) {
            return null;
        }
        if (user.getUsername().equals(username) && user.getServerId().equals(serverID)) {
            return user;
        }
        return null;
    }

    public boolean joinServer(Client client, String username, String accessToken, String serverID) throws IOException {
        User user = client.getUser();
        if (user == null) return false;
        return user.getUsername().equals(username) && user.getAccessToken().equals(accessToken) && updateServerID(user, serverID);
    }

    @SuppressWarnings("unchecked")
    public <T> T isSupport(Class<T> clazz) {
        if (clazz.isAssignableFrom(getClass())) return (T) this;
        return null;
    }

    @Override
    public abstract void close() throws IOException;

    public static class PasswordVerifyReport {
        public static final PasswordVerifyReport REQUIRED_2FA = new PasswordVerifyReport(-1);
        public static final PasswordVerifyReport FAILED = new PasswordVerifyReport(false);
        public static final PasswordVerifyReport OK = new PasswordVerifyReport(true);
        public final boolean success;
        public final boolean needMoreFactor;
        public final List<Integer> factors;

        public PasswordVerifyReport(boolean success) {
            this.success = success;
            this.needMoreFactor = false;
            this.factors = List.of();
        }

        public PasswordVerifyReport(int nextFactor) {
            this.success = false;
            this.needMoreFactor = true;
            this.factors = List.of(nextFactor);
        }

        public PasswordVerifyReport(List<Integer> factors) {
            this.success = false;
            this.needMoreFactor = false;
            this.factors = Collections.unmodifiableList(factors);
        }

        private PasswordVerifyReport(boolean success, boolean needMoreFactor, List<Integer> factors) {
            this.success = success;
            this.needMoreFactor = needMoreFactor;
            this.factors = factors;
        }
    }

    public static class OAuthAccessTokenExpired extends Exception {
        public OAuthAccessTokenExpired() {
        }

        public OAuthAccessTokenExpired(String message) {
            super(message);
        }

        public OAuthAccessTokenExpired(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
