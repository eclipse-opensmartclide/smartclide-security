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

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Service
public class FileUtilService {

    public String saveFolder(MultipartFile zip, String dir) throws IOException {

        //String basePath = Path.of("").toAbsolutePath().toString() + "/home/upload/";
        String basePath = "/home/upload/zip/";

        if(zip.getOriginalFilename() == null || zip.isEmpty()){
            return "";
        }


        ZipInputStream inputStream = new ZipInputStream(zip.getInputStream());
        Path path = Paths.get(makeDir(basePath + dir));

        Path ReturnPath =  path;
        int i=0;
        for(ZipEntry zipEntry; (zipEntry = inputStream.getNextEntry()) != null; ){
            if (i==0){
                ReturnPath = path.resolve(zipEntry.getName());
                i++;
            }
            Path resolvedPath = path.resolve(zipEntry.getName());
            if(!zipEntry.isDirectory()){
                Files.createDirectories(resolvedPath.getParent());
                Files.copy(inputStream, resolvedPath);
            }else{
                Files.createDirectories(resolvedPath);
            }
        }


        return ReturnPath.toString();

    }

    private static String  makeDir(String filePath) {

        String dirPath = "";
        if (filePath.lastIndexOf('/') > 0) {
            dirPath = filePath.substring(0, filePath.lastIndexOf('/'));
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }else{
               deleteFolder(dir);
               dir.mkdirs();
            }
        }

        return dirPath;
    }

    public static void deleteFolder(File file){
        for (File subFile : file.listFiles()) {
            if(subFile.isDirectory()) {
                deleteFolder(subFile);
            } else {
                subFile.delete();
            }
        }
        file.delete();
    }
}
