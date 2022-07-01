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
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//class TheiaServiceTest {
//
//    @Autowired
//    TheiaService theiaService;
//
//    @Test
//    void retrieveGithubCode() {
//        String url = "https://github.com/spring-projects/spring-mvc-showcase";
//        UUID id = UUID.randomUUID();
//        File dir = null;
//        try {
//            dir = this.theiaService.retrieveGithubCode(url, id);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertTrue(dir.exists(), "Directory created!");
//    }
//}