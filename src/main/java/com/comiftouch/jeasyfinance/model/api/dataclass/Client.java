package com.comiftouch.jeasyfinance.model.api.dataclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Client {
    @JsonProperty("client_id")
    private int clientId;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String nationality;
    private char sex;
    @JsonProperty("birth_date")
    private String birthDate;
    private String curp;
    private String domicile;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String email;
    @JsonProperty("civil_status")
    private String civilStatus;
    private String region;
    @JsonProperty("registration_date")
    private String registrationDate;
    @JsonProperty("is_deleted")
    private boolean deleted;

    public Client() {

    }

    @JsonIgnore
    public void transfer(Client client) {
        if (clientId > 0) {
            client.setClientId(clientId);
        }
        if (firstName != null && !firstName.isEmpty()) {
            client.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            client.setLastName(lastName);
        }
        if (nationality != null && !nationality.isEmpty()) {
            client.setNationality(nationality);
        }
        if (sex != '\u0000') {
            client.setSex(sex);
        }
        if (birthDate != null && !birthDate.isEmpty()) {
            client.setBirthDate(birthDate);
        }
        if (curp != null && !curp.isEmpty()) {
            client.setCurp(curp);
        }
        if (domicile != null && !domicile.isEmpty()) {
            client.setDomicile(domicile);
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            client.setPhoneNumber(phoneNumber);
        }
        if (email != null && !email.isEmpty()) {
            client.setEmail(email);
        }
        if (civilStatus != null && !civilStatus.isEmpty()) {
            client.setCivilStatus(civilStatus);
        }
        if (region != null && !region.isEmpty()) {
            client.setRegion(region);
        }
        if (registrationDate != null && !registrationDate.isEmpty()) {
            client.setRegistrationDate(registrationDate);
        }
        if (deleted) {
            client.setDeleted(true);
        }
    }


    @Override
    public String toString() {
        return "Client{" +
                "clientId=" + clientId +
                ", clientName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", nationality='" + nationality + '\'' +
                ", sex='" + sex + '\'' +
                ", brithDate='" + birthDate + '\'' +
                ", curp='" + curp + '\'' +
                ", domicile='" + domicile + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", civilStatus='" + civilStatus + '\'' +
                ", region='" + region + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", deleted=" + deleted +
                '}';
    }

    @JsonIgnore
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
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

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getDomicile() {
        return domicile;
    }

    public void setDomicile(String domicile) {
        this.domicile = domicile;
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

    public String getCivilStatus() {
        return civilStatus;
    }

    public void setCivilStatus(String civilStatus) {
        this.civilStatus = civilStatus;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
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
