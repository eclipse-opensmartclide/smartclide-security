package com.theia;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.client.RestTemplate;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
public class TheiaBackEndApplication {
	@Bean
	public MultipartConfigElement getMultiConfig(){
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize(DataSize.parse("4000MB"));
		factory.setMaxRequestSize(DataSize.parse("4000MB"));
		return factory.createMultipartConfig();
	}

	public static void main(String[] args) {





		SpringApplication.run(TheiaBackEndApplication.class, args);
	}

}
