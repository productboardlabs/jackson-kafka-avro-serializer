# Jackson Kafka Avro serializer

This is an implementation of Kafka Avro Serializer using Apache Jackson. In comparison to the official [Kafka Avro
Serializer](https://docs.confluent.io/current/schema-registry/serdes-develop/serdes-avro.html) it has the following advantages:

1. No code generation. No need to use ugly generated classes. You can use nice POJOs, object oriented programming.
2. Full power of Jackson
3. First-class support for Kotlin

Of course, code generation has its advantages. It takes care of configuration. Without it, we need to solve the configuration 
in some other way.

## Serialization
When serializing, we know the topic we want to send the message to and we have the object to be serialized. From that,
we need to get the schema to use for serialization and subject to verify the schema in the schema registry. 
 
## DefaultJacksonKafkaAvroSerializer
You can use `com.productboard.kafka.serializers.DefaultJacksonKafkaAvroSerializer` which uses [TopicNameStrategy](https://docs.confluent.io/current/schema-registry/serdes-develop/index.html#how-the-naming-strategies-work) to derive
the subject. Then it looks for `avro_schemas/{subject}.avsc` file in your classpath. If you are sending messages to topic 'topic', it will try to load the schema from `avro_schemas/topic-value.avsc`.
See the  [source](https://github.com/productboardlabs/jackson-kafka-avro-serializer/blob/master/src/main/java/com/productboard/kafka/serializers/DefaultJacksonKafkaAvroSerializer.java) for details.

Unfortunately, you can not use `RecordNameStrategy` or `TopicRecordNameStrategy` since we do not know the fully qualified name
of the record without finding the schema first. 

## AbstractJacksonKafkaAvroSerializer
For more complex scenarios extend `com.productboard.kafka.serializers.AbstractJacksonKafkaAvroSerializer` and implement the 
schema resolution by yourself, it's quite easy, just implement the `getSchemaFor(String topic, Object value)` method.

## Deserialization
When deserializing, we have the schema and the topic name for which we need to find the class to deserialize to.

## DefaultJacksonKafkaAvroSerializer
`com.productboard.kafka.serializers.DefaultJacksonKafkaAvroDeserializer` takes the fully qualified record name and tries to
deserialize to class with the same qualified name. If you need to change the class-name or package name, just override the
`getClassName(String topic, Schema schema)` method.

## AbstractJacksonKafkaAvroDeserializer
If you need anything more complex, just implement `com.productboard.kafka.serializers.AbstractJacksonKafkaAvroDeserializer` 
and its`getClassFor(String topic, Schema schema)` method.

# Installation
The package is available at JCenter.

## Maven

```xml
<repositories>
    <repository>
      <id>jcenter</id>
      <url>https://jcenter.bintray.com/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.productboard</groupId>
        <artifactId>jackson-kafka-avro-serializer</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
``` 

## Gradle

```
implementation("com.productboard:jackson-kafka-avro-serializer:0.1.0")
```
 
 TODO:
 - Work without schema registry
 - Support for use latest version flag 
 - Do not use deprecated methods from Kafka Avro serializer



