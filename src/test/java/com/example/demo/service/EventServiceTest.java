package com.example.demo.service;

import com.example.demo.model.Event;
import com.example.demo.util.DynamoDbUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private DynamoDbUtil dynamoDbUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllEvents() {
        List<Event> events = eventService.getAllEvents();
        assertEquals(10, events.size());
    }

    @Test
    void testGetEventByIdFound() {
        Event event = eventService.getEventById(1);
        assertNotNull(event);
        assertEquals("Butterfly 100M", event.getEventName());
    }

    @Test
    void testGetEventByIdNotFound() {
        Event event = eventService.getEventById(999); // Assuming this ID does not exist
        assertNull(event);
    }

    @Test
    void testGetRegisteredEventsWhenNoneFound() {
        when(dynamoDbUtil.getUserRegisteredEvents("user1")).thenReturn(new HashSet<>());

        List<Event> registeredEvents = eventService.getRegisteredEvents("user1");

        assertTrue(registeredEvents.isEmpty());
        verify(dynamoDbUtil, times(1)).getUserRegisteredEvents("user1");
    }

    @Test
    void testGetRegisteredEventsWithFoundEvents() {
        Set<Integer> registeredEventIds = new HashSet<>(Arrays.asList(1, 2));
        when(dynamoDbUtil.getUserRegisteredEvents("user1")).thenReturn(registeredEventIds);

        List<Event> registeredEvents = eventService.getRegisteredEvents("user1");

        assertEquals(2, registeredEvents.size());
        assertEquals("Butterfly 100M", registeredEvents.get(0).getEventName());
        assertEquals("Backstroke 100M", registeredEvents.get(1).getEventName());
    }

    @Test
    void testRegisterEventSuccess() {
        Event event = new Event(1, "Butterfly 100M", "Swimming", "2022-12-17 13:00:00", "2022-12-17 14:00:00");
        when(dynamoDbUtil.updateUserRegisteredEvents(any(String.class), anyInt())).thenReturn(true);

        boolean result = eventService.registerEvent("user1", event);

        assertTrue(result);
        verify(dynamoDbUtil, times(1)).updateUserRegisteredEvents("user1", 1);
    }

    @Test
    void testRegisterEventFailure() {
        Event event = new Event(1, "Butterfly 100M", "Swimming", "2022-12-17 13:00:00", "2022-12-17 14:00:00");
        when(dynamoDbUtil.updateUserRegisteredEvents(any(String.class), anyInt())).thenReturn(false);

        boolean result = eventService.registerEvent("user1", event);

        assertFalse(result);
        verify(dynamoDbUtil, times(1)).updateUserRegisteredEvents("user1", 1);
    }

    @Test
    void testUnregisterEvent() {
        String message = eventService.unregisterEvent("user1", 1);
        assertEquals("Event unregistered successfully!", message);
        verify(dynamoDbUtil, times(1)).removeUserRegisteredEvents("user1", 1);
    }
}
