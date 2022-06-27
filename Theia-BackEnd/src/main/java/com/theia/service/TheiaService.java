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
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class TheiaService {

    public File retrieveGithubCode(String url, UUID id) throws IOException {

        File dir = new File(System.getProperty("HOME") + "/upload/" + id.toString());

        if(dir.exists()){
            FileUtils.deleteDirectory(dir);
        }

        try {
            System.out.println("Cloning " + url +" into " + id.toString());
            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(Paths.get(System.getProperty("HOME") + "/upload/" + id.toString()).toFile())
                    .call();
            System.out.println("Completed Cloning");
        } catch (GitAPIException e) {
            System.out.println("Exception occurred while cloning repo");
            e.printStackTrace();
        }
        return dir;
    }
}
