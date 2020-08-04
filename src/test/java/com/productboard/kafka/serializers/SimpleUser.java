package com.productboard.kafka.serializers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimpleUser {
    private final String name;
    private final Integer favoriteNumber;
    private final String favoriteColor;

    @JsonCreator
    public SimpleUser(
            @JsonProperty("name") String name,
            @JsonProperty("favorite_number") Integer favoriteNumber,
            @JsonProperty("favorite_color") String favoriteColor
    ) {
        this.name = name;
        this.favoriteNumber = favoriteNumber;
        this.favoriteColor = favoriteColor;
    }

    public String getName() {
        return name;
    }

    public Integer getFavoriteNumber() {
        return favoriteNumber;
    }

    public String getFavoriteColor() {
        return favoriteColor;
    }
}
