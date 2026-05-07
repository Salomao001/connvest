package com.conninvest.backend.dto;

public class AuthResponse {
    private Long id;
    private String name;
    private String email;
    private java.util.List<String> profileTypes;
    
    public AuthResponse() {}

    public AuthResponse(Long id, String name, String email, java.util.List<String> profileTypes) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileTypes = profileTypes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public java.util.List<String> getProfileTypes() { return profileTypes; }
    public void setProfileTypes(java.util.List<String> profileTypes) { this.profileTypes = profileTypes; }
}
