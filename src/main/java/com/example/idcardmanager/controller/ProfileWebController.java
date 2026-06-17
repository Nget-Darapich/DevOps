package com.example.idcardmanager.controller;

import com.example.idcardmanager.model.Profile;
import com.example.idcardmanager.model.Template;
import com.example.idcardmanager.repository.ProfileRepository;
import com.example.idcardmanager.repository.TemplateRepository;
import com.example.idcardmanager.service.AssetGenerationService;
import com.example.idcardmanager.service.ProfileService;
import com.example.idcardmanager.service.TemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/profiles")
public class ProfileWebController {

    private final ProfileService profileService;
    private final AssetGenerationService assetGenerationService;
    private final TemplateService templateService;
    private final ProfileRepository profileRepository;
    private final TemplateRepository templateRepository;

    public ProfileWebController(ProfileService profileService, 
                               AssetGenerationService assetGenerationService,
                               TemplateService templateService,
                               ProfileRepository profileRepository,
                               TemplateRepository templateRepository) {
        this.profileService = profileService;
        this.assetGenerationService = assetGenerationService;
        this.templateService = templateService;
        this.profileRepository = profileRepository;
        this.templateRepository = templateRepository;
        
        // Initialize default templates on startup
        this.templateService.initializeDefaultTemplates();
    }

    /**
     * Display the profile creation form.
     */
    @GetMapping
    public String showProfileForm(Model model) {
        List<Template> templates = templateService.getAllTemplates();
        model.addAttribute("templates", templates);
        model.addAttribute("profile", new Profile());
        return "index";
    }

    /**
     * Generate a live preview of the card.
     */
    @PostMapping("/preview")
    public String livePreviewCard(@ModelAttribute Profile profile, 
                                  @RequestParam(value = "photo", required = false) MultipartFile file, 
                                  Model model) throws Exception {
        
        // If template ID is provided, fetch the full template object
        if (profile.getTemplate() != null && profile.getTemplate().getId() != null) {
            Optional<Template> template = templateRepository.findById(profile.getTemplate().getId());
            template.ifPresent(profile::setTemplate);
        }

        // Generate barcodes with proper defaults
        String uuid = profile.getUuid() != null ? profile.getUuid() : "PREVIEW";
        String qrCode = assetGenerationService.generateQRCodeBase64(uuid);
        
        String regNum = profile.getRegistrationNumber() != null && !profile.getRegistrationNumber().isBlank() 
            ? profile.getRegistrationNumber() 
            : "1234567890";
        String barcode = assetGenerationService.generateBarcodeBase64(regNum, profile.getBarcodeType());

        model.addAttribute("profile", profile);
        model.addAttribute("qrCodeBase64", qrCode);
        model.addAttribute("barcodeBase64", barcode);
        
        return "card_preview_fragment";
    }

    /**
     * Save a profile to the database.
     */
    @PostMapping("/save")
    public String saveProfile(@ModelAttribute Profile profile, 
                             @RequestParam(value = "photo", required = false) MultipartFile photoFile,
                             Model model) throws Exception {
        // Register/save the profile with photo handling
        Profile savedProfile = profileService.registerProfile(profile, photoFile);
        
        model.addAttribute("message", "Profile saved successfully!");
        model.addAttribute("profile", savedProfile);
        
        return "redirect:/profiles/" + savedProfile.getUuid();
    }

    /**
     * View a specific profile by UUID.
     */
    @GetMapping("/{uuid}")
    public String viewProfile(@PathVariable String uuid, Model model) throws Exception {
        Optional<Profile> profile = profileRepository.findByUuid(uuid);
        
        if (profile.isEmpty()) {
            model.addAttribute("error", "Profile not found");
            return "error";
        }

        Profile p = profile.get();
        String qrCode = assetGenerationService.generateQRCodeBase64(p.getUuid());
        String barcode = assetGenerationService.generateBarcodeBase64(
            p.getRegistrationNumber(), 
            p.getBarcodeType()
        );

        model.addAttribute("profile", p);
        model.addAttribute("qrCodeBase64", qrCode);
        model.addAttribute("barcodeBase64", barcode);
        
        return "card_preview_fragment";
    }

    /**
     * List all profiles.
     */
    @GetMapping("/list")
    public String listProfiles(Model model) {
        List<Profile> profiles = profileRepository.findAll();
        model.addAttribute("profiles", profiles);
        return "profiles_list";
    }

    /**
     * Search profiles by name.
     */
    @GetMapping("/search")
    public String searchProfiles(@RequestParam String name, Model model) {
        List<Profile> profiles = profileRepository.findByFullNameContainingIgnoreCase(name);
        model.addAttribute("profiles", profiles);
        model.addAttribute("searchTerm", name);
        return "profiles_list";
    }

    /**
     * Delete a profile by UUID.
     */
    @PostMapping("/{uuid}/delete")
    public String deleteProfile(@PathVariable String uuid, Model model) {
        Optional<Profile> profile = profileRepository.findByUuid(uuid);
        if (profile.isPresent()) {
            profileRepository.delete(profile.get());
            model.addAttribute("message", "Profile deleted successfully!");
        }
        return "redirect:/profiles";
    }
}