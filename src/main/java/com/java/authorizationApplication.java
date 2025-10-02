package com.java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.java.config.RsaKeyProperties;

@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class authorizationApplication {

	public static void main(String[] args) {
		SpringApplication.run(authorizationApplication.class, args);
	}

}
