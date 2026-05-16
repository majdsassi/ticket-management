package com.example.project.config;

import com.example.project.model.AppUser;
import com.example.project.model.Project;
import com.example.project.model.UserRole;
import com.example.project.repository.AppUserRepository;
import com.example.project.repository.ProjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AppUserRepository userRepository, ProjectRepository projectRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create admin user
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            AppUser admin = AppUser.builder()
                    .fullName("Administrator")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(UserRole.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            System.out.println("✓ Admin user created: admin@example.com / admin123");
        }

        // Create support agents
        if (userRepository.findByEmail("support1@example.com").isEmpty()) {
            AppUser support1 = AppUser.builder()
                    .fullName("John Support")
                    .email("support1@example.com")
                    .password(passwordEncoder.encode("support123"))
                    .role(UserRole.CUSTOMER_SUPPORT)
                    .active(true)
                    .build();
            userRepository.save(support1);
            System.out.println("✓ Support agent created: support1@example.com / support123");
        }

        if (userRepository.findByEmail("support2@example.com").isEmpty()) {
            AppUser support2 = AppUser.builder()
                    .fullName("Jane Support")
                    .email("support2@example.com")
                    .password(passwordEncoder.encode("support123"))
                    .role(UserRole.CUSTOMER_SUPPORT)
                    .active(true)
                    .build();
            userRepository.save(support2);
            System.out.println("✓ Support agent created: support2@example.com / support123");
        }

        // Create customer users
        if (userRepository.findByEmail("customer1@example.com").isEmpty()) {
            AppUser customer1 = AppUser.builder()
                    .fullName("Alice Customer")
                    .email("customer1@example.com")
                    .password(passwordEncoder.encode("customer123"))
                    .role(UserRole.CUSTOMER)
                    .active(true)
                    .build();
            userRepository.save(customer1);
            System.out.println("✓ Customer created: customer1@example.com / customer123");
        }

        if (userRepository.findByEmail("customer2@example.com").isEmpty()) {
            AppUser customer2 = AppUser.builder()
                    .fullName("Bob Customer")
                    .email("customer2@example.com")
                    .password(passwordEncoder.encode("customer123"))
                    .role(UserRole.CUSTOMER)
                    .active(true)
                    .build();
            userRepository.save(customer2);
            System.out.println("✓ Customer created: customer2@example.com / customer123");
        }

        // Create projects
        if (projectRepository.findAll().isEmpty()) {
            Project project1 = Project.builder()
                    .name("Website Redesign")
                    .description("Complete redesign of the company website with modern UI/UX")
                    .build();
            projectRepository.save(project1);

            Project project2 = Project.builder()
                    .name("Mobile App Development")
                    .description("Native mobile applications for iOS and Android platforms")
                    .build();
            projectRepository.save(project2);

            Project project3 = Project.builder()
                    .name("API Integration")
                    .description("Third-party API integrations and webhook implementations")
                    .build();
            projectRepository.save(project3);

            System.out.println("✓ Projects created successfully");
        }

        System.out.println("\n🎫 Ticket System is ready!");
        System.out.println("Login at: http://localhost:8089/login");
    }
}
