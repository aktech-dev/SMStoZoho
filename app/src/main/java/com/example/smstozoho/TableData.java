package com.example.smstozoho;

public class TableData {
    public String id;
    public String message;
    String amount;
    String type;
    String description;
    public String time;
    public String permission;
    public String status;

    public TableData(String id, String message,String amount,String type,String description, String time, String permission, String status) {
        this.id = id;
        this.message = message;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.time = time;
        this.permission = permission;
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
