package org.foxesworld.launcher.impl;

import org.junit.jupiter.api.Assertions;
import org.foxesworld.launcher.modules.LauncherInitContext;
import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.launcher.modules.LauncherModuleInfo;
import org.foxesworld.utils.Version;

public class Depend1Module extends LauncherModule {
    public Depend1Module() {
        super(new LauncherModuleInfo("depend1", new Version(1, 0, 0),
                0,
                new String[]{"depend3", "internal", "virtual"}));
    }

    @Override
    public void init(LauncherInitContext initContext) {
        InternalModule module = modulesManager.getModule(InternalModule.class);
        Assertions.assertEquals(module.getInitStatus(), InitStatus.FINISH);
        Depend3Module module1 = modulesManager.getModule(Depend3Module.class);
        Assertions.assertEquals(module1.getInitStatus(), InitStatus.FINISH);
        VirtualInterface virtualInterface = modulesManager.getModuleByInterface(VirtualInterface.class);
        Assertions.assertEquals(((LauncherModule) virtualInterface).getInitStatus(), InitStatus.FINISH);
    }
}
