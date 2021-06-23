package org.foxesworld.launcher.impl;

import org.foxesworld.launcher.modules.LauncherInitContext;
import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.launcher.modules.LauncherModuleInfo;

public class InternalModule extends LauncherModule {
    public InternalModule() {
        super(new LauncherModuleInfo("internal"));
    }

    @Override
    public void init(LauncherInitContext initContext) {
    }
}
