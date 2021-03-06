package io.github.productboardlabs.kafka.serializers;


import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.junit.jupiter.api.Test;

import static io.github.productboardlabs.kafka.serializers.TestData.defaultConfig;
import static io.github.productboardlabs.kafka.serializers.TestData.generatedUser;
import static io.github.productboardlabs.kafka.serializers.TestData.topic;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultJacksonKafkaAvroDeserializerTest {
    private final AbstractJacksonKafkaAvroDeserializer deserializer;
    private final KafkaAvroSerializer standardSerializer;

    DefaultJacksonKafkaAvroDeserializerTest() {
        this.deserializer = new DefaultJacksonKafkaAvroDeserializer();
        this.deserializer.configure(defaultConfig(), false);

        this.standardSerializer = new KafkaAvroSerializer();
        this.standardSerializer.configure(defaultConfig(), false);
    }

    @Test
    void shouldDeserializeObject() {
        byte[] payload = standardSerializer.serialize(topic, generatedUser);
        assertThat(deserializer.deserialize(topic, payload)).isEqualTo(generatedUser);
    }
}
