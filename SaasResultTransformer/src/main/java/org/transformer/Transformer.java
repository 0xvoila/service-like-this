package org.transformer;

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
import org.transformer.contants.Constants;
import org.transformer.models.*;
import org.transformer.serde.RequestResponseSerde;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class Transformer {
    static CloseableHttpClient httpClient = HttpClients.createDefault();


    static KvClient kvClient = EtcdClient.forEndpoint(Constants.ETC_HOST, Constants.ETC_PORT).withPlainText().build().getKvClient();

    static Logger logger = Logger.getLogger(Transformer.class);

    static ObjectMapper mapper = new ObjectMapper();

    public static void main(String args[]) throws IOException{

        final Serde<String> stringSerde = Serdes.String();
        final Serde<RequestResponse> requestResponseSerde = RequestResponseSerde.getRequestResponse();
        final Serde<User> userSerde = RequestResponseSerde.getUser();
        final Serde<Application> applicationSerde = RequestResponseSerde.getApplication();
        final Serde<Usage> usageSerde = RequestResponseSerde.getUsage();
        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, RequestResponse> requestStream = builder.stream(Constants.INPUT_KAFKA_QUEUE, Consumed.with(stringSerde, requestResponseSerde));


        KStream<String, Object> x = requestStream.mapValues((key, requestResponse) -> getResources(requestResponse))
                .flatMapValues(requestResponseList -> requestResponseList);

        Predicate<String, Object> userRequests = (key, value) -> isUserList(value);
        Predicate<String, Object> appRequests = (key, value) -> isAppList(value);
        Predicate<String, Object> usageRequests = (key, value) -> isUsageList(value);
        x.split()
                .branch(userRequests, Branched.withConsumer(ks -> ks.mapValues((key, value) -> User.class.cast(value))
                        .selectKey((key, user) -> { return user.getResourceName() + "/" + user.getAppName() + "/" + user.getSyncId();})
                        .mapValues((key,user) -> {logRequest(key + "/transformer/users/" + user.getUserId(), user); return user;})
                        .to(Constants.OUTPUT_USER_KAFKA_QUEUE, Produced.with(stringSerde, userSerde))))
                .branch(appRequests, Branched.withConsumer(ks -> ks.mapValues((key, value) -> Application.class.cast(value))
                        .selectKey((key, application) -> { return application.getResourceName() + "/" + application.getAppName() + "/" + application.getSyncId();})
                        .mapValues((key,application) -> {logRequest(key + "/transformer/application/" + application.getUserId(), application); return application;})
                        .to(Constants.OUTPUT_APPLICATION_KAFKA_QUEUE, Produced.with(stringSerde, applicationSerde))))
                .branch(usageRequests, Branched.withConsumer(ks -> ks.mapValues((key, value) -> Usage.class.cast(value))
                        .selectKey((key, usage) -> { return usage.getResourceName() + "/" + usage.getAppName() + "/" + usage.getSyncId();})
                        .mapValues((key,usage) -> {logRequest(key + "/transformer/usage/" + usage.getUserId(), usage); return usage;})
                        .to(Constants.OUTPUT_USAGE_KAFKA_QUEUE, Produced.with(stringSerde, usageSerde))));


        final KafkaStreams streams = new KafkaStreams(builder.build(), loadStreamConfig());
        final CountDownLatch latch = new CountDownLatch(10000000);

        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.exit(1);
        }
    }
    private static ArrayList<? extends Object> getResources(RequestResponse saasObject) {

        ArrayList<Object> saaSObjectsList = new ArrayList<>();

        try{
//            Properties properties = new Properties();
//            properties.setProperty("okta","org.example.generator.OktaTransformerUser");

            RangeResponse rangeResponse = getKey( "/transformer/" + saasObject.getAppName() + "/" + saasObject.getResourceName());
            if (rangeResponse != null && rangeResponse.getCount() == 0){
                rangeResponse = getKey("/transformer/" + saasObject.getAppName());
            }

            Class<?>  cl = null;
            if (rangeResponse != null && rangeResponse.getCount() == 0){
                cl = Class.forName("org.transformer.TransformerInterface");
            }
            else {
                cl = Class.forName(rangeResponse.getKvs(0).getValue().toStringUtf8());
            }

            logger.info(mapper.writeValueAsString(saasObject));
            TransformerInterface c = (TransformerInterface) cl.newInstance();
            saaSObjectsList = (ArrayList<Object>) c.transform(saasObject);

            saaSObjectsList.stream().forEach(x -> {
                try {
                    logger.info(mapper.writeValueAsString(x));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        catch(Exception e){
            System.out.println("Error in generating the response");
        }


        return saaSObjectsList;
    }

    public static Boolean isUserList(Object x){
        return x instanceof User;
    }

    public static Boolean isAppList(Object x){
        return x instanceof Application;
    }

    public static Boolean isUsageList(Object x){
        return x instanceof Usage;
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

    public static void logRequest(String key, Object object)  {
        try{

            kvClient.put(ByteString.copyFromUtf8((key)), ByteString.copyFromUtf8(mapper.writeValueAsString(object))).sync();
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
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, "result_transformer_20");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
            props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
                    LogAndContinueExceptionHandler.class);

        }
        return props;
    }

}
