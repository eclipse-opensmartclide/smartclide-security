package com.theia.service;


import com.google.gson.Gson;
import com.theia.model.SonarIssue;
//import org.json.JSONArray;
//import org.json.JSONObject;


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

@Service
public class SonarqubeService {


    public boolean projectExists(String id,String token) throws ParseException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(token, "");
        HttpEntity request = new HttpEntity(headers);
        //System.out.println("http://localhost:9000/api/projects/create&name=" + id + "&project="+id);

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9000/api/projects/search?projects=" + id ,
                HttpMethod.POST,
                request,
                String.class
        );
        String json = response.getBody();
//

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
    public void sonarMavenAnalysis(String sha,String name, String token) throws InterruptedException, IOException {

      final Process p1 = Runtime.getRuntime().exec("mvn -f"+ "/home/upload/" + sha + "/ package");

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

         final Process p = Runtime.getRuntime().exec("mvn -f " + "/home/upload/" +sha + "/  sonar:sonar -Dsonar.projectKey=" + name + " -Dsonar.host.url=http://localhost:9000 -Dsonar.login=" + token);

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

//    public void sonarPythonAnalysis(UUID id,String name, String token) throws InterruptedException, IOException {
////        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "docker run --rm --network=host -e SONAR_HOST_URL=\"http://localhost:9000\" -e SONAR_LOGIN=" + token + " -v \"" + System.getProperty("user.dir") + "/upload/" + id + ":/usr/src/\" sonarsource/sonar-scanner-cli -Dsonar.projectKey=" + id);
////       System.out.println("docker run --rm --network=custom_bridge -e SONAR_HOST_URL=\"http://localhost:9000\" -e SONAR_LOGIN=" + token + " -v \"" + System.getProperty("user.dir") + "/upload/" + id + ":/usr/src/\" sonarsource/sonar-scanner-cli -Dsonar.projectKey=" + id);
////        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "docker run --rm --network=host -e SONAR_HOST_URL=\"http://localhost:9000\" -e SONAR_LOGIN=" + token + " -v \"" + System.getProperty("user.dir") + "/upload/" + id + ":/usr/src/\" sonarsource/sonar-scanner-cli -Dsonar.projectKey=" + id);
////        System.out.println("docker run --rm --network=custom_bridge -e SONAR_HOST_URL=\"http://localhost:9000\" -e SONAR_LOGIN=" + token + " -v \"" + "/home/upload/" + id + ":/usr/src/\" sonarsource/sonar-scanner-cli -Dsonar.projectKey=" + id);
//        //System.out.println("sonar-scanner " +   "  -Dsonar.projectKey=scanner"+name +"  -Dsonar.host.url=http://localhost:9000 " + "  -Dsonar.login="+token);
//        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "cd /home/upload/"+id.toString()+ "&&sonar-scanner " +   "  -Dsonar.projectKey="+name +"  -Dsonar.host.url=http://localhost:9000 " + "  -Dsonar.login="+token);
//        builder.redirectErrorStream(true);
//
//
//
//       // Execute the command
//        try{
//            Process p = builder.start();
//            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line;
//            //Print the messages to the console for debugging purposes
//            while (true) {
//                line = r.readLine();
//                System.out.println(line);
//                if (line == null) { break; }
//            }
//        }catch(IOException e){
//            System.out.println(e.getMessage());
//        }
//
//        final Process p1 = Runtime.getRuntime().exec("\n" +" cd /home/upload/"+id.toString()+
//                "&&sonar-scanner \\n" +
//                "  -Dsonar.projectKey=scanner"+id.toString()+" \\n" +
//                "  -Dsonar.sources=. \\n" +
//                "  -Dsonar.host.url=http://localhost:9000 \\n" +
//                "  -Dsonar.login="+token);



    //}
    public void sonarScannerAnalysis(String sha,String name, String token) throws InterruptedException, IOException {

        //System.out.println("sonar-scanner " +   "  -Dsonar.projectKey="+id.toString() +"  -Dsonar.host.url=http://localhost:9000 " + "  -Dsonar.login="+token);
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "cd /home/upload/"+sha+ "&&sonar-scanner " +   "  -Dsonar.projectKey="+name+"  -Dsonar.host.url=http://localhost:9000 " + "  -Dsonar.login="+token);
        builder.redirectErrorStream(true);



        // Execute the command
        try{
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            //Print the messages to the console for debugging purposes
            while (true) {
                line = r.readLine();
                System.out.println(line);
                if (line == null) { break; }
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }

    }

    public void sonarCppAnalysis(String sha,String name, String token) throws IOException, InterruptedException {

        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "cd /home/upload/"+sha+"&&mkdir build"+"&&cppcheck --enable=all --inconclusive --xml --force . 2>build/report.xml");
        builder.redirectErrorStream(true);




        try{
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            //Print the messages to the console for debugging purposes
            while (true) {
                line = r.readLine();
                System.out.println(line);
                if (line == null) { break; }
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        List<String> lines = new ArrayList<>();
        TimeUnit.SECONDS.sleep(10);


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(token, "");
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response1 = restTemplate.exchange("http://localhost:9000/api/projects/create?name=" + name + "&project="+name,
                HttpMethod.POST,
                request,
                String.class
        );



        ResponseEntity<String> response2 = restTemplate.exchange("http://localhost:9000/api/qualityprofiles/add_project?project=" + name + "&qualityProfile=Sonar myway&language=cxx",
                HttpMethod.POST,
                request,
                String.class
        );



             ProcessBuilder builder2 = new ProcessBuilder("/bin/bash", "-c", "cd /home/upload/"+sha+ "&&sonar-scanner " +   "  -Dsonar.projectKey="+name +"  -Dsonar.host.url=http://localhost:9000 " + "  -Dsonar.login="+token + " -Dsonar.cxx.cppcheck.reportPaths=" +  "build/report.xml");




        TimeUnit.SECONDS.sleep(10);


        builder2.redirectErrorStream(true);

        try {
            Process p = builder2.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            while(true){
                line = r.readLine();
                if(line == null){break;}
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
           // System.out.println("http://localhost:9000/api/hotspots/search?projectKey=" + id + "&p=1&ps=500&sonarsourceSecurity=" + vul);
            ResponseEntity<String> response = restTemplate.exchange("http://localhost:9000/api/hotspots/search?projectKey=" + id + "&p=1&ps=500&sonarsourceSecurity=" + vul,
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
        for(String metric: metrics){
            params += metric + ",";
        }
        params += "ncloc";
       // System.out.println("http://localhost:9000/api/measures/component?component=" + id + "&metricKeys=" + params);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9000/api/measures/component?component=" + id + "&metricKeys=" + params,
                HttpMethod.GET,
                request,
                String.class
        );
       // System.out.println(response);
        // Getting metrics for Complexity, Maintability, Reliability and Lines of Code Hardcoded.
        String json = response.getBody();
//        JSONObject object = new JSONObject(json);

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(json);
        object = (JSONObject) object.get("component");
       // JSONArray slideContent = (JSONArray) object.get("presentationSlides");
        JSONArray array =  (JSONArray)object.get("measures");

        for(int i = 0; i < array.size(); i++){
            JSONObject jsonObject = (JSONObject) array.get(i);
            sonarMetrics.put(jsonObject.get("metric").toString(), Double.valueOf(jsonObject.get("value").toString()));
        }

        for(String key: sonarMetrics.keySet()){
            if(key.equals("ncloc")){
                break;
            }else{
                sonarMetrics.put(key, sonarMetrics.get(key) / sonarMetrics.get("ncloc"));
            }
        }

        return sonarMetrics;
    }

    public HashMap<String, Double> sonarqubeCustomCPP(String token, Double linesOfCode, String id, List<String> CppRules) throws ParserConfigurationException, IOException, SAXException, ParseException {





        HashMap<String, HashSet<String>> CppRulesXML = new HashMap<>();
        HashMap<String, HashSet<String>> CppRulesXMLCorrected = new HashMap<>();
        List<String> CppXMLs = Arrays.asList("Assignment","Dead_Code","Exception_Handling","IO","Misused_Functionality","NPE","Overflow","Resource_Handling","Str_Issues");


        // Iterate CPP Rules

        HashSet<String> AllRules = new HashSet<>();

        HashMap<String, Double> sonarqubeCppIssues = new HashMap<>();

        for (String rule : CppXMLs) {


            HashSet<String> SupressRules = new HashSet<>();

            File file = new File(Path.of("").toAbsolutePath().toString() + "/CppRules/" + rule+".xml");


            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
            //System.out.println("Root Element :" + document.getDocumentElement().getNodeName());
            NodeList nList = document.getElementsByTagName("suppress");
            //System.out.println("----------------------------");
            //System.out.println("-------------"+rule+"---------------");

            String element ="";

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                //System.out.println("\nCurrent Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    element = eElement.getElementsByTagName("id").item(0).getTextContent();
                    //System.out.println("element : " + element);
                    SupressRules.add(element);
                    AllRules.add(element);
                }
            }


            CppRulesXML.put(rule,SupressRules);


        }

        Iterator<String> it = AllRules.iterator();


        while (it.hasNext()) {
            String next= it.next();

            for (Map.Entry<String, HashSet<String>> set : CppRulesXML.entrySet()) {
                //System.out.println("Value    :"+next);
                //System.out.println("next:"+next);

                if (next.equals("shiftTooManyBitsSigned")){
                    //System.out.println("hey");
                }

                HashSet setKey = new HashSet<>();
                if (set.getValue().contains(next)) {
                    continue;
                } else {
                    setKey = CppRulesXMLCorrected.get(set.getKey());
                    if(setKey==null) {

                        setKey = new HashSet<>();

                        setKey.add(next);
                        CppRulesXMLCorrected.put(set.getKey(),setKey);


                    }
                    else{
                        setKey = CppRulesXMLCorrected.get(set.getKey());
                        //System.out.println("setKey    "+  setKey);
                        setKey.add(next);
                        CppRulesXMLCorrected.put(set.getKey(),setKey);


                 }}

            }
            // Print HashSet values
            //System.out.println("another one : " +next );
        }


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(token, "");
        //System.out.println("Token :" + token);
        HttpEntity request = new HttpEntity(headers);



        //Request cppcheck

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9000/api/issues/search?componentKeys=" + id + "&tags=cppcheck",
                HttpMethod.POST,
                request,
                String.class
        );

        String json = response.getBody();

//        JSONObject jsonData = new JSONObject(json);

        JSONParser parser = new JSONParser();
        JSONObject jsonData = (JSONObject) parser.parse(json);

        String cppRule = "";

        // get locations array from the JSON Object and store it into JSONArray

        JSONArray jsonArray = (JSONArray) jsonData.get("issues");
        // Iterate jsonArray using for loop

//        JSONObject object = new JSONObject(json);
//
//        object = (JSONObject) object.get("paging");


        //iterating cpp issues

        HashMap<String,Integer> Counter = new HashMap<String,Integer>();
        for (int i = 0; i < jsonArray.size(); i++) {

            // store each object in JSONObject
            JSONObject explrObject = (JSONObject) jsonArray.get(i);

            // get field value from JSONObject using get() method
            cppRule = explrObject.get("rule").toString();
            cppRule = cppRule.substring(cppRule.indexOf(":") + 1);


           // System.out.println("CppRule :" + cppRule);


            for (Map.Entry<String, HashSet<String>> set : CppRulesXMLCorrected.entrySet()) {

                if (set.getValue().contains(cppRule)) {
                    //System.out.println("Set :   " +set.getKey()+ "\n Value:   "   + set.getValue());

                    Counter.merge(set.getKey(), 1, Integer::sum);

                }
                //System.out.println("counter:  " + counter);

            }


        }


        Iterator<String> CppRulesIterator = CppRules.iterator();
        while (CppRulesIterator.hasNext()) {
            String next = CppRulesIterator.next();
            if(Counter.containsKey(next)){
                sonarqubeCppIssues.put(next,Counter.get(next)* 1000.0/ linesOfCode);

            }
            else {
                sonarqubeCppIssues.put(next,0.0);

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

       // System.out.println("http://localhost:9000/api/measures/component?component=" + id + "&metricKeys=ncloc");

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9000/api/measures/component?component=" + id + "&metricKeys=ncloc",
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
       // System.out.println("http://localhost:9000/api/issues/search?componentKeys=" + key +"&ps=500");

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9000/api/issues/search?componentKeys=" + key +"&ps=500",
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
