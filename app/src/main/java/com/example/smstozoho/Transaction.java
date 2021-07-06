package com.example.smstozoho;

public class Transaction {
    private String from_account_id;
    private String to_account_id;
    private String transaction_type;
    private float amount;
    private String description;

    public Transaction(String from_account_id, String to_account_id, String transaction_type, float amount,String description) {
        this.from_account_id = from_account_id;
        this.to_account_id = to_account_id;
        this.transaction_type = transaction_type;
        this.amount = amount;
        this.description = description;
    }
}
