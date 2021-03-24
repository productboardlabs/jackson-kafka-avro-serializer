package io.github.productboardlabs.kafka.serializers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

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

    @JsonProperty("favorite_number")
    public Integer getFavoriteNumber() {
        return favoriteNumber;
    }

    @JsonProperty("favorite_color")
    public String getFavoriteColor() {
        return favoriteColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleUser that = (SimpleUser) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(favoriteNumber, that.favoriteNumber) &&
                Objects.equals(favoriteColor, that.favoriteColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, favoriteNumber, favoriteColor);
    }
}
