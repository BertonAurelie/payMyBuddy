package com.aboc.payMyBuddy.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;


public class TransactionDto {
    @NotBlank(message = "Receiver email is required")
    private String receiver;

    @NotBlank(message = "Description is required")
    private String description;

    @Positive(message = "Amount must be positive")
    private double amount;

    public TransactionDto() {
    }

    public TransactionDto(String receiver, String description, double amount) {
        this.receiver = receiver;
        this.description = description;
        this.amount = amount;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
