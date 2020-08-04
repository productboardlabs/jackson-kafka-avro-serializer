package com.productboard.kafka.serializers;

import org.jetbrains.annotations.NotNull;

public interface SerializationMapping {

    @NotNull SchemaMetadata getSchemaFor(@NotNull String topic, @NotNull Object object);
}

