package com.usertag.api;

import com.usertag.api.controller.UserInTO;
import com.usertag.api.model.User;
import com.usertag.api.service.UserService;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceTest {

    private final UserService serviceUnderTest = new UserService();
    private final long timestamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault()).toInstant().toEpochMilli();

    @Test
    void shouldMapUserToUserTO_WithDuplicatesTagsInAddAndRemove() {
        // Given
        SortedSet<String> toAdd = new TreeSet<>();
        toAdd.add("tag1");
        toAdd.add("tag2");
        toAdd.add("beyhive_member");

        SortedSet<String> toRemove = new TreeSet<>();
        toRemove.add("tag2");
        toRemove.add("tag1");


        UserInTO userIn = new UserInTO(1, toAdd, toRemove, timestamp);

        // When
        User user = serviceUnderTest.create(userIn);

        // Then
        assertEquals(user.getId(), userIn.getId());
        assertEquals(user.getTags(), Set.of("beyhive_member"));
    }

    @Test
    void givenUserIsPost_whenUserInfoIsRetrieved_then200IsReceived()
            throws IOException {

        // Given
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost("http://localhost:1917/api/tags");

        String json = "{\"user\":\"1\",\"add\":[\"tag1\",\"tag2\"], \"remove\":[\"\"], \"timestamp\":\"" + timestamp + "\"}";
        StringEntity entity = new StringEntity(json);
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");

        // When
        CloseableHttpResponse response = client.execute(request);

        // Then
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        client.close();

    }

    @Test
    void givenBadUserRequest_whenUserInfoIsRetrieved_then400IsReceived()
            throws IOException {

        // Given
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost("http://localhost:1917/api/tags");

        String json = "{\"user\":\"1\",\"add\":[\"" + timestamp + "\"}";
        StringEntity entity = new StringEntity(json);
        request.setEntity(entity);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");

        // When
        CloseableHttpResponse response = client.execute(request);

        // Then
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(400);
        client.close();

    }
}
