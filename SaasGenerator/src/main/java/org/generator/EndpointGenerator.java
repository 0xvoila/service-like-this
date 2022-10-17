package org.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.log4j.Logger;
import org.generator.models.RequestResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class EndpointGenerator {
    static Logger logger = Logger.getLogger(EndpointGenerator.class);
    static ObjectMapper mapper = new ObjectMapper();

    static Properties properties = new Properties();
    public static void main(String args[]) throws IOException{

        final Serde<String> stringSerde = Serdes.String();
        final StreamsBuilder builder = new StreamsBuilder();


        KStream<String, String> endpointInputTopicStream = builder.stream("input_endpointgenerator", Consumed.with(stringSerde, stringSerde));
        endpointInputTopicStream.flatMapValues((value) -> {
            try {
                return getEndpoints(mapper.readValue(value, RequestResponse.class));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).to("output_endpointgenerator", Produced.with(stringSerde, stringSerde));


        final KafkaStreams streams = new KafkaStreams(builder.build(), loadStreamConfig());
        final CountDownLatch latch = new CountDownLatch(10000000);

        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.exit(1);
        }
    }
    private static ArrayList<RequestResponse> getEndpoints(RequestResponse saasObject) throws ClassNotFoundException, JsonProcessingException, InstantiationException, IllegalAccessException {

        Properties properties = new Properties();
        properties.setProperty("okta","org.example.generator.OktaEndpointGenerator");
        Class<?>  cl = Class.forName(properties.getProperty(saasObject.getAppName()));
        logger.info("generating end points for app " + properties.toString() );
        logger.info(mapper.writeValueAsString(saasObject));

        EndpointGeneratorInterface c = (EndpointGeneratorInterface) cl.newInstance();
        ArrayList<RequestResponse> saaSObjectsList = c.getNextEndpoints(saasObject);

        logger.info("following end points are generated");
        saaSObjectsList.stream().forEach(x -> {
            try {
                logger.info(mapper.writeValueAsString(x));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        return saaSObjectsList;
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
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, "enginde3554");

        }
        return props;
    }

}
