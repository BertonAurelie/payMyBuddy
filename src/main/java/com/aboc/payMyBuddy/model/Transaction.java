package com.aboc.payMyBuddy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TransactionDb")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserDb sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private UserDb receiver;

    @Column(name = "description_transaction")
    private String description;

    @Column(name = "amount")
    private double amount;

    //Constructor
    public Transaction(int id, UserDb sender, UserDb receiver, String description, double amount) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.description = description;
        this.amount = amount;
    }

    //Getter et Setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserDb getSender() {
        return sender;
    }

    public void setSender(UserDb sender) {
        this.sender = sender;
    }

    public UserDb getReceiver() {
        return receiver;
    }

    public void setReceiver(UserDb receiver) {
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
