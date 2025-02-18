package com.es.api_ciervus;

import com.es.api_ciervus.security.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
public class ApiCiervusApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiCiervusApplication.class, args);
	}

}
