package org.scheduler;

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
import org.scheduler.models.RequestResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Scheduler {

    static HashMap<String, HashMap<String, Object>> database = new HashMap<>();
    static HashMap<String, Stack<RequestResponse>> accountUrls = new HashMap<>();

    static Logger logger = Logger.getLogger(Scheduler.class);
    static ObjectMapper mapper = new ObjectMapper();

    static Properties properties = new Properties();

    public static void main(String args[]) throws IOException {
        HashMap<String, Object> c = new HashMap<String, Object>();
        c.put("priority", 1);
        c.put("last_response_status", 200);
        c.put("api_threshold", 20 );
        c.put("server_response_time", 1000);
        database.put("Google", c);

        c.put("priority", 2);
        c.put("last_response_status", 500);
        c.put("api_threshold", 50 );
        c.put("server_response_time", 5000);
        database.put("Microsoft", c);


        final Serde<String> stringSerde = Serdes.String();
        final StreamsBuilder builder = new StreamsBuilder();


        KStream<String, String> endpointInputTopicStream = builder.stream("input_scheduler", Consumed.with(stringSerde, stringSerde));
        endpointInputTopicStream.map((key, value) -> {
            try {
                return KeyValue.pair(key, mapper.writeValueAsString(addEndpoints(Arrays.asList(mapper.readValue(value, RequestResponse[].class)))));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).to("output_scheduler", Produced.with(stringSerde, stringSerde));


        final KafkaStreams streams = new KafkaStreams(builder.build(), loadStreamConfig());
        final CountDownLatch latch = new CountDownLatch(10000000);

        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.exit(1);
        }
    }

    public static void addEndpoints(List<RequestResponse> urls){

        for (RequestResponse res:
             urls) {
            //        Here use the bloom filter if url in urls has been traversed
            Stack<RequestResponse> x = accountUrls.get(res.getAccountName());
            if ( x == null ) {
                x = new Stack<>();
            }
            x.push(res);
            accountUrls.put(res.getAccountName(), x);
        }
    }

    public ArrayList<RequestResponse> getEndpoints(String accountName, int number){

        Stack<RequestResponse> x = accountUrls.get(accountName);
        ArrayList<RequestResponse> y = new ArrayList<>();

        if (x.size() >= number){
            for (int i=0; i<number; i++){
                y.add(x.pop());
            }
        }
        else{
            for (int i=0; i<x.size(); i++){
                y.add(x.pop());
            }
        }

        return y;
    }

//    This API, will let the engine know whether there are more urls to return right now. Remember it might be
    // possible to call that urls are not available to return now but may be available after X seconds.

    public Boolean hasNext(String accountName){
        Stack<RequestResponse> x = accountUrls.get(accountName);

        if ( x.empty()){
            return false;
        }
        else {
            return true;
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
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, "scheduler");

        }
        return props;
    }
}
