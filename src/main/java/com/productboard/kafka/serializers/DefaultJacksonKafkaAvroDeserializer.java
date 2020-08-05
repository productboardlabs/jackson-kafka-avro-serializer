package com.productboard.kafka.serializers;

import org.apache.avro.Schema;
import org.apache.kafka.common.errors.SerializationException;
import org.jetbrains.annotations.NotNull;

public class DefaultJacksonKafkaAvroDeserializer extends AbstractJacksonKafkaAvroDeserializer {
    @Override
    protected Class<?> getClassFor(@NotNull String topic, @NotNull Schema schema) {
        String className = getClassName(topic, schema);
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new SerializationException("Can not getClass for topic \"" + topic + "\" and schema \"" + className + "\"");
        }
    }

    @NotNull
    protected String getClassName(@NotNull String topic, @NotNull Schema schema) {
        return schema.getFullName();
    }
}
