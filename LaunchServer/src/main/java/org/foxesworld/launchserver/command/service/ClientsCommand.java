package org.foxesworld.launchserver.command.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.command.Command;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.WebSocketService;
import org.foxesworld.launchserver.socket.handlers.WebSocketFrameHandler;
import org.foxesworld.utils.helper.IOHelper;

import java.util.Base64;

public class ClientsCommand extends Command {
    private transient final Logger logger = LogManager.getLogger();

    public ClientsCommand(LaunchServer server) {
        super(server);
    }

    @Override
    public String getArgsDescription() {
        return null;
    }

    @Override
    public String getUsageDescription() {
        return "Show all connected clients";
    }

    @Override
    public void invoke(String... args) {
        WebSocketService service = server.nettyServerSocketHandler.nettyServer.service;
        service.channels.forEach((channel -> {
            WebSocketFrameHandler frameHandler = channel.pipeline().get(WebSocketFrameHandler.class);
            if (frameHandler == null) {
                logger.info("Channel {}", IOHelper.getIP(channel.remoteAddress()));
                return;
            }
            Client client = frameHandler.getClient();
            String ip = frameHandler.context.ip != null ? frameHandler.context.ip : IOHelper.getIP(channel.remoteAddress());
            if (!client.isAuth)
                logger.info("Channel {} | connectUUID {} | checkSign {}", ip, frameHandler.getConnectUUID(), client.checkSign ? "true" : "false");
            else {
                logger.info("Client name {} | ip {} | connectUUID {}", client.username == null ? "null" : client.username, ip, frameHandler.getConnectUUID());
                logger.info("userUUID: {} | session {}", client.uuid == null ? "null" : client.uuid.toString(), client.session == null ? "null" : client.session);
                logger.info("OAuth {} | session {}", client.useOAuth, client.sessionObject == null ? "null" : client.sessionObject);
                logger.info("Data: checkSign {} | auth_id {}", client.checkSign ? "true" : "false",
                        client.auth_id);
                if (client.trustLevel != null) {
                    logger.info("trustLevel | key {} | pubkey {}", client.trustLevel.keyChecked ? "checked" : "unchecked", client.trustLevel.publicKey == null ? "null" : new String(Base64.getEncoder().encode(client.trustLevel.publicKey)));
                }
                logger.info("Permissions: {} (permissions {} | flags {})", client.permissions == null ? "null" : client.permissions.toString(), client.permissions == null ? 0 : client.permissions.permissions, client.permissions == null ? 0 : client.permissions.flags);
            }
        }));
    }
}
