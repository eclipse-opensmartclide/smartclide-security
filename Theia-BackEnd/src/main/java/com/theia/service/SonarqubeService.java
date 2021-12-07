package com.theia.service;


import com.theia.model.SonarIssue;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SonarqubeService {

    public void sonarMavenAnalysis(UUID id, String token) throws InterruptedException, IOException {

//       final Process p1 = Runtime.getRuntime().exec(new String[]{"mvn", "package", "-DskipTests"}, null, new File(System.getProperty("user.dir") + "/upload/" + id));
       final Process p1 = Runtime.getRuntime().exec("mvn -f "+ System.getProperty("HOME") + "/upload/" + id.toString() + "/ package");

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

        final Process p = Runtime.getRuntime().exec("mvn -f " + System.getProperty("HOME") + "/upload/" + id.toString() + "/  sonar:sonar -Dsonar.projectKey=" + id.toString() + " -Dsonar.host.url=http://localhost:9000 -Dsonar.login=" + token);
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

    public void sonarPythonAnalysis(UUID id, String token) throws InterruptedException, IOException {
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "docker run --rm --network=host -e SONAR_HOST_URL=\"http://localhost:9000\" -e SONAR_LOGIN=" + token + " -v \"" + System.getProperty("user.dir") + "/upload/" + id + ":/usr/src/\" sonarsource/sonar-scanner-cli -Dsonar.projectKey=" + id);
        builder.redirectErrorStream(true);

        //Execute the command
        try{
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            //Print the messages to the console for debugging purposes
            while (true) {
                line = r.readLine();
                if (line == null) { break; }
            }
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    public void sonarCppAnalysis(UUID id, String token) throws IOException, InterruptedException {
        String output_dir = System.getProperty("user.dir") + "/upload/" + id;
        System.out.println(output_dir);
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "cppcheck --xml-version=2 \"" + output_dir  + "\" 2>" + output_dir  + "/report.xml");
        Process process = builder.start();

        List<String> lines = new ArrayList<>();
        TimeUnit.SECONDS.sleep(10);

        try {
            File file = new File(output_dir + "/report.xml");
            Scanner reader = new Scanner(file);
            while(reader.hasNextLine()){
                String line = reader.nextLine();
                lines.add(line);
            }
        }catch (FileNotFoundException e){
            System.out.println("An error occured.");
            System.out.println(e);
        }
        System.out.println(lines);
        if(!lines.contains("</errors>")){
            FileWriter fileWriter = new FileWriter(output_dir + "/report.xml", true);
            fileWriter.write("</errors>\n");
            fileWriter.write("</results>\n");
            fileWriter.close();
        }

        builder = new ProcessBuilder("/bin/bash", "-c", "docker run --rm --network=host -e SONAR_HOST_URL=\"http://localhost:9000\" -e SONAR_LOGIN=" + token + " -v \"" + System.getProperty("user.dir") + "/upload/" + id + ":/usr/src/\" sonarsource/sonar-scanner-cli -Dsonar.projectKey=" + id + " -Dsonar.cxx.file.suffixes=.cpp,.cxx,.cc,.c,.hxx,.hpp,.hh,.h -Dsonar.cxx.cppcheck.reportPaths=" + output_dir + "/report.xml");
        builder.redirectErrorStream(true);

        try {
            Process p = builder.start();
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


    
    public HashMap<String, Double> sonarqubeCustomVulnerabilities(String token, Set<String> vulnerabilities, String id){
        HashMap<String, Double> sonarqubeVulnerabilities = new HashMap<>();

//      Harcoded list of Vulnerabilities we want to search.

        for(String vul: vulnerabilities){
            HashMap<String, Double> sonarMetrics = new HashMap<>();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(token, "");
            HttpEntity request = new HttpEntity(headers);

            ResponseEntity<String> response = restTemplate.exchange("http://localhost:9000/api/hotspots/search?projectKey=" + id + "&p=1&ps=500&sonarsourceSecurity=" + vul,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            String json = response.getBody();
            JSONObject object = new JSONObject(json);
            object = (JSONObject) object.get("paging");
            sonarqubeVulnerabilities.put(vul, ((Integer)object.get("total") * 1000.0/ linesOfCode(token, id)));
        }

        return sonarqubeVulnerabilities;
    }
    
    public HashMap<String, Double> sonarqubeCustomMetrics(String token, Set<String> metrics, String id){
        HashMap<String, Double> sonarMetrics = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(token, "");
        HttpEntity request = new HttpEntity(headers);

        String params = "";
        for(String metric: metrics){
            params += metric + ",";
        }
        params += "ncloc";
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9000/api/measures/component?component=" + id + "&metricKeys=" + params,
                HttpMethod.GET,
                request,
                String.class
        );
        System.out.println(response);
        // Getting metrics for Complexity, Maintability, Reliability and Lines of Code Hardcoded.
        String json = response.getBody();
        JSONObject object = new JSONObject(json);
        object = (JSONObject) object.get("component");
        JSONArray array = new JSONArray(object.get("measures").toString());

        for(int i = 0; i < array.length(); i++){
            JSONObject jsonObject = array.getJSONObject(i);
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

    // Returns the lines of code of a project.
    public Double linesOfCode(String token, String id) {
        HashMap<String, Double> sonarMetrics = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(token, "");
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9000/api/measures/component?component=" + id + "&metricKeys=ncloc",
                HttpMethod.GET,
                request,
                String.class
        );

        // Getting metrics for Complexity, Maintability, Reliability and Lines of Code Hardcoded.
        String json = response.getBody();
        JSONObject object = new JSONObject(json);
        object = (JSONObject) object.get("component");
        JSONArray array = new JSONArray(object.get("measures").toString());

        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            sonarMetrics.put(jsonObject.get("metric").toString(), Double.valueOf(jsonObject.get("value").toString()));
        }

        return sonarMetrics.get("ncloc");
    }

    public List<SonarIssue> sonarqubeIssues(String key, String token){
        HashMap<String, Double> sonarMetrics = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(token, "");
        HttpEntity request = new HttpEntity(headers);
        List<SonarIssue> issues = new ArrayList<>();

        ResponseEntity<String> response = restTemplate.exchange("http://localhost:9000/api/issues/search?componentKeys=" + key +"&ps=500",
                HttpMethod.GET,
                request,
                String.class
        );

        // Getting metrics for Complexity, Maintability, Reliability and Lines of Code Hardcoded.
        String json = response.getBody();
        JSONObject object = new JSONObject(json);
        JSONArray array = new JSONArray(object.get("issues").toString());
        for(int i = 0; i < array.length(); i++){
            JSONObject input = (JSONObject) array.get(i);
            if(input.keySet().contains("line")){
                SonarIssue sonarIssue = new SonarIssue(input.get("severity").toString(), input.get("line").toString(), input.get("message").toString(), input.get("component").toString().replace(key + ":", ""));
                issues.add(sonarIssue);
            }
        }
        return issues;
    }
}
