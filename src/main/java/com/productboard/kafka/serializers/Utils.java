package com.productboard.kafka.serializers;

import org.apache.avro.Schema;
import org.apache.kafka.common.errors.SerializationException;

import java.io.IOException;
import java.io.InputStream;

class Utils {
    static Schema parseSchema(String path) {
        try {
            try (InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
                if (resource == null) {
                    throw new SerializationException("Can not find resource \""+path+"\" in class path.");
                }
                return new Schema.Parser().parse(resource);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
