package org.foxesworld.launchserver.manangers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.utils.helper.IOHelper;
import org.foxesworld.utils.helper.SecurityHelper;

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

public class KeyAgreementManager {
    public final ECPublicKey ecdsaPublicKey;
    public final ECPrivateKey ecdsaPrivateKey;
    public final RSAPublicKey rsaPublicKey;
    public final RSAPrivateKey rsaPrivateKey;

    @SuppressWarnings("unused")
    public KeyAgreementManager(ECPublicKey ecdsaPublicKey, ECPrivateKey ecdsaPrivateKey, RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
        this.ecdsaPublicKey = ecdsaPublicKey;
        this.ecdsaPrivateKey = ecdsaPrivateKey;
        this.rsaPublicKey = rsaPublicKey;
        this.rsaPrivateKey = rsaPrivateKey;
    }

    public KeyAgreementManager(Path keyDirectory) throws IOException, InvalidKeySpecException {
        Path ecdsaPublicKeyPath = keyDirectory.resolve("ecdsa_id.pub"), ecdsaPrivateKeyPath = keyDirectory.resolve("ecdsa_id");
        Logger logger = LogManager.getLogger();
        if (IOHelper.isFile(ecdsaPublicKeyPath) && IOHelper.isFile(ecdsaPrivateKeyPath)) {
            logger.info("Reading ECDSA keypair");
            ecdsaPublicKey = SecurityHelper.toPublicECDSAKey(IOHelper.read(ecdsaPublicKeyPath));
            ecdsaPrivateKey = SecurityHelper.toPrivateECDSAKey(IOHelper.read(ecdsaPrivateKeyPath));
        } else {
            logger.info("Generating ECDSA keypair");
            KeyPair pair = SecurityHelper.genECDSAKeyPair(new SecureRandom());
            ecdsaPublicKey = (ECPublicKey) pair.getPublic();
            ecdsaPrivateKey = (ECPrivateKey) pair.getPrivate();

            // Write key pair list
            logger.info("Writing ECDSA keypair list");
            IOHelper.write(ecdsaPublicKeyPath, ecdsaPublicKey.getEncoded());
            IOHelper.write(ecdsaPrivateKeyPath, ecdsaPrivateKey.getEncoded());
        }
        Path rsaPublicKeyPath = keyDirectory.resolve("rsa_id.pub"), rsaPrivateKeyPath = keyDirectory.resolve("rsa_id");
        if (IOHelper.isFile(rsaPublicKeyPath) && IOHelper.isFile(rsaPrivateKeyPath)) {
            logger.info("Reading RSA keypair");
            rsaPublicKey = SecurityHelper.toPublicRSAKey(IOHelper.read(rsaPublicKeyPath));
            rsaPrivateKey = SecurityHelper.toPrivateRSAKey(IOHelper.read(rsaPrivateKeyPath));
        } else {
            logger.info("Generating RSA keypair");
            KeyPair pair = SecurityHelper.genRSAKeyPair(new SecureRandom());
            rsaPublicKey = (RSAPublicKey) pair.getPublic();
            rsaPrivateKey = (RSAPrivateKey) pair.getPrivate();

            // Write key pair list
            logger.info("Writing RSA keypair list");
            IOHelper.write(rsaPublicKeyPath, rsaPublicKey.getEncoded());
            IOHelper.write(rsaPrivateKeyPath, rsaPrivateKey.getEncoded());
        }
    }
}
