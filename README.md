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
You can use `DefaultJacksonKafkaAvroSerializer` which uses [TopicNameStrategy](https://docs.confluent.io/current/schema-registry/serdes-develop/index.html#how-the-naming-strategies-work) to derive
the subject. Then it looks for `avro_schemas/{subject}.avsc` file in your classpath. If you are sending messages to topic 'topic', it will try to load the schema from `avro_schemas/topic-value.avsc`.
See the  [source](https://github.com/productboardlabs/jackson-kafka-avro-serializer/blob/master/src/main/java/com/productboard/kafka/serializers/DefaultJacksonKafkaAvroSerializer.java) for details.

Unfortunately, you can not use `RecordNameStrategy` or `TopicRecordNameStrategy` since we do not know the fully qualified name
of the record without finding the schema first. 

## AbstractJacksonKafkaAvroSerializer
For more complex scenarios extend `AbstractJacksonKafkaAvroSerializer` and implement the 
schema resolution by yourself, it's quite easy, just implement the `getSchemaFor(String topic, Object value)` method.

## Deserialization
When deserializing, we have the schema and the topic name for which we need to find the class to deserialize to.

## DefaultJacksonKafkaAvroDeserializer
`DefaultJacksonKafkaAvroDeserializer` takes the fully qualified record name and tries to
deserialize to class with the same qualified name. If you need to change the class-name or package name, just override the
`getClassName(String topic, Schema schema)` method.

## AbstractJacksonKafkaAvroDeserializer
If you need anything more complex, just implement `AbstractJacksonKafkaAvroDeserializer` 
and its`getClassFor(String topic, Schema schema)` method.

# Installation
The package is available at Maven Central.

## Maven

```xml
<dependencies>
    <dependency>
        <groupId>io.github.productboardlabs</groupId>
        <artifactId>jackson-kafka-avro-serializer</artifactId>
        <version>0.7.1</version>
    </dependency>
</dependencies>
``` 

## Gradle

```
implementation("io.github.productboardlabs:jackson-kafka-avro-serializer:0.7.1")
```
 
 TODO:
 - Work without schema registry
 - Support for use latest version flag 
 - Do not use deprecated methods from Kafka Avro serializer

# Deploying of new release version

To deploy a new release version to Maven Central, follow the [following guide](DEPLOYMENT.md).

# Release notes

### 0.10.0 - Upgrade of jackson to v 3, support for java lower than 17 dropped

### 0.9.2 - Provide possibility to override getSchemaById() - fixed (thanks @valentin-braun)

### 0.9.1 - Provide possibility to override getSchemaById() (thanks @valentin-braun)

### 0.7.1 - Support for size limit in deserialization

### 0.7.0 - Upgrade dependencies and remove use of deprecated methods

### 0.6.0 - Auto-register Jackson modules + do not use depracated methods

### 0.5.1 - Update libraries

### 0.5.0 - Null can be serialized without auto schema registration 

### 0.4.0 - Released to Maven central - groupId and package name changed

### 0.3.0 - Upgrade to kafka-avro-serializer 6.0.1



