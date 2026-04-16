package com.bookstore.service;

import com.bookstore.dto.AdminCreateAccountRequest;
import com.bookstore.dto.AdminUserResponseDTO;
import com.bookstore.dto.AccountProfileDTO;
import com.bookstore.entity.Customer;
import com.bookstore.entity.Staff;
import com.bookstore.entity.Account;
import com.bookstore.enums.AccountStatus;
import com.bookstore.repository.StaffRepository;
import com.bookstore.repository.AccountRepository;
import com.bookstore.repository.CustomerRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@SuppressWarnings("null")
public class AdminService {

    private final AccountRepository accountRepository;
    private final StaffRepository staffRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AccountRepository accountRepository, 
                        StaffRepository staffRepository,
                        CustomerRepository customerRepository,
                        PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.staffRepository = staffRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AdminUserResponseDTO createAccount(AdminCreateAccountRequest request) {
        // 1. Check existence
        if (accountRepository.existsById(request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' already exists");
        }

        // 2. Create new account
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(request.getRole().toUpperCase());
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);

        // 3. Automatically determine department based on role
        String department;
        String role = request.getRole().toUpperCase();
        department = switch (role) {
            case "ADMIN" -> "MANAGEMENT";
            case "STOREKEEPER" -> "WAREHOUSE";
            default -> "SALES"; // Default for STAFF
        };

        // 4. Create profile for new staff
        Staff staff = new Staff();
        staff.setAccount(account);
        staff.setFullName("NEW STAFF MEMBER");
        staff.setDepartment(department);
        staffRepository.save(staff);

        // 5. Build response DTO
        AdminUserResponseDTO response = new AdminUserResponseDTO();
        response.setUsername(account.getUsername());
        response.setRole(account.getRole());
        response.setStatus(account.getStatus());
        response.setCreatedAt(account.getCreatedAt());
        response.setDepartment(staff.getDepartment());
        return response;
    }


    @Transactional(readOnly = true)
    public List<AccountProfileDTO> getAllUsers() {
        // 1. Fetch all accounts
        List<Account> accounts = accountRepository.findAll();
        List<String> usernames = accounts.stream().map(Account::getUsername).collect(Collectors.toList());

        // 2. Fetch profiles in bulk
        Map<String, Customer> customerMap = customerRepository.findByAccount_UsernameIn(usernames)
                .stream()
                .collect(Collectors.toMap(kh -> kh.getAccount().getUsername(), kh -> kh));

        Map<String, Staff> staffMap = staffRepository.findByAccount_UsernameIn(usernames)
                .stream()
                .collect(Collectors.toMap(nv -> nv.getAccount().getUsername(), nv -> nv));

        // 3. Map to DTOs
        return accounts.stream()
            .map(account -> mapToAccountProfileDTO(account, customerMap, staffMap))
            .collect(Collectors.toList());
    }

    private AccountProfileDTO mapToAccountProfileDTO(Account account, Map<String, Customer> customerMap, Map<String, Staff> staffMap) {
        AccountProfileDTO dto = new AccountProfileDTO();
        dto.setUsername(account.getUsername());
        dto.setRole(account.getRole());

        if ("CUSTOMER".equalsIgnoreCase(account.getRole())) {
            Customer customer = customerMap.get(account.getUsername());
            if (customer != null) {
                dto.setFullName(customer.getFullName());
                dto.setPhone(customer.getPhone());
                dto.setShippingAddress(customer.getShippingAddress());
                dto.setLoyaltyPoints(customer.getLoyaltyPoints());
            }
        } else {
            Staff staff = staffMap.get(account.getUsername());
            if (staff != null) {
                dto.setFullName(staff.getFullName());
                dto.setPhone(staff.getPhone());
                dto.setDepartment(staff.getDepartment());
            }
        }
        return dto;
    }

    @Transactional
    public void updateUserStatus(String username, boolean status) {
        String currentAdmin = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        if (currentAdmin.equals(username)) {
            throw new IllegalArgumentException("You cannot lock or unlock your own account");
        }

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + username));
        
        account.setStatus(status ? AccountStatus.ACTIVE : AccountStatus.DISABLED);
        accountRepository.save(account);
    }

    @Transactional
    public void updateUserRole(String username, String role) {
        role = role.toUpperCase();
        if (!"ADMIN".equals(role) && !"STAFF".equals(role) && !"STOREKEEPER".equals(role)) {
            throw new IllegalArgumentException("Invalid role. Only ADMIN, STAFF, STOREKEEPER are accepted.");
        }

        String currentAdmin = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        if (currentAdmin.equals(username)) {
            throw new IllegalArgumentException("You cannot change your own role");
        }

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + username));
        
        if ("CUSTOMER".equals(account.getRole())) {
            throw new IllegalArgumentException("Cannot change Customer role directly here");
        }

        account.setRole(role);
        accountRepository.save(account);

        String department = switch (role) {
            case "ADMIN" -> "MANAGEMENT";
            case "STOREKEEPER" -> "WAREHOUSE";
            default -> "SALES";
        };

        staffRepository.findByAccount_Username(username).ifPresent(staff -> {
            staff.setDepartment(department);
            staffRepository.save(staff);
        });
    }
}
