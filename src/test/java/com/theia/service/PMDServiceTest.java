package com.theia.service;

import com.theia.model.PMDvalues;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PMDServiceTest {

    @Autowired
    TheiaService theiaService;

    @Autowired
    CKService ckService;

    @Autowired
    PMDService pmdService;

    @Test
    void filterRecords() {

    }

    //@Test
//    void generateCustomPMDValues() throws InterruptedException {
//
//        String url = "https://github.com/spring-projects/spring-mvc-showcase.git";
//        UUID id = UUID.randomUUID();
//        Pattern pattern = Pattern.compile("(\\/)(?!.*\\1)(.*)(.git)");
//        Matcher matcher = pattern.matcher(url);
//        String name = "";
//        if (matcher.find()) {
//            name = matcher.group(2);
//        }
//
//
//        File dir = new File("/home/upload/" + name);
//
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
//        PMDvalues pmdValues2;
//        try {
//             pmdValues2 = this.pmdService.generateCustomPMDValues(false,ckValues.get("loc"), dir.toString(), pmdProperties);
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
//            pmdValues2 = this.pmdService.generateCustomPMDValues(true,ckValues.get("loc"), dir.toString(), pmdProperties);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        assertEquals(pmdProperties.size(), pmdValues.keySet().size());
//        assertFalse(pmdValues.get("ResourceHandling").isNaN(), "Works!");
//    }
}