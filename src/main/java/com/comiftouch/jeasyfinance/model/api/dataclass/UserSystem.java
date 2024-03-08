package com.comiftouch.jeasyfinance.model.api.dataclass;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class UserSystem {
    @JsonProperty("user_id")
    private int userId;
    private String username;
    @JsonProperty("user_password")
    private String password;
    private int level;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String email;
    private String region;
    @JsonProperty("registration_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registrationDate;
    @JsonProperty("is_deleted")
    private boolean deleted;

    public UserSystem() {

    }

    @JsonIgnore
    public void transfer(UserSystem userSystem) {
        if (userId > 0) {
            userSystem.setUserId(userId);
        }
        if (username != null && !username.isEmpty()) {
            userSystem.setUsername(username);
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            userSystem.setPhoneNumber(phoneNumber);
        }
        if (level > 0) {
            userSystem.setLevel(level);
        }
        if (firstName != null && !firstName.isEmpty()) {
            userSystem.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            userSystem.setLastName(lastName);
        }
        if (email != null && !email.isEmpty()) {
            userSystem.setEmail(email);
        }
        if (region != null && !region.isEmpty()) {
            userSystem.setRegion(region);
        }
        if (registrationDate != null) {
            userSystem.setRegistrationDate(registrationDate);
        }
        if (deleted) {
            userSystem.setDeleted(true);
        }
    }

    @Override
    public String toString() {
        return "UserSystem{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", level=" + level +
                ", firthName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", region='" + region + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", deleted=" + deleted +
                '}';
    }

    @JsonIgnore
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
