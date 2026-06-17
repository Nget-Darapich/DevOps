package com.example.idcardmanager;

import com.example.idcardmanager.model.*;
import com.example.idcardmanager.repository.ProfileRepository;
import com.example.idcardmanager.repository.TemplateRepository;
import com.example.idcardmanager.service.ProfileService;
import com.example.idcardmanager.service.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Profile and Template persistence.
 */
@DataJpaTest
@Import({ProfileService.class, TemplateService.class})
@ActiveProfiles("test")
public class ProfileAndTemplateTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateService templateService;

    private Template defaultTemplate;

    @BeforeEach
    public void setup() {
        // Create a default template for testing
        defaultTemplate = Template.builder()
                .code("TEST_TEMPLATE")
                .name("Test Template")
                .organizationName("Test Org")
                .layout("VERTICAL")
                .primaryColor("#1d4ed8")
                .secondaryColor("#e0e7ff")
                .textColor("#111827")
                .tagline("Test Card")
                .build();
        templateRepository.save(defaultTemplate);
    }

    // ============ Template Repository Tests ============

    @Test
    public void testTemplateSaveAndRetrieve() {
        // Arrange & Act
        Optional<Template> retrieved = templateRepository.findByCode("TEST_TEMPLATE");

        // Assert
        assertTrue(retrieved.isPresent());
        assertEquals("Test Org", retrieved.get().getOrganizationName());
        assertEquals("#1d4ed8", retrieved.get().getPrimaryColor());
    }

    @Test
    public void testTemplateCodeUniqueness() {
        // Arrange
        Template duplicate = Template.builder()
                .code("TEST_TEMPLATE")
                .name("Duplicate")
                .organizationName("Org")
                .layout("VERTICAL")
                .primaryColor("#dc2626")
                .secondaryColor("#fee2e2")
                .textColor("#111827")
                .build();

        // Act & Assert - Should throw constraint violation
        assertThrows(Exception.class, () -> {
            templateRepository.save(duplicate);
            templateRepository.flush();
        });
    }

    @Test
    public void testTemplateCodeExists() {
        // Act
        boolean exists = templateRepository.existsByCode("TEST_TEMPLATE");

        // Assert
        assertTrue(exists);
    }

    @Test
    public void testTemplateCodeNotExists() {
        // Act
        boolean exists = templateRepository.existsByCode("NON_EXISTENT");

        // Assert
        assertFalse(exists);
    }

    // ============ Profile Repository Tests ============

    @Test
    public void testProfileCreationWithBuilder() {
        // Arrange
        Profile profile = ProfileBuilder.createDefault("John Doe", ProfileType.EMPLOYEE, defaultTemplate);

        // Act
        Profile saved = profileRepository.save(profile);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("John Doe", saved.getFullName());
        assertEquals(ProfileType.EMPLOYEE, saved.getType());
        assertNotNull(saved.getUuid());
        assertNotNull(saved.getRegistrationNumber());
        assertTrue(saved.getRegistrationNumber().contains("EMP"));
    }

    @Test
    public void testProfileFindByUuid() {
        // Arrange
        Profile profile = ProfileBuilder.createDefault("Jane Smith", ProfileType.STUDENT, defaultTemplate);
        Profile saved = profileRepository.save(profile);

        // Act
        Optional<Profile> retrieved = profileRepository.findByUuid(saved.getUuid());

        // Assert
        assertTrue(retrieved.isPresent());
        assertEquals("Jane Smith", retrieved.get().getFullName());
        assertEquals(ProfileType.STUDENT, retrieved.get().getType());
    }

    @Test
    public void testProfileFindByRegistrationNumber() {
        // Arrange
        Profile profile = Profile.builder()
                .uuid("test-uuid-001")
                .registrationNumber("2026-TEST-0001")
                .fullName("Test User")
                .type(ProfileType.USER)
                .template(defaultTemplate)
                .build();
        profileRepository.save(profile);

        // Act
        Optional<Profile> retrieved = profileRepository.findByRegistrationNumber("2026-TEST-0001");

        // Assert
        assertTrue(retrieved.isPresent());
        assertEquals("Test User", retrieved.get().getFullName());
    }

    @Test
    public void testProfileRegistrationNumberUniqueness() {
        // Arrange
        Profile profile1 = Profile.builder()
                .uuid("uuid-1")
                .registrationNumber("2026-UNIQUE-0001")
                .fullName("User 1")
                .type(ProfileType.EMPLOYEE)
                .template(defaultTemplate)
                .build();

        Profile profile2 = Profile.builder()
                .uuid("uuid-2")
                .registrationNumber("2026-UNIQUE-0001")
                .fullName("User 2")
                .type(ProfileType.EMPLOYEE)
                .template(defaultTemplate)
                .build();

        profileRepository.save(profile1);

        // Act & Assert - Should throw constraint violation
        assertThrows(Exception.class, () -> {
            profileRepository.save(profile2);
            profileRepository.flush();
        });
    }

    @Test
    public void testProfileSearchByName() {
        // Arrange
        Profile profile1 = Profile.builder()
                .uuid("uuid-1")
                .registrationNumber("2026-EMP-0001")
                .fullName("John Developer")
                .type(ProfileType.EMPLOYEE)
                .template(defaultTemplate)
                .build();

        Profile profile2 = Profile.builder()
                .uuid("uuid-2")
                .registrationNumber("2026-EMP-0002")
                .fullName("Jane Manager")
                .type(ProfileType.EMPLOYEE)
                .template(defaultTemplate)
                .build();

        profileRepository.save(profile1);
        profileRepository.save(profile2);

        // Act
        var results = profileRepository.findByFullNameContainingIgnoreCase("john");

        // Assert
        assertEquals(1, results.size());
        assertEquals("John Developer", results.get(0).getFullName());
    }

    @Test
    public void testProfilePrePersistAudit() {
        // Arrange
        Profile profile = Profile.builder()
                .uuid("uuid-audit")
                .registrationNumber("2026-AUD-0001")
                .fullName("Audit Test")
                .type(ProfileType.USER)
                .template(defaultTemplate)
                .build();

        // Act
        Profile saved = profileRepository.save(profile);

        // Assert
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        assertEquals(saved.getCreatedAt(), saved.getUpdatedAt());
        assertNotNull(saved.getIssueDate());
    }

    @Test
    public void testProfileCompleteData() {
        // Arrange
        LocalDate birthDate = LocalDate.of(1995, 5, 15);
        LocalDate issueDate = LocalDate.of(2024, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        Profile profile = Profile.builder()
                .uuid("uuid-complete")
                .registrationNumber("2026-COM-0001")
                .fullName("Complete Profile")
                .type(ProfileType.EMPLOYEE)
                .department("Engineering")
                .title("Senior Developer")
                .email("dev@example.com")
                .phone("+1-555-0123")
                .bloodGroup("O+")
                .dateOfBirth(birthDate)
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .photoFileName("photo_001.jpg")
                .photoContentType("image/jpeg")
                .template(defaultTemplate)
                .barcodeType(BarcodeType.CODE_128)
                .build();

        // Act
        Profile saved = profileRepository.save(profile);
        Optional<Profile> retrieved = profileRepository.findByUuid("uuid-complete");

        // Assert
        assertTrue(retrieved.isPresent());
        assertEquals("Engineering", retrieved.get().getDepartment());
        assertEquals("Senior Developer", retrieved.get().getTitle());
        assertEquals("O+", retrieved.get().getBloodGroup());
        assertEquals(birthDate, retrieved.get().getDateOfBirth());
        assertTrue(retrieved.get().hasPhoto());
    }

    @Test
    public void testProfileNoPhotoInitially() {
        // Arrange
        Profile profile = Profile.builder()
                .uuid("uuid-no-photo")
                .registrationNumber("2026-NOF-0001")
                .fullName("No Photo User")
                .type(ProfileType.USER)
                .template(defaultTemplate)
                .build();

        // Act & Assert
        assertFalse(profile.hasPhoto());
    }

    @Test
    public void testProfileRegistrationNumberExistence() {
        // Arrange
        Profile profile = Profile.builder()
                .uuid("uuid-exist")
                .registrationNumber("2026-EXI-0001")
                .fullName("Existing User")
                .type(ProfileType.EMPLOYEE)
                .template(defaultTemplate)
                .build();
        profileRepository.save(profile);

        // Act
        boolean exists = profileRepository.existsByRegistrationNumber("2026-EXI-0001");
        boolean notExists = profileRepository.existsByRegistrationNumber("2026-NOT-9999");

        // Assert
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    public void testTemplateServiceInitializeDefaults() {
        // Arrange
        templateRepository.deleteAll();

        // Act
        templateService.initializeDefaultTemplates();

        // Assert
        assertEquals(3, templateRepository.count());
        assertTrue(templateRepository.findByCode("PROF_BLUE").isPresent());
        assertTrue(templateRepository.findByCode("CORP_RED").isPresent());
        assertTrue(templateRepository.findByCode("MOD_GREEN").isPresent());
    }
}

    // ============ Template Repository Tests ============

    @Test
    public void testTemplateSaveAndRetrieve() {
        // Arrange & Act
        Optional<Template> retrieved = templateRepository.findByCode("TEST_TEMPLATE");

        // Assert
        assertTrue(retrieved.isPresent());
        assertEquals("Test Org", retrieved.get().getOrganizationName());
        assertEquals("#1d4ed8", retrieved.get().getPrimaryColor());
    }

    @Test
    public void testTemplateCodeUniqueness() {
        // Arrange
        Template duplicate = Template.builder()
                .code("TEST_TEMPLATE")
                .name("Duplicate")
                .organizationName("Org")
                .layout("VERTICAL")
                .primaryColor("#dc2626")
                .secondaryColor("#fee2e2")
                .textColor("#111827")
                .build();

        // Act & Assert - Should throw constraint violation
        assertThrows(Exception.class, () -> {
            templateRepository.save(duplicate);
            templateRepository.flush();
        });
    }

    @Test
    public void testTemplateCodeExists() {
        // Act
        boolean exists = templateRepository.existsByCode("TEST_TEMPLATE");

        // Assert
        assertTrue(exists);
    }

    @Test
    public void testTemplateCodeNotExists() {
        // Act
        boolean exists = templateRepository.existsByCode("NON_EXISTENT");

        // Assert
        assertFalse(exists);
    }

    // ============ Profile Repository Tests ============

    @Test
    public void testProfileCreationWithBuilder() {
        // Arrange
        Profile profile = ProfileBuilder.createDefault("John Doe", ProfileType.EMPLOYEE, defaultTemplate);

        // Act
        Profile saved = profileRepository.save(profile);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("John Doe", saved.getFullName());
        assertEquals(ProfileType.EMPLOYEE, saved.getType());
        assertNotNull(saved.getUuid());
        assertNotNull(saved.getRegistrationNumber());
        assertTrue(saved.getRegistrationNumber().contains("EMP"));
    }

    @Test
    public void testProfileFindByUuid() {
        // Arrange
        Profile profile = ProfileBuilder.createDefault("Jane Smith", ProfileType.STUDENT, defaultTemplate);
        Profile saved = profileRepository.save(profile);

        // Act
        Optional<Profile> retrieved = profileRepository.findByUuid(saved.getUuid());

        // Assert
        assertTrue(retrieved.isPresent());
        assertEquals("Jane Smith", retrieved.get().getFullName());
        assertEquals(ProfileType.STUDENT, retrieved.get().getType());
    }

    @Test
    public void testProfileFindByRegistrationNumber() {
        // Arrange
        Profile profile = Profile.builder()
                .uuid("test-uuid-001")
                .registrationNumber("2026-TEST-0001")
                .fullName("Test User")
                .type(ProfileType.USER)
                .template(defaultTemplate)
                .build();
        profileRepository.save(profile);

        // Act
        Optional<Profile> retrieved = profileRepository.findByRegistrationNumber("2026-TEST-0001");

        // Assert
        assertTrue(retrieved.isPresent());
        assertEquals("Test User", retrieved.get().getFullName());
    }

    @Test
    public void testProfileRegistrationNumberUniqueness() {
        // Arrange
        Profile profile1 = Profile.builder()
                .uuid("uuid-1")
                .registrationNumber("2026-UNIQUE-0001")
                .fullName("User 1")
                .type(ProfileType.EMPLOYEE)
                .template(defaultTemplate)
                .build();

        Profile profile2 = Profile.builder()
                .uuid("uuid-2")
                .registrationNumber("2026-UNIQUE-0001")
                .fullName("User 2")
                .type(ProfileType.EMPLOYEE)
                .template(defaultTemplate)
                .build();

        profileRepository.save(profile1);

        // Act & Assert - Should throw constraint violation
        assertThrows(Exception.class, () -> {
            profileRepository.save(profile2);
            profileRepository.flush();
        });
    }

    @Test
    public void testProfileSearchByName() {
        // Arrange
        Profile profile1 = Profile.builder()
                .uuid("uuid-1")
                .registrationNumber("2026-EMP-0001")
                .fullName("John Developer")
                .type(ProfileType.EMPLOYEE)
                .template(defaultTemplate)
                .build();

        Profile profile2 = Profile.builder()
                .uuid("uuid-2")
                .registrationNumber("2026-EMP-0002")
                .fullName("Jane Manager")
                .type(ProfileType.EMPLOYEE)
                .template(defaultTemplate)
                .build();

        profileRepository.save(profile1);
        profileRepository.save(profile2);

        // Act
        var results = profileRepository.findByFullNameContainingIgnoreCase("john");

        // Assert
        assertEquals(1, results.size());
        assertEquals("John Developer", results.get(0).getFullName());
    }

    @Test
    public void testProfilePrePersistAudit() {
        // Arrange
        Profile profile = Profile.builder()
                .uuid("uuid-audit")
                .registrationNumber("2026-AUD-0001")
                .fullName("Audit Test")
                .type(ProfileType.USER)
                .template(defaultTemplate)
                .build();

        // Act
        Profile saved = profileRepository.save(profile);

        // Assert
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        assertEquals(saved.getCreatedAt(), saved.getUpdatedAt());
        assertNotNull(saved.getIssueDate());
    }

    @Test
    public void testProfileCompleteData() {
        // Arrange
        LocalDate birthDate = LocalDate.of(1995, 5, 15);
        LocalDate issueDate = LocalDate.of(2024, 1, 1);
        LocalDate expiryDate = LocalDate.of(2027, 1, 1);

        Profile profile = Profile.builder()
                .uuid("uuid-complete")
                .registrationNumber("2026-COM-0001")
                .fullName("Complete Profile")
                .type(ProfileType.EMPLOYEE)
                .department("Engineering")
                .title("Senior Developer")
                .email("dev@example.com")
                .phone("+1-555-0123")
                .bloodGroup("O+")
                .dateOfBirth(birthDate)
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .photoFileName("photo_001.jpg")
                .photoContentType("image/jpeg")
                .template(defaultTemplate)
                .barcodeType(BarcodeType.CODE_128)
                .build();

        // Act
        Profile saved = profileRepository.save(profile);
        Optional<Profile> retrieved = profileRepository.findByUuid("uuid-complete");

        // Assert
        assertTrue(retrieved.isPresent());
        assertEquals("Engineering", retrieved.get().getDepartment());
        assertEquals("Senior Developer", retrieved.get().getTitle());
        assertEquals("O+", retrieved.get().getBloodGroup());
        assertEquals(birthDate, retrieved.get().getDateOfBirth());
        assertTrue(retrieved.get().hasPhoto());
    }

    @Test
    public void testProfileNoPhotoInitially() {
        // Arrange
        Profile profile = Profile.builder()
                .uuid("uuid-no-photo")
                .registrationNumber("2026-NOF-0001")
                .fullName("No Photo User")
                .type(ProfileType.USER)
                .template(defaultTemplate)
                .build();

        // Act & Assert
        assertFalse(profile.hasPhoto());
    }

    @Test
    public void testProfileRegistrationNumberExistence() {
        // Arrange
        Profile profile = Profile.builder()
                .uuid("uuid-exist")
                .registrationNumber("2026-EXI-0001")
                .fullName("Existing User")
                .type(ProfileType.EMPLOYEE)
                .template(defaultTemplate)
                .build();
        profileRepository.save(profile);

        // Act
        boolean exists = profileRepository.existsByRegistrationNumber("2026-EXI-0001");
        boolean notExists = profileRepository.existsByRegistrationNumber("2026-NOT-9999");

        // Assert
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    public void testTemplateServiceInitializeDefaults() {
        // Arrange
        templateRepository.deleteAll();

        // Act
        templateService.initializeDefaultTemplates();

        // Assert
        assertEquals(3, templateRepository.count());
        assertTrue(templateRepository.findByCode("PROF_BLUE").isPresent());
        assertTrue(templateRepository.findByCode("CORP_RED").isPresent());
        assertTrue(templateRepository.findByCode("MOD_GREEN").isPresent());
    }
}
