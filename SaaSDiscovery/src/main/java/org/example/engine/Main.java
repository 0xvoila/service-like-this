package org.example.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
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
import org.example.models.RequestResponse;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;
import java.util.concurrent.*;

public class Main {

    static Logger logger = Logger.getLogger(Main.class);
    static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws  IOException {

        final Serde<String> stringSerde = Serdes.String();
        final StreamsBuilder builder = new StreamsBuilder();

        builder.table("first_topic",Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as("first_topic_ktable").withKeySerde(stringSerde).withValueSerde(stringSerde));
//        builder.stream("first_topic", Consumed.with(stringSerde,stringSerde)).map((key, value) -> {

//                    try {
//                        return KeyValue.pair("12233",mapper.writeValueAsString(new RequestResponse("12233", "Google", "okta", null, null, null)
//                                                                            ));
//                    } catch (JsonProcessingException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//        ).to("input_endpointgenerator", Produced.with(stringSerde, stringSerde));
//        KStream<String, String> endpointOutputTopicStream = builder.stream("output_endpointgenerator", Consumed.with(stringSerde, stringSerde));
//        KStream<String, String> schedulerOutputTopicStream = builder.stream("output_scheduler", Consumed.with(stringSerde, stringSerde));
//        KStream<String, String> downloaderOutputTopicStream = builder.stream("output_downloader", Consumed.with(stringSerde, stringSerde));
//
//        endpointOutputTopicStream.to("input_scheduler", Produced.with(stringSerde, stringSerde));
//        schedulerOutputTopicStream.to("input_downloader", Produced.with(stringSerde, stringSerde));
//        downloaderOutputTopicStream.to("input_endpointgenerator", Produced.with(stringSerde, stringSerde));

        final KafkaStreams streams = new KafkaStreams(builder.build(),loadStreamConfig());
        final CountDownLatch latch = new CountDownLatch(10000000);

        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.exit(1);
        }

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
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, "engine");

        }

        return props;
    }
}
