package com.example.demo.model;

import org.springframework.data.annotation.Id;
import java.util.Set;

public class User {
    @Id
    private String username;
    private String password;
    private String emailId;
    private String firstName;
    private String lastName;
    private Set<Integer> registeredEvents;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Integer> getRegisteredEvents() {
        return registeredEvents;
    }

    public void setRegisteredEvents(Set<Integer> registeredEvents) {
        this.registeredEvents = registeredEvents;
    }

}