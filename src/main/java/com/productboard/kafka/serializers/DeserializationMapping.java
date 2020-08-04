package com.productboard.kafka.serializers;

import org.apache.avro.Schema;

public interface DeserializationMapping {
    Class<?> getClassFor(String topic, Schema schema);
}
