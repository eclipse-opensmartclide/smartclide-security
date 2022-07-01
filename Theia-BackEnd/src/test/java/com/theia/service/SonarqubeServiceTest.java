/*******************************************************************************
 * Copyright (C) 2021-2022 CERTH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
//package com.theia.service;
//
//import org.json.JSONObject;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//class SonarqubeServiceTest {
//
//    private static String token = "3fa6958c8209021fa8e2d7f0f2cb899256494601";
//
//    @Autowired
//    SonarqubeService sonarqubeService;
//
//    @Autowired
//    TheiaService theiaService;
//
//    @Test
//    void sonarJavaAnalysis() {
//        String url = "https://github.com/spring-projects/spring-mvc-showcase";
//        UUID id = UUID.randomUUID();
//        File dir = null;
//        try {
//            dir = this.theiaService.retrieveGithubCode(url, id);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertTrue(dir.exists(), "Directory created!");
//
//        try {
//            this.sonarqubeService.sonarJavaAnalysis(id, token);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(token, "");
//        HttpEntity request = new HttpEntity(headers);
//
//        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9000/api/components/app?component=" + id,
//                HttpMethod.GET,
//                request,
//                String.class
//        );
//        String json = response.getBody();
//        JSONObject object = new JSONObject(json);
//        String key = (String) object.get("key");
//        assertTrue(key.equals(id.toString()), "Analysis successful.");
//    }
//
//    @Test
//    void sonarPythonAnalysis() {
//        String url = "https://github.com/pallets/flask";
//        UUID id = UUID.randomUUID();
//        File dir = null;
//        try {
//            dir = this.theiaService.retrieveGithubCode(url, id);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertTrue(dir.exists(), "Directory created!");
//
//        try {
//            this.sonarqubeService.sonarPythonAnalysis(id, token);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(token, "");
//        HttpEntity request = new HttpEntity(headers);
//
//        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9000/api/components/app?component=" + id,
//                HttpMethod.GET,
//                request,
//                String.class
//        );
//        String json = response.getBody();
//        JSONObject object = new JSONObject(json);
//        String key = (String) object.get("key");
//        assertTrue(key.equals(id.toString()), "Analysis successful.");
//    }
//
//    @Test
//    void sonarqubeCustomVulnerabilities() {
//        String url = "https://github.com/pallets/flask";
//        UUID id = UUID.randomUUID();
//        File dir = null;
//        try {
//            dir = this.theiaService.retrieveGithubCode(url, id);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertTrue(dir.exists(), "Directory created!");
//
//        try {
//            this.sonarqubeService.sonarPythonAnalysis(id, token);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(token, "");
//        HttpEntity request = new HttpEntity(headers);
//        Set<String> vulnerabilities = new HashSet<>(){{
//           add("dos");
//           add("auth");
//        }};
//
//        HashMap<String, Double> sonarVuls = this.sonarqubeService.sonarqubeCustomVulnerabilities(token, vulnerabilities, id.toString());
//        assertTrue(sonarVuls.keySet().contains("auth"));
//        assertTrue(sonarVuls.keySet().contains("dos"));
//        assertTrue(sonarVuls.keySet().size() == 2);
//
//        assertFalse(sonarVuls.get("dos").isNaN());
//        assertFalse(sonarVuls.get("auth").isNaN());
//
//        vulnerabilities.add("weak-cryptography");
//        sonarVuls = this.sonarqubeService.sonarqubeCustomVulnerabilities(token, vulnerabilities, id.toString());
//        assertTrue(sonarVuls.keySet().contains("weak-cryptography"));
//        assertTrue(sonarVuls.keySet().size() == 3);
//
//        assertFalse(sonarVuls.get("weak-cryptography").isNaN());
//    }
//
//    @Test
//    void sonarqubeCustomMetrics() {
//        String url = "https://github.com/pallets/flask";
//        UUID id = UUID.randomUUID();
//        File dir = null;
//        try {
//            dir = this.theiaService.retrieveGithubCode(url, id);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertTrue(dir.exists(), "Directory created!");
//
//        try {
//            this.sonarqubeService.sonarPythonAnalysis(id, token);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBasicAuth(token, "");
//        HttpEntity request = new HttpEntity(headers);
//
//        Set<String> metrics = new HashSet<>(){{
//            add("complexity");
//            add("vulnerabilities");
//        }};
//        HashMap<String, Double> sonarMetrics = this.sonarqubeService.sonarqubeCustomMetrics(token, metrics, id.toString());
//        assertTrue(sonarMetrics.keySet().contains("complexity"));
//        assertTrue(sonarMetrics.keySet().contains("vulnerabilities"));
//        assertTrue(sonarMetrics.keySet().size() == 3);
//
//        assertFalse(sonarMetrics.get("complexity").isNaN());
//        assertFalse(sonarMetrics.get("vulnerabilities").isNaN());
//
//        metrics.add("violations");
//        sonarMetrics = this.sonarqubeService.sonarqubeCustomMetrics(token, metrics, id.toString());
//        assertTrue(sonarMetrics.keySet().contains("violations"));
//        assertTrue(sonarMetrics.keySet().size() == 4);
//
//        assertFalse(sonarMetrics.get("violations").isNaN());
//    }
//}

