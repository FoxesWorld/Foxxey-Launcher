package org.foxesworld.launchserver.modules.impl;

import org.foxesworld.launcher.modules.LauncherInitContext;
import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.launcher.modules.LauncherModuleInfo;
import org.foxesworld.launcher.modules.events.InitPhase;
import org.foxesworld.utils.Version;

public class LaunchServerCoreModule extends LauncherModule {
    public LaunchServerCoreModule() {
        super(new LauncherModuleInfo("LaunchServerCore", Version.getVersion()));
    }

    @Override
    public void init(LauncherInitContext initContext) {
        registerEvent(this::testEvent, InitPhase.class);
    }

    public void testEvent(InitPhase event) {
        //LogHelper.debug("[LaunchServerCore] Event LaunchServerInitPhase passed");
    }

    @Override
    public <T extends Event> boolean registerEvent(EventHandler<T> handle, Class<T> tClass) {
        return super.registerEvent(handle, tClass);
    }
}
