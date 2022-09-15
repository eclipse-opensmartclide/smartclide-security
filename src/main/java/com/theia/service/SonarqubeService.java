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



import com.theia.model.SonarIssue;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SonarqubeService {

    //Method that checks if the project is already analyzed in Sonarqube
    public boolean projectExists(String id,String token) throws ParseException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(token, "");
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange("http://sonarqube:9000/api/projects/search?projects=" + id ,
                HttpMethod.POST,
                request,
                String.class
        );
        String json = response.getBody();

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(json);
        Object check = object.get("components");
        if ( check.toString().equals("[]")){

            return  false;
        }
        else {
            return true;
        }

    }

    public boolean taskRunning(String taskId,String token) throws ParseException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(token, "");
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange("http://sonarqube:9000/api/ce/task?id=" + taskId,
                HttpMethod.GET,
                request,
                String.class
        );


        String json = response.getBody();


        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(json);

        object = (JSONObject) object.get("task");
        if(object.get("status").toString().equals("SUCCESS")){
            return true;
        }
        else{
            System.out.println(object.get("status").toString());

            return false;
        }
    }

    public void openTaskFile(String token,String sha) throws IOException, ParseException, InterruptedException {
        //Check if Task is complete
        File file = new File(
                "/home/upload/" + sha+"/report/report-task.txt");

        BufferedReader br
                = new BufferedReader(new FileReader(file));

        String st;
        Pattern pattern = Pattern.compile("(ceTaskId=)(.*)", Pattern.CASE_INSENSITIVE);

        String taskId = "";
        // Condition holds true till
        // there is character in a string
        while ((st = br.readLine()) != null){
           // System.out.println(st);
            Matcher matcher = pattern.matcher(st);
            boolean matchFound = matcher.find();
            if(matchFound) {
                //System.out.println("Match found");
                taskId = matcher.group(2);

                while (!taskRunning(taskId,token)){

                    System.out.println("Waited 3s");
                    TimeUnit.SECONDS.sleep(3);

                }

            } else {
                //System.out.println("Match not found");
            }

        }
        // Print the string



    }
    public void sonarMavenAnalysis(String sha,String name, String token) throws InterruptedException, IOException, ParseException {

        final Process p1 = Runtime.getRuntime().exec("mvn -f" + "/home/upload/" + sha + "/ package");

        new Thread(new Runnable() {
            public void run() {
                BufferedReader input = new BufferedReader(new InputStreamReader(p1.getInputStream()));
                String line = null;

                try {
                    while ((line = input.readLine()) != null)
                        System.out.println(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        p1.waitFor();


        File dir = new File("/home/upload/" + sha + "/report");

        final Process p = Runtime.getRuntime().exec("mvn  -f  " + "/home/upload/" + sha + "/  sonar:sonar -Dsonar.projectKey=" + name + " -Dsonar.host.url=http://sonarqube:9000 -Dsonar.login=" + token + " -Dsonar.working.directory=/home/upload/" + sha + "/report");

        new Thread(new Runnable() {
            public void run() {
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = null;

                try {
                    while ((line = input.readLine()) != null)
                        System.out.println(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        p.waitFor();

        openTaskFile(token, sha);



    }

    public void sonarScannerAnalysis(String sha,String name, String token) throws InterruptedException, IOException, ParseException {


        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "cd /home/upload/" + sha + "&&sonar-scanner " + "  -Dsonar.projectKey=" + name + "  -Dsonar.host.url=http://sonarqube:9000 " + "  -Dsonar.login=" + token + " -Dsonar.working.directory=/home/upload/" + sha + "/report/");
        builder.redirectErrorStream(true);


        // Execute the command
        try {
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            //Print the messages to the console for debugging purposes
            while (true) {
                line = r.readLine();
                System.out.println(line);
                if (line == null) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //Check if running and wait
        openTaskFile(token, sha);

    }

    //Analysis for CPP projects
    public void sonarCppAnalysis(String sha,String name, String token) throws IOException, InterruptedException, ParseException {

        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "cd /home/upload/" + sha + "&&mkdir build" + "&&cppcheck --enable=all --inconclusive --xml --force . 2>build/report.xml");
        builder.redirectErrorStream(true);


        try {
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            //Print the messages to the console for debugging purposes
            while (true) {
                line = r.readLine();
                System.out.println(line);
                if (line == null) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


        List<String> lines = new ArrayList<>();


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(token, "");
        HttpEntity request = new HttpEntity(headers);

        //Create quality profile
        try {


        ResponseEntity<String> response = restTemplate.exchange("http://sonarqube:9000/api/qualityprofiles/create?language=cxx&name=Sonar Myway",
                HttpMethod.POST,
                request,
                String.class


        );
            String json = response.getBody();


            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(json);

            JSONObject object1 = (JSONObject) object.get("errors");
            if (object1==null){
                object = (JSONObject) object.get("profile");
                String key = (String) object.get("key");

                //Add Rules to Quality Profile
                ResponseEntity<String> response4 = restTemplate.exchange("http://sonarqube:9000/api/qualityprofiles/activate_rules?activation=false&languages=cxx&targetKey="+key,
                        HttpMethod.POST,
                        request,
                        String.class
                );

            }

        } catch (Exception HttpClientErrorException$BadRequest) { }






        //Create a project in Sonarqube
        ResponseEntity<String> response1 = restTemplate.exchange("http://sonarqube:9000/api/projects/create?name=" + name + "&project=" + name,
                HttpMethod.POST,
                request,
                String.class
        );



        //Assign quality profile to the project before scan

        ResponseEntity<String> response2 = restTemplate.exchange("http://sonarqube:9000/api/qualityprofiles/add_project?project=" + name + "&qualityProfile=Sonar Myway&language=cxx",
                HttpMethod.POST,
                request,
                String.class
        );

        //Run CPP Sonarqube analysis
        ProcessBuilder builder2 = new ProcessBuilder("/bin/bash", "-c", "cd /home/upload/" + sha + "&&sonar-scanner " + "  -Dsonar.projectKey=" + name + "  -Dsonar.host.url=http://sonarqube:9000 " + "  -Dsonar.login=" + token + " -Dsonar.cxx.cppcheck.reportPaths=" + "build/report.xml" + " -Dsonar.working.directory=/home/upload/" + sha + "/report"+ " -Dsonar.cxx.file.suffixes=.cxx,.cpp,.cc,.c,.hxx,.hpp,.hh,.h"+ " -Dsonar.language=cxx"+ " -Dsonar.inclusions=**/*.cxx,**/*.cpp,**/*.cc,**/*.c,**/*.hxx,**/*.hpp,**/*.hh,**/*.h,**/*.r");


        builder2.redirectErrorStream(true);

        try {
            Process p = builder2.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        openTaskFile(token, sha);

    }



    
    public HashMap<String, Double> sonarqubeCustomVulnerabilities(String token, Set<String> vulnerabilities, String id,Double linesOfCode) throws ParseException {
        HashMap<String, Double> sonarqubeVulnerabilities = new HashMap<>();

//      Harcoded list of Vulnerabilities we want to search.

        for(String vul: vulnerabilities){
            HashMap<String, Double> sonarMetrics = new HashMap<>();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(token, "");
            HttpEntity request = new HttpEntity(headers);
           // System.out.println("http://sonarqube:9000/api/hotspots/search?projectKey=" + id + "&p=1&ps=500&sonarsourceSecurity=" + vul);
            ResponseEntity<String> response = restTemplate.exchange("http://sonarqube:9000/api/hotspots/search?projectKey=" + id + "&p=1&ps=500&sonarsourceSecurity=" + vul,
                    HttpMethod.GET,
                    request,
                    String.class
            );


            String json = response.getBody();


            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(json);

            object = (JSONObject) object.get("paging");
            long l = (long) object.get("total");
            sonarqubeVulnerabilities.put(vul, (l * 1000.0/ linesOfCode));
        }

        return sonarqubeVulnerabilities;
    }
    
    public HashMap<String, Double> sonarqubeCustomMetrics(String token, Set<String> metrics, String id) throws ParseException {
        HashMap<String, Double> sonarMetrics = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(token, "");
        //System.out.println("Token :" + token);
        HttpEntity request = new HttpEntity(headers);

        String params = "";
        for (String metric : metrics) {
            params += metric + ",";
        }
        params += "ncloc";

        ResponseEntity<String> response = restTemplate.exchange("http://sonarqube:9000/api/measures/component?component=" + id + "&metricKeys=" + params,
                HttpMethod.GET,
                request,
                String.class
        );

        String json = response.getBody();

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(json);
        object = (JSONObject) object.get("component");
        JSONArray array = (JSONArray) object.get("measures");

        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject = (JSONObject) array.get(i);
            sonarMetrics.put(jsonObject.get("metric").toString(), Double.valueOf(jsonObject.get("value").toString()));
        }

        for (String key : sonarMetrics.keySet()) {
            if (key.equals("ncloc")) {
                break;
            } else {
                sonarMetrics.put(key, sonarMetrics.get(key) / sonarMetrics.get("ncloc"));
            }
        }

        return sonarMetrics;
    }

    public HashMap<String, Double> sonarqubeCustomCPP(String token, Double linesOfCode, String id, List<String> CppRules) throws ParserConfigurationException, IOException, SAXException, ParseException {


        HashMap<String, HashSet<String>> CppRulesXML = new HashMap<>();
        HashMap<String, HashSet<String>> CppRulesXMLCorrected = new HashMap<>();
        List<String> CppXMLs = Arrays.asList("Assignment", "Dead_Code", "Exception_Handling", "IO", "Misused_Functionality", "NPE", "Overflow", "Resource_Handling", "Str_Issues");

        // Iterate CPP Rules

        HashSet<String> AllRules = new HashSet<>();

        HashMap<String, Double> sonarqubeCppIssues = new HashMap<>();

        for (String rule : CppXMLs) {


            HashSet<String> SupressRules = new HashSet<>();

            File file = new File( "/opt/resources/CppRules/" + rule + ".xml");


            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
            NodeList nList = document.getElementsByTagName("suppress");


            String element = "";

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    element = eElement.getElementsByTagName("id").item(0).getTextContent();
                    SupressRules.add(element);
                    AllRules.add(element);
                }
            }


            CppRulesXML.put(rule, SupressRules);


        }

        Iterator<String> it = AllRules.iterator();


        while (it.hasNext()) {
            String next = it.next();

            for (Map.Entry<String, HashSet<String>> set : CppRulesXML.entrySet()) {

                if (next.equals("shiftTooManyBitsSigned")) {
                    //System.out.println("hey");
                }

                HashSet setKey = new HashSet<>();
                if (set.getValue().contains(next)) {
                    continue;
                } else {
                    setKey = CppRulesXMLCorrected.get(set.getKey());
                    if (setKey == null) {

                        setKey = new HashSet<>();

                        setKey.add(next);
                        CppRulesXMLCorrected.put(set.getKey(), setKey);


                    } else {
                        setKey = CppRulesXMLCorrected.get(set.getKey());
                        setKey.add(next);
                        CppRulesXMLCorrected.put(set.getKey(), setKey);


                    }
                }

            }

        }


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(token, "");
        HttpEntity request = new HttpEntity(headers);


        //Request cppcheck

        ResponseEntity<String> response = restTemplate.exchange("http://sonarqube:9000/api/issues/search?componentKeys=" + id + "&tags=cppcheck",
                HttpMethod.POST,
                request,
                String.class
        );

        String json = response.getBody();

        JSONParser parser = new JSONParser();
        JSONObject jsonData = (JSONObject) parser.parse(json);

        String cppRule = "";

        // get locations array from the JSON Object and store it into JSONArray

        JSONArray jsonArray = (JSONArray) jsonData.get("issues");


        //iterating cpp issues

        HashMap<String, Integer> Counter = new HashMap<String, Integer>();
        for (int i = 0; i < jsonArray.size(); i++) {

            // store each object in JSONObject
            JSONObject explrObject = (JSONObject) jsonArray.get(i);

            // get field value from JSONObject using get() method
            cppRule = explrObject.get("rule").toString();
            cppRule = cppRule.substring(cppRule.indexOf(":") + 1);


            for (Map.Entry<String, HashSet<String>> set : CppRulesXMLCorrected.entrySet()) {

                if (set.getValue().contains(cppRule)) {

                    Counter.merge(set.getKey(), 1, Integer::sum);

                }

            }


        }

        //Calculate Scores
        Iterator<String> CppRulesIterator = CppRules.iterator();
        while (CppRulesIterator.hasNext()) {
            String next = CppRulesIterator.next();
            if (Counter.containsKey(next)) {
                sonarqubeCppIssues.put(next, Counter.get(next) * 1000.0 / linesOfCode);

            } else {
                sonarqubeCppIssues.put(next, 0.0);

            }
        }


        return sonarqubeCppIssues;
    }



    // Returns the lines of code of a project.
    public Double linesOfCode(String token, String id) throws ParseException {
        HashMap<String, Double> sonarMetrics = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(token, "");
        HttpEntity request = new HttpEntity(headers);

       // System.out.println("http://sonarqube:9000/api/measures/component?component=" + id + "&metricKeys=ncloc");

        ResponseEntity<String> response = restTemplate.exchange("http://sonarqube:9000/api/measures/component?component=" + id + "&metricKeys=ncloc",
                HttpMethod.GET,
                request,
                String.class
        );

        // Getting metrics for Complexity, Maintability, Reliability and Lines of Code Hardcoded.

        String json = response.getBody();
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(json);
        object = (JSONObject) object.get("component");
        JSONArray array = (JSONArray)object.get("measures");

        for (int i = 0; i < array.size(); i++) {
            JSONObject jsonObject = (JSONObject) array.get(i);
            sonarMetrics.put(jsonObject.get("metric").toString(), Double.valueOf(jsonObject.get("value").toString()));
        }

        return sonarMetrics.get("ncloc");
    }

    public List<SonarIssue> sonarqubeIssues(String key, String token) throws ParseException {
        HashMap<String, Double> sonarMetrics = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(token, "");
        HttpEntity request = new HttpEntity(headers);
        List<SonarIssue> issues = new ArrayList<>();

        ResponseEntity<String> response = restTemplate.exchange("http://sonarqube:9000/api/issues/search?componentKeys=" + key +"&ps=500",
                HttpMethod.GET,
                request,
                String.class
        );

        // Getting metrics for Complexity, Maintability, Reliability and Lines of Code Hardcoded.

        String json = response.getBody();
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(json);
        JSONArray array = (JSONArray)(object.get("issues"));
        for(int i = 0; i < array.size(); i++){
            JSONObject input = (JSONObject) array.get(i);
            if(input.keySet().contains("line")){
                SonarIssue sonarIssue = new SonarIssue(input.get("severity").toString(), input.get("line").toString(), input.get("message").toString(), input.get("component").toString().replace(key + ":", ""));
                issues.add(sonarIssue);
            }
        }
        return issues;
    }
}
