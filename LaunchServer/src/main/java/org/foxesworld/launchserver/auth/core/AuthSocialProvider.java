package org.foxesworld.launchserver.auth.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launcher.events.request.GetAvailabilityAuthRequestEvent;
import org.foxesworld.launcher.request.auth.AuthRequest;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.auth.AuthException;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.auth.AuthResponse;
import org.foxesworld.utils.ProviderMap;

import java.io.IOException;
import java.util.List;

public abstract class AuthSocialProvider implements AutoCloseable {
    public static final ProviderMap<AuthSocialProvider> providers = new ProviderMap<>("AuthSocialProvider");
    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger();
    private static boolean registredProviders = false;

    public static void registerProviders() {
        if (!registredProviders) {
            registredProviders = true;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T isSupport(Class<T> clazz) {
        if (clazz.isAssignableFrom(getClass())) return (T) this;
        return null;
    }

    public abstract void init(LaunchServer server, AuthCoreProvider provider);

    public abstract List<GetAvailabilityAuthRequestEvent.AuthAvailabilityDetails> getDetails(Client client);

    public abstract SocialResult preAuth(AuthResponse.AuthContext context, AuthRequest.AuthPasswordInterface password) throws AuthException;

    @Override
    public abstract void close() throws IOException;

    public static class SocialResult {
        public String login;
        public AuthRequest.AuthPasswordInterface password;
        public User user;

        public SocialResult(String login, AuthRequest.AuthPasswordInterface password, User user) {
            this.login = login;
            this.password = password;
            this.user = user;
        }

        @SuppressWarnings("unused")
        public static SocialResult ofLoginAndPassword(String login, AuthRequest.AuthPasswordInterface password) {
            return new SocialResult(login, password, null);
        }

        @SuppressWarnings("unused")
        public static SocialResult ofUser(User user) {
            return new SocialResult(null, null, user);
        }
    }
}
