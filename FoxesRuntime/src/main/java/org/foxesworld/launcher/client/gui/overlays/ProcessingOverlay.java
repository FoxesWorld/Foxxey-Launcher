package org.foxesworld.launcher.client.gui.overlays;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import org.foxesworld.launcher.client.gui.JavaFXApplication;
import org.foxesworld.launcher.client.gui.helper.LookupHelper;
import org.foxesworld.launcher.client.gui.scenes.AbstractScene;
import org.foxesworld.launcher.client.gui.impl.ContextHelper;
import org.foxesworld.launcher.request.Request;
import org.foxesworld.launcher.request.WebSocketEvent;
import org.foxesworld.utils.helper.LogHelper;

import java.io.IOException;
import java.util.function.Consumer;

public class ProcessingOverlay extends AbstractOverlay {
    private Node spinner;
    private Labeled description;

    public ProcessingOverlay(JavaFXApplication application) {
        super("overlay/processing/processing.fxml", application);
    }

    @Override
    protected void doInit() {
        // spinner = LookupHelper.lookup(pane, "#spinner"); //TODO: DrLeonardo?
        description = LookupHelper.lookup(layout, "#description");
    }

    @Override
    public String getName() {
        return "processing";
    }

    @Override
    public void reset() {
        description.textProperty().unbind();
        description.getStyleClass().remove("error");
        description.setText("...");
    }

    public void errorHandle(Throwable e) {
        super.errorHandle(e);
        description.textProperty().unbind();
        description.getStyleClass().add("error");
        description.setText(e.toString());
    }

    public final <T extends WebSocketEvent> void processRequest(AbstractScene scene, String message, Request<T> request, Consumer<T> onSuccess, EventHandler<ActionEvent> onError) {
        processRequest(scene, message, request, onSuccess, null, onError);
    }
    public final <T extends WebSocketEvent> void processRequest(AbstractScene scene, String message, Request<T> request, Consumer<T> onSuccess, Consumer<Throwable> onException, EventHandler<ActionEvent> onError) {
        try {
            scene.showOverlay(this, (e) -> {
                try {
                    description.setText(message);
                    application.service.request(request).thenAccept((result) -> {
                        LogHelper.dev("RequestFuture complete normally");
                        onSuccess.accept(result);
                    }).exceptionally((error) -> {
                        if(onException != null) onException.accept(error);
                        ContextHelper.runInFxThreadStatic(() -> errorHandle(error.getCause()));
                        hide(2500, scene, onError);
                        return null;
                    });
                } catch (IOException ex) {
                    errorHandle(ex);
                    hide(2500, scene, onError);
                }
            });
        } catch (Exception e) {
            errorHandle(e);
        }
    }
}
