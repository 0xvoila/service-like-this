package org.example.downloader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.example.Queue;
import org.example.models.RequestResponse;
import org.example.models.Response;

import javax.naming.NamingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Downloader {

    CloseableHttpClient httpClient = HttpClients.createDefault();
    Logger logger = Logger.getLogger(Downloader.class);

    ObjectMapper mapper = new ObjectMapper();

    public void submit(ArrayList<RequestResponse> urls ) throws InterruptedException {
//        Submit Urls to launch in new thread
        ExecutorService executorService = Executors.newFixedThreadPool(10000);
        for (RequestResponse url :
                urls) {
            executorService.submit(() -> {
                try {
                    downloadResource(url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (NamingException | TimeoutException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        executorService.shutdown();
        if (executorService.awaitTermination(100, TimeUnit.DAYS)){
            executorService.shutdownNow();
        }
    }

    public void downloadResource(RequestResponse saaSObject) throws IOException, NamingException, TimeoutException {

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
        publish(saaSObject);
    }

    public void publish(RequestResponse saaSObject) throws NamingException, IOException, TimeoutException {

        Queue.queue.add(saaSObject);
        logger.info("added the below endpoint into the queue");
        logger.info(mapper.writeValueAsString(saaSObject));

    }
}
