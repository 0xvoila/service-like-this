package org.reconcilor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.ibm.etcd.api.RangeResponse;
import com.ibm.etcd.client.EtcdClient;
import com.ibm.etcd.client.kv.KvClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.kstream.*;
import org.apache.log4j.Logger;
import org.reconcilor.contants.Constants;
import org.reconcilor.models.*;
import org.reconcilor.serde.RequestResponseSerde;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Reconcilor {
    static CloseableHttpClient httpClient = HttpClients.createDefault();


    static KvClient kvClient = EtcdClient.forEndpoint(Constants.ETC_HOST, Constants.ETC_PORT).withPlainText().build().getKvClient();

    static Logger logger = Logger.getLogger(Reconcilor.class);

    static ObjectMapper mapper = new ObjectMapper();

    public static void main(String args[]) throws IOException{

        final Serde<String> stringSerde = Serdes.String();
        final Serde<RequestResponse> requestResponseSerde = RequestResponseSerde.getRequestResponse();
        final Serde<User> userSerde = RequestResponseSerde.getUser();
        final Serde<Application> applicationSerde = RequestResponseSerde.getApplication();
        final Serde<Usage> usageSerde = RequestResponseSerde.getUsage();
        final StreamsBuilder builder = new StreamsBuilder();


        KStream<String, RequestResponse> userStream = builder.stream(Constants.INPUT_KAFKA_QUEUE, Consumed.with(stringSerde, requestResponseSerde)).peek((key, value) -> {System.out.println(value);});
        KStream<String, RequestResponse> appStream = builder.stream(Constants.INPUT_KAFKA_QUEUE, Consumed.with(stringSerde, requestResponseSerde)).peek((key, value) -> {System.out.println(value);});
        KStream<String, RequestResponse> usageStream = builder.stream(Constants.INPUT_KAFKA_QUEUE, Consumed.with(stringSerde, requestResponseSerde)).peek((key, value) -> {System.out.println(value);});


//        userStream.mapValues((key, requestResponse) -> getUsers(requestResponse))
//                .flatMapValues(requestResponseList -> requestResponseList)
//                .selectKey((key, value) -> { return value.getResourceName() + "/" + value.getAppName() + "/" + value.getSyncId();})
//                .to(Constants.OUTPUT_USER_KAFKA_QUEUE, Produced.with(stringSerde, userSerde));
//
//        appStream.mapValues((key, requestResponse) -> getApps(requestResponse))
//                .flatMapValues(requestResponseList -> requestResponseList)
//                .selectKey((key, value) -> { return value.getResourceName() + "/" + value.getAppName() + "/" + value.getSyncId();})
//                .to(Constants.OUTPUT_APPLICATION_KAFKA_QUEUE, Produced.with(stringSerde, applicationSerde));
//
//        usageStream.mapValues((key, requestResponse) -> getUsage(requestResponse))
//                .flatMapValues(requestResponseList -> requestResponseList)
//                .selectKey((key, value) -> { return value.getResourceName() + "/" + value.getAppName() + "/" + value.getSyncId();})
//                        .to(Constants.OUTPUT_USAGE_KAFKA_QUEUE, Produced.with(stringSerde, usageSerde));

        final KafkaStreams streams = new KafkaStreams(builder.build(), loadStreamConfig());
        final CountDownLatch latch = new CountDownLatch(10000000);

        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.exit(1);
        }
    }

    public static Boolean isDuplicate(String key, RequestResponse requestResponse){

        Boolean x =  getKey(key + "/generator/" + requestResponse.getRequest().getUuid()).getCount() != 0;
        return x;
    }
    public static void saveReport(String key, Report report)  {
        try{

            kvClient.put(ByteString.copyFromUtf8(key), ByteString.copyFromUtf8(mapper.writeValueAsString(report))).sync();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static RangeResponse getKey(String key) {
        try{
            RangeResponse response = kvClient.get(ByteString.copyFromUtf8((key))).sync();
            return response;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void deleteKey(String key) {

        System.out.println("Deleting key is " + key);
        kvClient.delete(ByteString.copyFromUtf8((key))).sync();
    }

    public static void logRequest(String key, RequestResponse requestResponse)  {
        try{

            kvClient.put(ByteString.copyFromUtf8((key)), ByteString.copyFromUtf8(mapper.writeValueAsString(requestResponse))).sync();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
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
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, "endpoint_generator8");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
            props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
                    LogAndContinueExceptionHandler.class);

        }
        return props;
    }

}
