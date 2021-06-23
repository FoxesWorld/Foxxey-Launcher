package org.foxesworld.launchserver.auth.protect.interfaces;

import org.foxesworld.launcher.events.request.GetSecureLevelInfoRequestEvent;
import org.foxesworld.launcher.events.request.SecurityReportRequestEvent;
import org.foxesworld.launcher.events.request.VerifySecureLevelKeyRequestEvent;
import org.foxesworld.launchserver.socket.Client;
import org.foxesworld.launchserver.socket.response.secure.SecurityReportResponse;
import org.foxesworld.utils.helper.SecurityHelper;

import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;

public interface SecureProtectHandler {
    default byte[] generateSecureLevelKey() {
        return SecurityHelper.randomBytes(128);
    }

    default void verifySecureLevelKey(byte[] publicKey, byte[] data, byte[] signature) throws InvalidKeySpecException, SignatureException {
        if (publicKey == null || signature == null) throw new InvalidKeySpecException();
        ECPublicKey pubKey = SecurityHelper.toPublicECDSAKey(publicKey);
        Signature sign = SecurityHelper.newECVerifySignature(pubKey);
        sign.update(data);
        sign.verify(signature);
    }

    GetSecureLevelInfoRequestEvent onGetSecureLevelInfo(GetSecureLevelInfoRequestEvent event);

    boolean allowGetSecureLevelInfo(Client client);

    default SecurityReportRequestEvent onSecurityReport(@SuppressWarnings("unused") SecurityReportResponse report,
                                                        @SuppressWarnings("unused") Client client) {
        return new SecurityReportRequestEvent();
    }

    default VerifySecureLevelKeyRequestEvent onSuccessVerify(Client client) {
        return new VerifySecureLevelKeyRequestEvent();
    }
}
