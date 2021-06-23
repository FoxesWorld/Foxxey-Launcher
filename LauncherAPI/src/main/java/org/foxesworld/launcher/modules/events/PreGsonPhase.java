package org.foxesworld.launcher.modules.events;

import com.google.gson.GsonBuilder;
import org.foxesworld.launcher.modules.LauncherModule;

public class PreGsonPhase extends LauncherModule.Event {
    public final GsonBuilder gsonBuilder;

    public PreGsonPhase(GsonBuilder gsonBuilder) {
        this.gsonBuilder = gsonBuilder;
    }
}
