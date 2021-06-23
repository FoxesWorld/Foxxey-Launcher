package org.foxesworld.launchserver.impl;

import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.config.LaunchServerConfig;
import org.foxesworld.launchserver.config.LaunchServerRuntimeConfig;

import java.io.IOException;

public class TestLaunchServerConfigManager implements LaunchServer.LaunchServerConfigManager {
    public LaunchServerConfig config;
    public LaunchServerRuntimeConfig runtimeConfig;

    public TestLaunchServerConfigManager() {
        config = LaunchServerConfig.getDefault(LaunchServer.LaunchServerEnv.TEST);
        runtimeConfig = new LaunchServerRuntimeConfig();
        runtimeConfig.reset();
    }

    @Override
    public LaunchServerConfig readConfig() throws IOException {
        return config;
    }

    @Override
    public LaunchServerRuntimeConfig readRuntimeConfig() throws IOException {
        return runtimeConfig;
    }

    @Override
    public void writeConfig(LaunchServerConfig config) throws IOException {

    }

    @Override
    public void writeRuntimeConfig(LaunchServerRuntimeConfig config) throws IOException {

    }
}
