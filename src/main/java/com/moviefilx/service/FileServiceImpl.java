package com.moviefilx.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl  implements FileService{

    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        //get name of the file to upload
        String filename = file.getOriginalFilename();

        //to get the file path
        String filePath = path + File.separator + filename;

        // create file object
        File f = new File(path);
        if(!f.exists()) {
            f.mkdir();
        }

        //copy the file and upload file to the path. If file already present it will replace with new file.
        //Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        //copy the file and upload file to the path.
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return filename;
    }

    @Override
    public InputStream getResourceFile(String path, String filename) throws FileNotFoundException {
        String filePath = path + File.separator + filename;
        return new FileInputStream(filePath);
    }
}
