package com.comiftouch.jeasyfinance.model.api.dataclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Loan {
    @JsonProperty("loan_id")
    private int loanId;
    @JsonProperty("session_id")
    private int sessionId;
    @JsonProperty("request_id")
    private int requestId;
    @JsonProperty("period_day")
    private int periodDay;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("loan_status")
    private int loanStatus;
    private boolean warranty;
    private String region;
    @JsonProperty("registration_date")
    private String registrationDate;
    @JsonProperty("is_deleted")
    private boolean deleted;

    public Loan() {

    }
    @JsonIgnore
    public void transfer(Loan loan) {
        if (loanId > 0) {
            loan.setLoanId(loanId);
        }
        if (sessionId > 0) {
            loan.setSessionId(sessionId);
        }
        if (requestId > 0) {
            loan.setRequestId(requestId);
        }
        if (periodDay > 0) {
            loan.setLoanId(loanId);
        }
        if (startDate != null && !startDate.isEmpty()) {
            loan.setStartDate(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            loan.setEndDate(endDate);
        }
        if (loanStatus > 0) {
            loan.setLoanStatus(loanStatus);
        }
        if (warranty) {
            loan.setWarranty(true);
        }
        if (region != null && !region.isEmpty()) {
            loan.setRegion(region);
        }
        if (registrationDate != null && !registrationDate.isEmpty()) {
            loan.setRegistrationDate(registrationDate);
        }
        if (deleted) {
            loan.setDeleted(true);
        }
    }

    @Override
    public String toString() {
        return "Loan{" +
                "landId=" + loanId +
                ", sessionId=" + sessionId +
                ", requestId=" + requestId +
                ", periodDay=" + periodDay +
                ", startDate='" + startDate + '\'' +
                ", entDate='" + endDate + '\'' +
                ", loanStatus=" + loanStatus +
                ", outstandingBalance=" + warranty +
                ", region='" + region + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", deleted=" + deleted +
                '}';
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getPeriodDay() {
        return periodDay;
    }

    public void setPeriodDay(int periodDay) {
        this.periodDay = periodDay;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(int loanStatus) {
        this.loanStatus = loanStatus;
    }

    public boolean isWarranty() {
        return warranty;
    }

    public void setWarranty(boolean warranty) {
        this.warranty = warranty;
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
