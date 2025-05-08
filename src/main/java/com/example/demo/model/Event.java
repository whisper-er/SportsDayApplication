package com.example.demo.model;

import org.springframework.data.annotation.Id;


public class Event {
    @Id
    private int id;
    private String eventName;
    private String eventCategory;
    private String startTime;
    private String endTime;

    public Event(int id, String eventName, String eventCategory, String startTime, String endTime) {
        this.id = id;
        this.eventName = eventName;
        this.eventCategory = eventCategory;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}