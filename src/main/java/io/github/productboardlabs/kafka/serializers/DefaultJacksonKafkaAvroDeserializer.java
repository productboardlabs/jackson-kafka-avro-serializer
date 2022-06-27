package io.github.productboardlabs.kafka.serializers;

import org.apache.avro.Schema;
import org.jetbrains.annotations.NotNull;

public class DefaultJacksonKafkaAvroDeserializer extends AbstractJacksonKafkaAvroDeserializer {
    public DefaultJacksonKafkaAvroDeserializer() {
        super();
    }

    public DefaultJacksonKafkaAvroDeserializer(int maxMessageSize) {
        super(maxMessageSize);
    }

    @Override
    protected Class<?> getClassFor(@NotNull String topic, @NotNull Schema schema) {
        String className = getClassName(topic, schema);
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundDeserializationException(className, topic);
        }
    }

    @NotNull
    protected String getClassName(@NotNull String topic, @NotNull Schema schema) {
        return schema.getFullName();
    }
}
