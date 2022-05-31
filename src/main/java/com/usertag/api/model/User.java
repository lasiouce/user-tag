package com.usertag.api.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.SortedSet;

@Builder
@Getter
@Setter
public class User {
    @JsonProperty("user")
    private int id;
    private SortedSet<String> tags;
    @JsonIgnore
    private long timestampInMillis;
}
