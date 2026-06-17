package com.example.idcardmanager.service;

import com.example.idcardmanager.model.Profile;
import com.example.idcardmanager.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    @Value("${app.upload.dir}")
    private String uploadDir;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile registerProfile(Profile profile, MultipartFile photoFile) throws Exception {
        // 1. Validation & Local File Handling
        if (photoFile != null && !photoFile.isEmpty()) {
            String contentType = photoFile.getContentType();
            if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
                throw new IllegalArgumentException("Only JPEG and PNG file types are accepted.");
            }
            
            File directory = new File(uploadDir);
            if (!directory.exists()) directory.mkdirs();

            String uniqueFileName = UUID.randomUUID() + "_" + photoFile.getOriginalFilename();
            Path path = Paths.get(uploadDir + uniqueFileName);
            Files.write(path, photoFile.getBytes());

            profile.setPhotoFileName(uniqueFileName);
            profile.setPhotoContentType(contentType);
        }

        // 2. Format Custom Registration Number if omitted (YEAR-DEPT-RANDOM)
        if (profile.getRegistrationNumber() == null || profile.getRegistrationNumber().isBlank()) {
            String year = String.valueOf(java.time.Year.now().getValue());
            String dept = profile.getDepartment() != null ? profile.getDepartment().toUpperCase() : "GEN";
            String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            profile.setRegistrationNumber(year + "-" + dept + "-" + uniqueSuffix);
        }

        if (profile.getUuid() == null) {
            profile.setUuid(UUID.randomUUID().toString());
        }

        return profileRepository.save(profile);
    }
}