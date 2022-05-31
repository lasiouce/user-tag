package com.usertag.api.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.SortedSet;

@Getter
@AllArgsConstructor
public class UserInTO {

    @JsonProperty("user")
    int id;
    SortedSet<String> add;
    SortedSet<String> remove;
    long timestampInMillis;
}
