package com.productboard.kafka.serializers;

import org.apache.avro.Schema;
import org.jetbrains.annotations.NotNull;

public interface DeserializationMapping {
    @NotNull Class<?> getClassFor(@NotNull String topic, @NotNull Schema schema);
}
