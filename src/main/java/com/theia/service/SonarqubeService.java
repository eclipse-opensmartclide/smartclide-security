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



import com.google.gson.*;
import com.theia.model.SonarIssue;


import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.theia.controller.SmartCLIDEController.host;



@Service
public class SonarqubeService {
    @Autowired
    private RestTemplate restTemplate;

    //Method that checks if the project is already analyzed in Sonarqube
    public boolean projectExists(String id,String sonar_user,String sonar_password) throws ParseException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(sonar_user, sonar_password);
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange("http://" + host + ":9000/api/projects/search?projects=" + id ,
                HttpMethod.GET,
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


    //Get hotspots from Sonarqube and group them
    public HashMap<String,JsonArray> hotspotSearch(Set<String> categories,String sonar_user, String sonar_password,String projectKey) throws ParseException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(sonar_user, sonar_password);
        HttpEntity request = new HttpEntity(headers);

        String hotspotCategories = "";

        for (String ele : categories) {
            hotspotCategories = hotspotCategories +","+ ele;

        }
        ResponseEntity<String> response = restTemplate.exchange("http://" + host + ":9000/api/hotspots/search?projectKey="+projectKey+"&sonarsourceSecurity="+ hotspotCategories ,
                HttpMethod.GET,

                request,
                String.class
        );

        String json = response.getBody();

        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        final JsonArray data = jsonObject.getAsJsonArray("hotspots");
        List<String> list = new ArrayList<String>();

        HashMap<String,JsonArray> jsonHash = new HashMap<>();
        JsonArray jsonCategories = new JsonArray();
        JsonArray hotArray = new JsonArray();
        for (JsonElement element : data) {
            JsonObject jsonHot = element.getAsJsonObject();
            String securityCategory = jsonHot.get("securityCategory").toString();
            securityCategory = securityCategory.substring(1, securityCategory.length() - 1);

            jsonHot.remove("author");
            jsonHot.remove("creationDate");
            jsonHot.remove("updateDate");
            jsonHot.remove("flows");
            jsonHot.remove("status");
            jsonHot.remove("key");

            if (categories.contains(securityCategory)){
                JsonArray temp = new JsonArray();

                temp =jsonHash.get(securityCategory);
                if(temp==null){
                    temp = new JsonArray();

                }
                temp.add(jsonHot);
                jsonHash.put(securityCategory,temp);
                hotArray.add(jsonHot);


            }
        }

        return jsonHash;
    }

