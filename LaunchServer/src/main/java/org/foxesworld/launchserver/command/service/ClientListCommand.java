package org.foxesworld.launchserver.command.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launcher.profiles.ClientProfile;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.command.Command;

public class ClientListCommand extends Command {
    private transient final Logger logger = LogManager.getLogger();

    public ClientListCommand(LaunchServer server) {
        super(server);
    }

    @Override
    public String getArgsDescription() {
        return null;
    }

    @Override
    public String getUsageDescription() {
        return "Show all online client profiles";
    }

    @Override
    public void invoke(String... args) throws Exception {
        for (ClientProfile clientProfile : server.getProfiles()) {
            logger.info(clientProfile.getTitle() + " | " + clientProfile.getClientGroup());
        }
    }
}
