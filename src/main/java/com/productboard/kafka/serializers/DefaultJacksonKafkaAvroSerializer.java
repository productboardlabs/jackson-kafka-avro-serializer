package com.productboard.kafka.serializers;

import java.util.Map;

import static com.productboard.kafka.serializers.Utils.parseSchema;

public class DefaultJacksonKafkaAvroSerializer extends AbstractJacksonKafkaAvroSerializer {
    private boolean isKey;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        super.configure(configs, isKey);
        this.isKey = isKey;
    }

    @Override
    protected SchemaMetadata getSchemaFor(String topic, Object object) {
        String subjectName = getSubjectName(topic, isKey, object, null);
        return new SchemaMetadata(parseSchema("avro_schemas/" + subjectName + ".avsc"), subjectName);
    }
}
