package com.example.demo.util;

import com.example.demo.model.User;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DynamoDbUtil {
    private static final Logger logger = LoggerFactory.getLogger(DynamoDbUtil.class);
    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "UserTable";

    public DynamoDbUtil(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void saveUser(User user) {
        Map<String, AttributeValue> itemValues = new HashMap<>();
        itemValues.put("username", AttributeValue.builder().s(user.getUsername()).build());
        itemValues.put("password", AttributeValue.builder().s(user.getPassword()).build());
        itemValues.put("emailId", AttributeValue.builder().s(user.getEmailId()).build());
        itemValues.put("firstName", AttributeValue.builder().s(user.getFirstName()).build());
        itemValues.put("lastName", AttributeValue.builder().s(user.getLastName()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        dynamoDbClient.putItem(request);
    }

    public User getUserByUsername(String username) {
        logger.info("Querying for user: {}", username);
        QueryRequest request = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("username = :username") // Specify the condition
                .expressionAttributeValues(Map.of(
                        ":username", AttributeValue.builder().s(username).build()
                ))
                .build();

        QueryResponse response = dynamoDbClient.query(request);
        List<User> users = new ArrayList<>();

        // Iterate through the items returned by the query
        for (Map<String, AttributeValue> item : response.items()) {
            User user = new User();
            user.setUsername(item.get("username").s());
            user.setPassword(item.get("password").s());
            user.setEmailId(item.get("emailId").s());
            if (item.containsKey("registeredEvents")) {
                user.setRegisteredEvents(item.get("registeredEvents").ns().stream()
                    .map(Integer::valueOf)
                    .collect(Collectors.toSet()));
            } else {
                user.setRegisteredEvents(new HashSet<>()); // Initialize to empty set if not present
            }
            users.add(user);
        }

        return users.isEmpty() ? null : users.getFirst();
    }

    public Set<Integer> getUserRegisteredEvents(String username) {
        User user = getUserByUsername(username);
        if (user != null) {
            Set<Integer> registeredEvents = new HashSet<>(user.getRegisteredEvents() != null ? user.getRegisteredEvents() : Collections.emptyList());
            logger.info("Registered events for user {} : {}" , username, registeredEvents);
            return registeredEvents;
        }
        return new HashSet<>();
    }

    public boolean updateUserRegisteredEvents(String username, Integer eventId) {
        User user = getUserByUsername(username);
        if (user == null) {
            logger.error("User not found: {}", username);
            return false; // Or handle this case as needed
        }

        // Retrieve current event IDs and add the new one
        Set<Integer> currentEventIds = Optional.ofNullable(user.getRegisteredEvents()).orElse(new HashSet<>());
        currentEventIds.add(eventId);

        // Create the update item request
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                    "username", AttributeValue.builder().s(username).build(),
                    "emailId", AttributeValue.builder().s(user.getEmailId()).build()))
                .updateExpression("SET registeredEvents = :registeredEvents")
                .expressionAttributeValues(Map.of(
                        ":registeredEvents", AttributeValue.builder()
                                .ns(currentEventIds.stream()
                                        .map(String::valueOf) // Convert Integer to String
                                        .collect(Collectors.toSet())) // Collect to a Set
                                .build()
                ))
                .build();

        try {
            dynamoDbClient.updateItem(updateItemRequest);
        } catch (DynamoDbException e) {
            logger.error("Error occurred while updating event ids: {}", eventId, e);
            return false;
        }
        return true;
    }

    public void removeUserRegisteredEvents(String username, Integer eventId) {
        User user = getUserByUsername(username);
        if (user == null) {
            logger.error("User not found: {}", username);
            return;
        }
        Set<Integer> currentEventIds = Optional.ofNullable(user.getRegisteredEvents()).orElse(new HashSet<>());
        currentEventIds.remove(eventId);

        // Create the update item request
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(
                    "username", AttributeValue.builder().s(username).build(),
                    "emailId", AttributeValue.builder().s(user.getEmailId()).build()))
                .updateExpression("SET registeredEvents = :registeredEvents")
                .expressionAttributeValues(Map.of(
                        ":registeredEvents", AttributeValue.builder()
                                .ns(currentEventIds.stream()
                                        .map(String::valueOf) // Convert Integer to String
                                        .collect(Collectors.toSet())) // Collect to a Set
                                .build()
                ))
                .build();

        try {
            dynamoDbClient.updateItem(updateItemRequest);
        } catch (DynamoDbException e) {
            logger.error("Error occurred while removing event id: {}", eventId, e);
        }
    }
}
