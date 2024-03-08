package com.comiftouch.jeasyfinance.model.api.dataclass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoanRequest {
    @JsonProperty("request_id")
    private int requestId;
    @JsonProperty("session_id")
    private int sessionId;
    @JsonProperty("client_id")
    private int clientId;
    @JsonProperty("aval_id")
    private int avalId;
    @JsonProperty("executive_id")
    private int executiveId;
    @JsonProperty("supervisor_id")
    private int supervisorId;
    @JsonProperty("client_full_name")
    private String clientFullName;
    @JsonProperty("aval_full_name")
    private String avalFullName;
    @JsonProperty("executive_full_name")
    private String executiveFullName;
    @JsonProperty("supervisor_full_name")
    private String supervisorFullName;
    private float amount;
    private float interest;
    @JsonProperty("request_date")
    private String requestDate;
    @JsonProperty("reference_url")
    private String referenceUrl;
    @JsonProperty("comments_url")
    private String commentsUrl;
    @JsonProperty("request_status")
    private int requestStatus;
    private String region;
    @JsonProperty("registration_date")
    private String registrationDate;
    @JsonProperty("is_deleted")
    private boolean deleted;

    public LoanRequest() {

    }

    @JsonIgnore
    public void transfer(LoanRequest loanRequest) {
        if (requestId > 0) {
            loanRequest.setRequestId(requestId);
        }
        if (sessionId > 0) {
            loanRequest.setSessionId(sessionId);
        }
        if (clientId != 0) {
            loanRequest.setClientId(clientId);
        }
        if (avalId != 0) {
            loanRequest.setAvalId(avalId);
        }
        if (executiveId != 0) {
            loanRequest.setExecutiveId(executiveId);
        }
        if (supervisorId != 0) {
            loanRequest.setSupervisorId(supervisorId);
        }
        if (clientFullName != null) {
            loanRequest.setClientFullName(clientFullName);
        }
        if (avalFullName != null) {
            loanRequest.setAvalFullName(avalFullName);
        }
        if (executiveFullName != null) {
            loanRequest.setExecutiveFullName(executiveFullName);
        }
        if (supervisorFullName != null) {
            loanRequest.setSupervisorFullName(supervisorFullName);
        }
        if (amount > 0) {
            loanRequest.setAmount(amount);
        }
        if (interest > 0) {
            loanRequest.setInterest(interest);
        }
        if (requestDate != null && !requestDate.isEmpty()) {
            loanRequest.setRequestDate(requestDate);
        }
        if (referenceUrl != null && !referenceUrl.isEmpty()) {
            loanRequest.setReferenceUrl(referenceUrl);
        }
        if (commentsUrl != null && !commentsUrl.isEmpty()) {
            loanRequest.setCommentsUrl(commentsUrl);
        }
        if (requestStatus != 0) {
            loanRequest.setRequestStatus(requestStatus);
        }
        if (region != null && !region.isEmpty()) {
            loanRequest.setRegion(region);
        }
        if (requestDate != null && !requestDate.isEmpty()) {
            loanRequest.setRequestDate(requestDate);
        }
        if (deleted) {
            loanRequest.setDeleted(true);
        }
    }

    @Override
    public String toString() {
        return "LoanRequest{" +
                "requestId=" + requestId +
                ", sessionId=" + sessionId +
                ", clientId=" + clientId +
                ", avalId=" + avalId +
                ", executiveId=" + executiveId +
                ", supervisorId=" + supervisorId +
                ", clientFullName='" + clientFullName + '\'' +
                ", avalFullName='" + avalFullName + '\'' +
                ", executiveFullName='" + executiveFullName + '\'' +
                ", supervisorFullName='" + supervisorFullName + '\'' +
                ", amount=" + amount +
                ", interest=" + interest +
                ", requestDate='" + requestDate + '\'' +
                ", referenceUrl='" + referenceUrl + '\'' +
                ", commentsUrl='" + commentsUrl + '\'' +
                ", requestStatus=" + requestStatus +
                ", region='" + region + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", deleted=" + deleted +
                '}';
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getAvalId() {
        return avalId;
    }

    public void setAvalId(int avalId) {
        this.avalId = avalId;
    }

    public int getExecutiveId() {
        return executiveId;
    }

    public void setExecutiveId(int executiveId) {
        this.executiveId = executiveId;
    }

    public int getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(int supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getClientFullName() {
        return clientFullName;
    }

    public void setClientFullName(String clientFullName) {
        this.clientFullName = clientFullName;
    }

    public String getAvalFullName() {
        return avalFullName;
    }

    public void setAvalFullName(String avalFullName) {
        this.avalFullName = avalFullName;
    }

    public String getExecutiveFullName() {
        return executiveFullName;
    }

    public void setExecutiveFullName(String executiveFullName) {
        this.executiveFullName = executiveFullName;
    }

    public String getSupervisorFullName() {
        return supervisorFullName;
    }

    public void setSupervisorFullName(String supervisorFullName) {
        this.supervisorFullName = supervisorFullName;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getInterest() {
        return interest;
    }

    public void setInterest(float interest) {
        this.interest = interest;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getReferenceUrl() {
        return referenceUrl;
    }

    public void setReferenceUrl(String referenceUrl) {
        this.referenceUrl = referenceUrl;
    }

    public String getCommentsUrl() {
        return commentsUrl;
    }

    public void setCommentsUrl(String commentsUrl) {
        this.commentsUrl = commentsUrl;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
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
