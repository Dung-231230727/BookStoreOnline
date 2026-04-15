package com.bookstore.config;

import com.bookstore.entity.Staff;
import com.bookstore.entity.Account;
import com.bookstore.repository.StaffRepository;
import com.bookstore.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AccountRepository accountRepository, 
                           StaffRepository staffRepository, 
                           PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Initialize or update the root ADMIN account
        Account adminAccount = accountRepository.findById("admin").orElse(null);
        
        if (adminAccount == null) {
            System.out.println(">>> Creating Root Admin account...");
            adminAccount = new Account();
            adminAccount.setUsername("admin");
            adminAccount.setRole("ADMIN");
            adminAccount.setIsActive(true);
        } else {
            System.out.println(">>> Resetting Root Admin password...");
        }

        // Always update password to admin123 to ensure access
        adminAccount.setPassword(passwordEncoder.encode("admin123"));
        accountRepository.save(adminAccount);

        if (staffRepository.findByAccount_Username("admin").isEmpty()) {
             System.out.println(">>> Creating Admin profile...");
             Staff adminProfile = new Staff();
             adminProfile.setAccount(adminAccount);
             adminProfile.setFullName("System Admin");
             adminProfile.setPhone("0000000000");
             adminProfile.setDepartment("MANAGEMENT");
             staffRepository.save(adminProfile);
        }
        
        System.out.println(">>> Root Admin ready: admin / admin123");
    }
}
