package com.example.idcardmanager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Application configuration properties.
 * Maps custom application properties from application.properties files.
 */
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Directory path for uploading and storing profile photos.
     * Example: uploads/photos/
     */
    private String uploadDir = "uploads/photos/";

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
