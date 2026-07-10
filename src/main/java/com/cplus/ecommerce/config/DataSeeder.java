package com.cplus.ecommerce.config;

import com.cplus.ecommerce.model.Role;
import com.cplus.ecommerce.model.User;
import com.cplus.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Buat akun ADMIN jika belum ada di database
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@cplus.com");
            // Enkripsi password menggunakan BCrypt
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            userRepository.save(admin);
            
            System.out.println("Akun Admin berhasil dibuat! (Username: admin | Password: admin123)");
        }

        // Buat akun USER biasa jika belum ada di database
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@cplus.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(Role.ROLE_USER);
            userRepository.save(user);
            
            System.out.println("Akun User berhasil dibuat! (Username: user | Password: user123)");
        }
    }
}
