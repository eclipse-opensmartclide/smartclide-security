package com.theia.controller;


import com.theia.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("/code/")
@CrossOrigin("*")
public class TheiaController {

    @Autowired
    private TheiaService theiaService;

    @Autowired
    private CKService ckService;

    @Autowired
    private PMDService pmdService;

    @Autowired
    private FileUtilService fileUtilService;

    @Autowired
    private DevSkimService devSkimService;


//  Endpoint, providing Github URL, downloading and analyzing the project with default values of the CK and PMD tools.
    @GetMapping("retrieve")
    public ResponseEntity<HashMap<String, HashMap<String, Float>>> retrieveCode(@RequestParam String url) throws IOException, InterruptedException {
        String dir = this.theiaService.retrieveGithubCode(url);
        HashMap<String, HashMap<String, Float>> anaylisis = new HashMap<>();
        HashMap<String, Float> ckValues = this.ckService.generateCKValues(dir);
        anaylisis.put("CK", ckValues);
        HashMap<String, Float> pmdValues = this.pmdService.pmdValues(ckValues.get("loc"), dir);
        anaylisis.put("PMD", pmdValues);
        this.devSkimService.generateDevSkimValues(dir);
        return new ResponseEntity<>(anaylisis, HttpStatus.OK);
    }

//  Endpoint uploading project as a zip folder, analyzing the project with default values of the CK and PMD tools.
    @PostMapping("uploadFolder")
    public ResponseEntity<HashMap<String, HashMap<String, Float>>> uploadFolder(@RequestPart("folder") MultipartFile zip, @RequestPart("dir") String dir) throws IOException, InterruptedException {
        String path = this.fileUtilService.saveFolder(zip, dir);
        HashMap<String, HashMap<String, Float>> analysis = new HashMap<>();
        HashMap<String, Float> ckValues = this.ckService.generateCKValues(path);
        analysis.put("CK", ckValues);
        HashMap<String, Float> pmdValues = this.pmdService.pmdValues(ckValues.get("loc"), path);
        analysis.put("PMD", pmdValues);
        HashMap<String, Float> propertyScores = MeasureService.measurePropertiesScore(analysis);
        analysis.put("Property Scores", propertyScores);
        HashMap<String, Float> characteristicScores = MeasureService.measureCharacteristicsScores(propertyScores);
        analysis.put("Characteristic Scores", characteristicScores);
        HashMap<String, Float> securityIndex = MeasureService.measureSecurityIndex(characteristicScores);
        analysis.put("Security index", securityIndex);
        this.devSkimService.generateDevSkimValues(path);
        return new ResponseEntity<>(analysis, HttpStatus.CREATED);
    }

//  Endpoint uploading project as a zip folder, analyzing the project with custom values and thresholds of the CK and PMD tools.
    @PostMapping("customizable")
    public ResponseEntity<HashMap<String, HashMap<String, Float>>> customizable(@RequestPart("folder") MultipartFile zip, @RequestPart("dir") String dir, @RequestPart("properties") LinkedHashMap<String, LinkedHashMap<String, List<Float>>> properties) throws IOException, InterruptedException {
//      Adding line of codes property as a mandatory field. This field will always be used and user won't have the ability to uncheck it.
        properties.get("CK").put("loc", new ArrayList<>());

//      Unzip the project in the dir path chosed. The dir is hardcoded for local testing.
        String path = this.fileUtilService.saveFolder(zip, dir);

//      Creating the analysis HashMap. This map will be returned from the endpoint, storing the results and the values of the properties of the tools chosed.
        HashMap<String, HashMap<String, Float>> analysis = new HashMap<>();

//      Analyzing project with CK tool, alongside with the properties the user chose for the CK tool.
        HashMap<String, Float> ckValues = this.ckService.generateCustomCKValues(path, new ArrayList<>(properties.get("CK").keySet()));
        analysis.put("CK", ckValues);

//      Analyzing with PMD tool, alongside with the properties the user chose for the PMD tool.
        HashMap<String, Float> pmdValues = this.pmdService.generateCustomPMDValues(ckValues.get("loc"), path, new ArrayList<>(properties.get("PMD").keySet()));
        analysis.put("PMD", pmdValues);

//      Calculating property scores for the properties the user chose.
        HashMap<String, Float> propertyScores = MeasureService.measureCustomPropertiesScore(analysis, properties);
        analysis.put("Property Scores", propertyScores);

//      Calculating characteristic scores for the characteristics the user chose.
        HashMap<String, Float> characteristicScores = MeasureService.measureCustomCharacteristicsScore(propertyScores, properties);
        analysis.put("Characteristic Scores", characteristicScores);

//      Calculating security index.
        HashMap<String, Float> securityIndex = MeasureService.measureSecurityIndex(characteristicScores);
        analysis.put("Security index", securityIndex);

//      Return the analysis map.
        return new ResponseEntity<>(analysis, HttpStatus.CREATED);
    }

//  Endpoint downloading report from CK tool as a CSV.
    @GetMapping(value = "ckReport", produces = "text/csv")
    public ResponseEntity downloadCKReport(@RequestParam("dir") String path) throws IOException {

        String workDir = "/home/anasmarg/Desktop/upload/" + path;
        File file = new File(workDir + "/ck.csv");

        this.ckService.parseCKReport(workDir);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + path + "/ck.csv")
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new FileSystemResource(file));
    }

// Endpoint analyzing and downloading report using Devskim tool.
    @GetMapping(value = "devskimReport", produces = "text/csv")
    public ResponseEntity downloadDevskimReport(@RequestPart("folder") MultipartFile zip, @RequestParam("dir") String dir) throws IOException {
        String path = this.fileUtilService.saveFolder(zip, dir);
        this.devSkimService.generateDevSkimValues(path);
        File file = new File(path + "devskim.txt");

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + path + "devskim.txt")
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new FileSystemResource(file));
    }
}