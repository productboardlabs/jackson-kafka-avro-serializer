package com.productboard.kafka.serializers;


import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import static com.productboard.kafka.serializers.TestData.*;
import static io.confluent.kafka.serializers.KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;

class JacksonKafkaAvroSerializerTest {
    private final JacksonKafkaAvroSerializer serializer;
    private final KafkaAvroDeserializer standardDeserializer;

    JacksonKafkaAvroSerializerTest() {
        this.serializer = new JacksonKafkaAvroSerializer() {
            @Override
            protected SerializationMapping getSerializationMapping() {
                return new TestSerializationMapping();
            }
        };
        this.serializer.configure(defaultConfig(), false);

        Map<String, Object> config = defaultConfig();
        config.put(SPECIFIC_AVRO_READER_CONFIG, true);
        this.standardDeserializer = new KafkaAvroDeserializer();
        this.standardDeserializer.configure(config, false);
    }


    @ParameterizedTest
    @MethodSource("basicTypes")
    void shouldSerializePrimitive(Object value) {
        byte[] payload = serializer.serialize(null, value);
        assertThat(standardDeserializer.deserialize(null, payload)).isEqualTo(value);
    }

    @Test
    void shouldSerializeObject() {
        byte[] payload = serializer.serialize(null, simpleUser);
        assertThat(standardDeserializer.deserialize(null, payload)).isEqualTo(generatedUser);
    }
    //TODO: Byte array
    // TODO: Use latest version
    // TODO: Do not register schema

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

    private static class TestSerializationMapping implements SerializationMapping {
        @Override
        public SchemaMetadata getSchemaFor(String topic, Object object) {
            return new SchemaMetadata(parseSchema("avro/sample.avsc"), "random");
        }
    }

    private static Schema parseSchema(String name) {
        try {
            return new Schema.Parser().parse(Thread.currentThread().getContextClassLoader().getResourceAsStream(name));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
