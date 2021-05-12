package com.theia.service;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class TheiaService {

    public String retrieveGithubCode(String url) throws IOException {
        String cloneDirectoryPath = url.replace("https://github.com/", "");
        cloneDirectoryPath = cloneDirectoryPath.replace("/", "_");
        cloneDirectoryPath = Path.of("").toAbsolutePath().toString() + "/upload/" + cloneDirectoryPath;
        File dir = new File(cloneDirectoryPath);

        if(dir.exists()){
            FileUtils.deleteDirectory(dir);
        }

        try {
            System.out.println("Cloning "+ url +" into "+cloneDirectoryPath);
            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(Paths.get(cloneDirectoryPath).toFile())
                    .call();
            System.out.println("Completed Cloning");
        } catch (GitAPIException e) {
            System.out.println("Exception occurred while cloning repo");
            e.printStackTrace();
        }

        return cloneDirectoryPath;
    }
}
