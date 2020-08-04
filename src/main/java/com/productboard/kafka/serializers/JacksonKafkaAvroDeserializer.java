package com.productboard.kafka.serializers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import com.fasterxml.jackson.dataformat.avro.AvroSchema;
import io.confluent.kafka.schemaregistry.avro.AvroSchemaProvider;
import io.confluent.kafka.schemaregistry.avro.AvroSchemaUtils;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDe;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.baseConfigDef;

public class JacksonKafkaAvroDeserializer extends AbstractKafkaSchemaSerDe implements Deserializer<Object>  {
    private static final int MAGIC_BYTE_LENGTH = 1;
    private static final int SUBJECT_ID_LENGTH = Integer.BYTES;

    private final AvroMapper mapper;
    private DeserializationMapping deserializationMapping;

    private static final int PREFIX_LENGTH = MAGIC_BYTE_LENGTH + SUBJECT_ID_LENGTH;

    public JacksonKafkaAvroDeserializer() {
        mapper = createAvroMapper();
    }

    protected AvroMapper createAvroMapper() {
        AvroMapper mapper = new AvroMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    private Object deserializePrimitive(byte[] payload, Schema schema, int dataLength) throws IOException {
        Object result = new GenericDatumReader<>(schema)
                .read(null, DecoderFactory.get().binaryDecoder(payload, PREFIX_LENGTH, dataLength, null));
        return schema.getType().equals(Schema.Type.STRING) ? result.toString() : result;
    }

    private boolean isPrimitiveSchema(Schema schema) {
        return AvroSchemaUtils.getPrimitiveSchemas().containsValue(schema);
    }

    private Schema getSchema(int schemaId) throws IOException, RestClientException {
        return schemaRegistry.getById(schemaId);
    }

    private byte getMagicByte(byte[] payload) {
        return payload[0];
    }

    private int getSchemaId(byte[] payload) {
        return ByteBuffer.wrap(payload, MAGIC_BYTE_LENGTH, SUBJECT_ID_LENGTH).getInt();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.configureClientProperties(new AbstractKafkaSchemaSerDeConfig(baseConfigDef(), configs), new AvroSchemaProvider());
        this.deserializationMapping= getDeserializationMapping();
    }

    @Override
    public Object deserialize(String topic, byte[] payload) {
        if (payload == null) {
            return null;
        }

        if (getMagicByte(payload) != MAGIC_BYTE) {
            throw new SerializationException("Unknown magic byte!");
        }
        int schemaId = getSchemaId(payload);
        try {
            Schema schema = getSchema(schemaId);
            int dataLength = payload.length - PREFIX_LENGTH;
            if (isPrimitiveSchema(schema)) {
                return deserializePrimitive(payload, schema, dataLength);
            } else {
                return mapper.readerFor(deserializationMapping.getClassFor(topic, schema))
                        .with(new AvroSchema(schema))
                        .readValue(payload, PREFIX_LENGTH, dataLength);
            }
        } catch (IOException | RestClientException e) {
            throw new SerializationException("Error when deserializing", e);
        }
    }

    protected DeserializationMapping getDeserializationMapping() {
        throw new IllegalStateException("TODO");
    }

    @Override
    public Object deserialize(String topic, Headers headers, byte[] payload) {
        return this.deserialize(topic, payload);
    }

    @Override
    public void close() {
    }
}
