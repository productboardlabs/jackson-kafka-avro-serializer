package io.github.productboardlabs.kafka.serializers;

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
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.dataformat.avro.AvroMapper;
import tools.jackson.dataformat.avro.AvroSchema;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import static io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig.baseConfigDef;

public abstract class AbstractJacksonKafkaAvroDeserializer extends AbstractKafkaSchemaSerDe implements Deserializer<Object> {
    private static final int MAGIC_BYTE_LENGTH = 1;
    private static final int SUBJECT_ID_LENGTH = Integer.BYTES;

    private final AvroMapper mapper;

    private static final int PREFIX_LENGTH = MAGIC_BYTE_LENGTH + SUBJECT_ID_LENGTH;

    /**
     * Deserialization fails if message is larger than given size.
     */
    private final int maxMessageSize;

    public AbstractJacksonKafkaAvroDeserializer() {
        this(Integer.MAX_VALUE);
    }
    public AbstractJacksonKafkaAvroDeserializer(int maxMessageSize) {
        mapper = createAvroMapper();
        this.maxMessageSize = maxMessageSize;
    }

    protected abstract Class<?> getClassFor(@NotNull String topic, @NotNull Schema schema);



    @NotNull
    protected AvroMapper createAvroMapper() {
        return createAvroMapperBuilder().build();
    }

    /**
     * A pre-configured AvroMapper builder that can be further customized in subclasses.
     */
    @NotNull
    protected AvroMapper.Builder createAvroMapperBuilder() {
        return Utils
                .createAvroMapperBuilder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
        return ((io.confluent.kafka.schemaregistry.avro.AvroSchema) getSchemaById(schemaId)).rawSchema();
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
    }

    @Override
    public Object deserialize(String topic, byte[] payload) {
        if (payload == null) {
            return null;
        }

        if (getMagicByte(payload) != MAGIC_BYTE) {
            throw new SerializationException("Unknown magic byte!");
        }

        if (payload.length > maxMessageSize) {
            throw new MessageTooLargeDeserializationException(topic, payload.length, maxMessageSize);
        }

        int schemaId = getSchemaId(payload);
        try {
            Schema schema = getSchema(schemaId);
            int dataLength = payload.length - PREFIX_LENGTH;
            if (isPrimitiveSchema(schema)) {
                return deserializePrimitive(payload, schema, dataLength);
            } else {
                return mapper.readerFor(getClassFor(topic, schema))
                        .with(new AvroSchema(schema))
                        .readValue(payload, PREFIX_LENGTH, dataLength);
            }
        } catch (IOException | RestClientException e) {
            throw new SerializationException("Error when deserializing", e);
        }
    }

    @Override
    public Object deserialize(String topic, Headers headers, byte[] payload) {
        return this.deserialize(topic, payload);
    }

    @Override
    public void close() {
    }
}
