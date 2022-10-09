package org.example.downloader;

import com.rabbitmq.client.ConnectionFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.example.Queue;
import org.example.models.SaaSObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class Downloader {

    CloseableHttpClient httpClient = HttpClients.createDefault();

    public void submit(ArrayList<SaaSObject> urls ) throws InterruptedException {
//        Submit Urls to launch in new thread
        ExecutorService executorService = Executors.newFixedThreadPool(10000);
        for (SaaSObject url :
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

    public void downloadResource(SaaSObject saaSObject) throws IOException, NamingException, TimeoutException {

        System.out.println("URL is " +saaSObject.getRequest().getURI());
        CloseableHttpResponse response = httpClient.execute(saaSObject.getRequest());

        saaSObject.setRequest(response);

//        System.out.println(EntityUtils.toString(response.getEntity()));
        publish(saaSObject);
    }

    public void publish(SaaSObject saaSObject) throws NamingException, IOException, TimeoutException {

        Queue.queue.add(saaSObject);

    }
}
