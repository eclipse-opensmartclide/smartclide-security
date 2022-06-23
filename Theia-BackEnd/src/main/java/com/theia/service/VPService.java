package com.theia.service;

import org.apache.commons.collections.map.HashedMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Iterator;

@Service
public class VPService {

    public HashMap<String, Double> vulnerabilityPrediction(String url, String language){
        HashMap<String, Double> vp = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange("http://iti-724.iti.gr:5002/Security/VulnerabilityAssessment?project_path=" + url + "&language=" + language,
                HttpMethod.GET,
                request,
                String.class
        );

        String json = response.getBody();
        JSONObject object = new JSONObject(json);
        JSONArray results = object.getJSONArray("results");
        for(int i = 0; i < results.length(); i++){
            JSONObject file = (JSONObject) results.get(i);
            if(file.get("vulnerability_flag").equals(1)){
                vp.put((String) file.get("file_path"), 1d);
            }else{
                vp.put((String) file.get("file_path"), 0d);
            }
        }
        return vp;
    }
}
