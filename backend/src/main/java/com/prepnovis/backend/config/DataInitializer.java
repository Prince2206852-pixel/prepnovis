package com.prepnovis.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.prepnovis.backend.entity.Role;
import com.prepnovis.backend.repository.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {

        createRoleIfNotExists(
                "USER",
                "Default application user"
        );

    }

    private void createRoleIfNotExists(String roleName, String description) {

        if (roleRepository.findByName(roleName).isEmpty()) {

            Role role = new Role();
            role.setName(roleName);
            role.setDescription(description);

            roleRepository.save(role);

            System.out.println("✅ Created role : " + roleName);

        } else {

            System.out.println("✔ Role already exists : " + roleName);

        }
    }

}