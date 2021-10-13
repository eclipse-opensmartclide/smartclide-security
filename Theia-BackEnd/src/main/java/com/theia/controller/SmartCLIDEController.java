package com.theia.controller;


import com.theia.model.SonarIssue;
import com.theia.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


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


    private static String token = "3fa6958c8209021fa8e2d7f0f2cb899256494601";

    //  Endpoint, providing Github URL, downloading and analyzing the project with default values of the CK and PMD tools.
    @PostMapping("/analyze")
    public ResponseEntity<HashMap<String, HashMap<String, Double>>> githubRetrieve(@RequestPart("url") String url, @RequestPart("language")String language, @RequestPart("properties") LinkedHashMap<String, LinkedHashMap<String, List<Double>>> properties, @RequestPart("sonarqube")LinkedHashMap<String, LinkedHashMap<String, List<Double>>> sonarProperties) throws IOException, InterruptedException {
      if(language.equals("Maven")){
          UUID id = UUID.randomUUID();
          properties.get("CK").put("loc", new ArrayList<>());
          HashMap<String, HashMap<String, Double>> sonarAnalysis = new HashMap<>();
          HashMap<String, Double> sonarPropertyScores = new HashMap<>();
          Set<String> sonarMetrics = Set.copyOf(sonarProperties.get("metricKeys").keySet());

          properties.put("Sonarqube", sonarProperties.get("metricKeys"));
          properties.get("Sonarqube").putAll(sonarProperties.get("vulnerabilities"));


//      Downloading the Github Project.
          File dir = this.theiaService.retrieveGithubCode(url, id);
          LinkedHashMap<String, HashMap<String, Double>> analysis = new LinkedHashMap<>();

//      Analyzing project with CK tool, alongside with the default values chosed for the CK tool.
          HashMap<String, Double> ckValues = this.ckService.generateCustomCKValues(dir, new ArrayList<>(properties.get("CK").keySet()));
          analysis.put("CK", ckValues);

//      Analyzing with PMD tool, alongside with default values chosed for the PMD tool.
          HashMap<String, Double> pmdValues = this.pmdService.generateCustomPMDValues(ckValues.get("loc"), dir.toString(), new ArrayList<>(properties.get("PMD").keySet()));
          analysis.put("PMD", pmdValues);

          //SONARQUBE
          this.sonarqubeService.sonarMavenAnalysis(id, token);
          TimeUnit.SECONDS.sleep(30);
//      Analyze Sonarqube Metrics Hardcoded.
          sonarAnalysis.put("Sonarqube", this.sonarqubeService.sonarqubeCustomMetrics(token, sonarMetrics, id.toString()));

//      Analyze Sonarqube Vulnerabilities Hardcoded.
          sonarAnalysis.get("Sonarqube").putAll(this.sonarqubeService.sonarqubeCustomVulnerabilities(token, sonarProperties.get("vulnerabilities").keySet(), id.toString()));

          analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));
          HashMap<String, Double> propertyScores = MeasureService.measureCustomPropertiesScore(analysis, properties);
          propertyScores.put("weak_cryptography", propertyScores.get("weak-cryptography"));
          propertyScores.remove("weak-cryptography");
          propertyScores.put("sql_injection", propertyScores.get("sql-injection"));
          propertyScores.remove("sql-injection");
          propertyScores.put("insecure_conf", propertyScores.get("insecure-conf"));
          propertyScores.remove("insecure-conf");
          analysis.put("Property_Scores", propertyScores);

//      Calculating characteristic scores for the characteristics the user chose.
          HashMap<String, Double> characteristicScores = MeasureService.measureCustomCharacteristicsScore(propertyScores, properties);
          analysis.put("Characteristic_Scores", characteristicScores);

//      Calculating security index.
          HashMap<String, Double> securityIndex = MeasureService.measureSecurityIndex(characteristicScores);
          analysis.put("Security_index", securityIndex);

          analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));

