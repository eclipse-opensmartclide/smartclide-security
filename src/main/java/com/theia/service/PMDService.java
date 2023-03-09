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

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.theia.model.PMDvalues;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.csv.*;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class PMDService {
    
    public List<String> filterRecords(List<List<String>> records){
        records.remove(0);
        return records.stream().map(x -> x.get(x.size() -1)).collect(Collectors.toList());
    }

//   Services accepting user input producing results.
    public PMDvalues generateCustomPMDValues(boolean exists,Double loc, String path, List<String> rulesets) throws IOException, InterruptedException {
        HashMap<String, Double> pmdValues = new HashMap<>();
        HashMap<String, List<Map<String, String>>> recordCategories = new HashMap<>();

        PMDvalues valuesPMD = new PMDvalues();
        for(String ruleset: rulesets){
            List<List<String>> records = new ArrayList<List<String>>();
            File file = new File(path + "/" + ruleset + ".csv");

            if (exists==false) {
                pmdValues.put(ruleset, 0d);
                System.out.println(path);
               Process process = Runtime.getRuntime().exec(System.getenv("HOME") + "/pmd-bin-6.30.0/bin/run.sh pmd -d " + path + " -R " + "/opt/resources/Rulesets/" + ruleset + ".xml -f csv -r " + path + "/" + ruleset + ".csv");
                System.out.println("THIS    " + System.getenv("HOME") + "/pmd-bin-6.30.0/bin/run.sh pmd -d " + path + " -R " + Path.of("").toAbsolutePath().toString() + "/Rulesets/" + ruleset + ".xml -f csv -r " + path + "/" + ruleset + ".csv");

                // HashMap<String,List<List<String>>> recordsHas = new HashMap<>();

                TimeUnit.SECONDS.sleep(4);

                System.out.println("PAth:  " + path);
                try {
                    BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
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
            }
            CsvSchema csv = CsvSchema.emptySchema().withHeader();
            CsvMapper csvMapper = new CsvMapper();
            MappingIterator<Map<String, String>> mappingIterator =  csvMapper.reader().forType(Map.class).with(csv).readValues(file);

            List<Map<String, String>> list = mappingIterator.readAll();
            try (CSVReader csvReader = new CSVReader(new FileReader(path + "/" + ruleset + ".csv"));) {
                String[] values = null;
                HashMap<String,String> value_hash = new HashMap<>();
                while ((values = csvReader.readNext()) != null) {
                    records.add(Arrays.asList(values));
                }
            } catch (CsvValidationException | FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(records.size() == 0){
                continue;
            }else{
                valuesPMD.records = records;
                //valuesPMD
                pmdValues.put(ruleset, MeasureService.measurePMDProperties(filterRecords(records), loc));
            }
            recordCategories.put(ruleset,list);
        }
        valuesPMD.recordCategories=recordCategories;
        valuesPMD.measurePMDProperties=pmdValues;
        return valuesPMD;
    }

}
