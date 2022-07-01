/*******************************************************************************
 * Copyright (C) 2021-2022 CERTH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
//package com.theia.model;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//class SonarIssueTest {
//
//
//    @Test
//    void getSeverity() {
//        SonarIssue sonarIssue = new SonarIssue("CRITICAL", "89", "test", "path");
//
//        assertTrue(sonarIssue.getSeverity().equals("CRITICAL"));
//    }
//
//    @Test
//    void setSeverity() {
//        SonarIssue sonarIssue = new SonarIssue("CRITICAL", "89", "test", "path");
//        sonarIssue.setSeverity("INFO");
//        assertTrue(sonarIssue.getSeverity().equals("INFO"));
//    }
//
//    @Test
//    void getLine() {
//        SonarIssue sonarIssue = new SonarIssue("CRITICAL", "89", "test", "path");
//        assertTrue(sonarIssue.getLine().equals("89"));
//    }
//
//    @Test
//    void setLine() {
//        SonarIssue sonarIssue = new SonarIssue("CRITICAL", "89", "test", "path");
//        sonarIssue.setLine("90");
//        assertTrue(sonarIssue.getLine().equals("90"));
//    }
//
//    @Test
//    void getMessage() {
//        SonarIssue sonarIssue = new SonarIssue("CRITICAL", "89", "test", "path");
//        assertTrue(sonarIssue.getMessage().equals("test"));
//    }
//
//    @Test
//    void setMessage() {
//        SonarIssue sonarIssue = new SonarIssue("CRITICAL", "89", "test", "path");
//        sonarIssue.setMessage("retest");
//        assertTrue(sonarIssue.getMessage().equals("retest"));
//    }
//
//    @Test
//    void getPath() {
//        SonarIssue sonarIssue = new SonarIssue("CRITICAL", "89", "test", "path");
//        assertTrue(sonarIssue.getPath().equals("path"));
//    }
//
//    @Test
//    void setPath() {
//        SonarIssue sonarIssue = new SonarIssue("CRITICAL", "89", "test", "path");
//        sonarIssue.setPath("repath");
//        assertTrue(sonarIssue.getPath().equals("repath"));
//    }
//}
