package com.duan2.tournamentTDTU.fileUpload;

import com.duan2.tournamentTDTU.models.SchoolYear;
import com.duan2.tournamentTDTU.services.SchoolYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class FileService implements FileRepository {
    @Value("${file.path}")
    private String filePath;

    @Autowired
    private SchoolYearService schoolYearService;

    private String generateFileName(MultipartFile file, String fileName) {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = fileName + fileExtension;
        return newFileName;
    }

    @Override
    public void save(MultipartFile file, String fileName) {
        String dir = System.getProperty("user.dir")+"/"+filePath;

        String newFileName = generateFileName(file,fileName);

        try{
            Path uploadPath = Paths.get(dir);
            Path filePath = uploadPath.resolve(newFileName);

            file.transferTo(filePath.toFile());
        }
        catch (IOException e){
            System.out.println(e.getMessage());
//            throw new RuntimeException(e);
        }
    }






}
