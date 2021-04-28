package com.theia.service;

import com.theia.model.Property;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class MeasureService {

    private static final HashMap<String, HashMap<String, Float>> thresholds = new HashMap<>(){
        {
            put("Assignment", new HashMap<>(){{put("min", 0f); put("average", 1.1470918148148148f); put("max", 19.607843f);}});
            put("Logging", new HashMap<>(){{put("min", 0f); put("average", 0f); put("max", 0f);}});
            put("NullPointer", new HashMap<>(){{put("min", 0f); put("average", 0f); put("max", 0f);}});
            put("MisusedFunctionality", new HashMap<>(){{put("min", 0f); put("average", 0.630511037037037f); put("max", 12.145749f);}});
            put("ResourceHandling", new HashMap<>(){{put("min", 0f); put("average", 7.019480540740741f); put("max", 23.255814f);}});
            put("Adjustability", new HashMap<>(){{put("min", 0f); put("average", 1.5801136703703704f); put("max", 12.145749f);}});
            put("ExceptionHandling", new HashMap<>(){{put("min", 0f); put("average", 3.3420624037037037f); put("max", 11.627907f);}});
            put("cbo", new HashMap<>(){{put("min", 0.011363637f); put("average", 0.20745469122222224f); put("max", 0.507772f);}});
            put("wmc", new HashMap<>(){{put("min", 0.07772021f); put("average", 0.22039069111111112f); put("max", 0.3114754f);}});
            put("lcom", new HashMap<>(){{put("min", 0f); put("average", 0.12518119402962963f); put("max",  0.7053763f);}});
        }
    };

    private static HashMap<String, List<Float>> weights = new HashMap<>(){
        {
            put("Confidentiality", new ArrayList<>(){{add(0.1f);add(0.2f);add(0.1f);add(0.005f);add(0.05f);add(0.15f);add(0.05f);add(0.1f);add(0.15f);add(0.05f);}});
            put("Integrity", new ArrayList<>(){{add(0.2f);add(0.15f);add(0.005f);add(0.15f);add(0.05f);add(0.1f);add(0.05f);add(0.1f);add(0.1f);add(0.05f);}});
            put("Availability", new ArrayList<>(){{add(0.05f);add(0.2f);add(0.005f);add(0.05f);add(0.1f);add(0.1f);add(0.05f);add(0.1f);add(0.15f);add(0.15f);}});
        }
    };

    public static HashMap<String, Float> measureCKProperties(List<List<String>> metrics){

        HashMap<String, Float> ckProperties = new HashMap<>();
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
        ckProperties.put(properties.get(index), total.get(index).floatValue());
        List<Float> propertyValues = total.stream().map(e -> e.floatValue()/ total.get(total.size() - 1)).collect(Collectors.toList());
        propertyValues.remove(index);
        properties.remove(index);

        for (int i = 0; i < propertyValues.size(); i++){
            ckProperties.put(properties.get(i), propertyValues.get(i));
        }

        return ckProperties;
    }

    public static Float measurePMDProperties(List<String> metrics, Float loc) throws IOException {


        Map<String, Float> pmdProperties = new HashMap<String, Float>();


        for(String value: metrics){
            increment(pmdProperties, value);
        }

        for(String key: pmdProperties.keySet()){
            pmdProperties.put(key, pmdProperties.get(key) * 1000 / loc);
        }

        float sum = 0;
        for(String key: pmdProperties.keySet()){
            sum += pmdProperties.get(key);
        }

        return sum;
    }

    public static HashMap<String, Float> measurePropertiesScore(HashMap<String, HashMap<String, Float>> analysis){
        HashMap<String, Float> scores = new HashMap<>();
        for(String tool: analysis.keySet()){
            for(String characteristic: analysis.get(tool).keySet()){
                if(characteristic.equals("loc")){
                    continue;
                }else{
                    Property attr = new Property(characteristic, analysis.get(tool).get(characteristic));
                    attr.setThresholds(thresholds.get(characteristic));
                    scores.put(characteristic, attr.calculateScore());
                }
            }
        }
        return scores;
    }

    public static HashMap<String, Float> measureCharacteristicsScores(HashMap<String, Float> propertiesScores){
        HashMap<String, Float> characteristicScores = new HashMap<>();
        List<Float> scores = new ArrayList<Float>(propertiesScores.values());
        for(String characteristic: weights.keySet()){
            float score = 0f;
            for(int i = 0; i < propertiesScores.keySet().size(); i++){
                score += scores.get(i) * weights.get(characteristic).get(i);
            }

            characteristicScores.put(characteristic, score);
        }

        return characteristicScores;
    }

    public static  HashMap<String, Float> measureSecurityIndex(HashMap<String, Float> characteristicScores){

        HashMap<String, Float> securityIndex = new HashMap<>();
        float securityindx = 0f;
        for(String characteristic: characteristicScores.keySet()){
            securityindx += 0.33 * characteristicScores.get(characteristic);
        }

        securityIndex.put("Security Index", securityindx);
        return securityIndex;
    }

    public static<K> void increment(Map<K, Float> map, K key) {
        map.putIfAbsent(key, 0f);
        map.put(key, map.get(key) + 1);
    }

    //Customizable endpoint.
    public static HashMap<String, Float> measureCustomPropertiesScore(HashMap<String, HashMap<String, Float>> analysis, LinkedHashMap<String, LinkedHashMap<String, List<Float>>> properties){
        HashMap<String, Float> scores = new HashMap<>();
        for(String tool: analysis.keySet()){
            for(String characteristic: analysis.get(tool).keySet()){
                if(characteristic.equals("loc")){
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

    public static HashMap<String, Float> measureCustomCharacteristicsScore(HashMap<String, Float> propertiesScores, LinkedHashMap<String, LinkedHashMap<String, List<Float>>> properties){
        HashMap<String, Float> characteristicScores = new HashMap<>();
        List<Float> scores = new ArrayList<Float>(propertiesScores.values());
        for(String characteristic: properties.get("Characteristics").keySet()){
            float score = 0f;
            for(int i = 0; i < propertiesScores.keySet().size(); i++){
                score += scores.get(i) * properties.get("Characteristics").get(characteristic).get(i);
            }

            characteristicScores.put(characteristic, score);
        }

        return characteristicScores;
    }
}
