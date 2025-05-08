package com.example.demo.util;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DynamoDbUtilTest {

    @Mock
    private DynamoDbClient dynamoDbClient;

    @InjectMocks
    private DynamoDbUtil dynamoDbUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("hashedPassword");
        user.setEmailId("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");

        // Call the method
        dynamoDbUtil.saveUser(user);

        // Verify that the putItem method was called with the expected request
        verify(dynamoDbClient).putItem(any(PutItemRequest.class));
    }

    @Test
    void testGetUserByUsername_UserFound() {
        // Given
        String username = "existingUser";
        User expectedUser = new User();
        expectedUser.setUsername(username);
        expectedUser.setPassword("hashedpassword");
        expectedUser.setEmailId("test@example.com");

        Map<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("username", AttributeValue.builder().s(expectedUser.getUsername()).build());
        itemValues.put("password", AttributeValue.builder().s(expectedUser.getPassword()).build());
        itemValues.put("emailId", AttributeValue.builder().s(expectedUser.getEmailId()).build());

        // Mocking the response to return a user
        QueryResponse mockResponse = QueryResponse.builder()
                .items(Collections.singletonList(itemValues))
                .build();

        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(mockResponse);

        // When
        User user = dynamoDbUtil.getUserByUsername(username);

        // Then
        assertNotNull(user); // Assert that the user is found
        assertEquals(expectedUser.getUsername(), user.getUsername());
    }

    @Test
    void testGetUserByUsername_UserNotFound() {
        // Given
        String username = "nonexistentUser";

        // Mocking the response from DynamoDbClient when querying for the user
        QueryResponse mockResponse = QueryResponse.builder()
                .items(Collections.emptyList()) // No items means user not found
                .build();

        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(mockResponse);

        // When
        User user = dynamoDbUtil.getUserByUsername(username);

        // Then
        assertNull(user); // Assert that the returned user is null
    }

    @Test
    void testUpdateUserRegisteredEvents_UserFound() {
        String username = "testUser";
        int eventId = 1;

        User user = new User();
        user.setUsername(username);
        user.setEmailId("test@example.com");
        user.setRegisteredEvents(new HashSet<>());
        DynamoDbUtil spyDynamoDbUtil = spy(dynamoDbUtil);

        doReturn(user).when(spyDynamoDbUtil).getUserByUsername(username);

        // Call the method
        boolean result = spyDynamoDbUtil.updateUserRegisteredEvents(username, eventId);

        // Assertions
        assertTrue(result);
        assertTrue(user.getRegisteredEvents().contains(eventId));
        verify(dynamoDbClient).updateItem(any(UpdateItemRequest.class));
    }

    @Test
    void testUpdateUserRegisteredEvents_UserNotFound() {
        // Arrange
        String username = "nonexistentUser";
        Integer eventId = 1;

        // Create a spy for DynamoDbUtil
        DynamoDbUtil spyDynamoDbUtil = spy(dynamoDbUtil);

        // Mock the behavior of getUserByUsername to return null
        doReturn(null).when(spyDynamoDbUtil).getUserByUsername(username);

        // Act
        boolean result = spyDynamoDbUtil.updateUserRegisteredEvents(username, eventId);

        // Assert
        assertFalse(result);
        verify(dynamoDbClient, never()).updateItem(any(UpdateItemRequest.class)); // Verify that updateItem was never called
    }

    @Test
    void testRemoveUserRegisteredEvents_UserFound() {
        String username = "testUser";
        int eventId = 1;

        User user = new User();
        user.setUsername(username);
        user.setEmailId("test@example.com");
        user.setRegisteredEvents(new HashSet<>(Collections.singleton(eventId)));

        DynamoDbUtil spyDynamoDbUtil = spy(dynamoDbUtil);
        doReturn(user).when(spyDynamoDbUtil).getUserByUsername(username);

        // Call the method
        spyDynamoDbUtil.removeUserRegisteredEvents(username, eventId);

        // Assertions
        assertFalse(user.getRegisteredEvents().contains(eventId));
        verify(dynamoDbClient).updateItem(any(UpdateItemRequest.class));
    }

    @Test
    void testRemoveUserRegisteredEvents_UserNotFound() {
        String username = "unknownUser";
        int eventId = 1;

        DynamoDbUtil spyDynamoDbUtil = spy(dynamoDbUtil);
        doReturn(null).when(spyDynamoDbUtil).getUserByUsername(username);

        // Call the method
        spyDynamoDbUtil.removeUserRegisteredEvents(username, eventId);

        // Assertions
        verify(dynamoDbClient, never()).updateItem(any(UpdateItemRequest.class));
    }

    @Test
    void testGetUserByUsername_UserFound_WithRegisteredEvents() {
        String username = "existingUser";
        User expectedUser = new User();
        expectedUser.setUsername(username);
        expectedUser.setPassword("hashedPassword");
        expectedUser.setEmailId("user@example.com");
        expectedUser.setRegisteredEvents(new HashSet<>(Arrays.asList(1, 2, 3))); // Add registered events

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("username", AttributeValue.builder().s(expectedUser.getUsername()).build());
        item.put("password", AttributeValue.builder().s(expectedUser.getPassword()).build());
        item.put("emailId", AttributeValue.builder().s(expectedUser.getEmailId()).build());
        item.put("registeredEvents", AttributeValue.builder().ns("1", "2", "3").build()); // Simulate registered events

        QueryResponse queryResponse = mock(QueryResponse.class);
        when(queryResponse.items()).thenReturn(Collections.singletonList(item));
        when(dynamoDbClient.query(any(QueryRequest.class))).thenReturn(queryResponse);

        // Call the method
        User actualUser = dynamoDbUtil.getUserByUsername(username);

        // Assertions
        assertNotNull(actualUser);
        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        assertEquals(expectedUser.getPassword(), actualUser.getPassword());
        assertEquals(expectedUser.getEmailId(), actualUser.getEmailId());
        assertEquals(expectedUser.getRegisteredEvents(), actualUser.getRegisteredEvents());
    }

    @Test
    void testGetUserRegisteredEvents_UserFound_WithRegisteredEvents() {
        String username = "existingUser";
        User user = new User();
        user.setUsername(username);
        user.setPassword("hashedPassword");
        user.setEmailId("user@example.com");
        user.setRegisteredEvents(new HashSet<>(Arrays.asList(1, 2, 3))); // User has registered events

        // Mocking the method to return the user
        DynamoDbUtil spyDynamoDbUtil = spy(dynamoDbUtil);
        doReturn(user).when(spyDynamoDbUtil).getUserByUsername(username);

        // Call the method
        Set<Integer> registeredEvents = spyDynamoDbUtil.getUserRegisteredEvents(username);

        // Assertions
        assertNotNull(registeredEvents);
        assertEquals(3, registeredEvents.size());
        assertTrue(registeredEvents.containsAll(Arrays.asList(1, 2, 3)));
    }

    @Test
    void testGetUserRegisteredEvents_UserNotFound() {
        String username = "nonexistentUser";

        // Mocking the method to return null
        DynamoDbUtil spyDynamoDbUtil = spy(dynamoDbUtil);
        doReturn(null).when(spyDynamoDbUtil).getUserByUsername(username);

        // Call the method
        Set<Integer> registeredEvents = spyDynamoDbUtil.getUserRegisteredEvents(username);

        // Assertions
        assertNotNull(registeredEvents);
        assertTrue(registeredEvents.isEmpty());
    }

    @Test
    void testGetUserRegisteredEvents_UserFound_NoRegisteredEvents() {
        String username = "userWithNoEvents";
        User user = new User();
        user.setUsername(username);
        user.setPassword("hashedPassword");
        user.setEmailId("user@example.com");
        user.setRegisteredEvents(null); // User has no registered events

        // Mocking the method to return the user
        DynamoDbUtil spyDynamoDbUtil = spy(dynamoDbUtil);
        doReturn(user).when(spyDynamoDbUtil).getUserByUsername(username);

        // Call the method
        Set<Integer> registeredEvents = spyDynamoDbUtil.getUserRegisteredEvents(username);

        // Assertions
        assertNotNull(registeredEvents);
        assertTrue(registeredEvents.isEmpty()); // Should be empty since there are no registered events
    }

    @Test
    void testUpdateUserRegisteredEvents_ExceptionThrown() {
        String username = "testUser";
        Integer eventId = 1;

        // Mock user to be returned
        User user = new User();
        user.setUsername(username);
        user.setEmailId("user@example.com");
        user.setRegisteredEvents(new HashSet<>(Collections.singletonList(eventId)));
        Logger logger = LoggerFactory.getLogger(DynamoDbUtil.class);

        // Mock the getUserByUsername method
        DynamoDbUtil spyDynamoDbUtil = spy(dynamoDbUtil);
        doReturn(user).when(spyDynamoDbUtil).getUserByUsername(username);

        // Mock the updateItem method to throw DynamoDbException
        doThrow(DynamoDbException.class).when(dynamoDbClient).updateItem(any(UpdateItemRequest.class));

        // Call the method
        boolean result = spyDynamoDbUtil.updateUserRegisteredEvents(username, eventId);

        // Assertions
        assertFalse(result); // Should return false due to the exception
    }

    @Test
    void testRemoveUserRegisteredEvents_ExceptionThrown() {
        String username = "testUser";
        int eventId = 1;

        // Mock a user with registered events
        User user = new User();
        user.setUsername(username);
        user.setEmailId("test@example.com");
        user.setRegisteredEvents(new HashSet<>(Collections.singleton(eventId)));

        // Create a spy to allow partial mocking
        DynamoDbUtil spyDynamoDbUtil = Mockito.spy(dynamoDbUtil);
        Mockito.doReturn(user).when(spyDynamoDbUtil).getUserByUsername(username);

        // Mock the updateItem method to throw DynamoDbException
        doThrow(DynamoDbException.class).when(dynamoDbClient).updateItem(any(UpdateItemRequest.class));

        // Call the method
        spyDynamoDbUtil.removeUserRegisteredEvents(username, eventId);

        // Verify that the logger was called with the correct message
        // Note: Directly verifying the logger may require a logging framework setup to capture logs
        // You can also use a custom appender to check for the specific log output
        // For example, with Logback you can set up an appender to capture logs and then assert on it.

        // Assert that the updateItem method was called
        verify(dynamoDbClient).updateItem(any(UpdateItemRequest.class));
    }
}
