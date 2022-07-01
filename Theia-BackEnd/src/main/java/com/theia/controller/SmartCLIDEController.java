/*******************************************************************************
 * Copyright (C) 2021-2022 CERTH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.theia.controller;


import com.theia.service.*;
import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/smartclide")
@CrossOrigin("*")
public class SmartCLIDEController {

    @Autowired
    private TheiaService theiaService;

    @Autowired
    private CKService ckService;

    @Autowired
    private PMDService pmdService;

    @Autowired
    private FileUtilService fileUtilService;

    @Autowired
    private SonarqubeService sonarqubeService;

    @Autowired
    private VPService vpService;

//Environmental variable, token to access Sonarqube.
    private static String token = "d3b6eaec7d63025f148d9d3345dc8be68c428f82";

    //  Endpoint, providing Github URL, downloading and analyzing the project with default values of the CK and PMD tools.
    @PostMapping("/analyze")

      public ResponseEntity<HashMap<String, HashMap<String, Double>>> githubRetrieve(@RequestParam("url") String url, @RequestParam("language")String language, @RequestBody LinkedHashMap<String, LinkedHashMap<String, List<Double>>> sonarProperties) throws IOException, InterruptedException, ParserConfigurationException, SAXException, ParseException {

        UUID id = UUID.randomUUID();

        if(language.equals("Maven")){


          sonarProperties.get("CK").put("loc", new ArrayList<>());
          HashMap<String, HashMap<String, Double>> sonarAnalysis = new HashMap<>();
          HashMap<String, Double> sonarPropertyScores = new HashMap<>();
          Set<String> sonarMetrics = Set.copyOf(sonarProperties.get("metricKeys").keySet());
//
//      Downloading the Github Project.


//            if(dir.exists()){
//                FileUtils.deleteDirectory(dir);
//            }

            String branchSHA =  this.theiaService.retrieveGithubCode(url, id);
            Pattern pattern = Pattern.compile("(\\/)(?!.*\\1)(.*)(.git)");
            Matcher matcher = pattern.matcher(url);
            String name= "";

            // Throws exception on failure
//            Files.createDirectory(Paths.get("/home/upload/"+branchSHA),
//                    PosixFilePermissions.asFileAttribute(
//                            PosixFilePermissions.fromString("rwxr-x---")
//                    ));
            File dir = new File("/home/upload/" + branchSHA);

            Files.setPosixFilePermissions(Paths.get("/home/upload/"+branchSHA), PosixFilePermissions.fromString("rwxr-x---"));


            if (matcher.find())
            {
                name =matcher.group(2);
            }


          LinkedHashMap<String, HashMap<String, Double>> analysis = new LinkedHashMap<>();

//      Analyzing project with CK tool, alongside with the default values chosed for the CK tool

          ArrayList <String> stone = new ArrayList<>(sonarProperties.get("CK").keySet());

          HashMap<String, Double> ckValues = this.ckService.generateCustomCKValues(dir, new ArrayList<>(sonarProperties.get("CK").keySet()));
          analysis.put("CK", ckValues);

//      Analyzing with PMD tool, alongside with default values chosed for the PMD tool.

          HashMap<String, Double> pmdValues = this.pmdService.generateCustomPMDValues(ckValues.get("loc"), dir.toString(), new ArrayList<>(sonarProperties.get("PMD").keySet()));
          analysis.put("PMD", pmdValues);

          //SONARQUBE

            if(!sonarqubeService.projectExists(name,token)) {
                this.sonarqubeService.sonarMavenAnalysis(branchSHA,name, token);
                //TimeUnit.SECONDS.sleep(0);

            }

//      Analyze Sonarqube Metrics Hardcoded.
            Double linesOfCode = this.sonarqubeService.linesOfCode(token,name);


//      Analyze
//      Sonarqube Vulnerabilities Hardcoded.

          sonarAnalysis.put("Sonarqube",this.sonarqubeService.sonarqubeCustomVulnerabilities(token, sonarProperties.get("Sonarqube").keySet(), name,linesOfCode));


          analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));
          HashMap<String, Double> propertyScores = MeasureService.measureCustomPropertiesScore(analysis, sonarProperties);
          analysis.put("metrics", this.sonarqubeService.sonarqubeCustomMetrics(token, sonarMetrics,name));

          propertyScores.put("weak_cryptography", propertyScores.get("weak-cryptography"));
          propertyScores.remove("weak-cryptography");
          propertyScores.put("sql_injection", propertyScores.get("sql-injection"));
          propertyScores.remove("sql-injection");
          propertyScores.put("insecure_conf", propertyScores.get("insecure-conf"));
          propertyScores.remove("insecure-conf");
          analysis.put("Property_Scores", propertyScores);



//      Calculating characteristic res for the characteristics the user chose.
          HashMap<String, Double> characteristicScores = MeasureService.measureCustomCharacteristicsScore(propertyScores, sonarProperties);
          analysis.put("Characteristic_Scores", characteristicScores);

////      Calculating security index.
          HashMap<String, Double> securityIndex = MeasureService.measureSecurityIndex(characteristicScores);
          analysis.put("Security_index", securityIndex);

          analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));

////      Include Vulnerability Prediction Model.
//

//////      Return the analysis map.
            return new ResponseEntity<>(analysis, HttpStatus.CREATED);
      }
        else if(language.equals("Python")){

            HashMap<String, HashMap<String, Double>> sonarAnalysis = new HashMap<>();
            HashMap<String, Double> sonarPropertyScores = new HashMap<>();
            Set<String> sonarMetrics = Set.copyOf(sonarProperties.get("metricKeys").keySet());



//      Downloading the Github Project.
            File dir = new File("/home/upload/" + id.toString());
            if(dir.exists()){
                FileUtils.deleteDirectory(dir);
            }

            String branchSHA =  this.theiaService.retrieveGithubCode(url, id);
            Pattern pattern = Pattern.compile("(\\/)(?!.*\\1)(.*)(.git)");
            Matcher matcher = pattern.matcher(url);
            String name= "";

            if (matcher.find())
            {
               name =matcher.group(2);
            }



            if(!sonarqubeService.projectExists(name,token)) {
                this.sonarqubeService.sonarScannerAnalysis(branchSHA,name, token);
                //TimeUnit.SECONDS.sleep(30);

            }
                LinkedHashMap<String, HashMap<String, Double>> analysis = new LinkedHashMap<>();

           // UUID id2 = UUID.fromString( "1fd8c91b-8dc5-4bb0-927a-f51ae9786658");

            Double linesOfCode = this.sonarqubeService.linesOfCode(token,name);


            sonarAnalysis.put("metrics", this.sonarqubeService.sonarqubeCustomMetrics(token, sonarMetrics, name));

            sonarAnalysis.put("Sonarqube",this.sonarqubeService.sonarqubeCustomVulnerabilities(token, sonarProperties.get("Sonarqube").keySet(),name,linesOfCode));

          analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));
          HashMap<String, Double> propertyScores = MeasureService.measureCustomPropertiesScore(analysis, sonarProperties);
          analysis.put("Property_Scores", propertyScores);

//      Calculating characteristic scores for the characteristics the user chose.
          HashMap<String, Double> characteristicScores = MeasureService.measureCustomCharacteristicsScore(propertyScores, sonarProperties);
          analysis.put("Characteristic_Scores", characteristicScores);

//      Calculating security index.
          HashMap<String, Double> securityIndex = MeasureService.measureSecurityIndex(characteristicScores);
          analysis.put("Security_index", securityIndex);

          analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));
          analysis.put("ProjectKey", new HashMap<String, Double>() {{
              put(id.toString(), 0d);
          }});

//      Return the analysis map.
          return new ResponseEntity<>(analysis, HttpStatus.CREATED);
      }
        else if (language.equals("Javascript")) {


          HashMap<String, HashMap<String, Double>> sonarAnalysis = new HashMap<>();
          HashMap<String, Double> sonarPropertyScores = new HashMap<>();
          Set<String> sonarMetrics = Set.copyOf(sonarProperties.get("metricKeys").keySet());


          LinkedHashMap<String, HashMap<String, Double>> analysis = new LinkedHashMap<>();

            File dir = new File("/home/upload/" + id.toString());
            if(dir.exists()){
                FileUtils.deleteDirectory(dir);
            }

            String branchSHA =  this.theiaService.retrieveGithubCode(url, id);
            Pattern pattern = Pattern.compile("(\\/)(?!.*\\1)(.*)(.git)");
            Matcher matcher = pattern.matcher(url);
            String name= "";

            if (matcher.find())
            {
                name =matcher.group(2);
            }



            if(!sonarqubeService.projectExists(name,token)) {
                this.sonarqubeService.sonarScannerAnalysis(branchSHA,name, token);
                //TimeUnit.SECONDS.sleep(20);

            }


          Double linesOfCode = this.sonarqubeService.linesOfCode(token,name);


          sonarAnalysis.put("metrics", this.sonarqubeService.sonarqubeCustomMetrics(token, sonarMetrics, name));

          sonarAnalysis.put("Sonarqube",this.sonarqubeService.sonarqubeCustomVulnerabilities(token, sonarProperties.get("Sonarqube").keySet(),name,linesOfCode));

          analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));

            HashMap<String, Double> propertyScores = MeasureService.measureCustomPropertiesScore(analysis, sonarProperties);
          analysis.put("Property_Scores", propertyScores);
          analysis.put("metrics", sonarAnalysis.get("metrics"));

//      Calculating characteristic scores for the characteristics the user chose.

          HashMap<String, Double> characteristicScores = MeasureService.measureCustomCharacteristicsScore(propertyScores, sonarProperties);
          analysis.put("Characteristic_Scores", characteristicScores);

//      Calculating security index.

          HashMap<String, Double> securityIndex = MeasureService.measureSecurityIndex(characteristicScores);
          analysis.put("Security_index", securityIndex);

          analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));
            String finalName = name;
            analysis.put("ProjectKey", new HashMap<String, Double>() {{
              put(finalName, 0d);
          }});

//      Return the analysis map.
          return new ResponseEntity<>(analysis, HttpStatus.CREATED);

        }
        else if (language.equals("CPP")) {



            File dir = new File("/home/upload/" + id.toString());

            if(dir.exists()){
                FileUtils.deleteDirectory(dir);
            }

            String branchSHA =  this.theiaService.retrieveGithubCode(url, id);

            Pattern pattern = Pattern.compile("(\\/)(?!.*\\1)(.*)(.git)");
            Matcher matcher = pattern.matcher(url);
            String name= "";


            if (matcher.find())
            {
                name =matcher.group(2);
            }


            File folderSHA = new File("/home/upload/" + name);
            dir.renameTo(folderSHA);

            //      Downloading the Github Project.

            if(!sonarqubeService.projectExists(name,token)) {
                this.sonarqubeService.sonarCppAnalysis(branchSHA,name, token);

                TimeUnit.SECONDS.sleep(30);

            }


            HashMap<String, HashMap<String, Double>> sonarAnalysis = new HashMap<>();
            HashMap<String, Double> sonarPropertyScores = new HashMap<>();



            //  File dir = this.theiaService.retrieveGithubCode(url, id);
            LinkedHashMap<String, HashMap<String, Double>> analysis = new LinkedHashMap<>();


//          TimeUnit.SECONDS.sleep(180);

            Double linesOfCode = this.sonarqubeService.linesOfCode(token,name);


            sonarAnalysis.put("Sonarqube", this.sonarqubeService.sonarqubeCustomCPP(token, linesOfCode,name, new ArrayList<>(sonarProperties.get("Sonarqube").keySet())));


            analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));
            HashMap<String, Double> propertyScores = MeasureService.measureCustomPropertiesScore(analysis, sonarProperties);
            sonarAnalysis.put("Property_Scores", propertyScores);



//      Calculating characteristic scores for the characteristics the user chose.
            HashMap<String, Double> characteristicScores = MeasureService.measureCustomCharacteristicsScore(propertyScores, sonarProperties);
            sonarAnalysis.put("Characteristic_Scores", characteristicScores);

//      Calculating security index.
            HashMap<String, Double> securityIndex = MeasureService.measureSecurityIndex(characteristicScores);
            sonarAnalysis.put("Security_index", securityIndex);

            analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));
            sonarAnalysis.put("ProjectKey", new HashMap<String, Double>() {{
                put(id.toString(), 0d);
            }});


            return new ResponseEntity<>(sonarAnalysis, HttpStatus.CREATED);

        } else{
          //      Return the analysis map.
          return new ResponseEntity<>(null, HttpStatus.CREATED);
      }
    }



    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }

}
