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

import com.google.gson.Gson;
import com.theia.model.Property;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MeasureService {

    public static HashMap<String, Double> measureCKProperties(List<List<String>> metrics){

        HashMap<String, Double> ckProperties = new HashMap<>();
        List<List<Integer>> integerMetrics = new ArrayList<>();

        if(metrics.size() == 0){
            return ckProperties;
        }
        List<String> properties = metrics.get(0);
        metrics.remove(0);


        for(List<String> metric: metrics){
            integerMetrics.add(metric.stream().map(Integer::parseInt).collect(Collectors.toList()));
        }

        int max = integerMetrics.stream().map(List::size).max(Comparator.naturalOrder()).get();
        int depth = integerMetrics.size();

        List<Integer> total = IntStream.range(0, max)
                .map(x -> IntStream.range(0, depth)
                        .map(y -> integerMetrics.get(y).get(x))
                        .sum())
                .mapToObj(Integer::valueOf)
                .collect(Collectors.toList());

        int index = properties.indexOf("loc");
        ckProperties.put(properties.get(index), total.get(index).doubleValue());
        List<Double> propertyValues = total.stream().map(e -> e.doubleValue()/ total.get(total.size() - 1)).collect(Collectors.toList());
        propertyValues.remove(index);
        properties.remove(index);

        for (int i = 0; i < propertyValues.size(); i++){
            ckProperties.put(properties.get(i), propertyValues.get(i));
        }

        return ckProperties;
    }

    public static Double measurePMDProperties(List<String> metrics, Double loc) throws IOException {

        Map<String, Double> pmdProperties = new HashMap<String, Double>();


        for(String value: metrics){
            increment(pmdProperties, value);
        }

        for(String key: pmdProperties.keySet()){
            pmdProperties.put(key, pmdProperties.get(key) * 1000 / loc);
        }

        double sum = 0;
        for(String key: pmdProperties.keySet()){
            sum += pmdProperties.get(key);
        }

        return sum;
    }


    public static  HashMap<String, Double> measureSecurityIndex(HashMap<String, Double> characteristicScores){

        HashMap<String, Double> securityIndex = new HashMap<>();
        Double securityindx = 0d;
        for(String characteristic: characteristicScores.keySet()){
            securityindx += 1.0 / characteristicScores.size() * characteristicScores.get(characteristic);
        }

        securityIndex.put("Security_Index", securityindx);
        return securityIndex;
    }

    public static<K> void increment(Map<K, Double> map, K key) {
        map.putIfAbsent(key, 0d);
        map.put(key, map.get(key) + 1);
    }

    //Customizable endpoint.
    public static HashMap<String, Double> measureCustomPropertiesScore(HashMap<String, HashMap<String, Double>> analysis, LinkedHashMap<String, LinkedHashMap<String, List<Double>>> properties){
        HashMap<String, Double> scores = new HashMap<>();


        for(String tool: analysis.keySet()){
            for(String characteristic: analysis.get(tool).keySet()){
                if(characteristic.equals("loc") || characteristic.equals("ncloc")){
                    continue;
                }else{
                        Property attr = new Property(characteristic, analysis.get(tool).get(characteristic));
                        attr.setCustomThresholds(properties.get(tool).get(characteristic));
                        scores.put(characteristic, attr.calculateCustomScore());
                }
            }
        }

        return scores;
    }

    public static HashMap<String, Double> measureCustomCharacteristicsScore(HashMap<String, Double> propertiesScores, LinkedHashMap<String, LinkedHashMap<String, List<Double>>> properties){
        HashMap<String, Double> characteristicScores = new HashMap<>();
        List<Double> scores = new ArrayList<Double>(propertiesScores.values());
        for(String characteristic: properties.get("Characteristics").keySet()){
            Double score = 0d;
            for(int i = 0; i < propertiesScores.keySet().size(); i++){
                score += scores.get(i) * properties.get("Characteristics").get(characteristic).get(i);
            }

            characteristicScores.put(characteristic, score);
        }

        return characteristicScores;
    }
}
