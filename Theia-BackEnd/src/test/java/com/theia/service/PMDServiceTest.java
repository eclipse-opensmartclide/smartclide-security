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
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//class PMDServiceTest {
//
//    @Autowired
//    TheiaService theiaService;
//
//    @Autowired
//    CKService ckService;
//
//    @Autowired
//    PMDService pmdService;
//
//    @Test
//    void filterRecords() {
//
//    }
//
//    @Test
//    void generateCustomPMDValues() {
//        String url = "https://github.com/spring-projects/spring-mvc-showcase";
//        UUID id = UUID.randomUUID();
//        File dir = null;
//        try {
//            dir = this.theiaService.retrieveGithubCode(url, id);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        List<String> ckProperties = new ArrayList<>(){{
//            add("loc");
//        }};
//
//        HashMap<String, Double> ckValues = null;
//        try {
//            ckValues = this.ckService.generateCustomCKValues(dir, ckProperties);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        assertFalse(ckValues.get("loc").isNaN(), "Numerical Results are correct.");
//
//
//        List<String> pmdProperties = new ArrayList<>(){{
//            add("Assignment");
//            add("ExceptionHandling");
//            add("NullPointer");
//        }};
//
//        HashMap<String, Double> pmdValues = new HashMap<>();
//
//        try {
//            pmdValues = this.pmdService.generateCustomPMDValues(ckValues.get("loc"), dir.toString(), pmdProperties);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        assertEquals(pmdProperties.size(), pmdValues.keySet().size());
//        assertFalse(pmdValues.get("Assignment").isNaN(), "Numerical Working.");
//        assertFalse(pmdValues.get("NullPointer").isNaN(), "Numerical Working.");
//
//        pmdProperties.add("ResourceHandling");
//        try {
//            pmdValues = this.pmdService.generateCustomPMDValues(ckValues.get("loc"), dir.toString(), pmdProperties);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        assertEquals(pmdProperties.size(), pmdValues.keySet().size());
//        assertFalse(pmdValues.get("ResourceHandling").isNaN(), "Works!");
//    }
//}
