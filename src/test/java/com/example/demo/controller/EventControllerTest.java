package com.example.demo.controller;

import com.example.demo.model.Event;
import com.example.demo.model.EventRequest;
import com.example.demo.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventControllerTest {

    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllEvents() {
        Event event1 = new Event(1, "Event 1", "Category 1", "2024-01-01T10:00", "2024-01-01T12:00");
        Event event2 = new Event(2, "Event 2", "Category 2", "2024-01-02T10:00", "2024-01-02T12:00");
        List<Event> events = Arrays.asList(event1, event2);

        when(eventService.getAllEvents()).thenReturn(events);

        ResponseEntity<List<Event>> response = eventController.getAllEvents();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(events, response.getBody());
        verify(eventService, times(1)).getAllEvents();
    }

    @Test
    void testRegisterEventSuccess() {
        EventRequest request = new EventRequest();
        request.setUsername("user1");
        request.setEvent(new Event(1, "Event 1", "Category 1", "2024-01-01T10:00", "2024-01-01T12:00"));

        when(eventService.registerEvent(any(String.class), any(Event.class))).thenReturn(true);

        ResponseEntity<String> response = eventController.registerEvent(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Event registered successfully.", response.getBody());
        verify(eventService, times(1)).registerEvent("user1", request.getEvent());
    }

    @Test
    void testRegisterEventFailure() {
        EventRequest request = new EventRequest();
        request.setUsername("user1");
        request.setEvent(new Event(1, "Event 1", "Category 1", "2024-01-01T10:00", "2024-01-01T12:00"));

        when(eventService.registerEvent(any(String.class), any(Event.class))).thenReturn(false);

        ResponseEntity<String> response = eventController.registerEvent(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to register event.", response.getBody());
        verify(eventService, times(1)).registerEvent("user1", request.getEvent());
    }

    @Test
    void testGetRegisteredEvents() {
        Event event1 = new Event(1, "Event 1", "Category 1", "2024-01-01T10:00", "2024-01-01T12:00");
        List<Event> registeredEvents = Arrays.asList(event1);

        when(eventService.getRegisteredEvents("user1")).thenReturn(registeredEvents);

        ResponseEntity<List<Event>> response = eventController.getRegisteredEvents("user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(registeredEvents, response.getBody());
        verify(eventService, times(1)).getRegisteredEvents("user1");
    }

    @Test
    void testUnregisterEvent() {
        EventRequest request = new EventRequest();
        request.setUsername("user1");
        request.setEvent(new Event(1, "Event 1", "Category 1", "2024-01-01T10:00", "2024-01-01T12:00"));
        String expectedMessage = "Event unregistered successfully.";

        when(eventService.unregisterEvent(any(String.class), any(Integer.class))).thenReturn(expectedMessage);

        ResponseEntity<String> response = eventController.unregisterEvent(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
        verify(eventService, times(1)).unregisterEvent("user1", 1);
    }
}
