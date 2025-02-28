package org.foxesworld.launcher.client.gui.stage;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.foxesworld.launcher.client.gui.JavaFXApplication;
import org.foxesworld.launcher.client.gui.impl.AbstractStage;
import org.foxesworld.utils.helper.LogHelper;

import java.io.IOException;

public class PrimaryStage extends AbstractStage {
    public PrimaryStage(Stage primaryStage, String title) {
        super(primaryStage);
        primaryStage.setTitle(title);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        // Icons
        try {
            Image icon = new Image(JavaFXApplication.getResourceURL("favicon.png").toString());
            stage.getIcons().add(icon);
        } catch (IOException e) {
            LogHelper.error(e);
        }
    }

    @Override
    public void close() {
        Platform.exit();
    }
}
