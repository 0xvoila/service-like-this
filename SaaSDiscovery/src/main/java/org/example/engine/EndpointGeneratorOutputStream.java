package org.example.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class EndpointGeneratorOutputStream {

    public static void main(String args[]){
        String resourceName = "kafka.properties"; // could also be a constant
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try(InputStream resourceStream = loader.getResourceAsStream(resourceName)){
            try {
                props.load(resourceStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, "engine-endpointgenerator-stream1");

        }
        catch(Exception e){

        }

        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();
        final StreamsBuilder builder = new StreamsBuilder();

        ObjectMapper mapper = new ObjectMapper();
        KStream<String, String> stream = builder.stream("first_topic", Consumed.with(stringSerde, stringSerde));

        stream.foreach((key, value) -> {
            try {
                System.out.println(mapper.readTree(value));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        final CountDownLatch latch = new CountDownLatch(12);

        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.exit(1);
        }


    }

}
