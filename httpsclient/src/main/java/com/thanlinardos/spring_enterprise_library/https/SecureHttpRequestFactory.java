package com.thanlinardos.spring_enterprise_library.https;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.NonNull;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.HttpURLConnection;

public class SecureHttpRequestFactory extends SimpleClientHttpRequestFactory {

    private final SSLContext sslContext;

    public SecureHttpRequestFactory(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    protected void prepareConnection(@NonNull HttpURLConnection connection, @NonNull String httpMethod) throws IOException {
        if (connection instanceof HttpsURLConnection httpsURLConnection) {
            httpsURLConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            // In a secure production environment, hostname verification should be enabled to ensure
            // that the server being accessed is the intended one and to prevent potential security vulnerabilities.
            httpsURLConnection.setHostnameVerifier((hostname, session) -> false);
        }
        super.prepareConnection(connection, httpMethod);
    }
}