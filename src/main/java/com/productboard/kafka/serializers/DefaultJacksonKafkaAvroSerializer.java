package com.productboard.kafka.serializers;

import org.jetbrains.annotations.NotNull;

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
    @NotNull
    protected SchemaMetadata getSchemaFor(@NotNull String topic, @NotNull Object object) {
        String subjectName = getSubjectName(topic, isKey, object, null);
        return new SchemaMetadata(parseSchema(getPath(topic, object, subjectName)), subjectName);
    }

    @NotNull
    protected String getPath(@NotNull String topic, @NotNull Object value, @NotNull String subjectName) {
        return "avro_schemas/" + subjectName + ".avsc";
    }
}
