package com.productboard.kafka.serializers;

import example.avro.User;
import org.junit.jupiter.params.provider.Arguments;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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
        return defaultConfig("mock://test");
    }

    static Map<String, Object> defaultConfig(String schemaRegistryUrl) {
        Map<String, Object> config = new HashMap<>();
        config.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        return config;
    }

    static Stream<Arguments> basicTypes() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of("test"),
                Arguments.of(1),
                Arguments.of(3.14f),
                Arguments.of(3.14d),
                Arguments.of(2L)
        );
    }
}
