package org.foxesworld.launcher.profiles.optional;

import org.foxesworld.launcher.LauncherNetworkAPI;

@Deprecated
public enum OptionalType {
    @LauncherNetworkAPI
    FILE,
    @LauncherNetworkAPI
    CLASSPATH,
    @LauncherNetworkAPI
    JVMARGS,
    @LauncherNetworkAPI
    CLIENTARGS
}
