package org.foxesworld.launcher.client.gui.config;

import org.foxesworld.launcher.NewLauncherSettings;
import org.foxesworld.launcher.client.JavaRuntimeModule;
import org.foxesworld.launcher.client.gui.config.RuntimeSettings;
import org.foxesworld.launcher.managers.SettingsManager;

public class StdSettingsManager extends SettingsManager {

    @Override
    public NewLauncherSettings getDefaultConfig() {
        NewLauncherSettings newLauncherSettings = new NewLauncherSettings();
        newLauncherSettings.userSettings.put(JavaRuntimeModule.RUNTIME_NAME, RuntimeSettings.getDefault());
        return newLauncherSettings;
    }
}
