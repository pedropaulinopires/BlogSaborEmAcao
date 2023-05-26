package com.saboremacao.blog.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
@Service
public class UploadImage {

    //    private static final String DIRECTORY = "C:\\Users\\Usuario\\Documents\\IntelljProjects\\SaborEmAcao-Blog\\src\\main\\resources\\static\\images-revenue";
    private static final String DIRECTORY = "C:\\Users\\Usuario\\Documents\\GitHub\\SaborEmAcao-Blog\\src\\main\\resources\\static\\images-revenue";


    public static boolean uploadImage(MultipartFile file, String id) {
        boolean successUpload = false;
        if (!file.isEmpty()) {
            String nameFile = file.getOriginalFilename();
            try {
                if (!file.isEmpty()) {
                    byte[] bytes = file.getBytes();
                    Path caminho = Paths.get(DIRECTORY + File.separator + String.valueOf(id) + file.getOriginalFilename());
                    Files.write(caminho, bytes);
                    successUpload = true;

                }
            } catch (IOException e) {
                e.printStackTrace();
                successUpload = false;
            }

        } else {
            log.info("erro save file image");
            successUpload = false;
        }
        return successUpload;
    }

    public static boolean deleteImage(String filename) {
        try {
            Path path = Paths.get(DIRECTORY + File.separator + filename);
            Files.deleteIfExists(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
