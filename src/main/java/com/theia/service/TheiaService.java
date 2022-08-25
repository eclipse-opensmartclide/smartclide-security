package com.theia.service;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TheiaService {

    public String retrieveGithubCode(String url, UUID id) throws IOException {

        //Get hashcode of latest commit of git repository and download the repository

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = restTemplate.exchange(url +"/info/refs?service=git-upload-pack",
                HttpMethod.GET,
                request,
                String.class
        ) ;

        String name= "";
        name = response.getBody();

        Pattern pattern = Pattern.compile("(.*) (refs\\/heads\\/master)");
        Pattern pattern2 = Pattern.compile("(.*) (refs\\/heads\\/main)");

        Matcher matcher = pattern.matcher(name);
        Matcher matcher2 = pattern2.matcher(name);



        if (matcher.find())
        {
            name =matcher.group(1);
        }
        else if(matcher2.find()){
            name  = matcher2.group(1);

        }
        else{
            name = id.toString();
        }

        Path path = Path.of("/home/upload/"+name);

        if (Files.exists(path)) {

            return name;

        }
        else {


            try {

                System.out.println("Cloning " + url + " into " + id.toString());
                Git repo = Git.cloneRepository()
                        .setURI(url)
                        .setDirectory(Paths.get("/home/upload/" + name).toFile())
                        .call();

                System.out.println("Completed Cloning");
            } catch (GitAPIException e) {
                System.out.println("Exception occurred while cloning repo");
                e.printStackTrace();
            }
            return name;
        }
    }
}
