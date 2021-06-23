package org.foxesworld.launcher.impl;

import org.foxesworld.launcher.modules.LauncherInitContext;
import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.launcher.modules.LauncherModuleInfo;
import org.foxesworld.utils.Version;

public class CyclicDependModule extends LauncherModule {
    public CyclicDependModule() {
        super(new LauncherModuleInfo("cyclic1",
                new Version(1, 0, 0),
                2, new String[]{"cyclic2"}));
    }

    @Override
    public void init(LauncherInitContext initContext) {

    }
}
