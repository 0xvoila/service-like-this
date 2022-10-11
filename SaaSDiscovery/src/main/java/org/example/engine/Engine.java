package org.example.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.log4j.Logger;
import org.example.Queue;
import org.example.downloader.Downloader;
import org.example.generator.EndpointGenerator;
import org.example.models.RequestResponse;
import org.example.scheduler.Scheduler;

import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.*;

public class Engine {

    EndpointGenerator endpointGenerator = new EndpointGenerator();
    Scheduler scheduler = new Scheduler();
    Downloader downloader = new Downloader();

    Logger logger = Logger.getLogger(Engine.class);
    ObjectMapper mapper = new ObjectMapper();

    public static void main( String[] args ) throws InterruptedException, IOException {

//            kafkaConsumer();

        Engine engine = new Engine();
        engine.start();
    }

    public void start() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            try {
                setup();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException | InterruptedException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        executorService.submit(() -> {
            try {
                consume();
            } catch (NamingException | IOException | TimeoutException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });



//        System.out.println("This is the end");
    }
    public void setup() throws InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException, JsonProcessingException {

        RequestResponse saaSObject = new RequestResponse("11312", "Google", "okta", null, null, null);
        logger.info("Got new sync request");
        logger.info(mapper.writeValueAsString(saaSObject));


        ArrayList<RequestResponse> saaSObjectsList = endpointGenerator.getNextEndpoints(saaSObject);
        logger.info("Endpoints generated for this sync are ");
        saaSObjectsList.stream().forEach(x -> {
            try {
                logger.info(mapper.writeValueAsString(x));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        scheduler.addEndPoints("Google",saaSObjectsList);
        logger.info("added these end points to scheduler");
        logger.info(mapper.writeValueAsString(saaSObject));


        while(true){
            if(scheduler.hasNext("Google")){
                Thread.sleep(10);
                ArrayList<RequestResponse> saaSObjects = scheduler.getEndpoints( "Google" , 1);
                logger.info("got these end points from scheduler to run ");
                saaSObjectsList.stream().forEach(x -> {
                    try {
                        logger.info(mapper.writeValueAsString(x));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
//                System.out.println("Urls is " + saaSObjects.get(0).getRequest().getURI());
                downloader.submit(saaSObjects);
                logger.info("submitted the endpoints to the downloader service");
            }

        }
    }
    public void consume() throws NamingException, IOException, TimeoutException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        while(true){
            RequestResponse x = Queue.queue.poll();

            if ( x != null){
                logger.info("below end points are downloaded from downloader service");

                logger.info(mapper.writeValueAsString(x));

                ArrayList<RequestResponse> saaSObjectsList = endpointGenerator.getNextEndpoints(x);
                logger.info("submitting below endpoint to generator service to generate end points");
                logger.info(mapper.writeValueAsString(x));
                logger.info("below endpoints are generated");
                saaSObjectsList.stream().forEach(y -> {
                    try {
                        logger.info(mapper.writeValueAsString(y));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

                scheduler.addEndPoints("Google",saaSObjectsList);
                logger.info("below endpoints are added to scheduler for schedule");
                saaSObjectsList.stream().forEach(y -> {
                    try {
                        logger.info(mapper.writeValueAsString(x));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

            }
        }
    }

    public void kafkaConsumer(){
        String resourceName = "kafka.properties"; // could also be a constant
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            props.load(resourceStream);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//            props.put(KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, RequestResponse.class);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-1");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            props.put(ProducerConfig.ACKS_CONFIG, "all");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

            final Consumer<String, String> consumer = new KafkaConsumer<String, String>(props);
            consumer.subscribe(Arrays.asList("first_topic"));

            Long total_count = 0L;

            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        String key = record.key();
                        String value = record.value();
//                        total_count += value.getCount();
                        System.out.printf("Consumed record with key %s and value %s, and updated total count to %d%n", key, value, total_count);
                    }
                }
            } finally {
                consumer.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
