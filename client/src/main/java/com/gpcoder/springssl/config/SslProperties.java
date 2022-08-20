package com.gpcoder.springssl.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "server.ssl")
public class SslProperties {

    private String keyStore;

    private String keyStorePassword;

    private String keyPassword;

    private String trustStore;

    private String trustStorePassword;
}
