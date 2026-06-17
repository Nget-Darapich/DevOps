package com.example.idcardmanager.model;
import java.util.UUID;
public class ProfileBuilder {
    public static Profile createDefault(String fullName, ProfileType type, Template template) {
        String uniqueReg = java.time.Year.now().getValue() + "-" + type.name().substring(0, 3) + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        
        return Profile.builder()
                .uuid(UUID.randomUUID().toString())
                .registrationNumber(uniqueReg)
                .fullName(fullName)
                .type(type)
                .template(template)
                .barcodeType(BarcodeType.CODE_128)
                .build();
    }
}
