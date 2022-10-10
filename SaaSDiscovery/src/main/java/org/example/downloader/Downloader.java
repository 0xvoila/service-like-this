package org.example.downloader;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.apache.log4j.Logger;
import org.example.Queue;
import org.example.models.RequestResponse;

import javax.naming.NamingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;


public class Downloader {

    CloseableHttpClient httpClient = HttpClients.createDefault();
    Logger logger = Logger.getLogger(Downloader.class);

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

//        executorService.awaitTermination(100, TimeUnit.DAYS);
    }

    public void downloadResource(RequestResponse saaSObject) throws IOException, NamingException, TimeoutException {

        CloseableHttpResponse response = httpClient.execute(saaSObject.getRequest());
        logger.info("downloaded response for the below saasobject");
        logger.info(saaSObject.toString());

        saaSObject.setResponse(response);

        logger.info("publishing the below saas object to engine after downloading from internet");
        logger.info(saaSObject.toString());
        publish(saaSObject);
    }

    public void publish(RequestResponse saaSObject) throws NamingException, IOException, TimeoutException {

        Queue.queue.add(saaSObject);
        logger.info("added the below endpoint into the queue");
        logger.info(saaSObject.toString());

    }
}
