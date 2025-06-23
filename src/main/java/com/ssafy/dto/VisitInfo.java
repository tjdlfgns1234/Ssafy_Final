package com.ssafy.dto;

public class VisitInfo {
    private int day;
    private int order;
    private String key;

    public VisitInfo(int day, int order, String key) {
        this.day = day;
        this.order = order;
        this.key = key;
    }

    public int getDay() { return day; }
    public int getOrder() { return order; }
    public String getKey() { return key; }
}