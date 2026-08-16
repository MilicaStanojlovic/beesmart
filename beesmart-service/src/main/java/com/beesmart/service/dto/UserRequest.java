package com.beesmart.service.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * Admin create/update payload for a beekeeper account.
 * On update the password may be left blank, meaning "keep the existing one".
 */
public class UserRequest {

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    private String password;

    private String fullName;

    @Email(message = "Email is not valid")
    private String email;

    private Boolean active;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
