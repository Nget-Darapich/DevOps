package com.example.idcardmanager.service;

import com.example.idcardmanager.model.Template;
import com.example.idcardmanager.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    /**
     * Retrieve all available templates.
     */
    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    /**
     * Find a template by its unique code.
     */
    public Optional<Template> findByCode(String code) {
        return templateRepository.findByCode(code);
    }

    /**
     * Find a template by its ID.
     */
    public Optional<Template> findById(Long id) {
        return templateRepository.findById(id);
    }

    /**
     * Create or update a template.
     */
    public Template saveTemplate(Template template) {
        return templateRepository.save(template);
    }

    /**
     * Check if a template code already exists.
     */
    public boolean codeExists(String code) {
        return templateRepository.existsByCode(code);
    }

    /**
     * Delete a template by ID.
     */
    public void deleteTemplate(Long id) {
        templateRepository.deleteById(id);
    }

    /**
     * Initialize default templates if none exist.
     */
    public void initializeDefaultTemplates() {
        if (templateRepository.count() == 0) {
            // Professional Blue Template
            Template professionalBlue = Template.builder()
                    .code("PROF_BLUE")
                    .name("Professional Blue")
                    .organizationName("Organization")
                    .layout("VERTICAL")
                    .primaryColor("#1d4ed8")
                    .secondaryColor("#e0e7ff")
                    .textColor("#111827")
                    .tagline("Digital Identity Card")
                    .build();

            // Corporate Red Template
            Template corporateRed = Template.builder()
                    .code("CORP_RED")
                    .name("Corporate Red")
                    .organizationName("Corporation")
                    .layout("VERTICAL")
                    .primaryColor("#dc2626")
                    .secondaryColor("#fee2e2")
                    .textColor("#111827")
                    .tagline("Official ID Card")
                    .build();

            // Modern Green Template
            Template modernGreen = Template.builder()
                    .code("MOD_GREEN")
                    .name("Modern Green")
                    .organizationName("Enterprise")
                    .layout("VERTICAL")
                    .primaryColor("#059669")
                    .secondaryColor("#d1fae5")
                    .textColor("#111827")
                    .tagline("Smart Card System")
                    .build();

            templateRepository.save(professionalBlue);
            templateRepository.save(corporateRed);
            templateRepository.save(modernGreen);
        }
    }
}
