package org.foxesworld.launcher.client.gui.utils;

import org.foxesworld.launcher.client.gui.JavaFXApplication;
import org.foxesworld.launcher.client.gui.config.RuntimeSettings;
import org.foxesworld.launcher.client.gui.helper.JavaVersionsHelper;
import org.foxesworld.launcher.client.gui.service.StateService;
import org.foxesworld.launcher.profiles.ClientProfile;
import org.foxesworld.launcher.profiles.optional.OptionalFile;
import org.foxesworld.launcher.profiles.optional.OptionalView;
import org.foxesworld.launcher.profiles.optional.triggers.OptionalTrigger;
import org.foxesworld.launcher.profiles.optional.triggers.OptionalTriggerContext;
import org.foxesworld.utils.helper.JavaHelper;

public class TriggerManager {
    private final StateService stateService;
    private final JavaFXApplication application;

    public TriggerManager(JavaFXApplication application) {
        this.stateService = application.stateService;
        this.application = application;
    }

    public void process(ClientProfile profile, OptionalView view) {
        TriggerManagerContext context = new TriggerManagerContext(profile);
        for(OptionalFile optional : view.all) {
            if(optional.triggersList == null) continue;
            boolean isRequired = false;
            int success = 0;
            int fail = 0;
            for(OptionalTrigger trigger : optional.triggersList) {
                if(trigger.required) isRequired = true;
                if(trigger.check(optional, context)) {
                    success++;
                } else {
                    fail++;
                }
            }
            if(isRequired) {
                if(fail == 0) view.enable(optional, true, null);
                else view.disable(optional, null);
            } else {
                if(success > 0) view.enable(optional, false, null);
            }
        }
    }

    private class TriggerManagerContext implements OptionalTriggerContext {
        private final ClientProfile profile;

        private TriggerManagerContext(ClientProfile profile) {
            this.profile = profile;
        }

        @Override
        public ClientProfile getProfile() {
            return profile;
        }

        @Override
        public String getUsername() {
            return stateService.getUsername();
        }

        @Override
        public JavaHelper.JavaVersion getJavaVersion() {
            RuntimeSettings.ProfileSettings profileSettings = application.getProfileSettings();
            for(JavaHelper.JavaVersion version : JavaVersionsHelper.javaVersions) {
                if(profileSettings.javaPath != null && profileSettings.javaPath.equals(version.jvmDir.toString())) {
                    return version;
                }
            }
            return JavaHelper.JavaVersion.getCurrentJavaVersion();
        }
    }
}
