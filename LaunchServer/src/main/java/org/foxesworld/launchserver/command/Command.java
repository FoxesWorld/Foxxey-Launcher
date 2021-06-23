package org.foxesworld.launchserver.command;

import org.foxesworld.launchserver.LaunchServer;

import java.util.Map;

public abstract class Command extends org.foxesworld.utils.command.Command {
    protected final LaunchServer server;


    protected Command(LaunchServer server) {
        super();
        this.server = server;
    }

    public Command(Map<String, org.foxesworld.utils.command.Command> childCommands, LaunchServer server) {
        super(childCommands);
        this.server = server;
    }
}
