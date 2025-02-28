package org.foxesworld.launchserver.binary.tasks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.foxesworld.launchserver.LaunchServer;
import org.foxesworld.launchserver.helper.SignHelper;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class CertificateAutogenTask implements LauncherBuildTask {
    private final LaunchServer server;
    private transient final Logger logger = LogManager.getLogger();
    public X509Certificate certificate;
    public X509CertificateHolder bcCertificate;
    public CMSSignedDataGenerator signedDataGenerator;

    public CertificateAutogenTask(LaunchServer server) {
        this.server = server;
    }

    @Override
    public String getName() {
        return "CertificateAutogen";
    }

    @Override
    public Path process(Path inputFile) throws IOException {
        if (signedDataGenerator != null) return inputFile;
        try {
            logger.warn("You are using an auto-generated certificate (sign.enabled false). It is not good");
            logger.warn("It is highly recommended that you use the correct certificate (sign.enabled true)");
            logger.warn("You can use GenerateCertificateModule or your own certificate.");
            X500NameBuilder subject = new X500NameBuilder();
            subject.addRDN(BCStyle.CN, server.config.projectName.concat(" Autogenerated"));
            subject.addRDN(BCStyle.O, server.config.projectName);
            LocalDateTime startDate = LocalDate.now().atStartOfDay();
            X509v3CertificateBuilder builder = new X509v3CertificateBuilder(
                    subject.build(),
                    new BigInteger("0"),
                    Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(startDate.plusDays(3650).atZone(ZoneId.systemDefault()).toInstant()),
                    new X500Name("CN=ca"),
                    SubjectPublicKeyInfo.getInstance(server.keyAgreementManager.ecdsaPublicKey.getEncoded()));
            builder.addExtension(Extension.extendedKeyUsage, false, new ExtendedKeyUsage(KeyPurposeId.id_kp_codeSigning));
            JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA256WITHECDSA");
            ContentSigner signer = csBuilder.build(server.keyAgreementManager.ecdsaPrivateKey);
            bcCertificate = builder.build(signer);
            certificate = new JcaX509CertificateConverter().setProvider("BC")
                    .getCertificate(bcCertificate);
            ArrayList<Certificate> chain = new ArrayList<>();
            chain.add(certificate);
            signedDataGenerator = SignHelper.createSignedDataGenerator(server.keyAgreementManager.ecdsaPrivateKey, certificate, chain, "SHA256WITHECDSA");
        } catch (OperatorCreationException | CMSException | CertificateException e) {
            logger.error("Certificate generate failed", e);
        }
        return inputFile;
    }

    @Override
    public boolean allowDelete() {
        return false;
    }
}
