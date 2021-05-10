package com.theia.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class DevSkimService {

//  Analyzing project with DevSkim tool and producing Devskim report.
    public void generateDevSkimValues(String path) throws IOException {
        Process process = Runtime.getRuntime().exec("devskim analyze " + path + " " + path +"devskim.txt -f json");

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while(true){
                line = r.readLine();
                if(line == null){
                    break;
                }
            }
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}
