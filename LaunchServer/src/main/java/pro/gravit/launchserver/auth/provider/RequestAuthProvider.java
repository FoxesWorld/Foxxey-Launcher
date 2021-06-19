package pro.gravit.launchserver.auth.provider;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import pro.gravit.launcher.ClientPermissions;
import pro.gravit.launcher.request.auth.AuthRequest;
import pro.gravit.launcher.request.auth.password.AuthPlainPassword;
import pro.gravit.launchserver.LaunchServer;
import pro.gravit.launchserver.auth.AuthException;
import pro.gravit.utils.helper.CommonHelper;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.SecurityHelper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class RequestAuthProvider extends AuthProvider {
    private transient final HttpClient client = HttpClient.newBuilder()
            .build();
    public String url;
    public int timeout = 5000;

    @Override
    public void init(LaunchServer srv) {
        super.init(srv);
        if (url == null) throw new RuntimeException("[Verify][AuthProvider] url cannot be null");
    }

    @Override
    public AuthProviderResult auth(String login, AuthRequest.AuthPasswordInterface password, String ip, String hwid) throws IOException, URISyntaxException, InterruptedException {
        if (!(password instanceof AuthPlainPassword)) throw new AuthException("This password type not supported");
        HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .uri(new URI(getFormattedURL(login, ((AuthPlainPassword) password).password, hwid)))
                .header("User-Agent", IOHelper.USER_AGENT)
                .timeout(Duration.ofMillis(timeout))
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString());

        // Match username
        String currentResponse = response.body();
        JsonObject jsonObject = (JsonObject) JsonParser.parseString(currentResponse);
        if (jsonObject.has("hardwareId")) {
            boolean isHwid = jsonObject.get("hardwareId").getAsBoolean();
            if (isHwid) {
                int balance = jsonObject.get("balance").getAsInt();
                int userGroup = jsonObject.get("userGroup").getAsInt();
                return new AuthProviderResult(jsonObject.get("login").getAsString(), SecurityHelper.randomStringToken(), new ClientPermissions(0, 0), balance, userGroup);
            }
            return authError(jsonObject.get("message").getAsString());
        }
        return authError(jsonObject.get("message").getAsString());
    }

    @Override
    public void close() {
        // Do nothing
    }

    private String getFormattedURL(String login, String password, String hwid) {
        return CommonHelper.replace(url, "login", IOHelper.urlEncode(login), "password", IOHelper.urlEncode(password), "hwid", IOHelper.urlEncode(hwid));
    }
}
