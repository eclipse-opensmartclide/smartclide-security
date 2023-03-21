package com.theia.controller;


import com.google.gson.*;
import com.theia.model.PMDvalues;
import com.theia.service.*;

import org.apache.commons.io.FileUtils;
import org.jdom2.JDOMException;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.*;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/smartclide")

public class SmartCLIDEController {
	
	
	@Value("${sonar.user}")
    private String sonar_user;
    
	@Value("${sonar.password}")
	private String sonar_password;

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
		
	
    //  Endpoint, providing GitHub URL, downloading and analyzing the project with default values of the CK and PMD tools.

    //Analyze compiled java project from zip file
    @PostMapping(value = "analyze_local",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE} )
    public ResponseEntity<JsonObject> githubRetrieve(@RequestParam MultipartFile zip,@RequestPart  LinkedHashMap<String, LinkedHashMap<String, List<Double>>> sonarProperties) throws IOException, InterruptedException, ParserConfigurationException, SAXException, ParseException {



        String filename = zip.getOriginalFilename();
        String StringDir = this.fileUtilService.saveFolder(zip, filename);
        String name =filename.substring(0, filename.lastIndexOf('.'));

        File dir = new File(StringDir);


        //Get CK amd metricKey values from the request

        sonarProperties.get("CK").put("loc", new ArrayList<>());
        HashMap<String, HashMap<String, Double>> sonarAnalysis = new HashMap<>();
        Set<String> sonarMetrics = Set.copyOf(sonarProperties.get("metricKeys").keySet());

        LinkedHashMap<String, HashMap<String, Double>> analysis = new LinkedHashMap<>();

        //Analyzing project with CK tool, alongside with the default values chosed for the CK tool

        ArrayList<String> stone = new ArrayList<>(sonarProperties.get("CK").keySet());

        Files.setPosixFilePermissions(Paths.get(StringDir), PosixFilePermissions.fromString("rwxr-x---"));


        HashMap<String, Double> ckValues = this.ckService.generateCustomCKValues(dir, new ArrayList<>(sonarProperties.get("CK").keySet()));
        analysis.put("CK", ckValues);

        //Analyzing with PMD tool, alongside with default values chosed for the PMD tool.
        PMDvalues valuesPMD = new PMDvalues();

        //HashMap<String, Double> pmdValues = this.pmdService.generateCustomPMDValues(ckValues.get("loc"), StringDir.toString(), new ArrayList<>(sonarProperties.get("PMD").keySet()));

        valuesPMD = this.pmdService.generateCustomPMDValues(false,ckValues.get("loc"), StringDir.toString(), new ArrayList<>(sonarProperties.get("PMD").keySet()));
        HashMap<String, Double> pmdValues = valuesPMD.measurePMDProperties;
        analysis.put("PMD", pmdValues);

        //SONARQUBE checking if already analyzed and analyze

        if (!sonarqubeService.projectExists(name, sonar_user,sonar_password)) {
            this.sonarqubeService.sonarMavenAnalysis(name, name, sonar_user,sonar_password,"zip");
            //TimeUnit.SECONDS.sleep(0);

        }

        //Analyze Sonarqube Metrics Hardcoded.
        Double linesOfCode = this.sonarqubeService.linesOfCode(sonar_user,sonar_password, name);


        //Analyze
        //Sonarqube Vulnerabilities Hardcoded.

        sonarAnalysis.put("Sonarqube", this.sonarqubeService.sonarqubeCustomVulnerabilities(sonar_user,sonar_password, sonarProperties.get("Sonarqube").keySet(), name, linesOfCode));


        analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));
        HashMap<String, Double> propertyScores = MeasureService.measureCustomPropertiesScore(analysis, sonarProperties);
        analysis.put("metrics", this.sonarqubeService.sonarqubeCustomMetrics(sonar_user,sonar_password, sonarMetrics, name));
        analysis.put("Property_Scores", propertyScores);


        //Calculating characteristic res for the characteristics the user chose.
        HashMap<String, Double> characteristicScores = MeasureService.measureCustomCharacteristicsScore(propertyScores, sonarProperties);
        analysis.put("Characteristic_Scores", characteristicScores);

        ////Calculating security index.
        HashMap<String, Double> securityIndex = MeasureService.measureSecurityIndex(characteristicScores);
        analysis.put("Security_index", securityIndex);

        analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));

        ////// Return the analysis map.
        Set<String> catg = new HashSet<>(sonarAnalysis.get("Sonarqube").keySet());

        //Get hotspots from Sonarqube

        HashMap<String, JsonArray> hashHot = new HashMap<>();
        hashHot = this.sonarqubeService.hotspotSearch(catg, sonar_user, sonar_password, name);

        Gson gson = new Gson();

        //Create JsonObjects from PMD and Hotspot results and add them to the response
        String jsonHash = gson.toJson(hashHot);
        String jsonRecords = gson.toJson(valuesPMD.recordCategories);
        String jsonAnalysis = gson.toJson(analysis);

        JsonObject jsonObject = new Gson().fromJson(jsonAnalysis, JsonObject.class);
        JsonObject jsonObjectHash = new Gson().fromJson(jsonHash, JsonObject.class);
        JsonObject jsonObjectRecords= new Gson().fromJson(jsonRecords, JsonObject.class);


        jsonObject.add("Hotspots", jsonObjectHash);
        jsonObject.add("PMD_issues", jsonObjectRecords);

        return new ResponseEntity<JsonObject>(jsonObject, HttpStatus.OK);

    }

    //Vulnerability Assessment API
    @RequestMapping(method = RequestMethod.GET, value = "/VulnerabilityAssessment")
    public ResponseEntity<JsonObject> vulnerabilityPrediction(@RequestParam("project") String url, @RequestParam("lang")String language, @RequestParam("user_name") Optional<String> user_name) throws IOException, InterruptedException, ParserConfigurationException, SAXException, ParseException {

        String analysis = this.vpService.vulnerabilityPrediction(url, language, user_name);
        JsonObject jsonObject = new Gson().fromJson(analysis, JsonObject.class);

        return new ResponseEntity<>(jsonObject, HttpStatus.CREATED);

    }




    @RequestMapping(method = RequestMethod.POST, value = "/analyze", params = {"url", "language"})
    //public ResponseEntity<HashMap<String, HashMap<String, Double>>> githubRetrieve(@RequestParam("url") String url, @RequestParam("language")String language, @RequestBody LinkedHashMap<String, LinkedHashMap<String, List<Double>>> sonarProperties) throws IOException, InterruptedException, ParserConfigurationException, SAXException, ParseException {
    public ResponseEntity<JsonObject> githubRetrieve(@RequestParam("url") String url, @RequestParam("language")String language, @RequestBody LinkedHashMap<String, LinkedHashMap<String, List<Double>>> sonarProperties) throws IOException, InterruptedException, ParserConfigurationException, SAXException, ParseException, JDOMException, XPathExpressionException {
        UUID id = UUID.randomUUID();


        Pattern pattern = Pattern.compile("(\\/)(?!.*\\1)(.*)(.git)");
        Matcher matcher = pattern.matcher(url);
        String name = "";

        if (matcher.find()) {
            name = matcher.group(2);
        }
				
        boolean analyzed = this.sonarqubeService.projectExists(name, sonar_user, sonar_password);
        File dir = new File("/home/upload/" + name);

        if ((dir.exists())&&(!analyzed)) {
                FileUtils.deleteDirectory(dir);
        }

        boolean exists = this.theiaService.retrieveGithubCode(url, id);



        if (language.equals("Maven")) {


            //Get CK amd metricKey values from the request

            sonarProperties.get("CK").put("loc", new ArrayList<>());
            HashMap<String, HashMap<String, Double>> sonarAnalysis = new HashMap<>();
            Set<String> sonarMetrics = Set.copyOf(sonarProperties.get("metricKeys").keySet());


            //Download git repository if it is not downloaded already, create a folder name with SHA from latest commit


            Files.setPosixFilePermissions(Paths.get("/home/upload/" + name), PosixFilePermissions.fromString("rwxr-x---"));


            LinkedHashMap<String, HashMap<String, Double>> analysis = new LinkedHashMap<>();

            //Analyzing project with CK tool, alongside with the default values chosed for the CK tool

            ArrayList<String> stone = new ArrayList<>(sonarProperties.get("CK").keySet());

            HashMap<String, Double> ckValues = this.ckService.generateCustomCKValues(dir, new ArrayList<>(sonarProperties.get("CK").keySet()));
            analysis.put("CK", ckValues);

            //Analyzing with PMD tool, alongside with default values chosed for the PMD tool.
            PMDvalues valuesPMD = new PMDvalues();

            //SONARQUBE checking if already analyzed and analyze

            if ((!analyzed)) {
                this.sonarqubeService.sonarMavenAnalysis(name, name, sonar_user, sonar_password, "git");
                //TimeUnit.SECONDS.sleep(0);
                valuesPMD = this.pmdService.generateCustomPMDValues(exists,ckValues.get("loc"), dir.toString(), new ArrayList<>(sonarProperties.get("PMD").keySet()));

            }
            else{
                valuesPMD = this.pmdService.generateCustomPMDValues(exists,ckValues.get("loc"), dir.toString(), new ArrayList<>(sonarProperties.get("PMD").keySet()));

            }

            HashMap<String, Double> pmdValues = valuesPMD.measurePMDProperties;

            analysis.put("PMD", pmdValues);

            //Analyze Sonarqube Metrics Hardcoded.
            Double linesOfCode = this.sonarqubeService.linesOfCode(sonar_user, sonar_password, name);


            //Analyze
            //Sonarqube Vulnerabilities Hardcoded.

            sonarAnalysis.put("Sonarqube", this.sonarqubeService.sonarqubeCustomVulnerabilities(sonar_user, sonar_password, sonarProperties.get("Sonarqube").keySet(), name, linesOfCode));


            analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));

            Set<String> catg = new HashSet<>(sonarAnalysis.get("Sonarqube").keySet());