//      Include Vulnerability Prediction Model.
          analysis.put("Vulnerability_Prediction", this.vpService.vulnerabilityPrediction(url, "java"));
          analysis.put("ProjectKey", new HashMap<String, Double>(){{put(id.toString(), 0d);}});

//      Return the analysis map.
          return new ResponseEntity<>(analysis, HttpStatus.CREATED);
      }else {
          UUID id = UUID.randomUUID();
          HashMap<String, HashMap<String, Double>> sonarAnalysis = new HashMap<>();
          HashMap<String, Double> sonarPropertyScores = new HashMap<>();
          Set<String> sonarMetrics = Set.copyOf(sonarProperties.get("metricKeys").keySet());

          properties.put("Sonarqube", sonarProperties.get("metricKeys"));
          properties.get("Sonarqube").putAll(sonarProperties.get("vulnerabilities"));


//      Downloading the Github Project.
          File dir = this.theiaService.retrieveGithubCode(url, id);
          LinkedHashMap<String, HashMap<String, Double>> analysis = new LinkedHashMap<>();


          this.sonarqubeService.sonarPythonAnalysis(id, token);
          TimeUnit.SECONDS.sleep(30);


          sonarAnalysis.put("Sonarqube", this.sonarqubeService.sonarqubeCustomMetrics(token, sonarMetrics, id.toString()));

          sonarAnalysis.get("Sonarqube").putAll(this.sonarqubeService.sonarqubeCustomVulnerabilities(token, sonarProperties.get("vulnerabilities").keySet(), id.toString()));

          analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));
          HashMap<String, Double> propertyScores = MeasureService.measureCustomPropertiesScore(analysis, properties);
          analysis.put("Property_Scores", propertyScores);

