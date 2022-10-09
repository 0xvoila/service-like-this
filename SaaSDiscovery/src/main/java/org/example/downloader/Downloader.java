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

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;


public class Downloader {

    CloseableHttpClient httpClient = HttpClients.createDefault();

    public void submit(ArrayList<String> urls ){
//        Submit Urls to launch in new thread
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (String url :
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
    }

    public void downloadResource(String url) throws IOException, NamingException, TimeoutException {

        System.out.println("URL is " +url);
        HttpGet request = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(request);

        HashMap<HttpGet, HttpResponse> c = new HashMap<HttpGet, HttpResponse>();
        c.put(request, response);
        System.out.println(EntityUtils.toString(response.getEntity()));
        publish(c);
    }

    public void publish(Map<HttpGet, HttpResponse> x) throws NamingException, IOException, TimeoutException {

        Queue.queue.add(x);

    }
}
