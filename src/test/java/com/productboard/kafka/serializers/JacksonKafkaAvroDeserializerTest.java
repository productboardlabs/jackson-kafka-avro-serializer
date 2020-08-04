package com.productboard.kafka.serializers;


import example.avro.User;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;

class JacksonKafkaAvroDeserializerTest {
    private final JacksonKafkaAvroDeserializer deserializer;
    private final KafkaAvroSerializer standardSerializer;

    JacksonKafkaAvroDeserializerTest() {
        this.deserializer = new JacksonKafkaAvroDeserializer() {
            @Override
            protected DeserializationMapping getDeserializationMapping() {
                return new TestDeserializationMapping();
            }
        };
        Map<String, Object> config = new HashMap<>();
        config.put(SCHEMA_REGISTRY_URL_CONFIG, "mock://test");
        this.deserializer.configure(config, false);

        this.standardSerializer = new KafkaAvroSerializer();
        this.standardSerializer.configure(config, false);
    }


    @Test
    void shouldDeserializeNull() {
        assertThat(deserializer.deserialize(null, null)).isNull();
    }

    @Test
    void shouldDeserializeObject() {
        User value = new User();
        value.setName("John");
        value.setFavoriteNumber(42);

        byte[] payload = standardSerializer.serialize(null, value);
        assertThat(deserializer.deserialize("generated", payload)).isEqualTo(value);
        assertThat(deserializer.deserialize("simple", payload)).isEqualToComparingFieldByField(value);
    }

    @ParameterizedTest
    @MethodSource("basicTypes")
    void shouldDeserializePrimitive(Object value) {
        byte[] payload = standardSerializer.serialize(null, value);
        assertThat(deserializer.deserialize(null, payload)).isEqualTo(value);
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

    static class TestDeserializationMapping implements DeserializationMapping {

        @Override
        public Class<?> getClassFor(String topic, Schema schema) {
            if ("generated".equals(topic)) {
                return User.class;
            } else {
                return SimpleUser.class;
            }
        }
    }
}
