package com.example.smstozoho;

import android.widget.LinearLayout;

import java.util.List;

public class ExpenseList {
    private int code;
    private String message;
    private List<Chartofaccounts> chartofaccounts;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Chartofaccounts> getChartofaccounts() {
        return chartofaccounts;
    }

    public void setChartofaccounts(List<Chartofaccounts> chartofaccounts) {
        this.chartofaccounts = chartofaccounts;
    }
}
