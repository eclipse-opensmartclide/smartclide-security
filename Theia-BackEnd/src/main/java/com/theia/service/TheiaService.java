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


        //System.out.println("home = " +System.getProperty("user.home"));
        //File dir = new File("/home/upload/" + id.toString());
        String hashCode = "";
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
        //System.out.println(name);
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
                Git some = Git.cloneRepository()
                        .setURI(url)
                        .setDirectory(Paths.get("/home/upload/" + name).toFile())
                        .call();

                //Ref head = retrieveGithubCode().substring().getRepository().exactRef("HEAD");

//            Ref head = some.getRepository().exactRef("HEAD");
                Ref headhash = some.getRepository().getAllRefs().get("HEAD");

                // System.out.println("Ref of HEAD: " + headhash + ": " + headhash.getName() + " - " + headhash.getObjectId().getName());
                hashCode = headhash.getObjectId().getName();


               System.out.println("Hash " + hashCode);

                //System.out.println(some.getRepository().getBranch());

                System.out.println("Completed Cloning");
            } catch (GitAPIException e) {
                System.out.println("Exception occurred while cloning repo");
                e.printStackTrace();
            }
            return name;
        }
    }
}

