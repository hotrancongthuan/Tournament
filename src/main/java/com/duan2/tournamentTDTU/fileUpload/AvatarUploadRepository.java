package com.duan2.tournamentTDTU.fileUpload;

import org.springframework.web.multipart.MultipartFile;

public interface AvatarUploadRepository {
    void save(MultipartFile file);
}