//            JsonArray jsonObject = new JsonParser().parse(jsonHotspots).getAsJsonArray();
            HashMap<String, Double> propertyScores = MeasureService.measureCustomPropertiesScore(analysis, sonarProperties);
            analysis.put("metrics", this.sonarqubeService.sonarqubeCustomMetrics(sonar_user, sonar_password, sonarMetrics, name));

            analysis.put("Property_Scores", propertyScores);


            //Calculating characteristic res for the characteristics the user chose.
            HashMap<String, Double> characteristicScores = MeasureService.measureCustomCharacteristicsScore(propertyScores, sonarProperties);
            analysis.put("Characteristic_Scores", characteristicScores);

            ////Calculating security index.
            HashMap<String, Double> securityIndex = MeasureService.measureSecurityIndex(characteristicScores);
            analysis.put("Security_index", securityIndex);

            analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));

            ////// Return the analysis map.
            HashMap<String, JsonArray> hashHot = new HashMap<>();
            hashHot = this.sonarqubeService.hotspotSearch(catg, sonar_user, sonar_password, name);


            Gson gson = new Gson();
            String jsonHash = gson.toJson(hashHot);
            String jsonRecords = gson.toJson(valuesPMD.recordCategories);
            String jsonAnalysis = gson.toJson(analysis);

            JsonObject jsonObject = new Gson().fromJson(jsonAnalysis, JsonObject.class);
            JsonObject jsonObjectHash = new Gson().fromJson(jsonHash, JsonObject.class);
            JsonObject jsonObjectRecords= new Gson().fromJson(jsonRecords, JsonObject.class);

            jsonObject.add("Hotspots", jsonObjectHash);
            jsonObject.add("PMD_issues", jsonObjectRecords);

            return new ResponseEntity<JsonObject>(jsonObject, HttpStatus.OK);

        } else if ((language.equals("Javascript")) || (language.equals("Python"))) {


            //  Get Metric Keys
            HashMap<String, HashMap<String, Double>> sonarAnalysis = new HashMap<>();

            Set<String> sonarMetrics = Set.copyOf(sonarProperties.get("metricKeys").keySet());


            LinkedHashMap<String, HashMap<String, Double>> analysis = new LinkedHashMap<>();

//            File dir = new File("/home/upload/" + id.toString());
//            if (dir.exists()) {
//                FileUtils.deleteDirectory(dir);
//            }




            if (!analyzed) {
                this.sonarqubeService.sonarScannerAnalysis( name, sonar_user,sonar_password);
                //TimeUnit.SECONDS.sleep(20);

            }


            Double linesOfCode = this.sonarqubeService.linesOfCode(sonar_user,sonar_password, name);


            sonarAnalysis.put("metrics", this.sonarqubeService.sonarqubeCustomMetrics(sonar_user,sonar_password, sonarMetrics, name));

            sonarAnalysis.put("Sonarqube", this.sonarqubeService.sonarqubeCustomVulnerabilities(sonar_user,sonar_password, sonarProperties.get("Sonarqube").keySet(), name, linesOfCode));

            analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));

            HashMap<String, Double> propertyScores = MeasureService.measureCustomPropertiesScore(analysis, sonarProperties);
            analysis.put("Property_Scores", propertyScores);
            analysis.put("metrics", sonarAnalysis.get("metrics"));

            // Calculating characteristic scores for the characteristics the user chose.

            HashMap<String, Double> characteristicScores = MeasureService.measureCustomCharacteristicsScore(propertyScores, sonarProperties);
            analysis.put("Characteristic_Scores", characteristicScores);

            // Calculating security index.

            HashMap<String, Double> securityIndex = MeasureService.measureSecurityIndex(characteristicScores);
            analysis.put("Security_index", securityIndex);

            analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));

            Set<String> catg =new HashSet<>(sonarAnalysis.get("Sonarqube").keySet());


            HashMap<String, JsonArray> hashHot = new HashMap<>();
            hashHot = this.sonarqubeService.hotspotSearch(catg, sonar_user, sonar_password, name);

            Gson gson = new Gson();
            String jsonHash = gson.toJson(hashHot);

            String jsonAnalysis = gson.toJson(analysis);

            JsonObject jsonObject = new Gson().fromJson(jsonAnalysis, JsonObject.class);
            JsonObject jsonObjectHash = new Gson().fromJson(jsonHash, JsonObject.class);

            jsonObject.add("Hotspots", jsonObjectHash);

            return new ResponseEntity<JsonObject>(jsonObject, HttpStatus.OK);

        } else if (language.equals("CPP")) {


            //Download git repository if it is not downloaded already, create a folder name with SHA from latest commit

            //File dir = new File("/home/upload/" + id.toString());

//            if (dir.exists()) {
//                FileUtils.deleteDirectory(dir);
//            }



            //File folderSHA = new File("/home/upload/" + name);
            //dir.renameTo(folderSHA);

            //Run CPP analysis
            if (!analyzed) {
                this.sonarqubeService.sonarCppAnalysis(exists,name, name,  sonar_user,sonar_password);

                TimeUnit.SECONDS.sleep(30);

            }
            else if(exists==false){

                this.sonarqubeService.runCPPcheck(name);


            }


            HashMap<String, HashMap<String, Double>> sonarAnalysis = new HashMap<>();
            LinkedHashMap<String, HashMap<String, Double>> analysis = new LinkedHashMap<>();


            Double linesOfCode = this.sonarqubeService.linesOfCode(sonar_user,sonar_password, name);

            sonarAnalysis.put("Sonarqube", this.sonarqubeService.sonarqubeCustomCPP(sonar_user,sonar_password, linesOfCode, name, new ArrayList<>(sonarProperties.get("Sonarqube").keySet())));
            analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));
            HashMap<String, Double> propertyScores = MeasureService.measureCustomPropertiesScore(analysis, sonarProperties);
            sonarAnalysis.put("Property_Scores", propertyScores);


            // Calculating characteristic scores for the characteristics the user chose.
            HashMap<String, Double> characteristicScores = MeasureService.measureCustomCharacteristicsScore(propertyScores, sonarProperties);
            sonarAnalysis.put("Characteristic_Scores", characteristicScores);

            // Calculating security index.
            HashMap<String, Double> securityIndex = MeasureService.measureSecurityIndex(characteristicScores);
            sonarAnalysis.put("Security_index", securityIndex);


            analysis.put("Sonarqube", sonarAnalysis.get("Sonarqube"));
            Gson gson = new Gson();
            String jsonString = gson.toJson(sonarAnalysis);
            String xmlcpp = gson.toJson(this.sonarqubeService.iterateXML(name));


            JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
            JsonArray jsonXML= new Gson().fromJson(xmlcpp, JsonArray.class);
            jsonObject.add("CPP",jsonXML);

            return new ResponseEntity<JsonObject>(jsonObject, HttpStatus.OK);


        }else {
            //      Return the analysis map.
            return new ResponseEntity<>(null, HttpStatus.CREATED);
        }
    }



    public static void updateEnv(String name, String val) throws ReflectiveOperationException {
        Map<String, String> env = System.getenv();
        Field field = env.getClass().getDeclaredField("m");
        field.setAccessible(true);
        ((Map<String, String>) field.get(env)).put(name, val);
    }

    public List<Object> toList(JsonArray array)  {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.size(); i++) {
            Object value = array.get(i);

            list.add(value);
        }
        return list;
    }





    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }

}