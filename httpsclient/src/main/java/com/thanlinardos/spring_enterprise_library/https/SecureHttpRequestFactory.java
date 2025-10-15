package com.thanlinardos.spring_enterprise_library.https;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import jakarta.annotation.Nonnull;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * A custom HTTP request factory that configures SSL settings for HTTPS connections.
 * It extends the SimpleClientHttpRequestFactory to provide SSL context configuration.
 */
public class SecureHttpRequestFactory extends SimpleClientHttpRequestFactory {

    private final SSLContext sslContext;

    /**
     * Constructs a SecureHttpRequestFactory with the specified SSL context.
     *
     * @param sslContext the SSL context to be used for HTTPS connections
     */
    public SecureHttpRequestFactory(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    /**
     * Prepares the given HttpURLConnection by configuring SSL settings if it's an instance of HttpsURLConnection.
     * It sets the SSL socket factory and hostname verifier for secure connections.
     *
     * @param connection the HttpURLConnection to be prepared
     * @param httpMethod the HTTP method (e.g., GET, POST) to be used
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void prepareConnection(@Nonnull HttpURLConnection connection, @Nonnull String httpMethod) throws IOException {
        if (connection instanceof HttpsURLConnection httpsURLConnection) {
            httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            // In a secure production environment, hostname verification should be enabled to ensure
            // that the server being accessed is the intended one and to prevent potential security vulnerabilities.
            httpsURLConnection.setHostnameVerifier((hostname, session) -> false);
        }
        super.prepareConnection(connection, httpMethod);
    }
}