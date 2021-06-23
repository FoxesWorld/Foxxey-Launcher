package org.foxesworld.launchserver.command.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.auth.AuthProviderPair;
import org.foxesworld.launchserver.command.Command;
import org.foxesworld.utils.command.CommandException;

import java.io.IOException;
import java.util.UUID;

public final class UsernameToUUIDCommand extends Command {
    private transient final Logger logger = LogManager.getLogger();

    public UsernameToUUIDCommand(LaunchServer server) {
        super(server);
    }

    @Override
    public String getArgsDescription() {
        return "<username> <auth_id>";
    }

    @Override
    public String getUsageDescription() {
        return "Convert player username to UUID";
    }

    @Override
    public void invoke(String... args) throws CommandException, IOException {
        verifyArgs(args, 1);
        AuthProviderPair pair;
        if (args.length > 1) pair = server.config.getAuthProviderPair(args[1]);
        else pair = server.config.getAuthProviderPair();
        if (pair == null) throw new IllegalStateException(String.format("Auth %s not found", args[1]));
        String username = parseUsername(args[0]);

        // Get UUID by username
        UUID uuid = pair.handler.usernameToUUID(username);
        if (uuid == null)
            throw new CommandException(String.format("Unknown username: '%s'", username));

        // Print UUID
        logger.info("UUID of player '{}': {}", username, uuid);
    }
}
