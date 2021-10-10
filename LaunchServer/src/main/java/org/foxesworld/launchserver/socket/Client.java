package org.foxesworld.launchserver.socket;

import org.foxesworld.launcher.ClientPermissions;
import org.foxesworld.launcher.profiles.ClientProfile;
import org.foxesworld.launcher.request.secure.HardwareReportRequest;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.auth.AuthProviderPair;
import org.foxesworld.launchserver.socket.response.auth.AuthResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    public UUID session;
    public boolean useOAuth;
    public String auth_id;
    public long timestamp;
    public AuthResponse.ConnectTypes type;
    public ClientProfile profile;
    public boolean isAuth;
    public boolean checkSign;
    public ClientPermissions permissions;
    public String username;
    public UUID uuid;
    public TrustLevel trustLevel;
    public int groupId;
    public int balance;

    public transient AuthProviderPair auth;

    public transient org.foxesworld.launchserver.auth.core.User coreObject;

    public transient org.foxesworld.launchserver.auth.core.UserSession sessionObject;

    public transient Map<String, Object> properties;

    public Map<String, String> serializableProperties;

    public transient AtomicInteger refCount = new AtomicInteger(1);

    public Client(UUID session) {
        this.session = session;
        timestamp = System.currentTimeMillis();
        type = null;
        isAuth = false;
        permissions = ClientPermissions.DEFAULT;
        username = "";
        checkSign = false;
        groupId = 4;
        balance = 0;
    }

    //Данные авторизации
    @SuppressWarnings("unused")
    public void up() {
        timestamp = System.currentTimeMillis();
    }

    public void updateAuth(LaunchServer server) {
        if (!isAuth) return;
        if (auth_id.isEmpty()) auth = server.config.getAuthProviderPair();
        else auth = server.config.getAuthProviderPair(auth_id);
    }

    @SuppressWarnings({"unchecked", "unused"})
    public <T> T getProperty(String name) {
        if (properties == null) properties = new HashMap<>();
        return (T) properties.get(name);
    }

    @SuppressWarnings("unused")
    public <T> void setProperty(String name, T object) {
        if (properties == null) properties = new HashMap<>();
        properties.put(name, object);
    }

    @SuppressWarnings("unused")
    public String getSerializableProperty(String name) {
        if (serializableProperties == null) serializableProperties = new HashMap<>();
        return serializableProperties.get(name);
    }

    @SuppressWarnings("unused")
    public void setSerializableProperty(String name, String value) {
        if (serializableProperties == null) serializableProperties = new HashMap<>();
        serializableProperties.put(name, value);
    }

    public org.foxesworld.launchserver.auth.core.User getUser() {
        if (coreObject != null) return coreObject;
        if (auth != null && uuid != null) {
            coreObject = auth.core.getUserByUUID(uuid);
        }
        return coreObject;
    }

    public static class TrustLevel {
        public byte[] verifySecureKey;
        public boolean keyChecked;
        public byte[] publicKey;
        public HardwareReportRequest.HardwareInfo hardwareInfo;
        // May be used later
        @SuppressWarnings("unused")
        public double rating;
        @SuppressWarnings("unused")
        public long latestMillis;
    }
}
