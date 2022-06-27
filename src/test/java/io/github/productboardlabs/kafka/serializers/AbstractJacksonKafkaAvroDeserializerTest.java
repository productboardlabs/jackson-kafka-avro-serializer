package io.github.productboardlabs.kafka.serializers;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import example.avro.User;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.kafka.common.errors.SerializationException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.util.StringUtils;

import java.util.stream.Stream;

import static io.github.productboardlabs.kafka.serializers.TestData.defaultConfig;
import static io.github.productboardlabs.kafka.serializers.TestData.generatedUser;
import static io.github.productboardlabs.kafka.serializers.TestData.simpleUser;
import static io.github.productboardlabs.kafka.serializers.TestData.topic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        try (AbstractJacksonKafkaAvroDeserializer deserializer = new TestDeserializer()) {
            deserializer.configure(defaultConfig(), false);

            byte[] payload = standardSerializer.serialize(topic, generatedUser);
            assertThat(deserializer.deserialize("generated", payload)).isEqualTo(generatedUser);
        }
    }

    @Test
    void shouldFailOnTooLargeMessage() {
        try (AbstractJacksonKafkaAvroDeserializer deserializer = new TestDeserializer()) {
            deserializer.configure(defaultConfig(), false);

            User user = new User();
            user.setName("Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            byte[] payload = standardSerializer.serialize(topic, user);
            assertThatThrownBy(() -> deserializer.deserialize("generated", payload))
                    .isInstanceOf(SerializationException.class);
        }
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

    private static class TestDeserializer extends AbstractJacksonKafkaAvroDeserializer {
        public TestDeserializer() {
            super(50);
        }

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
    }
}
