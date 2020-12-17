package com.productboard.kafka.serializers;

import org.apache.kafka.common.errors.SerializationException;

/**
 * Thrown when the class to be deserialized to can not be found.
 */
public class ClassNotFoundDeserializationException extends SerializationException {
    public ClassNotFoundDeserializationException(String className, String topic, String schema) {
        super("Can not get class \"" + className + "\" for topic \"" + topic + "\" and schema \"" + schema + "\"");
    }
}
