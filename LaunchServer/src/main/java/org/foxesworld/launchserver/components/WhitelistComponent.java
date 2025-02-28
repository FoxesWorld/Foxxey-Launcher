package org.foxesworld.launchserver.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.Reconfigurable;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.auth.AuthResponse;
import org.foxesworld.launchserver.socket.response.auth.JoinServerResponse;
import org.foxesworld.utils.HookException;
import org.foxesworld.utils.command.Command;
import org.foxesworld.utils.command.SubCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WhitelistComponent extends Component implements AutoCloseable, Reconfigurable {
    private transient LaunchServer server;
    private transient final Logger logger = LogManager.getLogger();
    public String message = "auth.message.techwork";
    public boolean enabled = true;
    public List<String> whitelist = new ArrayList<>();

    @Override
    public void init(LaunchServer launchServer) {
        this.server = launchServer;
        this.server.authHookManager.preHook.registerHook(this::hookAuth);
        this.server.authHookManager.joinServerHook.registerHook(this::hookJoin);
    }

    public boolean hookAuth(AuthResponse.AuthContext context, Client client) throws HookException {
        if (enabled) {
            if (!whitelist.contains(context.login)) {
                throw new HookException(message);
            }
        }
        return false;
    }

    public boolean hookJoin(JoinServerResponse response, Client client) throws HookException {
        if (enabled) {
            if (!whitelist.contains(response.username)) {
                throw new HookException(message);
            }
        }
        return false;
    }

    @Override
    public void close() throws Exception {
        this.server.authHookManager.preHook.unregisterHook(this::hookAuth);
        this.server.authHookManager.joinServerHook.unregisterHook(this::hookJoin);
    }

    @Override
    public Map<String, Command> getCommands() {
        var commands = defaultCommandsMap();
        commands.put("setmessage", new SubCommand("[new message]", "set message") {
            @Override
            public void invoke(String... args) throws Exception {
                verifyArgs(args, 1);
                message = args[0];
                logger.info("Message: {}", args[0]);
            }
        });
        commands.put("whitelist.add", new SubCommand("[login]", "add login to whitelist") {
            @Override
            public void invoke(String... args) throws Exception {
                verifyArgs(args, 1);
                whitelist.add(args[0]);
                logger.info("{} added to whitelist", args[0]);
            }
        });
        commands.put("whitelist.remove", new SubCommand("[login]", "remove login from whitelist") {
            @Override
            public void invoke(String... args) throws Exception {
                verifyArgs(args, 1);
                whitelist.remove(args[0]);
                logger.info("{} removed from whitelist", args[0]);
            }
        });
        commands.put("disable", new SubCommand() {
            @Override
            public void invoke(String... args) {
                enabled = false;
                logger.info("Whitelist disabled");
            }
        });
        commands.put("enable", new SubCommand() {
            @Override
            public void invoke(String... args) {
                enabled = true;
                logger.info("Whitelist enabled");
            }
        });
        return commands;
    }
}
