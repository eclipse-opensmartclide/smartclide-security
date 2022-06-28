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

import com.github.mauricioaniche.ck.CK;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MeasureServiceTest {

    @Autowired
    CKService ckService;

    @Autowired
    MeasureService measureService;

    @Test
    void measureCKProperties() {
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

        HashMap<String, Double> measure = MeasureService.measureCKProperties(metrics);
        assertFalse(measure.isEmpty());
        assertTrue(measure.keySet().contains("wmc"));
        assertFalse(measure.get("wmc").isNaN());

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

        measure = MeasureService.measureCKProperties(metrics);
        assertFalse(measure.isEmpty());
        assertTrue(measure.keySet().contains("cbo"));
        assertFalse(measure.get("cbo").isNaN());

    }

    @Test
    void measurePMDProperties() {
        List<String> metrics = new ArrayList<>(){{
            add("Assignment");
            add("Logging");
        }};

        try {
            Double measures = MeasureService.measurePMDProperties(metrics, 1000d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void measureSecurityIndex() {
    }

    @Test
    void measureCustomPropertiesScore() {
    }

    @Test
    void measureCustomCharacteristicsScore() {
    }
}
