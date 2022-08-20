package com.gpcoder.springssl.config;

import com.gpcoder.springssl.exception.SslConfigException;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.SslProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Configuration
public class WebClientConfig {

    private static final int TIME_OUT_IN_MS = 5000;
    private static final String KEY_STORE_TYPE = "JKS";

    private final SslProperties sslProperties;

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .secure(this::configSsl)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIME_OUT_IN_MS)
                .responseTimeout(Duration.ofMillis(TIME_OUT_IN_MS))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(TIME_OUT_IN_MS, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(TIME_OUT_IN_MS, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    private void configSsl(SslProvider.SslContextSpec sslSpec) {
        try {
            sslSpec.sslContext(SslContextBuilder.forClient()
                    .keyManager(buildKeyManagerFactory())
                    .trustManager(buildTrustManagerFactory())
                    .build());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | IOException | CertificateException e) {
            throw new SslConfigException("Failed to config SSL", e);
        }
    }

    private KeyManagerFactory buildKeyManagerFactory() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore keyStore = loadJksFile(sslProperties.getKeyStore(), sslProperties.getKeyStorePassword());

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, sslProperties.getKeyPassword().toCharArray());

        return keyManagerFactory;
    }

    private TrustManagerFactory buildTrustManagerFactory() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore trustStore = loadJksFile(sslProperties.getTrustStore(), sslProperties.getTrustStorePassword());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        return trustManagerFactory;
    }

    private KeyStore loadJksFile(String jksFile, String password) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE);
        trustStore.load(new FileInputStream((ResourceUtils.getFile(jksFile))), password.toCharArray());

        return trustStore;
    }
}
