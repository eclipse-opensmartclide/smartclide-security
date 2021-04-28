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

        String basePath = "/home/anasmarg/Desktop/upload/";

        if(zip.getOriginalFilename() == null || zip.isEmpty()){
            return "";
        }


        ZipInputStream inputStream = new ZipInputStream(zip.getInputStream());
        Path path = Paths.get(makeDir(basePath + dir));

        for(ZipEntry zipEntry; (zipEntry = inputStream.getNextEntry()) != null; ){
            Path resolvedPath = path.resolve(zipEntry.getName());
            if(!zipEntry.isDirectory()){
                Files.createDirectories(resolvedPath.getParent());
                Files.copy(inputStream, resolvedPath);
            }else{
                Files.createDirectories(resolvedPath);
            }
        }


        return basePath + dir;

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
