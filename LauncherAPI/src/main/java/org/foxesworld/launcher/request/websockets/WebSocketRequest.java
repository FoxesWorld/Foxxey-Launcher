package org.foxesworld.launcher.request.websockets;

import org.foxesworld.utils.TypeSerializeInterface;

public interface WebSocketRequest extends TypeSerializeInterface {
    String getType();
}
