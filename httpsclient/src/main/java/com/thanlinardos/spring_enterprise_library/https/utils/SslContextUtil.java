package com.thanlinardos.spring_enterprise_library.https.utils;

import com.thanlinardos.spring_enterprise_library.https.properties.KeyAndTrustStoreProperties;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Utility class for building SSL contexts and REST clients with custom keystore and truststore configurations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SslContextUtil {

    /**
     * The default type of the keystore and truststore.
     */
    private static final String KEY_STORE_TYPE = "PKCS12";

    /**
     * Builds a RESTEasy client with the specified keystore and truststore properties.
     *
     * @param keystore   the keystore properties
     * @param truststore the truststore properties
     * @return a configured RESTEasy client
     * @throws UnrecoverableKeyException if the key cannot be recovered from the keystore
     * @throws CertificateException      if there is an error with the certificates
     * @throws KeyStoreException         if there is an error with the keystore
     * @throws IOException               if there is an I/O error
     * @throws NoSuchAlgorithmException  if the algorithm for key management is not available
     * @throws KeyManagementException    if there is an error managing keys
     */
    public static Client buildResteasyClient(KeyAndTrustStoreProperties keystore, KeyAndTrustStoreProperties truststore) throws UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        return ClientBuilder.newBuilder()
                .sslContext(buildSSLContext(keystore, truststore))
                .build();
    }

    /**
     * Builds an SSL context with the specified keystore and truststore properties.
     *
     * @param keystore   the keystore properties
     * @param truststore the truststore properties
     * @return a configured SSL context
     * @throws KeyStoreException         if there is an error with the keystore
     * @throws IOException               if there is an I/O error
     * @throws NoSuchAlgorithmException  if the algorithm for key management is not available
     * @throws CertificateException      if there is an error with the certificates
     * @throws KeyManagementException    if there is an error managing keys
     * @throws UnrecoverableKeyException if the key cannot be recovered from the keystore
     */
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
        keyStorePassword = keystore.password().toCharArray();
        keyStoreObject.load(keystore.path().getInputStream(), keyStorePassword);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStoreObject, keyStorePassword);
        return keyStorePassword;
    }

    private static void initializeTruststore(KeyAndTrustStoreProperties truststore, KeyStore trustStoreObject) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {
        trustStoreObject.load(truststore.path().getInputStream(), truststore.password().toCharArray());
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStoreObject);
    }
}
