package com.theia.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Property {

    private String name;
    private Float value;
    private HashMap<String, Float> thresholds = new HashMap<>();
    private Float score;
    private List<Float> customThresholds = new ArrayList<>();

    public Property(String name, Float value) {
        this.name = name;
        this.value = value;
    }

    public void setThresholds(HashMap<String, Float> thresholds){
        this.thresholds = thresholds;
    }


    public Float calculateScore(){
        if (this.value <= thresholds.get("min")){
            this.score = 1f;
        }else if(this.value <= thresholds.get("average")){
            this.score = 1 - (0.5f / (thresholds.get("average") - thresholds.get("min"))) * (this.value - thresholds.get("min"));
        }else if(this.value < this.thresholds.get("max")){
            this.score = (0.5f / (thresholds.get("max") - thresholds.get("average"))) * (thresholds.get("max") - this.value);
        }else{
            this.score = 0f;
        }

        return this.score;
    }

    public Float calculateCustomScore(){
        if (this.value <= customThresholds.get(0)){
            this.score = 1f;
        }else if(this.value <= customThresholds.get(1)){
            this.score = 1 - (0.5f / (customThresholds.get(1) - customThresholds.get(0))) * (this.value - customThresholds.get(0));
        }else if(this.value < this.customThresholds.get(2)){
            this.score = (0.5f / (customThresholds.get(2) - customThresholds.get(1))) * (customThresholds.get(2) - this.value);
        }else{
            this.score = 0f;
        }

        return this.score;
    }

    public void setCustomThresholds(List<Float> customThresholds) {
        this.customThresholds = customThresholds;
    }
}
