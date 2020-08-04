package com.productboard.kafka.serializers;

import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchemaProvider;
import io.confluent.kafka.schemaregistry.avro.AvroSchemaUtils;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDe;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.baseConfigDef;

public abstract class AbstractJacksonKafkaAvroSerializer extends AbstractKafkaSchemaSerDe implements Serializer<Object> {
    private final Map<String, Schema> primitiveSchemas = AvroSchemaUtils.getPrimitiveSchemas();

    private final AvroMapper mapper = createAvroMapper();

    private SerializationMapping serializationMapping;

    @NotNull
    protected abstract SerializationMapping getSerializationMapping();

    @NotNull
    protected AvroMapper createAvroMapper() {
        return new AvroMapper();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.configureClientProperties(new AbstractKafkaSchemaSerDeConfig(baseConfigDef(), configs), new AvroSchemaProvider());
        serializationMapping = getSerializationMapping();
    }

    @Override
    public byte[] serialize(String topic, Object data) {
        SchemaMetadata schema = getSchema(topic, data);
        int schemaId;
        try {
            schemaId = schemaRegistry.register(schema.getSubject(), schema.getSchema());
        } catch (Exception e) {
            throw new SerializationException("Can not fetch schema", e);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            baos.write(MAGIC_BYTE);
            baos.write(ByteBuffer.allocate(Integer.BYTES).putInt(schemaId).array());
            write(data, schema.getSchema(), baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new SerializationException("Can not serialize data", e);
        }
    }

    private void write(Object data, Schema schema, ByteArrayOutputStream baos) throws IOException {
        if (isPrimitive(data)) {
            new GenericDatumWriter<>(schema, GenericData.get())
                    .write(data, EncoderFactory.get().directBinaryEncoder(baos, null));
        } else {
            mapper.writer(new AvroSchema(schema)).writeValue(baos, data);
        }
    }

    private boolean isPrimitive(Object data) {
        return data == null || data instanceof Number || data instanceof String;
    }

    private SchemaMetadata getSchema(String topic, Object object) {
        if (object == null) {
            return getPrimitiveSchema("Null");
        } else if (object instanceof Boolean) {
            return getPrimitiveSchema("Boolean");
        } else if (object instanceof Integer) {
            return getPrimitiveSchema("Integer");
        } else if (object instanceof Long) {
            return getPrimitiveSchema("Long");
        } else if (object instanceof Float) {
            return getPrimitiveSchema("Float");
        } else if (object instanceof Double) {
            return getPrimitiveSchema("Double");
        } else if (object instanceof CharSequence) {
            return getPrimitiveSchema("String");
        } else {
            return serializationMapping.getSchemaFor(topic, object);
        }
    }

    private SchemaMetadata getPrimitiveSchema(String type) {
        return new SchemaMetadata(primitiveSchemas.get(type), "null-value");
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Object data) {
        return serialize(topic, data);
    }


    @Override
    public void close() {
    }
}
