package org.foxesworld.launcher.impl;

import org.junit.jupiter.api.Assertions;
import org.foxesworld.launcher.modules.LauncherInitContext;
import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.launcher.modules.LauncherModuleInfo;
import org.foxesworld.utils.Version;

public class Depend2Module extends LauncherModule {
    public Depend2Module() {
        super(new LauncherModuleInfo("depend2"));
    }

    @Override
    public void preInitAction() {
        modulesManager.loadModule(new InternalModule());
        modulesManager.loadModule(new ProvidedModule());
    }

    @Override
    public void init(LauncherInitContext initContext) {
        requireModule("depend1", new Version(1, 0, 0));
        try {
            requireModule("dependNotFound", new Version(1, 0, 0));
            Assertions.fail("dependNotFound");
        } catch (RuntimeException ignored) {

        }
        try {
            requireModule("depend1", new Version(10, 0, 0));
            Assertions.fail("depend1 high version");
        } catch (RuntimeException ignored) {

        }
    }
}
