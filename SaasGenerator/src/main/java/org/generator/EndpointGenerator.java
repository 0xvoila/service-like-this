package org.generator;

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
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.log4j.Logger;
import org.generator.contants.Constants;
import org.generator.models.Report;
import org.generator.models.RequestResponse;
import org.generator.serde.RequestResponseSerde;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class EndpointGenerator {
    static CloseableHttpClient httpClient = HttpClients.createDefault();


    static KvClient kvClient = EtcdClient.forEndpoint(Constants.ETC_HOST, Constants.ETC_PORT).withPlainText().build().getKvClient();

    static Logger logger = Logger.getLogger(EndpointGenerator.class);

    static ObjectMapper mapper = new ObjectMapper();

    public static void main(String args[]) throws IOException{

        final Serde<String> stringSerde = Serdes.String();
        final Serde<RequestResponse> requestResponseSerde = RequestResponseSerde.getRequestResponse();
        final Serde<Report> reportSerde = RequestResponseSerde.getReport();
        final StreamsBuilder builder = new StreamsBuilder();


        KStream<String, RequestResponse> requestStream = builder.stream(Constants.INPUT_KAFKA_QUEUE, Consumed.with(stringSerde, requestResponseSerde));

        Predicate<String, RequestResponse> failureRequests = (key, requestResponse) -> {return isDuplicate(key, requestResponse);};
        Predicate<String, RequestResponse> successRequests = (key, requestResponse) -> {return !isDuplicate(key, requestResponse);};

        KStream<String, RequestResponse> requestStreamRekey = requestStream.mapValues((key, requestResponse) -> getEndpoints(requestResponse))
                .flatMapValues(requestResponseList -> requestResponseList)
                .selectKey((key, value) -> {if (value.getRequest() == null && value.getResponse() == null ) { return value.getResourceName() + "/" + value.getAppName() + "/" + value.getSyncId();} return key;});

        requestStreamRekey.split()
                        .branch(failureRequests, Branched.withConsumer(ks -> ks.to(Constants.DUPLICATE_KAFKA_QUEUE, Produced.with(stringSerde, requestResponseSerde))))
                        .branch(successRequests, Branched.withConsumer(ks -> ks.to(Constants.SUCCESS_KAFKA_QUEUE, Produced.with(stringSerde, requestResponseSerde))));
        KStream<String, RequestResponse> successStream = builder.stream(Constants.SUCCESS_KAFKA_QUEUE,Consumed.with(stringSerde, requestResponseSerde));
        successStream.filter(successRequests)
                .mapValues((key, value) -> {logRequest(key + "/generator/" + value.getRequest().getUuid(), value); return value;})
                .to(Constants.OUTPUT_KAFKA_QUEUE,Produced.with(stringSerde,requestResponseSerde));

        KStream<String, RequestResponse> failureStream = builder.stream(Constants.DUPLICATE_KAFKA_QUEUE,Consumed.with(stringSerde, requestResponseSerde));
        KTable<String, Report> successReport = builder.stream(Constants.SUCCESS_KAFKA_QUEUE,Consumed.with(stringSerde, requestResponseSerde)).groupByKey().aggregate(() -> {return new Report();}, (key, requestResponse, agg) -> {agg.incrementSuccess(); return agg;}, Materialized.<String, Report, KeyValueStore<String, Report>>as("success-request").with(stringSerde, reportSerde));
        KTable<String, Report> failureReport = builder.stream(Constants.DUPLICATE_KAFKA_QUEUE,Consumed.with(stringSerde, requestResponseSerde)).groupByKey().aggregate(() -> {return new Report();}, (key, requestResponse, agg) -> {agg.incrementFailure(); return agg;}, Materialized.<String, Report, KeyValueStore<String, Report>>as("duplicate-request").with(stringSerde, reportSerde));

        successReport.leftJoin(failureReport, (success, duplicate) -> {
            if (success != null && duplicate != null){
                 success.setTotalFailure(duplicate.getTotalFailure());
            }
            else if ( success != null && duplicate == null){
                return success;
            }
            return success;
        });

        successReport.toStream().foreach((key, value) -> {
            saveReport(key + "/generator/report", value);
        });

        final KafkaStreams streams = new KafkaStreams(builder.build(), loadStreamConfig());
        final CountDownLatch latch = new CountDownLatch(10000000);

        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.exit(1);
        }
    }
    private static ArrayList<RequestResponse> getEndpoints(RequestResponse saasObject) {

        ArrayList<RequestResponse> saaSObjectsList = new ArrayList<>();

        try{
//            Properties properties = new Properties();
//            properties.setProperty("okta","org.example.generator.OktaEndpointGenerator");

            RangeResponse rangeResponse = getKey(saasObject.getResourceName() + "/" + saasObject.getAppName());
            if (rangeResponse != null && rangeResponse.getCount() == 0){
                rangeResponse = getKey(saasObject.getAppName());
            }

            Class<?>  cl = Class.forName(rangeResponse.getKvs(0).getValue().toStringUtf8());
            logger.info(mapper.writeValueAsString(saasObject));
            EndpointGeneratorInterface c = (EndpointGeneratorInterface) cl.newInstance();
            saaSObjectsList = c.getNextEndpoints(saasObject);

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