//      Calculating characteristic scores for the characteristics the user chose.
          HashMap<String, Double> characteristicScores = MeasureService.measureCustomCharacteristicsScore(propertyScores, properties);
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
    }

    @PostMapping("/javaClient")
    public ResponseEntity<LinkedHashMap<String, HashMap<String, Double>>> javaClient(@RequestPart("url") String url, @RequestPart("properties")LinkedHashMap<String, LinkedHashMap<String, List<Double>>> properties, @RequestPart("sonarqube")LinkedHashMap<String, LinkedHashMap<String, List<Double>>> sonarProperties) throws IOException, InterruptedException {
        UUID id = UUID.randomUUID();
        properties.get("CK").put("loc", new ArrayList<>());
        HashMap<String, HashMap<String, Double>> sonarAnalysis = new HashMap<>();
        HashMap<String, Double> sonarPropertyScores = new HashMap<>();
        Set<String> sonarMetrics = Set.copyOf(sonarProperties.get("metricKeys").keySet());

        properties.put("Sonarqube", sonarProperties.get("metricKeys"));
        properties.get("Sonarqube").putAll(sonarProperties.get("vulnerabilities"));


//      Downloading the Github Project.
        File dir = this.theiaService.retrieveGithubCode(url, id);
        LinkedHashMap<String, HashMap<String, Double>> analysis = new LinkedHashMap<>();
//      Analyzing project with CK tool, alongside with the default values chosed for the CK tool.
        HashMap<String, Double> ckValues = this.ckService.generateCustomCKValues(dir, new ArrayList<>(properties.get("CK").keySet()));
        analysis.put("CK", ckValues);

//      Analyzing with PMD tool, alongside with default values chosed for the PMD tool.
        HashMap<String, Double> pmdValues = this.pmdService.generateCustomPMDValues(ckValues.get("loc"), dir.toString(), new ArrayList<>(properties.get("PMD").keySet()));
        analysis.put("PMD", pmdValues);

        //SONARQUBE
        this.sonarqubeService.sonarMavenAnalysis(id, token);
        TimeUnit.SECONDS.sleep(15);
//      Analyze Sonarqube Metrics Hardcoded.
        sonarAnalysis.put("Sonarqube", this.sonarqubeService.sonarqubeCustomMetrics(token, sonarMetrics, id.toString()));

//      Analyze Sonarqube Vulnerabilities Hardcoded.
        sonarAnalysis.get("Sonarqube").putAll(this.sonarqubeService.sonarqubeCustomVulnerabilities(token, sonarProperties.get("vulnerabilities").keySet(), id.toString()));
        analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));

        return new ResponseEntity<>(analysis, HttpStatus.OK);
    }

    @PostMapping("/pythonClient")
    public ResponseEntity<LinkedHashMap<String, HashMap<String, Double>>> pythonClient(@RequestPart("url") String url, @RequestPart("properties")LinkedHashMap<String, LinkedHashMap<String, List<Double>>> properties, @RequestPart("sonarqube")LinkedHashMap<String, LinkedHashMap<String, List<Double>>> sonarProperties) throws IOException, InterruptedException {
        UUID id = UUID.randomUUID();
        HashMap<String, HashMap<String, Double>> sonarAnalysis = new HashMap<>();
        HashMap<String, Double> sonarPropertyScores = new HashMap<>();
        Set<String> sonarMetrics = Set.copyOf(sonarProperties.get("metricKeys").keySet());

        properties.put("Sonarqube", sonarProperties.get("metricKeys"));
        properties.get("Sonarqube").putAll(sonarProperties.get("vulnerabilities"));


//      Downloading the Github Project.
        File dir = this.theiaService.retrieveGithubCode(url, id);
        LinkedHashMap<String, HashMap<String, Double>> analysis = new LinkedHashMap<>();


        this.sonarqubeService.sonarPythonAnalysis(id, token);
        TimeUnit.SECONDS.sleep(5);

//      Analyze Sonarqube Metrics Hardcoded.
        sonarAnalysis.put("Sonarqube", this.sonarqubeService.sonarqubeCustomMetrics(token, sonarMetrics, id.toString()));

//      Analyze Sonarqube Vulnerabilities Hardcoded.
        sonarAnalysis.get("Sonarqube").putAll(this.sonarqubeService.sonarqubeCustomVulnerabilities(token, sonarProperties.get("vulnerabilities").keySet(), id.toString()));

        analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));

        return new ResponseEntity<>(analysis, HttpStatus.OK);
    }

    @GetMapping("/cpp")
    public ResponseEntity<Void> cppTesting(@RequestPart("url")String url) throws IOException {
        UUID id = UUID.randomUUID();
        File dir = this.theiaService.retrieveGithubCode(url, id);
        this.sonarqubeService.sonarCppAnalysis(id, token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//  Endpoint uploading project as a zip folder, analyzing the project with default values of the CK and PMD tools.
//    @PostMapping("uploadFolder")
//    public ResponseEntity<HashMap<String, HashMap<String, Float>>> uploadFolder(@RequestPart("folder") MultipartFile zip, @RequestPart("dir") String dir) throws IOException, InterruptedException {
//        String path = this.fileUtilService.saveFolder(zip, dir);
//        HashMap<String, HashMap<String, Float>> analysis = new HashMap<>();
//        HashMap<String, Float> ckValues = this.ckService.generateCKValues(path);
//        analysis.put("CK", ckValues);
//        HashMap<String, Float> pmdValues = this.pmdService.pmdValues(ckValues.get("loc"), path);
//        analysis.put("PMD", pmdValues);
//        HashMap<String, Float> propertyScores = MeasureService.measurePropertiesScore(analysis);
//        analysis.put("Property Scores", propertyScores);
//        HashMap<String, Float> characteristicScores = MeasureService.measureCharacteristicsScores(propertyScores);
//        analysis.put("Characteristic Scores", characteristicScores);
//        HashMap<String, Float> securityIndex = MeasureService.measureSecurityIndex(characteristicScores);
//        analysis.put("Security index", securityIndex);
//        this.devSkimService.generateDevSkimValues(path);
//        return new ResponseEntity<>(analysis, HttpStatus.CREATED);
//    }

////  Endpoint uploading project as a zip folder, analyzing the project with custom values and thresholds of the CK and PMD tools.
//    @PostMapping("customizable")
//    public ResponseEntity<HashMap<String, HashMap<String, Float>>> customizable(@RequestPart("folder") MultipartFile zip, @RequestPart("dir") String dir, @RequestPart("properties") LinkedHashMap<String, LinkedHashMap<String, List<Float>>> properties) throws IOException, InterruptedException {
//        UUID id = UUID.randomUUID();
//
//        //      Adding line of codes property as a mandatory field. This field will always be used and user won't have the ability to uncheck it.
//        properties.get("CK").put("loc", new ArrayList<>());
//
////      Unzip the project in the dir path chosed. The dir is hardcoded for local testing.
//        String path = this.fileUtilService.saveFolder(zip, dir);
//
////      Creating the analysis HashMap. This map will be returned from the endpoint, storing the results and the values of the properties of the tools chosed.
//        HashMap<String, HashMap<String, Float>> analysis = new HashMap<>();
//
////      Analyzing project with CK tool, alongside with the properties the user chose for the CK tool.
//        HashMap<String, Float> ckValues = this.ckService.generateCustomCKValues(path, new ArrayList<>(properties.get("CK").keySet()));
//        analysis.put("CK", ckValues);
//
////      Analyzing with PMD tool, alongside with the properties the user chose for the PMD tool.
//        HashMap<String, Float> pmdValues = this.pmdService.generateCustomPMDValues(ckValues.get("loc"), path, new ArrayList<>(properties.get("PMD").keySet()));
//        analysis.put("PMD", pmdValues);
//
////      Calculating property scores for the properties the user chose.
//        HashMap<String, Float> propertyScores = MeasureService.measureCustomPropertiesScore(analysis, properties);
//        analysis.put("Property Scores", propertyScores);
//
////      Calculating characteristic scores for the characteristics the user chose.
//        HashMap<String, Float> characteristicScores = MeasureService.measureCustomCharacteristicsScore(propertyScores, properties);
//        analysis.put("Characteristic Scores", characteristicScores);
//
////      Calculating security index.
//        HashMap<String, Float> securityIndex = MeasureService.measureSecurityIndex(characteristicScores);
//        analysis.put("Security index", securityIndex);
//
////      Return the analysis map.
//        return new ResponseEntity<>(analysis, HttpStatus.CREATED);
//    }

////  Endpoint downloading report from CK tool as a CSV.
//    @GetMapping(value = "ckReport", produces = "text/csv")
//    public ResponseEntity downloadCKReport(@RequestParam("dir") String path) throws IOException {
//
//
//        String workDir = Path.of("").toAbsolutePath().toString() + "/upload/" + path;
//        File file = new File(workDir + "/ck.csv");
//
//        this.ckService.parseCKReport(workDir);
//
//        return ResponseEntity.ok()
//                .header("Content-Disposition", "attachment; filename=" + path + "/ck.csv")
//                .contentLength(file.length())
//                .contentType(MediaType.parseMediaType("text/csv"))
//                .body(new FileSystemResource(file));
//    }

    @GetMapping("/sonarIssues")
    public ResponseEntity<List<SonarIssue>> test(@RequestParam("projectKey")String key){
        return new ResponseEntity<>(this.sonarqubeService.sonarqubeIssues(key, token), HttpStatus.OK);
    }

    @GetMapping("/dummy")
    public ResponseEntity<Void> dummy(){
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("dir")
    public ResponseEntity<String> dir(){
        System.out.println();
        return new ResponseEntity<>(System.getProperty("user.dir"), HttpStatus.OK);
    }
}