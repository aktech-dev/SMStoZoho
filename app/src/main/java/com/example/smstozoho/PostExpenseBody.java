package com.example.smstozoho;

public class PostExpenseBody {
    private String account_id;
    private String date;
    private float amount;
    private String description;
    private String reference_number;

    public PostExpenseBody(String account_id, String date, float amount, String description, String reference_number) {
        this.account_id = account_id;
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.reference_number = reference_number;
    }

    public String getReference_number() {
        return reference_number;
    }

    public void setReference_number(String reference_number) {
        this.reference_number = reference_number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
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
}
