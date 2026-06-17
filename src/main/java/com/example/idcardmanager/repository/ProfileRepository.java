package com.example.idcardmanager.repository;

import com.example.idcardmanager.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByRegistrationNumber(String registrationNumber);
    Optional<Profile> findByUuid(String uuid);
    List<Profile> findByFullNameContainingIgnoreCase(String name);
    boolean existsByRegistrationNumber(String registrationNumber);
}