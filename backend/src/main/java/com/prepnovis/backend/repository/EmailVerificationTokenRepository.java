package com.prepnovis.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prepnovis.backend.entity.EmailVerificationToken;
import com.prepnovis.backend.entity.User;

public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken, UUID> {

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

    void deleteAllByUser(User user);
}