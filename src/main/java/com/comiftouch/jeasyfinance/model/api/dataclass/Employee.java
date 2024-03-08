package com.comiftouch.jeasyfinance.model.api.dataclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Employee {
    @JsonProperty("employee_id")
    private int employeeId;
    private String clue;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String email;
    private String region;
    private String position;
    @JsonProperty("registration_date")
    private String registrationDate;
    @JsonProperty("is_deleted")
    private boolean deleted;

    public void transfer(Employee employee) {
        if (employeeId > 0) {
            employee.setEmployeeId(employeeId);
        }
        if (clue != null && !clue.isEmpty()) {
            employee.setClue(clue);
        }
        if (firstName != null && !firstName.isEmpty()) {
            employee.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            employee.setLastName(lastName);
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            employee.setPhoneNumber(phoneNumber);
        }
        if (email != null && !email.isEmpty()) {
            employee.setEmail(email);
        }
        if (region != null && !region.isEmpty()) {
            employee.setRegion(region);
        }
        if (position != null && !position.isEmpty()) {
            employee.setPosition(position);
        }
        if (registrationDate != null && !registrationDate.isEmpty()) {
            employee.setRegistrationDate(registrationDate);
        }
        if (deleted) {
            employee.setDeleted(true);
        }
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", clue='" + clue + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", region='" + region + '\'' +
                ", position='" + position + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", deleted=" + deleted +
                '}';
    }

    @JsonIgnore
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getClue() {
        return clue;
    }

    public void setClue(String clue) {
        this.clue = clue;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
