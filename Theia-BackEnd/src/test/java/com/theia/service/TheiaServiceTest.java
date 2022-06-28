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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

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
        File dir = null;
        try {
            dir = this.theiaService.retrieveGithubCode(url, id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(dir.exists(), "Directory created!");
    }
}
