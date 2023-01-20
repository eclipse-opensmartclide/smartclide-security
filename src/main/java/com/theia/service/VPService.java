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


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Optional;

@Service
public class VPService {

    public String vulnerabilityPrediction(String url, String language, Optional<String> username){
        HashMap<String, Double> vp = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity request = new HttpEntity(headers);

        String requestsURL = "";
        if (username.isPresent()){
            requestsURL=   "http://160.40.52.130:5008/VulnerabilityAssessment?project=" + url + "&lang=" + language + "&user_name="+username;
        }
        else {
            requestsURL=   "http://160.40.52.130:5008/VulnerabilityAssessment?project=" + url + "&lang=" + language ;
        }
        ResponseEntity<String> response = restTemplate.exchange(requestsURL,
                HttpMethod.GET,
                request,
                String.class
        );


        String json = response.getBody();
//        JSONObject object = new JSONObject(json);
//        JSONArray results = object.getJSONArray("results");
//        for(int i = 0; i < results.length(); i++){
//            JSONObject file = (JSONObject) results.get(i);
//            if(file.get("vulnerability_flag").equals(1)){
//                vp.put((String) file.get("file_path"), 1d);
//            }else{
//                vp.put((String) file.get("file_path"), 0d);
//            }
//        }
        return json;
    }
}
