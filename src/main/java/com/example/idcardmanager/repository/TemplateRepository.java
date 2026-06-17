package com.example.idcardmanager.repository;

import com.example.idcardmanager.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    Optional<Template> findByCode(String code);
    boolean existsByCode(String code);
}