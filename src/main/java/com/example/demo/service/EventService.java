package com.example.demo.service;

import com.example.demo.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Service;
import com.example.demo.util.DynamoDbUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private final DynamoDbUtil dynamoDbUtil;

    public EventService(DynamoDbUtil dynamoDbUtil) {
        this.dynamoDbUtil = dynamoDbUtil;
    }

    @GetMapping("/api/events")
    public List<Event> getAllEvents() {
        return Arrays.asList(
            new Event(1, "Butterfly 100M", "Swimming", 
                "2022-12-17T13:00:00Z", 
                "2022-12-17T14:00:00Z"),
            new Event(2, "Backstroke 100M", "Swimming", 
                "2022-12-17T13:30:00Z", 
                "2022-12-17T14:30:00Z"),
            new Event(3, "Freestyle 400M", "Swimming", 
                "2022-12-17T15:00:00Z", 
                "2022-12-17T16:00:00Z"),
            new Event(4, "High Jump", "Athletics", 
                "2022-12-17T13:00:00Z", 
                "2022-12-17T14:00:00Z"),
            new Event(5, "Triple Jump", "Athletics", 
                "2022-12-17T16:00:00Z", 
                "2022-12-17T17:00:00Z"),
            new Event(6, "Long Jump", "Athletics", 
                "2022-12-17T17:00:00Z", 
                "2022-12-17T18:00:00Z"),
            new Event(7, "100M Sprint", "Athletics", 
                "2022-12-17T17:00:00Z", 
                "2022-12-17T18:00:00Z"),
            new Event(8, "Lightweight 60kg", "Boxing", 
                "2022-12-17T18:00:00Z", 
                "2022-12-17T19:00:00Z"),
            new Event(9, "Middleweight 75 kg", "Boxing", 
                "2022-12-17T19:00:00Z", 
                "2022-12-17T20:00:00Z"),
            new Event(10, "Heavyweight 91kg", "Boxing", 
                "2022-12-17T20:00:00Z", 
                "2022-12-17T22:00:00Z")
        );
    }

    public Event getEventById(Integer eventId) {
        List<Event> allEvents = getAllEvents();
        return allEvents.stream()
                        .filter(event -> event.getId() == eventId)
                        .findFirst()
                        .orElse(null); // Return null if event not found
    }

    public List<Event> getRegisteredEvents(String username) {
        Set<Integer> registeredEventIds = dynamoDbUtil.getUserRegisteredEvents(username);
    
        if (registeredEventIds.isEmpty()) {
            logger.info("No registered events found for user: {}", username);
            return new ArrayList<>(); // Return an empty list if no events are found
        }

        List<Event> registeredEvents = new ArrayList<>();
        for (Integer eventId : registeredEventIds) {
            Event event = getEventById(eventId); // Fetch the event by ID
            if (event != null) {
                registeredEvents.add(event);
            }
        }

        return registeredEvents;
    }

    public boolean registerEvent(String username, Event event) {
        return dynamoDbUtil.updateUserRegisteredEvents(username, event.getId());
    }

    public String unregisterEvent(String username, int eventId) {
        dynamoDbUtil.removeUserRegisteredEvents(username, eventId);
        return "Event unregistered successfully!";
    }
}