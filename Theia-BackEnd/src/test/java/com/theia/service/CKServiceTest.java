/*******************************************************************************
 * Copyright (C) 2021-2022 CERTH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.theia.service;


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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CKServiceTest {

    @Autowired
    TheiaService theiaService;

    @Autowired
    CKService ckService;


    @Test
    void generateCustomCKValues()  {
        String url = "https://github.com/spring-projects/spring-mvc-showcase";
        UUID id = UUID.randomUUID();
        File dir = null;
        try {
            dir = this.theiaService.retrieveGithubCode(url, id);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> ckProperties = new ArrayList<>(){{
            add("loc");
            add("wmc");
        }};

        HashMap<String, Double> ckValues = null;
        try {
            ckValues = this.ckService.generateCustomCKValues(dir, ckProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(2, ckValues.keySet().size());
        assertFalse(ckValues.get("wmc").isNaN(), "Numercal result successfull.");
        assertFalse(ckValues.get("loc").isNaN(), "Numercal result successfull.");


        ckProperties.add("cbo");
        try {
            ckValues = this.ckService.generateCustomCKValues(dir, ckProperties);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(3, ckValues.keySet().size());
        assertFalse(ckValues.get("cbo").isNaN(), "Numerical results successfull.");
    }

    @Test
    void filterCKRecords(){
        List<String> ckProperties = new ArrayList<>(){{
            add("loc");
            add("wmc");
        }};

        List<List<String>> records = new ArrayList<>(){{
           add(new ArrayList<>(){{
               add("cbo");
               add("wmc");
               add("dit");
               add("lcom");
               add("wmc");
               add("loc");
           }});

           add(new ArrayList<>(){{
              add("5");
              add("3");
              add("4");
              add("5");
              add("2");
              add("15");
           }});

           add(new ArrayList<>(){{
               add("3");
               add("1");
               add("4");
               add("10");
               add("1");
               add("35");
           }});
            add(new ArrayList<>(){{
                add("3");
                add("1");
                add("4");
                add("10");
                add("1");
                add("9");
            }});
            add(new ArrayList<>(){{
                add("3");
                add("1");
                add("4");
                add("10");
                add("1");
                add("12");
            }});
        }};

        List<List<String>> metrics = CKService.filterCKRecords(records, ckProperties);

        assertEquals(records.size(), metrics.size());
        assertTrue(metrics.get(0).contains("wmc"), "Works!");

        ckProperties.add("cbo");
        records.add(new ArrayList<>(){{
            add("3");
            add("1");
            add("4");
            add("10");
            add("1");
            add("12");
        }});
        metrics = CKService.filterCKRecords(records, ckProperties);

        assertEquals(records.size(), metrics.size());
        assertTrue(metrics.get(0).contains("cbo"));
    }
}
