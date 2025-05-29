package com.ascendcorp.exam.model;

public class TransferResponse {

    private String responseCode;
    private String description;
    private String referenceCode1;
    private String referenceCode2;
    private String amount;
    private String bankTransactionID;

    public TransferResponse() {

    }

    public TransferResponse(String responseCode, String description, String referenceCode1, String referenceCode2, String amount, String bankTransactionID) {
        this.responseCode = responseCode;
        this.description = description;
        this.referenceCode1 = referenceCode1;
        this.referenceCode2 = referenceCode2;
        this.amount = amount;
        this.bankTransactionID = bankTransactionID;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getDescription() {
        return description;
    }

    public String getReferenceCode1() {
        return referenceCode1;
    }

    public String getReferenceCode2() {
        return referenceCode2;
    }

    public String getAmount() {
        return amount;
    }

    public String getBankTransactionID() {
        return bankTransactionID;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setReferenceCode1(String referenceCode1) {
        this.referenceCode1 = referenceCode1;
    }

    public void setReferenceCode2(String referenceCode2) {
        this.referenceCode2 = referenceCode2;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setBankTransactionID(String bankTransactionID) {
        this.bankTransactionID = bankTransactionID;
    }

    @Override
    public String toString() {
        return "TransferResponse{" +
                "responseCode='" + responseCode + '\'' +
                ", description='" + description + '\'' +
                ", referenceCode1='" + referenceCode1 + '\'' +
                ", referenceCode2='" + referenceCode2 + '\'' +
                ", amount='" + amount + '\'' +
                ", bankTransactionID='" + bankTransactionID + '\'' +
                '}';
    }
}
