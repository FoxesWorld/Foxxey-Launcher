package org.foxesworld.launcher.client.gui.stage;

import org.foxesworld.launcher.client.gui.JavaFXApplication;
import org.foxesworld.launcher.client.gui.impl.AbstractStage;

public class ConsoleStage extends AbstractStage {
    public ConsoleStage(JavaFXApplication application) {
        super(application.newStage());
        stage.setTitle(String.format("%s Launcher Console", application.config.projectName));
        stage.setResizable(false);
    }
}
