package com.productboard.kafka.serializers;

import example.avro.User;

import java.util.HashMap;
import java.util.Map;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;

class TestData {
    static final SimpleUser simpleUser = new SimpleUser("John", 42, null);

    static final User generatedUser = new User();

    static final String topic = "topic";

    static {
        generatedUser.setName("John");
        generatedUser.setFavoriteNumber(42);
    }

    static Map<String, Object> defaultConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(SCHEMA_REGISTRY_URL_CONFIG, "mock://test");
        return config;
    }

}
