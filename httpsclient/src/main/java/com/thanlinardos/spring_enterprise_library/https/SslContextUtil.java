package com.thanlinardos.spring_enterprise_library.https;

import com.thanlinardos.spring_enterprise_library.model.properties.KeyAndTrustStoreProperties;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class SslContextUtil {

    private SslContextUtil() {
    }

    private static final String KEY_STORE_TYPE = "PKCS12";

    public static Client buildResteasyClient(KeyAndTrustStoreProperties keystore, KeyAndTrustStoreProperties truststore) throws UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        return ClientBuilder.newBuilder()
                .sslContext(buildSSLContext(keystore, truststore))
                .build();
    }

    public static SSLContext buildSSLContext(KeyAndTrustStoreProperties keystore, KeyAndTrustStoreProperties truststore) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException {
        KeyStore truststoreObject = null;
        if (truststore.isEnabled()) {
            truststoreObject = KeyStore.getInstance(KEY_STORE_TYPE);
            initializeTruststore(truststore, truststoreObject);
        }
        KeyStore keystoreObject = null;
        char[] keystorePassword = null;
        if (keystore.isEnabled()) {
            keystoreObject = KeyStore.getInstance(KEY_STORE_TYPE);
            keystorePassword = initializeKeystore(keystore, keystoreObject);
        }

        return SSLContextBuilder
                .create()
                .loadTrustMaterial(truststoreObject, (chain, authType) -> true)
                .loadKeyMaterial(keystoreObject, keystorePassword)
                .setKeyStoreType(KEY_STORE_TYPE)
                .build();
    }

    private static char[] initializeKeystore(KeyAndTrustStoreProperties keystore, KeyStore keyStoreObject) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableKeyException {
        char[] keyStorePassword;
        keyStorePassword = keystore.getPassword().toCharArray();
        keyStoreObject.load(keystore.getPath().getInputStream(), keyStorePassword);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStoreObject, keyStorePassword);
        return keyStorePassword;
    }

    private static void initializeTruststore(KeyAndTrustStoreProperties truststore, KeyStore trustStoreObject) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        trustStoreObject.load(truststore.getPath().getInputStream(), truststore.getPassword().toCharArray());
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStoreObject);
    }
}
