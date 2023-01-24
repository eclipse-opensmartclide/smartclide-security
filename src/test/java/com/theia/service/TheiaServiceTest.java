package com.theia.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class TheiaServiceTest {

    @Autowired
    TheiaService theiaService;

    @Test
    void retrieveGithubCode() {
        String url = "https://github.com/spring-projects/spring-mvc-showcase";
        UUID id = UUID.randomUUID();
        Pattern pattern = Pattern.compile("(\\/)(?!.*\\1)(.*)(.git)");
        Matcher matcher = pattern.matcher(url);
        String name = "";

        if (matcher.find()) {
            name = matcher.group(2);
        }
        try {
            boolean downloaded= this.theiaService.retrieveGithubCode(url, id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File directory = new File("/home/upload/"+name);
        assertTrue(directory.exists(), "Directory created!");
    }
}