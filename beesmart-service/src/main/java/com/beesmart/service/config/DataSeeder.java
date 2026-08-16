package com.beesmart.service.config;

import com.beesmart.service.model.Role;
import com.beesmart.service.model.User;
import com.beesmart.service.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the demo accounts on first startup.
 *
 * Implemented as a CommandLineRunner rather than @PostConstruct so that it runs after
 * Hibernate's ddl-auto=update has created the app_users table.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean seedEnabled;

    public DataSeeder(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${beesmart.seed.enabled:true}") boolean seedEnabled) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedEnabled = seedEnabled;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled || userRepository.count() > 0) {
            return;
        }

        userRepository.save(new User(
                "admin",
                passwordEncoder.encode("admin123"),
                "System Administrator",
                "admin@beesmart.local",
                Role.ADMIN));

        userRepository.save(new User(
                "beekeeper",
                passwordEncoder.encode("bee123"),
                "Demo Beekeeper",
                "beekeeper@beesmart.local",
                Role.BEEKEEPER));

        System.out.println("[SEED] Created default accounts: admin/admin123, beekeeper/bee123");
    }
}
