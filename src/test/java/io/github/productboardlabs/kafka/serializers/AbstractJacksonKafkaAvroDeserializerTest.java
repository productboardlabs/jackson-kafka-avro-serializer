package io.github.productboardlabs.kafka.serializers;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import example.avro.User;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.github.productboardlabs.kafka.serializers.TestData.defaultConfig;
import static io.github.productboardlabs.kafka.serializers.TestData.generatedUser;
import static io.github.productboardlabs.kafka.serializers.TestData.simpleUser;
import static io.github.productboardlabs.kafka.serializers.TestData.topic;
import static org.assertj.core.api.Assertions.assertThat;

class AbstractJacksonKafkaAvroDeserializerTest {
    private final AbstractJacksonKafkaAvroDeserializer deserializer;
    private final KafkaAvroSerializer standardSerializer;

    AbstractJacksonKafkaAvroDeserializerTest() {
        this.deserializer = new AbstractJacksonKafkaAvroDeserializer() {
            @Override
            protected Class<?> getClassFor(@NotNull String topic, @NotNull Schema schema) {
                return SimpleUser.class;
            }
        };
        this.deserializer.configure(defaultConfig(), false);

        this.standardSerializer = new KafkaAvroSerializer();
        this.standardSerializer.configure(defaultConfig(), false);
    }


    @Test
    void shouldDeserializeNull() {
        assertThat(deserializer.deserialize(topic, null)).isNull();
    }

    @Test
    void shouldDeserializeSimpleObject() {
        byte[] payload = standardSerializer.serialize(topic, generatedUser);
        assertThat(deserializer.deserialize("simple", payload)).isEqualTo(simpleUser);
    }

    @Test
    void shouldDeserializeGeneratedObject() {
        AbstractJacksonKafkaAvroDeserializer deserializer = new AbstractJacksonKafkaAvroDeserializer() {
            @Override
            protected Class<?> getClassFor(@NotNull String topic, @NotNull Schema schema) {
                return User.class;
            }

            @Override
            protected @NotNull AvroMapper createAvroMapper() {
                AvroMapper mapper = super.createAvroMapper();
                mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
                return mapper;
            }
        };
        deserializer.configure(defaultConfig(), false);

        byte[] payload = standardSerializer.serialize(topic, generatedUser);
        assertThat(deserializer.deserialize("generated", payload)).isEqualTo(generatedUser);
    }

    @ParameterizedTest
    @MethodSource("basicTypes")
    void shouldDeserializePrimitive(Object value) {
        byte[] payload = standardSerializer.serialize(topic, value);
        assertThat(deserializer.deserialize(topic, payload)).isEqualTo(value);
    }

    static Stream<Arguments> basicTypes() {
        return TestData.basicTypes();
    }
}
