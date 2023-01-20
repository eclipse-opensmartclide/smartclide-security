package com.theia.service;

import com.github.mauricioaniche.ck.CK;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

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
        Double result = 2.0;
        try {
            Double measures = MeasureService.measurePMDProperties(metrics, 1000d);
            Assert.assertEquals(result,measures);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Test
    void measureSecurityIndex() {
        Map<String, Double> characteristicScores = new HashMap<>();
        HashMap<String, Double> securityIndex = new HashMap<>();
        HashMap<String, Double> securityIndexReturn = new HashMap<>();

        securityIndex.put("Security_Index",0.8);
        characteristicScores.put("Availability",0.7);
        characteristicScores.put("Confidentiality",0.8);
        characteristicScores.put("Confidentiality",0.9);
        securityIndexReturn = MeasureService.measureSecurityIndex( securityIndex);



        Assert.assertEquals(securityIndex, securityIndexReturn);






    }

    @Test
    void measureCustomPropertiesScore() {


        String propertiesJson = "{\"Sonarqube\":{\"Assignment\":[0.0,0.013234192551328933,1.5479876160990713],\"Exception_Handling\":[0.0,0.024419175132769335,2.2172949002217295],\"IO\":[0.0,0.0015070136414874827,0.1989258006763477],\"Misused_Functionality\":[0.0,0.024207864640426638,3.0959752321981426],\"NPE\":[0.0,0.024207864640426638,3.0959752321981426],\"Overflow\":[0.0,0.024207864640426638,3.0959752321981426],\"Dead_Code\":[0.0,0.024207864640426638,3.0959752321981426]},\"Characteristics\":{\"Confidentiality\":[0.05,0.1,0.05,0.15,0.2,0.15,0.3],\"Integrity\":[0.1,0.05,0.3,0.1,0.1,0.2,0.15],\"Availability\":[0.2,0.15,0.1,0.05,0.3,0.1,0.1]}}";
        String analysisJson ="{\"Sonarqube\":{\"Assignment\":0.0,\"Exception_Handling\":0.0,\"NPE\":0.0,\"IO\":0.0,\"Misused_Functionality\":36.36363636363637,\"Dead_Code\":0.0,\"Overflow\":0.0}}";
        String returnCustom = "{\"Assignment\":1.0,\"Exception_Handling\":1.0,\"NPE\":1.0,\"IO\":1.0,\"Misused_Functionality\":0.0,\"Dead_Code\":1.0,\"Overflow\":1.0}";
        Type listType = new TypeToken<LinkedHashMap<String, LinkedHashMap<String, List<Double>>>>() {
        }.getType();


        LinkedHashMap<String, LinkedHashMap<String, List<Double>>> sonarProperties = new Gson().fromJson(propertiesJson, listType);

        Type listType1 = new TypeToken<HashMap<String, HashMap<String, Double>>>() {
        }.getType();

        HashMap<String, HashMap<String, Double>> sonarAnalysis = new Gson().fromJson(analysisJson, listType1);

        Type listType2 = new TypeToken<HashMap<String, Double>>() {
        }.getType();

        HashMap<String, Double> sonarReturn = new Gson().fromJson(returnCustom, listType2);


        HashMap<String, Double> measureCustomScore = MeasureService.measureCustomPropertiesScore(sonarAnalysis,sonarProperties);



        Assert.assertEquals(measureCustomScore, sonarReturn);


    }

    @Test
    void measureCustomCharacteristicsScore() {
    }
}