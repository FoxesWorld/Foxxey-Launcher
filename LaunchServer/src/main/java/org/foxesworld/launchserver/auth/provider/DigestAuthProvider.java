package org.foxesworld.launchserver.auth.provider;

import org.foxesworld.launchserver.auth.AuthException;
import org.foxesworld.utils.helper.SecurityHelper;
import org.foxesworld.utils.helper.SecurityHelper.DigestAlgorithm;

@SuppressWarnings("unused")
public abstract class DigestAuthProvider extends AuthProvider {
    private DigestAlgorithm digest;


    protected final void verifyDigest(String validDigest, String password) throws AuthException {
        boolean valid;
        if (digest == DigestAlgorithm.PLAIN)
            valid = password.equals(validDigest);
        else if (validDigest == null)
            valid = false;
        else {
            byte[] actualDigest = SecurityHelper.digest(digest, password);
            valid = SecurityHelper.toHex(actualDigest).equals(validDigest);
        }

        // Verify is valid
        if (!valid)
            authError("Incorrect username or password");
    }
}
