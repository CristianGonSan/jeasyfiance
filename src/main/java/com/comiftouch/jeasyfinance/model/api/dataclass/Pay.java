package com.comiftouch.jeasyfinance.model.api.dataclass;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pay {
    @JsonProperty("pay_id")
    private int payId;
    @JsonProperty("session_id")
    private int sessionId;
    @JsonProperty("loan_id")
    private int loanId;
    private String date;
    private float amount;
    @JsonProperty("registration_date")
    private String registrationDate;
    private boolean paid;

    public Pay() {

    }

    public void transfer(Pay pay) {

    }

    @Override
    public String toString() {
        return "Pay{" +
                "payId=" + payId +
                ", sessionId=" + sessionId +
                ", loanId=" + loanId +
                ", date='" + date + '\'' +
                ", amount=" + amount +
                ", registrationDate='" + registrationDate + '\'' +
                ", paid=" + paid +
                '}';
    }

    public int getPayId() {
        return payId;
    }

    public void setPayId(int payId) {
        this.payId = payId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
