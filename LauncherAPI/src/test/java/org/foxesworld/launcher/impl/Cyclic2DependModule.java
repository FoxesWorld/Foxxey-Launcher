package org.foxesworld.launcher.impl;

import org.foxesworld.launcher.modules.LauncherInitContext;
import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.launcher.modules.LauncherModuleInfo;
import org.foxesworld.utils.Version;

public class Cyclic2DependModule extends LauncherModule {
    public Cyclic2DependModule() {
        super(new LauncherModuleInfo("cyclic2",
                new Version(1, 0, 0),
                2, new String[]{"cyclic1"}));
    }

    @Override
    public void init(LauncherInitContext initContext) {

    }
}
