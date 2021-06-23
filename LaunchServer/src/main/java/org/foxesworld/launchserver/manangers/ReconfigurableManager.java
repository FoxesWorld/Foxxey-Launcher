package org.foxesworld.launchserver.manangers;

import org.foxesworld.launchserver.Reconfigurable;
import org.foxesworld.utils.command.Command;
import org.foxesworld.utils.command.CommandException;
import org.foxesworld.utils.command.basic.HelpCommand;
import org.foxesworld.utils.helper.VerifyHelper;

import java.util.HashMap;
import java.util.Map;

public class ReconfigurableManager {
    private final HashMap<String, Command> RECONFIGURABLE = new HashMap<>();

    public void registerReconfigurable(String name, Reconfigurable reconfigurable) {
        VerifyHelper.putIfAbsent(RECONFIGURABLE, name.toLowerCase(), new ReconfigurableVirtualCommand(reconfigurable.getCommands()),
                String.format("Reconfigurable has been already registered: '%s'", name));
    }

    public void unregisterReconfigurable(String name) {
        RECONFIGURABLE.remove(name.toLowerCase());
    }

    @SuppressWarnings("unused")
    public void call(String name, String action, String[] args) throws Exception {
        Command commands = RECONFIGURABLE.get(name);
        if (commands == null) throw new CommandException(String.format("Reconfigurable %s not found", name));
        Command command = commands.childCommands.get(action);
        if (command == null) throw new CommandException(String.format("Action %s.%s not found", name, action));
        command.invoke(args);
    }

    @SuppressWarnings("unused")
    public void printHelp(String name) throws CommandException {
        Command commands = RECONFIGURABLE.get(name);
        if (commands == null) throw new CommandException(String.format("Reconfigurable %s not found", name));
        HelpCommand.printSubCommandsHelp(name, commands);
    }

    public Map<String, Command> getCommands() {
        return RECONFIGURABLE;
    }

    private static class ReconfigurableVirtualCommand extends Command {
        public ReconfigurableVirtualCommand(Map<String, Command> childs) {
            super(childs);
        }

        @Override
        public String getArgsDescription() {
            return null;
        }

        @Override
        public String getUsageDescription() {
            return null;
        }

        @Override
        public void invoke(String... args) throws Exception {
            invokeSubcommands(args);
        }
    }
}
