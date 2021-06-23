package org.foxesworld.launchserver.manangers;

import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.utils.Version;

import java.util.HashMap;
import java.util.Map;

public class FeaturesManager {
    private final Map<String, String> map;

    public FeaturesManager(LaunchServer server) {
        map = new HashMap<>();
        addFeatureInfo("version", Version.getVersion().getVersionString());
        addFeatureInfo("projectName", server.config.projectName);
    }

    public Map<String, String> getMap() {
        return map;
    }

    @SuppressWarnings("unused")
    public String getFeatureInfo(String name) {
        return map.get(name);
    }

    @SuppressWarnings("UnusedReturnValue")
    public String addFeatureInfo(String name, String featureInfo) {
        return map.put(name, featureInfo);
    }

    @SuppressWarnings("unused")
    public String removeFeatureInfo(String name) {
        return map.remove(name);
    }
}
