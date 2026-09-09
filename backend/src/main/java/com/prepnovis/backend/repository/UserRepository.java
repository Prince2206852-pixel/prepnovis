package com.prepnovis.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prepnovis.backend.entity.User;
import com.prepnovis.backend.entity.enums.AuthProvider;

public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = "role")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "role")
    Optional<User> findByAuthProviderAndProviderId(
        AuthProvider authProvider,
        String providerId
);
}