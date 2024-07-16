package com.duan2.tournamentTDTU.fileUpload;

import com.duan2.tournamentTDTU.models.User;
import com.duan2.tournamentTDTU.services.SchoolYearService;
import com.duan2.tournamentTDTU.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AvatarUpload implements AvatarUploadRepository{
    @Value("${file.avatars}")
    private String avatarsPath;

    @Autowired
    private SchoolYearService schoolYearService;

    @Autowired
    private UserService userService;

    @Override
    public void save(MultipartFile file) {
        String dir = System.getProperty("user.dir")+"/"+avatarsPath;

//        String newFileName = changeAvatarFileName(file);

        try{
            Path uploadPath = Paths.get(dir);
            Path filePath = uploadPath.resolve(changeAvatarFileName(file));
            file.transferTo(filePath.toFile());
        }
        catch (IOException e){
            System.out.println(e.getMessage());
//            throw new RuntimeException(e);
        }
    }

    public String changeAvatarFileName(MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User user = userService.getUserById(username);

            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String newFileName = "Avatar_"+user.getID()+ fileExtension;
            return newFileName;
        }
        return "";
    }
}
