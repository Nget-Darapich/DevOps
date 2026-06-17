-- Initialize ID Card Database
-- Database: A-NGET_Darapich-db

-- Use the created database
USE `A-NGET_Darapich-db`;

-- Create templates table (must be created before profiles due to FK constraint)
CREATE TABLE IF NOT EXISTS `templates` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `code` VARCHAR(60) NOT NULL UNIQUE,
    `name` VARCHAR(80) NOT NULL,
    `organization_name` VARCHAR(120),
    `layout` VARCHAR(20) NOT NULL,
    `primary_color` VARCHAR(7) NOT NULL,
    `secondary_color` VARCHAR(7) NOT NULL,
    `text_color` VARCHAR(7) NOT NULL,
    `tagline` VARCHAR(255),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_code` (`code`)
);

-- Insert default templates
INSERT INTO `templates` (`code`, `name`, `organization_name`, `layout`, `primary_color`, `secondary_color`, `text_color`, `tagline`)
VALUES
    ('PROF_BLUE', 'Professional Blue', 'Organization', 'VERTICAL', '#1d4ed8', '#e0e7ff', '#111827', 'Digital Identity Card'),
    ('CORP_RED', 'Corporate Red', 'Corporation', 'VERTICAL', '#dc2626', '#fee2e2', '#111827', 'Official ID Card'),
    ('MOD_GREEN', 'Modern Green', 'Enterprise', 'VERTICAL', '#059669', '#d1fae5', '#111827', 'Smart Card System')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- Create profiles table
CREATE TABLE IF NOT EXISTS `profiles` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `uuid` VARCHAR(36) NOT NULL UNIQUE,
    `registration_number` VARCHAR(64) NOT NULL UNIQUE,
    `type` VARCHAR(16) NOT NULL,
    `full_name` VARCHAR(120) NOT NULL,
    `department` VARCHAR(80),
    `title` VARCHAR(120),
    `email` VARCHAR(120),
    `phone` VARCHAR(40),
    `blood_group` VARCHAR(60),
    `date_of_birth` DATE,
    `issue_date` DATE,
    `expiry_date` DATE,
    `photo_file_name` VARCHAR(255),
    `photo_content_type` VARCHAR(60),
    `template_id` BIGINT,
    `barcode_type` VARCHAR(16),
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_profile_reg_number` UNIQUE (`registration_number`),
    FOREIGN KEY (`template_id`) REFERENCES `templates`(`id`)
);

-- Note: File uploads directory is created by the application at runtime
