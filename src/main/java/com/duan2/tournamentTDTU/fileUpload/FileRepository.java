package com.duan2.tournamentTDTU.fileUpload;

import org.springframework.web.multipart.MultipartFile;

public interface FileRepository {

    void save(MultipartFile file, String fileName);
}
