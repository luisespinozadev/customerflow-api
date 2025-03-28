package dev.luisespinoza.customerflow.config;

import dev.luisespinoza.customerflow.role.Role;
import dev.luisespinoza.customerflow.role.RoleRepository;
import dev.luisespinoza.customerflow.user.UserRepository;
import dev.luisespinoza.customerflow.user.UserRequest;
import dev.luisespinoza.customerflow.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class AppInitializer {

    private static final Logger logger = LoggerFactory.getLogger(AppInitializer.class);

    @Value("${initial-setup.admin.email}")
    private String adminEmail;
    @Value("${initial-setup.admin.password}")
    private String adminPassword;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Bean
    @Order(1)
    public CommandLineRunner RoleInitializer () {
        return args -> {
            if (roleRepository.findByName("ADMIN").isEmpty()) {
                roleRepository.save(Role.builder().name("ADMIN").build());
                logger.info("RoleInitializer: Created role ADMIN");
            }
            if (roleRepository.findByName("USER").isEmpty()) {
                roleRepository.save(Role.builder().name("USER").build());
                logger.info("RoleInitializer: Created role USER");
            }
        };
    }

    @Bean
    @Order(2)
    public CommandLineRunner AdminUserInitializer () {
        return args -> {

            if (userRepository.existsByIdIsNotNull()) {
                logger.info("AdminUserInitializer: Users table already populated. Admin user creation not required.");
                return;
            }

            if (adminEmail == null || adminEmail.trim().isEmpty()) {
                logger.info("AdminUserInitializer: Admin email is null or empty. Skipping admin creation.");
                return;
            }

            if (adminPassword == null || adminPassword.trim().isEmpty()) {
                logger.info("AdminUserInitializer: Admin password is null or empty. Skipping admin creation.");
                return;
            }

            UserRequest userRequest = new UserRequest(
                    null,
                    null,
                    adminEmail,
                    adminPassword,
                    List.of("ADMIN")
            );
            userService.create(userRequest);

            logger.info("AdminUserInitializer: Admin user created");
        };
    }
}
