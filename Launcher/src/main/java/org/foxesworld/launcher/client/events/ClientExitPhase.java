package org.foxesworld.launcher.client.events;

import org.foxesworld.launcher.modules.events.ClosePhase;

public class ClientExitPhase extends ClosePhase {
    public final int code;

    public ClientExitPhase(int code) {
        this.code = code;
    }
}
