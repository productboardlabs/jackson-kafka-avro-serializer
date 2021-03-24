package io.github.productboardlabs.kafka.serializers;

import org.apache.avro.Schema;
import org.jetbrains.annotations.NotNull;

public class SchemaMetadata {
    private final Schema schema;
    private final String subject;


    public SchemaMetadata(@NotNull Schema schema, @NotNull String subject) {
        this.schema = schema;
        this.subject = subject;
    }

    @NotNull
    public Schema getSchema() {
        return schema;
    }

    @NotNull
    public String getSubject() {
        return subject;
    }
}
