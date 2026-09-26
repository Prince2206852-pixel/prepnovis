package com.prepnovis.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prepnovis.backend.entity.User;
import com.prepnovis.backend.entity.enums.AuthProvider;

import jakarta.persistence.LockModeType;

public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = "role")
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = "role")
    Optional<User> findByAuthProviderAndProviderId(
        AuthProvider authProvider,
        String providerId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT u
        FROM User u
        WHERE u.email = :email
        """)
    Optional<User> findByEmailForQuestionNumberUpdate(
        @Param("email") String email
    );
}