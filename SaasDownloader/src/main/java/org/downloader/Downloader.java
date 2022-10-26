package org.downloader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.ibm.etcd.api.KeyValue;
import com.ibm.etcd.api.RangeRequest;
import com.ibm.etcd.api.RangeResponse;
import com.ibm.etcd.client.EtcdClient;
import com.ibm.etcd.client.KvStoreClient;
import com.ibm.etcd.client.kv.KvClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.errors.LogAndFailExceptionHandler;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.log4j.Logger;
import org.downloader.models.Report;
import org.downloader.models.RequestResponse;
import org.downloader.models.Response;
import org.downloader.serde.JsonSerializer;
import org.downloader.serde.RequestResponseSerde;

import redis.clients.jedis.Jedis;

import javax.naming.NamingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Downloader {

    static CloseableHttpClient httpClient = HttpClients.createDefault();


    static KvClient kvClient = EtcdClient.forEndpoint("localhost", 2379).withPlainText().build().getKvClient();

    static Logger logger = Logger.getLogger(Downloader.class);

    static ObjectMapper mapper = new ObjectMapper();

    static Jedis jedis = new Jedis();

    public static void main (String args[]) throws InterruptedException, IOException {

        final Serde<String> stringSerde = Serdes.String();
        final Serde<RequestResponse> requestResponseSerde = RequestResponseSerde.getRequestResponse();
        final Serde<Report> reportSerde = RequestResponseSerde.getReport();
        final StreamsBuilder builder = new StreamsBuilder();


        KStream<String, RequestResponse> requestStream = builder.stream("downloader-input", Consumed.with(stringSerde, requestResponseSerde));
        Predicate<String, RequestResponse> delayedRequests = (key, requestResponse) -> hasReachedThreshold(requestResponse);
        Predicate<String, RequestResponse> nonDelayedRequests = (key, requestResponse) -> !hasReachedThreshold(requestResponse);

        requestStream.split()
                .branch(delayedRequests, Branched.withConsumer(ks -> ks.mapValues((key,value) -> {logRequest(key + "/downloader/delayed/" + value.getRequest().getUuid(), value); return value;}).to("downloader-delayed", Produced.with(stringSerde, requestResponseSerde))))
                .branch(nonDelayedRequests, Branched.withConsumer(ks -> ks.to("downloader-execute", Produced.with(stringSerde, requestResponseSerde))));

        KStream<String, RequestResponse> delayedStream = builder.stream("downloader-delayed", Consumed.with(stringSerde, requestResponseSerde));

        delayedStream.split()
                .branch(delayedRequests, Branched.withConsumer(ks -> ks.to("downloader-delayed", Produced.with(stringSerde, requestResponseSerde))))
                .branch(nonDelayedRequests, Branched.withConsumer(ks -> ks.mapValues((key, value) -> {deleteKey(key + "/downloader/delayed/" + value.getRequest().getUuid()); return value;}).to("downloader-execute", Produced.with(stringSerde, requestResponseSerde))));

        KStream<String, RequestResponse> executeStream = builder.stream("downloader-execute", Consumed.with(stringSerde, requestResponseSerde));
        executeStream.mapValues((key, requestResponse ) -> {
            try {
                return downloadResource(requestResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Predicate<String, RequestResponse> successRequests = (key, requestResponse) -> isSuccessful(requestResponse);
        Predicate<String, RequestResponse> failureRequests = (key, requestResponse) -> !isSuccessful(requestResponse);

        executeStream.split()
                .branch(successRequests, Branched.withConsumer(ks -> ks.mapValues((key,value) -> {logRequest(key + "/downloader/success/" + value.getRequest().getUuid(), value); return value;}).to("downloader-success", Produced.with(stringSerde, requestResponseSerde))))
                .branch(failureRequests, Branched.withConsumer(ks -> ks.mapValues((key,value) -> {logRequest(key + "/downloader/failure/" + value.getRequest().getUuid(), value); return value;}).to("downloader-failure", Produced.with(stringSerde, requestResponseSerde))));

        KTable<String, Report> failureReport = builder.stream("downloader-failure", Consumed.with(stringSerde, requestResponseSerde)).groupByKey().aggregate(() -> {return new Report();}, (key, requestResponse, agg) -> {agg.incrementFailure(); return agg;}, Materialized.<String, Report, KeyValueStore<String, Report>>as("failure-request").with(stringSerde, reportSerde));
        KTable<String, Report> successReport = builder.stream("downloader-success", Consumed.with(stringSerde, requestResponseSerde)).groupByKey().aggregate(() -> {return new Report();}, (key, requestResponse, agg) -> {agg.incrementSuccess(); return agg;}, Materialized.<String, Report, KeyValueStore<String, Report>>as("success-request").with(stringSerde, reportSerde));
        KTable<String, Report> receivedReport = builder.stream("downloader-input", Consumed.with(stringSerde, requestResponseSerde)).groupByKey().aggregate(() -> {return new Report();}, (key, requestResponse, agg) -> {agg.incrementReceived(); return agg;}, Materialized.<String, Report, KeyValueStore<String, Report>>as("received-request").with(stringSerde, reportSerde));
        KTable<String, Report> delayedReport = builder.stream("downloader-delayed", Consumed.with(stringSerde, requestResponseSerde)).groupByKey().aggregate(() -> {return new Report();}, (key, requestResponse, agg) -> {agg.incrementDelayed(); return agg;}, Materialized.<String, Report, KeyValueStore<String, Report>>as("received-request").with(stringSerde, reportSerde));

        KTable<String, Report> totalReport = receivedReport
                .leftJoin(successReport , (received, success) -> {
                        if (received != null && success != null){
                            received.setTotalSuccess(success.getTotalSuccess()); return received;
                        }
                        else if ( received != null && success == null ){
                            return received;
                        }
                        else if (received == null && success != null ){
                            return success;
                        }
                        else {
                            return received;
                        }
                })
                .leftJoin(failureReport, (received, failure) -> {
                    if ( received != null && failure != null){
                        received.setTotalFailure(failure.getTotalFailure()); return received;
                    }
                    else if (received != null && failure == null ){
                        return received;
                    }
                    else {
                        return failure;
                    }
                })
                .leftJoin(delayedReport, (received, delayed) -> {
                    if ( received != null && delayed != null){
                        received.setTotalDelayed(delayed.getTotalDelayed()); return received;
                    }
                    else if (received != null && delayed == null ){
                        return received;
                    }
                    else {
                        return delayed;
                    }
                });

        totalReport.toStream().foreach((key, report) -> {
            try {
                System.out.println(mapper.writeValueAsString(report));
                saveReport(key + "/downloader/report", report);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        final KafkaStreams streams = new KafkaStreams(builder.build(), loadStreamConfig());
        final CountDownLatch latch = new CountDownLatch(10000000);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
            }
        });


        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            streams.close();
            System.exit(1);
        }

    }

    public static void saveReport(String key, Report report)  {
        try{

            jedis.set(key  , mapper.writeValueAsString(report));
            kvClient.put(ByteString.copyFromUtf8(key), ByteString.copyFromUtf8(mapper.writeValueAsString(report)));
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static RequestResponse getKey(String key, String status) {
        try{
            RangeResponse response = kvClient.get(ByteString.copyFromUtf8((key  + status))).sync();
            return mapper.readValue(response.getKvs(0).getValue().toStringUtf8(), RequestResponse.class);
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

    public static Boolean hasReachedThreshold(RequestResponse res) {

//        Here we have to write the bucket code to fetch the available tokens
//        If tokens are available then return true else return false;

        try{
            CloseableHttpResponse response = httpClient.execute(new HttpGet("http://localhost:8080/consume/abc"));
            String val = EntityUtils.toString(response.getEntity());
            if ( val.equals("true")){
                return false;
            }
            else{
                return true;
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static Boolean isSuccessful(RequestResponse res){
        if ( res.getResponse().getResponseCode() == 200){
            return true;
        }
        else{
            return false;
        }
    }

    public static RequestResponse downloadResource(RequestResponse saaSObject) throws IOException{

        String queryString = saaSObject.getRequest().getQueryParam().entrySet().stream().map(entrySet -> entrySet.getKey() + "=" + entrySet.getValue()).collect(Collectors.joining("&"));
        String url = saaSObject.getRequest().getUrl() + "?" + queryString;
        CloseableHttpResponse response = httpClient.execute(new HttpGet(url));
        logger.info("downloaded response for the below saasobject");
        logger.info(mapper.writeValueAsString(saaSObject));

        Response res = new Response();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(EntityUtils.toString(response.getEntity()));
        res.setResponse(json);

        res.setResponseCode(response.getStatusLine().getStatusCode());

        Map<String, Object> responseHeaders = Stream.of(response.getAllHeaders()).collect(Collectors.toMap(header -> header.getName(), header -> header.getValue()));
        res.setHeaders((HashMap<String, Object>) responseHeaders);

        saaSObject.setResponse(res);
        logger.info("publishing the below saas object to engine after downloading from internet");
        logger.info(mapper.writeValueAsString(saaSObject));
        return saaSObject;
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
            props.put(StreamsConfig.APPLICATION_ID_CONFIG, "thisisanother");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
            props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
                    LogAndContinueExceptionHandler.class);

        }
        return props;
    }
}
