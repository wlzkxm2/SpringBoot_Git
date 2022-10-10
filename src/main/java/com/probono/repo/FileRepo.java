package com.probono.repo;

import com.probono.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepo extends JpaRepository<Files, String> {
//    Optional<Files> findByfileId(Long id);
//    Files findUserID(String userid);
}
