package org.foxesworld.launcher.impl;

import org.foxesworld.launcher.impl.event.CancelEvent;
import org.foxesworld.launcher.impl.event.NormalEvent;
import org.foxesworld.launcher.modules.LauncherInitContext;
import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.launcher.modules.LauncherModuleInfo;

public class TestModule extends LauncherModule {

    public TestModule() {
        super(new LauncherModuleInfo("testModule"));
    }

    @Override
    public void init(LauncherInitContext initContext) {
        registerEvent(this::testevent, NormalEvent.class);
        registerEvent(this::testevent2, CancelEvent.class);
    }

    public void testevent(NormalEvent event) {
        event.passed = true;
    }

    public void testevent2(CancelEvent cancelEvent) {
        cancelEvent.cancel();
    }
}
