package org.foxesworld.launchserver.socket.response.secure;

import io.netty.channel.ChannelHandlerContext;
import org.foxesworld.launchserver.auth.protect.interfaces.SecureProtectHandler;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.SimpleResponse;

import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;

public class VerifySecureLevelKeyResponse extends SimpleResponse {
    public byte[] publicKey;
    public byte[] signature;

    @Override
    public String getType() {
        return "verifySecureLevelKey";
    }

    @Override
    public void execute(ChannelHandlerContext ctx, Client client) {
        if (!(server.config.protectHandler instanceof SecureProtectHandler) || client.trustLevel == null || client.trustLevel.verifySecureKey == null) {
            sendError("This method not allowed");
            return;
        }
        SecureProtectHandler secureProtectHandler = (SecureProtectHandler) server.config.protectHandler;
        try {
            secureProtectHandler.verifySecureLevelKey(publicKey, client.trustLevel.verifySecureKey, signature);
        } catch (InvalidKeySpecException e) {
            sendError("Invalid public key");
            return;
        } catch (SignatureException e) {
            sendError("Invalid signature");
            return;
        } catch (SecurityException e) {
            sendError(e.getMessage());
            return;
        }
        client.trustLevel.keyChecked = true;
        client.trustLevel.publicKey = publicKey;
        try {
            sendResult(secureProtectHandler.onSuccessVerify(client));
        } catch (SecurityException e) {
            sendError(e.getMessage());
        }

    }
}
