package com.productboard.kafka.serializers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    protected SchemaMetadata getSchemaFor(@NotNull String topic, @Nullable Object object) {
        String subjectName = getSubjectName(topic, isKey, object, null);
        return new SchemaMetadata(parseSchema(getPath(topic, object, subjectName)), subjectName);
    }

    @NotNull
    protected String getPath(@NotNull String topic, @Nullable Object object, @NotNull String subjectName) {
        return "avro_schemas/" + subjectName + ".avsc";
    }
}
