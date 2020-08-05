package com.productboard.kafka.serializers

import com.productboard.kafka.serializers.TestData.topic
import org.apache.avro.Schema
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KotlinTest {
    private val deserializer: DefaultJacksonKafkaAvroDeserializer = MyKotlinJacksonKafkaAvroDeserializer().apply {
        configure(TestData.defaultConfig(), false)
    }
    private val serializer: DefaultJacksonKafkaAvroSerializer = DefaultJacksonKafkaAvroSerializer().apply {
        configure(TestData.defaultConfig(), false)
    }

    @Test
    fun shouldDeserializeObject() {
        val user = UserKotlin("John", 42, null)
        val payload = serializer.serialize(topic, user)
        assertThat(deserializer.deserialize(topic, payload)).isEqualTo(user)
    }
}

private class MyKotlinJacksonKafkaAvroDeserializer : DefaultJacksonKafkaAvroDeserializer() {
    override fun getClassName(topic: String, schema: Schema): String {
        return "com.productboard.kafka.serializers.${schema.name}Kotlin"
    }
}
