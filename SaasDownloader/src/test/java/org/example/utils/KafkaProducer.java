package org.example.utils;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.StreamsConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KafkaProducer {

    public static void produce(String key, String topic, int noOfFailedMessages, int noOfSuccessMessages ) throws IOException {

        Producer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer
                <String, String>(loadStreamConfig());

        for(int i = 1; i <= noOfSuccessMessages; i++){
            String url = "https://jsonplaceholder.typicode.com/todos/" + i;
            producer.send(new ProducerRecord<String, String>(topic,
                    key, "{\"syncId\":\"4567\", \"appName\":\"okta\", \"accountName\":\"Google\", \"resourceName\":\"users\", \"request\":{\"url\":\"" + url + "\", \"uuid\": \"" + i + "\" }}"));
        }

        for(int i = -noOfFailedMessages; i < 0; i++){
            String url = "https://jsonplaceholder.typicode.com/todos/" + i;
            producer.send(new ProducerRecord<String, String>(topic,
                    key, "{\"syncId\":\"4567\", \"appName\":\"okta\", \"accountName\":\"Google\", \"resourceName\":\"users\", \"request\":{\"url\":\"" + url + "\", \"uuid\": \"" + i + "\" }}"));
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
