package org.foxesworld.launchserver.command.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launcher.request.auth.password.AuthPlainPassword;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.auth.AuthProviderPair;
import org.foxesworld.launchserver.auth.provider.AuthProvider;
import org.foxesworld.launchserver.auth.provider.AuthProviderResult;
import org.foxesworld.launchserver.command.Command;

import java.util.UUID;

public final class AuthCommand extends Command {
    private transient final Logger logger = LogManager.getLogger();

    public AuthCommand(LaunchServer server) {
        super(server);
    }

    @Override
    public String getArgsDescription() {
        return "<login> <password> <auth_id>";
    }

    @Override
    public String getUsageDescription() {
        return "Try to auth with specified login and password";
    }

    @Override
    public void invoke(String... args) throws Exception {
        verifyArgs(args, 3);
        AuthProviderPair pair;
        if (args.length > 3) pair = server.config.getAuthProviderPair(args[2]);
        else pair = server.config.getAuthProviderPair();
        if (pair == null) throw new IllegalStateException(String.format("Auth %s not found", args[1]));

        String login = args[0];
        String password = args[1];
        String hwid = args[2];

        // Authenticate
        AuthProvider provider = pair.provider;
        AuthProviderResult result = provider.auth(login, new AuthPlainPassword(password), "127.0.0.1", hwid);
        UUID uuid = pair.handler.auth(result);

        // Print auth successful message
        logger.info("UUID: {}, Username: '{}', Access Token: '{}'", uuid, result.username, result.accessToken);
    }
}
