package com.theia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Property {

    private String name;
    private Double value;
    private HashMap<String, Double> thresholds = new HashMap<>();
    private Double score;
    private List<Double> customThresholds = new ArrayList<>();

    public Property(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    public void setThresholds(HashMap<String, Double> thresholds){
        thresholds = thresholds;
    }


    public Double calculateScore(){
        if (this.value <= thresholds.get("min")){
            this.score = 1d;
        }else if(this.value <= thresholds.get("average")){
            this.score = 1 - (0.5 / (thresholds.get("average") - thresholds.get("min"))) * (this.value - thresholds.get("min"));
        }else if(this.value < this.thresholds.get("max")){
            this.score = (0.5 / (thresholds.get("max") - thresholds.get("average"))) * (thresholds.get("max") - this.value);
        }else{
            this.score = 0d;
        }

        return this.score;
    }

    public Double calculateCustomScore(){
        if (this.value <= customThresholds.get(0)){
            this.score = 1d;
        }else if(this.value <= customThresholds.get(1)){
            this.score = 1 - (0.5 / (customThresholds.get(1) - customThresholds.get(0))) * (this.value - customThresholds.get(0));
        }else if(this.value < this.customThresholds.get(2)){
            this.score = (0.5f / (customThresholds.get(2) - customThresholds.get(1))) * (customThresholds.get(2) - this.value);
        }else{
            this.score = 0d;
        }

        return this.score;
    }

    public void setCustomThresholds(List<Double> customThresholds) {
        this.customThresholds = customThresholds;
    }
}
