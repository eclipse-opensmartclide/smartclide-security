/*******************************************************************************
 * Copyright (C) 2021-2022 CERTH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
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
public class 	TheiaBackEndApplication {


	@Bean
	public RestTemplate restTesmplate() {
		return new RestTemplate();
	}
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
