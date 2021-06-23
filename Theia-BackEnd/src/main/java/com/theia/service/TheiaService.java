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

        File dir = new File(System.getProperty("user.dir") + "/upload/" + id.toString());

        if(dir.exists()){
            FileUtils.deleteDirectory(dir);
        }

        try {
            System.out.println("Cloning " + url +" into " + id.toString());
            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(Paths.get(System.getProperty("user.dir") + "/upload/" + id.toString()).toFile())
                    .call();
            System.out.println("Completed Cloning");
        } catch (GitAPIException e) {
            System.out.println("Exception occurred while cloning repo");
            e.printStackTrace();
        }
        return dir;
    }
}
