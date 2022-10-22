package org.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.log4j.Logger;
import org.generator.models.AggregateReport;
import org.generator.models.JacksonTest;
import org.generator.models.RequestResponse;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class EndpointGenerator {
    static Logger logger = Logger.getLogger(EndpointGenerator.class);
    static ObjectMapper mapper = new ObjectMapper();

    static Properties properties = new Properties();
    public static void main(String args[]) throws IOException{

        Producer<String, String> producer = new KafkaProducer
                <String, String>(loadStreamConfig());

        for(int i = 0; i < 10000; i++){
            producer.send(new ProducerRecord<String, String>("downloader-input",
                    "4556", "{\"syncId\":\"4567\", \"appName\":\"okta\", \"accountName\":\"Google\", \"resourceName\":\"users\", \"request\":{\"url\":\"https://jsonplaceholder.typicode.com/todos/1\"}}"));
        }

        System.out.println("Message sent successfully");
        producer.close();

    }

    public static Properties loadStreamConfig() throws IOException {
        String resourceName = "kafka.properties"; // could also be a constant
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            try {
                props.load(resourceStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, "group11");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put("key.serializer",
                    "org.apache.kafka.common.serialization.StringSerializer");

            props.put("value.serializer",
                    "org.apache.kafka.common.serialization.StringSerializer");

        }
        return props;
    }

}
