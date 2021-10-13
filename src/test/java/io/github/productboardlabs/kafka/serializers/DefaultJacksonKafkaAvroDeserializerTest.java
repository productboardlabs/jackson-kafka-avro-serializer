package io.github.productboardlabs.kafka.serializers;


import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static io.github.productboardlabs.kafka.serializers.TestData.defaultConfig;
import static io.github.productboardlabs.kafka.serializers.TestData.generatedUser;
import static io.github.productboardlabs.kafka.serializers.TestData.simpleUser;
import static io.github.productboardlabs.kafka.serializers.TestData.topic;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultJacksonKafkaAvroDeserializerTest {
    private final AbstractJacksonKafkaAvroDeserializer deserializer;
    private final KafkaAvroSerializer standardSerializer;

    DefaultJacksonKafkaAvroDeserializerTest() {
        this.deserializer = new DefaultJacksonKafkaAvroDeserializer() {
            @Override
            protected @NotNull String getClassName(@NotNull String topic, @NotNull Schema schema) {
                return SimpleUser.class.getCanonicalName();
            }
        };
        this.deserializer.configure(defaultConfig(), false);

        this.standardSerializer = new KafkaAvroSerializer();
        this.standardSerializer.configure(defaultConfig(), false);
    }

    @Test
    void shouldDeserializeObject() {
        byte[] payload = standardSerializer.serialize(topic, generatedUser);
        assertThat(deserializer.deserialize(topic, payload)).isEqualTo(simpleUser);
    }
}
