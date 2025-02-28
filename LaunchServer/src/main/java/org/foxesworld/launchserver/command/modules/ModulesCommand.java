package org.foxesworld.launchserver.command.modules;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launcher.LauncherTrustManager;
import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.launcher.modules.LauncherModuleInfo;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.command.Command;
import org.foxesworld.launchserver.launchermodules.LauncherModuleLoader;

import java.security.cert.X509Certificate;
import java.util.Arrays;

public class ModulesCommand extends Command {
    private transient final Logger logger = LogManager.getLogger();

    public ModulesCommand(LaunchServer server) {
        super(server);
    }

    @Override
    public String getArgsDescription() {
        return null;
    }

    @Override
    public String getUsageDescription() {
        return "get all modules";
    }

    @Override
    public void invoke(String... args) {
        for (LauncherModule module : server.modulesManager.getModules()) {
            LauncherModuleInfo info = module.getModuleInfo();
            LauncherTrustManager.CheckClassResult checkStatus = module.getCheckResult();
            logger.info("[MODULE] {} v: {} p: {} deps: {} sig: {}", info.name, info.version.getVersionString(), info.priority, Arrays.toString(info.dependencies), checkStatus == null ? "null" : checkStatus.type);
            printCheckStatusInfo(checkStatus);
        }
        for (LauncherModuleLoader.ModuleEntity entity : server.launcherModuleLoader.launcherModules) {
            LauncherTrustManager.CheckClassResult checkStatus = entity.checkResult;
            logger.info("[LAUNCHER MODULE] {} sig: {}", entity.path.getFileName().toString(), checkStatus == null ? "null" : checkStatus.type);
            printCheckStatusInfo(checkStatus);
        }
    }

    private void printCheckStatusInfo(LauncherTrustManager.CheckClassResult checkStatus) {
        if (checkStatus != null && checkStatus.endCertificate != null) {
            X509Certificate cert = checkStatus.endCertificate;
            logger.info("[MODULE CERT] Module signer: {}", cert.getSubjectDN().getName());
        }
        if (checkStatus != null && checkStatus.rootCertificate != null) {
            X509Certificate cert = checkStatus.rootCertificate;
            logger.info("[MODULE CERT] Module signer CA: {}", cert.getSubjectDN().getName());
        }
    }
}
