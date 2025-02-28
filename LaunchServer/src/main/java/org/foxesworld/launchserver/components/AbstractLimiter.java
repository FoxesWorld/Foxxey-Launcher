package org.foxesworld.launchserver.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launcher.NeedGarbageCollection;
import org.foxesworld.launchserver.Reconfigurable;
import org.foxesworld.utils.command.Command;
import org.foxesworld.utils.command.SubCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractLimiter<T> extends Component implements NeedGarbageCollection, Reconfigurable {
    public final List<T> exclude = new ArrayList<>();
    protected final transient Map<T, LimitEntry> map = new HashMap<>();
    private transient final Logger logger = LogManager.getLogger();
    public int rateLimit;
    public int rateLimitMillis;

    @Override
    public Map<String, Command> getCommands() {
        Map<String, Command> commands = new HashMap<>();
        commands.put("gc", new SubCommand() {
            @Override
            public void invoke(String... args) {
                long size = map.size();
                garbageCollection();
                logger.info("Cleared {} entity", size);
            }
        });
        commands.put("clear", new SubCommand() {
            @Override
            public void invoke(String... args) {
                long size = map.size();
                map.clear();
                logger.info("Cleared {} entity", size);
            }
        });
        commands.put("addExclude", new SubCommand() {
            @Override
            public void invoke(String... args) throws Exception {
                verifyArgs(args, 1);
                exclude.add(getFromString(args[0]));
            }
        });
        commands.put("rmExclude", new SubCommand() {
            @Override
            public void invoke(String... args) throws Exception {
                verifyArgs(args, 1);
                exclude.remove(getFromString(args[0]));
            }
        });
        commands.put("clearExclude", new SubCommand() {
            @Override
            public void invoke(String... args) {
                exclude.clear();
            }
        });

        return commands;
    }

    protected abstract T getFromString(String str);

    @Override
    public void garbageCollection() {
        long time = System.currentTimeMillis();
        map.entrySet().removeIf((e) -> e.getValue().time + rateLimitMillis < time);
    }

    public boolean check(T address) {
        if (exclude.contains(address)) return true;
        LimitEntry entry = map.get(address);
        if (entry == null) {
            map.put(address, new LimitEntry());
            return true;
        } else {
            long time = System.currentTimeMillis();
            if (entry.trys < rateLimit) {
                entry.trys++;
                entry.time = time;
                return true;
            }
            if (entry.time + rateLimitMillis < time) {
                entry.trys = 1;
                entry.time = time;
                return true;
            }
            return false;
        }
    }

    static class LimitEntry {
        long time;
        int trys;

        @SuppressWarnings("unused")
        public LimitEntry(long time, int trys) {
            this.time = time;
            this.trys = trys;
        }

        public LimitEntry() {
            time = System.currentTimeMillis();
            trys = 0;
        }
    }
}
