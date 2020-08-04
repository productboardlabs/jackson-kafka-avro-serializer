package com.productboard.kafka.serializers;

import org.apache.avro.Schema;

public class SchemaMetadata {
    private final Schema schema;
    private final String subject;

    public SchemaMetadata(Schema schema, String subject) {
        this.schema = schema;
        this.subject = subject;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getSubject() {
        return subject;
    }
}
