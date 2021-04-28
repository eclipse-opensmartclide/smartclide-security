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



    @GetMapping("retrieve")
    public ResponseEntity<HashMap<String, HashMap<String, Float>>> retrieveCode(@RequestParam String url) throws IOException, InterruptedException {
        String dir = this.theiaService.retrieveGithubCode(url);
        HashMap<String, HashMap<String, Float>> anaylisis = new HashMap<>();
        HashMap<String, Float> ckValues = this.ckService.generateCKValues(dir);
        anaylisis.put("CK", ckValues);
        HashMap<String, Float> pmdValues = this.pmdService.pmdValues(ckValues.get("loc"), dir);
        anaylisis.put("PMD", pmdValues);
        return new ResponseEntity<>(anaylisis, HttpStatus.OK);
    }


    @GetMapping(value = "ckReport", produces = "text/csv")
    public ResponseEntity downloadCKReport(@RequestParam String path) throws IOException {

        String workDir = "/home/anasmarg/Desktop/upload/" + path;
        File file = new File(workDir + "/ck.csv");

        this.ckService.parseCKReport(workDir);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + path + "/ck.csv")
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new FileSystemResource(file));
    }

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
        return new ResponseEntity<>(analysis, HttpStatus.CREATED);
    }

    @PostMapping("customizable")
    public ResponseEntity<HashMap<String, HashMap<String, Float>>> customizable(@RequestPart("folder") MultipartFile zip, @RequestPart("dir") String dir, @RequestPart("properties") LinkedHashMap<String, LinkedHashMap<String, List<Float>>> properties) throws IOException, InterruptedException {
        properties.get("CK").put("loc", new ArrayList<>());
        String path = this.fileUtilService.saveFolder(zip, dir);
        HashMap<String, HashMap<String, Float>> analysis = new HashMap<>();
        HashMap<String, Float> ckValues = this.ckService.generateCustomCKValues(path, new ArrayList<>(properties.get("CK").keySet()));
        analysis.put("CK", ckValues);
        HashMap<String, Float> pmdValues = this.pmdService.generateCustomPMDValues(ckValues.get("loc"), path, new ArrayList<>(properties.get("PMD").keySet()));
        analysis.put("PMD", pmdValues);
        HashMap<String, Float> propertyScores = MeasureService.measureCustomPropertiesScore(analysis, properties);
        analysis.put("Property Scores", propertyScores);
        HashMap<String, Float> characteristicScores = MeasureService.measureCustomCharacteristicsScore(propertyScores, properties);
        analysis.put("Characteristic Scores", characteristicScores);
        HashMap<String, Float> securityIndex = MeasureService.measureSecurityIndex(characteristicScores);
        analysis.put("Security index", securityIndex);
        return new ResponseEntity<>(analysis, HttpStatus.CREATED);
    }
}