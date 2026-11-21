package com.amigosecreto.v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.amigosecreto.v1.core",
		"com.amigosecreto.v1.infrastructure",
		"com.amigosecreto.v1.api",
		"com.amigosecreto.v1.api.rest",
		"com.amigosecreto.v1.security",
		"com.amigosecreto.v1.shared"
})
public class V1Application {

	public static void main(String[] args) {
		SpringApplication.run(V1Application.class, args);
	}

}
