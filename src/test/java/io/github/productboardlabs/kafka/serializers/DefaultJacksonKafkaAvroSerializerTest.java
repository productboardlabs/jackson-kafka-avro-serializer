package io.github.productboardlabs.kafka.serializers;


import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.schemaregistry.testutil.MockSchemaRegistry;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.common.errors.SerializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.productboardlabs.kafka.serializers.TestData.defaultConfig;
import static io.github.productboardlabs.kafka.serializers.TestData.generatedUser;
import static io.github.productboardlabs.kafka.serializers.TestData.simpleUser;
import static io.github.productboardlabs.kafka.serializers.TestData.topic;
import static io.github.productboardlabs.kafka.serializers.Utils.parseSchema;
import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS;
import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;
import static io.confluent.kafka.serializers.KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultJacksonKafkaAvroSerializerTest {
    private final KafkaAvroDeserializer standardDeserializer;

    DefaultJacksonKafkaAvroSerializerTest() {
        Map<String, Object> config = defaultConfig();
        config.put(SPECIFIC_AVRO_READER_CONFIG, true);
        this.standardDeserializer = new KafkaAvroDeserializer();
        this.standardDeserializer.configure(config, false);
    }

    private AbstractJacksonKafkaAvroSerializer defaultSerializer() {
        DefaultJacksonKafkaAvroSerializer serializer = new DefaultJacksonKafkaAvroSerializer();
        serializer.configure(defaultConfig(), false);
        return serializer;
    }


    @ParameterizedTest
    @MethodSource("basicTypes")
    void shouldSerializePrimitive(Object value) {
        byte[] payload = defaultSerializer().serialize(topic, value);
        assertThat(standardDeserializer.deserialize(topic, payload)).isEqualTo(value);
    }

    @Test
    void shouldSerializeObject() {
        byte[] payload = defaultSerializer().serialize(topic, simpleUser);
        assertThat(standardDeserializer.deserialize(topic, payload)).isEqualTo(generatedUser);
    }

    @Test
    void shouldSerializeByteArray() {
        defaultSerializer().serialize(topic, new byte[]{1, 2, 3});
    }

    @Test
    void shouldSerializeObjectWithoutAutomaticSchemaRegistry() throws IOException, RestClientException {
        DefaultJacksonKafkaAvroSerializer serializer = new DefaultJacksonKafkaAvroSerializer();
        String schemaScope = "no-auto";
        String schemaRegistryUrl = "mock://" + schemaScope;
        Map<String, Object> config = defaultConfig(schemaRegistryUrl);
        config.put(SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        config.put(AUTO_REGISTER_SCHEMAS, false);
        serializer.configure(config, false);

        // expect exeption - schema not registered
        assertThatThrownBy(() -> serializer.serialize(topic, simpleUser)).isInstanceOf(SerializationException.class);

        SchemaRegistryClient registryClient = MockSchemaRegistry.getClientForScope(schemaScope);
        registryClient.register("topic-value", parseSchema("avro_schemas/topic-value.avsc"));

        // should pass - schema registered
        assertThat(serializer.serialize(topic, simpleUser)).isNotNull();
    }

    // TODO: Use latest version
    // TODO: Support byte-array

    static Stream<Arguments> basicTypes() {
        return TestData.basicTypes();
    }
}