    public boolean taskRunning(String taskId,String sonar_user,String sonar_password) throws ParseException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(sonar_user, sonar_password);
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange("http://" + host + ":9000/api/ce/task?id=" + taskId,
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

    public void openTaskFile(String sonar_user,String sonar_password,String sha) throws IOException, ParseException, InterruptedException {
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

                while (!taskRunning(taskId, sonar_user,sonar_password)){

                    System.out.println("Waited 3s");
                    TimeUnit.SECONDS.sleep(3);

                }

            } else {
                //System.out.println("Match not found");
            }

        }
        // Print the string



    }
    public void sonarMavenAnalysis(String sha,String name, String sonar_user,String sonar_password,String type) throws InterruptedException, IOException, ParseException {
        System.out.println("mvn -f" + "/home/upload/" + sha + "/  package -Dmaven.test.skip=true");


        File dir = new File("/home/upload/" + name + "/report");

        if(type.equals("git")) {


            final Process p1 = Runtime.getRuntime().exec("mvn -f" + "/home/upload/" + sha + "/  package -Dmaven.test.skip=true");

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

            final Process p = Runtime.getRuntime().exec("mvn  -f  " + "/home/upload/" + sha + "/  sonar:sonar -Dsonar.projectKey=" + name + " -Dsonar.host.url=http://" + host + ":9000  -Dsonar.login="+sonar_user+" -Dsonar.password="+sonar_password +" -Dmaven.test.skip=true " + " -Dsonar.working.directory=/home/upload/" + sha + "/report");
            //sonar-scanner -Dsonar.projectKey=stest -Dsonar.java.binaries=. -Dsonar.exclusions=*.java -Dsonar.host.url=http://localhost:9000 -Dsonar.login=admin  -Dsonar.password=1234



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

        }
        else{
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c","cd /home/upload/" + sha +  "&&sonar-scanner " + "  -Dsonar.projectKey=" + name +" -Dsonar.java.binaries=. -Dsonar.exclusions=*.java "+  "-Dsonar.host.url=http://" + host + ":9000 " + "  -Dsonar.login="+sonar_user+" -Dsonar.password="+sonar_password +  " -Dsonar.working.directory=/home/upload/" + sha + "/report/");

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
        }

        // System.out.println("mvn  -f  " + "/home/upload/" + sha + "/  sonar:sonar -Dsonar.projectKey=" + name+ "-Dsonar.java.binaries=. -Dsonar.exclusions=*.java" + " -Dsonar.host.url=http://" + host + ":9000  -Dsonar.login="+sonar_user+" -Dsonar.password="+sonar_password +" -Dmaven.test.skip=true " + " -Dsonar.working.directory=/home/upload/" + sha + "/report");

        //final Process p = Runtime.getRuntime().exec("mvn  -f  " + "/home/upload/" + sha + "/  sonar:sonar -Dsonar.projectKey=" + name + " -Dsonar.host.url=http://" + host + ":9000 -Dsonar.login=" + token + " -Dsonar.working.directory=/home/upload/" + sha + "/report");


        openTaskFile(sonar_user,sonar_password, sha);




    }

    public static void stringToDom(String xmlSource, String filename)
            throws IOException {
        java.io.FileWriter fw = new java.io.FileWriter(filename );
        fw.write(xmlSource);
        fw.close();
    }


    public void sonarScannerAnalysis(String name, String sonar_user,String sonar_password) throws InterruptedException, IOException, ParseException {


        //ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "cd /home/upload/" + sha + "&&sonar-scanner " + "  -Dsonar.projectKey=" + name + "  -Dsonar.host.url=http://" + host + ":9000 " + "  -Dsonar.login=" + token + " -Dsonar.working.directory=/home/upload/" + sha + "/report/");
        System.out.println("cd /home/upload/" + name + "&&sonar-scanner " + "  -Dsonar.projectKey=" + name + "  -Dsonar.host.url=http://" + host + ":9000 " + "  -Dsonar.login="+sonar_user+" -Dsonar.password="+sonar_password +  " -Dsonar.working.directory=/home/upload/" + name + "/report/");
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "cd /home/upload/" + name + "&&sonar-scanner " + "  -Dsonar.projectKey=" + name + "  -Dsonar.host.url=http://" + host + ":9000 " + "  -Dsonar.login="+sonar_user+" -Dsonar.password="+sonar_password +  " -Dsonar.working.directory=/home/upload/" + name + "/report/");

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
        openTaskFile( sonar_user,sonar_password, name);

    }


    public void runCPPcheck(String name) throws IOException {
        Files.setPosixFilePermissions(Paths.get("/home/upload/" + name), PosixFilePermissions.fromString("rwxr-x---"));
        System.out.println("sudo cd /home/upload/" + name + "&&sudo mkdir build" + "&&sudo cppcheck --enable=all --inconclusive --xml --force . 2>build/report.xml");
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "cd /home/upload/" + name + "&&mkdir build" + "&&cppcheck --enable=all --inconclusive --xml --force . 2>build/report.xml");
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
    }
    //Analysis for CPP projects
    public void sonarCppAnalysis(boolean exists,String sha,String name, String sonar_user,String sonar_password) throws IOException, InterruptedException, ParseException {

        runCPPcheck(name);




        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(sonar_user, sonar_password);
        HttpEntity request = new HttpEntity(headers);

        //Create quality profile
        try {


            ResponseEntity<String> response = restTemplate.exchange("http://" + host + ":9000/api/qualityprofiles/create?language=cxx&name=Sonar Myway",
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
                ResponseEntity<String> response4 = restTemplate.exchange("http://" + host + ":9000/api/qualityprofiles/activate_rules?activation=false&languages=cxx&targetKey="+key,
                        HttpMethod.POST,
                        request,
                        String.class
                );

            }

        } catch (Exception HttpClientErrorException$BadRequest) { }






        //Create a project in Sonarqube
        ResponseEntity<String> response1 = restTemplate.exchange("http://" + host + ":9000/api/projects/create?name=" + name + "&project=" + name,
                HttpMethod.POST,
                request,
                String.class
        );



        //Assign quality profile to the project before scan

        ResponseEntity<String> response2 = restTemplate.exchange("http://" + host + ":9000/api/qualityprofiles/add_project?project=" + name + "&qualityProfile=Sonar Myway&language=cxx",
                HttpMethod.POST,
                request,
                String.class
        );

        //Run CPP Sonarqube analysis
        ProcessBuilder builder2 = new ProcessBuilder("/bin/bash", "-c", "cd /home/upload/" + sha + "&&sonar-scanner " + "  -Dsonar.projectKey=" + name + "  -Dsonar.host.url=http://" + host + ":9000 " + "  -Dsonar.login=" + sonar_user + " -Dsonar.password="+sonar_password+" -Dsonar.cxx.cppcheck.reportPaths=" + "build/report.xml" + " -Dsonar.working.directory=/home/upload/" + sha + "/report"+ " -Dsonar.cxx.file.suffixes=.cxx,.cpp,.cc,.c,.hxx,.hpp,.hh,.h"+ " -Dsonar.language=cxx"+ " -Dsonar.inclusions=**/*.cxx,**/*.cpp,**/*.cc,**/*.c,**/*.hxx,**/*.hpp,**/*.hh,**/*.h,**/*.r");


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

        openTaskFile(sonar_user,sonar_password, sha);

    }

    public List<Map<String,String>> iterateXML(String path) throws ParserConfigurationException, IOException, SAXException, JDOMException, XPathExpressionException {

        List<Map<String,String>> errors = new LinkedList<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("/home/upload/"+path+"/build/report.xml"));
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression exp = xPath.compile("//error");
            NodeList nl = (NodeList)exp.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nl.getLength(); i++) {
                Map<String,String> error = new HashMap<>();
                NamedNodeMap nodeMap = nl.item(i).getAttributes();
                for(int l=0; l<nodeMap.getLength(); l++){
                    String name= nodeMap.item(l).getNodeName();
                    String value = nodeMap.item(l).getNodeValue();
                    error.put(name,value);
                }
                Node s  = nl.item(i);
                NodeList lista =  s.getChildNodes();
                for(int j=0; j< lista.getLength(); j++) {
                    NamedNodeMap first = lista.item(j).getAttributes();
                    if(first!=null) {
                        for(int k =0; k<first.getLength(); k++) {
                            Node childAttribute = first.item(k);
                            error.put(childAttribute.getNodeName(), childAttribute.getNodeValue());
                        }
                    }
                }
                errors.add(error);


            }
            System.out.println("Found " + nl.getLength() + " results");
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException ex) {
            ex.printStackTrace();
        }
        return(errors);

    }




    public HashMap<String, Double> sonarqubeCustomVulnerabilities(String sonar_user,String sonar_password, Set<String> vulnerabilities, String id,Double linesOfCode) throws ParseException {
        HashMap<String, Double> sonarqubeVulnerabilities = new HashMap<>();

//      Harcoded list of Vulnerabilities we want to search.

        for(String vul: vulnerabilities){
            HashMap<String, Double> sonarMetrics = new HashMap<>();
            //RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(sonar_user,sonar_password);
            HttpEntity request = new HttpEntity(headers);
            ResponseEntity<String> response = restTemplate.exchange("http://" + host + ":9000/api/hotspots/search?projectKey=" + id + "&p=1&ps=500&sonarsourceSecurity=" + vul,
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

    public HashMap<String, Double> sonarqubeCustomMetrics(String sonar_user,String sonar_password, Set<String> metrics, String id) throws ParseException {
        HashMap<String, Double> sonarMetrics = new HashMap<>();
       // RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(sonar_user,sonar_password);
        HttpEntity request = new HttpEntity(headers);

        String params = "";
        for (String metric : metrics) {
            params += metric + ",";
        }
        params += "ncloc";

        ResponseEntity<String> response = restTemplate.exchange("http://" + host + ":9000/api/measures/component?component=" + id + "&metricKeys=" + params,
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

    public HashMap<String, Double> sonarqubeCustomCPP(String sonar_user,String sonar_password, Double linesOfCode, String id, List<String> CppRules) throws ParserConfigurationException, IOException, SAXException, ParseException {


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


        //RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(sonar_user, sonar_password);
        HttpEntity request = new HttpEntity(headers);


        //Request cppcheck

        ResponseEntity<String> response = restTemplate.exchange("http://" + host + ":9000/api/issues/search?componentKeys=" + id + "&tags=cppcheck",
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
    public Double linesOfCode(String sonar_user,String sonar_password, String id) throws ParseException {
        HashMap<String, Double> sonarMetrics = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(sonar_user,sonar_password);
        HttpEntity request = new HttpEntity(headers);

        // System.out.println("http://" + host + ":9000/api/measures/component?component=" + id + "&metricKeys=ncloc");

        ResponseEntity<String> response = restTemplate.exchange("http://" + host + ":9000/api/measures/component?component=" + id + "&metricKeys=ncloc",
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

        ResponseEntity<String> response = restTemplate.exchange("http://" + host + ":9000/api/issues/search?componentKeys=" + key +"&ps=500",
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
