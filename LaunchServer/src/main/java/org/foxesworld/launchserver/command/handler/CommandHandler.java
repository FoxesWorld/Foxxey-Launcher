package org.foxesworld.launchserver.command.handler;

import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.command.auth.AuthCommand;
import org.foxesworld.launchserver.command.auth.UUIDToUsernameCommand;
import org.foxesworld.launchserver.command.auth.UsernameToUUIDCommand;
import org.foxesworld.launchserver.command.basic.*;
import org.foxesworld.launchserver.command.hash.*;
import org.foxesworld.launchserver.command.modules.LoadModuleCommand;
import org.foxesworld.launchserver.command.modules.ModulesCommand;
import org.foxesworld.launchserver.command.news.NewsListCommand;
import org.foxesworld.launchserver.command.news.SyncNewsCommand;
import org.foxesworld.launchserver.command.service.*;
import org.foxesworld.utils.command.BaseCommandCategory;
import org.foxesworld.utils.command.basic.ClearCommand;
import org.foxesworld.utils.command.basic.DebugCommand;
import org.foxesworld.utils.command.basic.GCCommand;
import org.foxesworld.utils.command.basic.HelpCommand;

public abstract class CommandHandler extends org.foxesworld.utils.command.CommandHandler {
    public static void registerCommands(org.foxesworld.utils.command.CommandHandler handler, LaunchServer server) {
        BaseCommandCategory basic = new BaseCommandCategory();
        // Register basic commands
        basic.registerCommand("help", new HelpCommand(handler));
        basic.registerCommand("version", new VersionCommand(server));
        basic.registerCommand("build", new BuildCommand(server));
        basic.registerCommand("stop", new StopCommand(server));
        basic.registerCommand("restart", new RestartCommand(server));
        basic.registerCommand("reload", new ReloadCommand(server));
        basic.registerCommand("debug", new DebugCommand());
        basic.registerCommand("clear", new ClearCommand(handler));
        basic.registerCommand("gc", new GCCommand());
        basic.registerCommand("loadModule", new LoadModuleCommand(server));
        basic.registerCommand("modules", new ModulesCommand(server));
        basic.registerCommand("test", new TestCommand(server));
        Category basicCategory = new Category(basic, "basic", "Base LaunchServer commands");
        handler.registerCategory(basicCategory);

        // Register sync commands
        BaseCommandCategory updates = new BaseCommandCategory();
        updates.registerCommand("indexAsset", new IndexAssetCommand(server));
        updates.registerCommand("unindexAsset", new UnindexAssetCommand(server));
        updates.registerCommand("downloadAsset", new DownloadAssetCommand(server));
        updates.registerCommand("downloadClient", new DownloadClientCommand(server));
        updates.registerCommand("syncBinaries", new SyncBinariesCommand(server));
        updates.registerCommand("syncUpdates", new SyncUpdatesCommand(server));
        updates.registerCommand("syncProfiles", new SyncProfilesCommand(server));
        updates.registerCommand("syncUP", new SyncUPCommand(server));
        updates.registerCommand("saveProfiles", new SaveProfilesCommand(server));
        updates.registerCommand("syncNews", new SyncNewsCommand(server));
        Category updatesCategory = new Category(updates, "updates", "Update and Sync Management");
        handler.registerCategory(updatesCategory);

        // Register auth commands
        BaseCommandCategory auth = new BaseCommandCategory();
        auth.registerCommand("auth", new AuthCommand(server));
        auth.registerCommand("usernameToUUID", new UsernameToUUIDCommand(server));
        auth.registerCommand("uuidToUsername", new UUIDToUsernameCommand(server));
        Category authCategory = new Category(auth, "auth", "User Management");
        handler.registerCategory(authCategory);

        //Register service commands
        BaseCommandCategory service = new BaseCommandCategory();
        service.registerCommand("config", new ConfigCommand(server));
        service.registerCommand("serverStatus", new ServerStatusCommand(server));
        service.registerCommand("notify", new NotifyCommand(server));
        service.registerCommand("component", new ComponentCommand(server));
        service.registerCommand("clients", new ClientsCommand(server));
        service.registerCommand("clientList", new ClientListCommand(server));
        service.registerCommand("signJar", new SignJarCommand(server));
        service.registerCommand("signDir", new SignDirCommand(server));
        service.registerCommand("pingServers", new PingServersCommand(server));
        service.registerCommand("securitycheck", new SecurityCheckCommand(server));
        service.registerCommand("newsList", new NewsListCommand(server));
        Category serviceCategory = new Category(service, "service", "Managing LaunchServer Components");
        handler.registerCategory(serviceCategory);
    }
}
