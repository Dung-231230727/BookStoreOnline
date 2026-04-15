package com.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginResponse {
    @Schema(example = "admin")
    private String username;

    @Schema(example = "ADMIN")
    private String role;

    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    private String fullName;
    private String email;
    private String phone;
    private String address;

    @Schema(example = "Bearer")
    private String tokenType = "Bearer";

    public LoginResponse() {}

    public LoginResponse(String username, String role, String token, String fullName) {
        this.username = username;
        this.role = role;
        this.token = token;
        this.fullName = fullName;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
}
