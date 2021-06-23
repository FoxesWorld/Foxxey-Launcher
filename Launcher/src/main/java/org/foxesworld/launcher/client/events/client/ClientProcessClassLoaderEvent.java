package org.foxesworld.launcher.client.events.client;

import org.foxesworld.launcher.LauncherEngine;
import org.foxesworld.launcher.modules.LauncherModule;
import org.foxesworld.launcher.profiles.ClientProfile;

public class ClientProcessClassLoaderEvent extends LauncherModule.Event {
    public final LauncherEngine clientInstance;
    public final ClassLoader clientClassLoader;
    public final ClientProfile profile;

    public ClientProcessClassLoaderEvent(LauncherEngine clientInstance, ClassLoader clientClassLoader, ClientProfile profile) {
        this.clientInstance = clientInstance;
        this.clientClassLoader = clientClassLoader;
        this.profile = profile;
    }
}
