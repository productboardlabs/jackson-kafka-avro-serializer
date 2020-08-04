package com.productboard.kafka.serializers;

public interface SerializationMapping {
    public SchemaMetadata getSchemaFor(String topic, Object object);
}

