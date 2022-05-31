package com.usertag.api.service;

import com.usertag.api.controller.UserInTO;
import com.usertag.api.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class UserService {

    private final ConcurrentMap<Integer, User> userMap;
    private final LocalDateTime ldt = LocalDateTime.now();
    Logger logger = LoggerFactory.getLogger(UserService.class);


    public UserService() {
        this.userMap = new ConcurrentHashMap<>();
    }

    public User createOrUpdate(UserInTO userIn) {
        if (null != get(userIn.getId())) {
            return update(userIn);
        }
        return create(userIn);
    }

    public User create(UserInTO input) {
        final var userToAdd = User.builder()
                .id(input.getId())
                .tags(addAndRemoveTagsCreation(input.getAdd(), input.getRemove()))
                .timestampInMillis(ZonedDateTime.of(ldt, ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
        logger.info("Adding user with id: {} and tags: {}", userToAdd.getId(), userToAdd.getTags());
        userMap.put(userToAdd.getId(), userToAdd);
        return userToAdd;
    }

    private SortedSet<String> addAndRemoveTagsCreation(SortedSet<String> toAdd, SortedSet<String> toRemove) {
        toAdd.removeAll(toRemove);
        return toAdd;
    }

    public User get(Integer id) {
        return userMap.get(id);
    }

    public User update(UserInTO userIn) {
        if (null != get(userIn.getId())) {
            var user = get(userIn.getId());
            user.setTimestampInMillis(ZonedDateTime.of(ldt, ZoneId.systemDefault()).toInstant().toEpochMilli());
            return userMap.replace(userIn.getId(), Objects.requireNonNull(mapUserInToUser(userIn)));
        }
        return null;
    }

    private User mapUserInToUser(UserInTO userIn) {
        final var userToUpdate = get(userIn.getId());
        userToUpdate.setTags(addAndRemoveTagsUpdate(userIn.getAdd(), userIn.getRemove(), userToUpdate.getTags()));
        logger.info("User {} already exist, updating it with new tags: {}", userIn.getId(), userToUpdate.getTags());
        return userToUpdate;
    }

    private SortedSet<String> addAndRemoveTagsUpdate(SortedSet<String> toAdd, SortedSet<String> toRemove, SortedSet<String> currentTags) {
        toAdd.removeAll(toRemove);
        currentTags.removeAll(toRemove);
        currentTags.addAll(toAdd);
        return currentTags;
    }
}
