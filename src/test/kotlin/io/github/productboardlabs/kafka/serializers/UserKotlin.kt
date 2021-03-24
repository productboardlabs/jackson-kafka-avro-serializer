package io.github.productboardlabs.kafka.serializers

import com.fasterxml.jackson.annotation.JsonProperty

data class UserKotlin(
    @JsonProperty("name") val name: String,
    @JsonProperty("favorite_number") val favoriteNumber: Int?,
    @JsonProperty("favorite_color") val favoriteColor: String?
)
