package com.gpcoder.springssl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class SpringSslClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSslClientApplication.class, args);
	}

}
