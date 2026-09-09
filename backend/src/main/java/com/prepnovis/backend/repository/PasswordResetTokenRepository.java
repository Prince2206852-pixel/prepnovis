package com.prepnovis.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prepnovis.backend.entity.PasswordResetToken;
import com.prepnovis.backend.entity.User;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    void deleteAllByUser(User user);
}