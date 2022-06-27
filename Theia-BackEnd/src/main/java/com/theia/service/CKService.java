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

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKClassResult;
import com.github.mauricioaniche.ck.CKNotifier;
import com.github.mauricioaniche.ck.ResultWriter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class CKService {

    public static List<List<String>> filterCKRecords(List<List<String>> records, List<String> properties){
        List<List<String>> metrics = new ArrayList<List<String>>();
        List<Integer> indexes= new ArrayList<>();
        for(String property: properties){
            indexes.add(records.get(0).indexOf(property));
        }
        for(List<String> record: records){
            List<String> metric = new ArrayList<>();

            for(Integer i : indexes){
                metric.add(record.get(i));
            }

            metrics.add(metric);
        }
        return metrics;
    }

//    public static void parseCKReport(File dir) throws IOException {
//        List<List<String>> records = new ArrayList<List<String>>();
//
//        try (CSVReader csvReader = new CSVReader(new FileReader(dir + "/class.csv"));) {
//            String[] values = null;
//
//            while ((values = csvReader.readNext()) != null) {
//                records.add(Arrays.asList(values));
//            }
//        } catch (CsvValidationException | FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//       int[] indexes = new int[]{records.get(0).indexOf("file"), records.get(0).indexOf("cbo"), records.get(0).indexOf("wmc"), records.get(0).indexOf("lcom"), records.get(0).indexOf("loc")};
//
//        List<List<String>> ckRecords = new ArrayList<>();
//
//        for(List<String> record: records){
//
//            List<String> filteredRecords = new ArrayList<>();
//            for(int index: indexes){
//                filteredRecords.add(record.get(index));
//            }
//            ckRecords.add(filteredRecords);
//        }
//
//        ckRecords.add(0, Arrays.asList("=============== CLASS.CSV ==============="));
//
//        writeCSVReport(ckRecords,dir.toString() + "/ck");
//    }

//    public static void writeCSVReport(List<List<String>> metrics, String filename) throws IOException {
//        File file = new File(filename + ".csv");
//
//        FileWriter fileWriter = new FileWriter(file);
//        CSVWriter writer = new CSVWriter(fileWriter);
//
//        for(List<String> metric:metrics){
//            String[] array = metric.toArray(String[]::new);
//            writer.writeNext(array);
//        }
//        writer.close();
//    }


//   Services accepting user input producing CK properties results.
    public HashMap<String, Double> generateCustomCKValues(File dir, List<String> ckProperties) throws IOException {
        boolean useJars = false;
        int maxAtOnce = 0;
        boolean variablesAndFields = true;

        ResultWriter writer = new ResultWriter(dir.toString() + "/class.csv", dir + "/methods.csv", dir + "/variable.csv", dir + "/fields.csv", variablesAndFields);


        new CK(false, maxAtOnce, true).calculate(dir.toString(), new CKNotifier() {
            @Override
            public void notify(CKClassResult ckClassResult) {
                try {
                    writer.printResult(ckClassResult);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void notifyError(String sourceFilePath, Exception e) {
                System.err.println("Error in " + sourceFilePath);
                e.printStackTrace(System.err);
            }
        });

        writer.flushAndClose();

        List<List<String>> records = new ArrayList<List<String>>();


        try (CSVReader csvReader = new CSVReader(new FileReader(dir.toString() + "/class.csv"));) {
            String[] values = null;

            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }

        List<List<String>> metrics = filterCKRecords(records, ckProperties);

        return MeasureService.measureCKProperties(metrics);
    }


}
