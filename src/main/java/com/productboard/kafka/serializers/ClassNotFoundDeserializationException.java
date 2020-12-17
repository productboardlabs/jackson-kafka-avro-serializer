package com.productboard.kafka.serializers;

import org.apache.kafka.common.errors.SerializationException;

/**
 * Thrown when the class to be deserialized to can not be found.
 */
public class ClassNotFoundDeserializationException extends SerializationException {
    ClassNotFoundDeserializationException(String className, String topic) {
        super("Can not get class \"" + className + "\" for topic \"" + topic + "\"");
    }
}
