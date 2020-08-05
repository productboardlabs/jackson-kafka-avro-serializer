package com.productboard.kafka.serializers;

import com.fasterxml.jackson.dataformat.avro.AvroMapper;
import org.apache.avro.Schema;
import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    static Schema parseSchema(String path) {
        try {
            try (InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
                if (resource == null) {
                    throw new SerializationException("Can not find resource \"" + path + "\" in class path.");
                }
                return new Schema.Parser().parse(resource);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    static AvroMapper createAvroMapper() {
        AvroMapper mapper = new AvroMapper();
        try {
            mapper.registerModule((com.fasterxml.jackson.databind.Module) Class.forName("com.fasterxml.jackson.module.kotlin.KotlinModule").getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            logger.debug("Can not register Kotlin Module", e);
        }
        return mapper;
    }
}
