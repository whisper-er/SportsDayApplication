package com.example.demo.controller;

import com.example.demo.model.Event;
import com.example.demo.model.EventRequest;
import com.example.demo.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventService eventService;

    @GetMapping("/all")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerEvent(@RequestBody EventRequest request) {
        boolean success = eventService.registerEvent(request.getUsername(), request.getEvent());
        String message = success ? "Event registered successfully." : "Failed to register event.";
        if(success){
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }

    @GetMapping("/registered")
    public ResponseEntity<List<Event>> getRegisteredEvents(@RequestParam String username) {
        List<Event> registeredEvents = eventService.getRegisteredEvents(username);
        return ResponseEntity.ok(registeredEvents);
    }

    @PostMapping("/unregister")
    public ResponseEntity<String> unregisterEvent(@RequestBody EventRequest request) {
        String message = eventService.unregisterEvent(request.getUsername(), request.getEvent().getId());
        return ResponseEntity.ok(message);
    }
}