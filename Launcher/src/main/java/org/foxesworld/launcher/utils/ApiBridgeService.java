package org.foxesworld.launcher.utils;

import org.foxesworld.launcher.Launcher;
import org.foxesworld.launcher.LauncherTrustManager;

import java.security.cert.X509Certificate;

public class ApiBridgeService {
    public static LauncherTrustManager.CheckClassResult checkCertificates(X509Certificate[] certs) {
        LauncherTrustManager trustManager = Launcher.getConfig().trustManager;
        return trustManager.checkCertificates(certs, trustManager::stdCertificateChecker);
    }

    public static void checkCertificatesSuccess(X509Certificate[] certs) throws Exception {
        LauncherTrustManager trustManager = Launcher.getConfig().trustManager;
        trustManager.checkCertificatesSuccess(certs, trustManager::stdCertificateChecker);
    }
}
