package io.github.productboardlabs.kafka.serializers;

import org.apache.kafka.common.errors.SerializationException;

/**
 * Thrown when the message is larger than limit set in the AbstractJacksonKafkaAvroDeserializer.
 */
public class MessageTooLargeDeserializationException extends SerializationException {
    private final String topic;
    private final int messageSize;
    private final int maxMessageSize;

    MessageTooLargeDeserializationException(String topic, int messageSize, int maxMessageSize) {
        super("Message in topic " + topic + " was larger than the limit. size=" + messageSize + " maxMessageSize=" + maxMessageSize);
        this.topic = topic;
        this.messageSize = messageSize;
        this.maxMessageSize = maxMessageSize;
    }

    public String getTopic() {
        return topic;
    }

    public int getMessageSize() {
        return messageSize;
    }

    public int getMaxMessageSize() {
        return maxMessageSize;
    }
}
