package org.example.engine;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.example.Queue;
import org.example.downloader.Downloader;
import org.example.generator.EndpointGenerator;
import org.example.scheduler.Scheduler;

import javax.naming.NamingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;


public class Engine {

    EndpointGenerator endpointGenerator = new EndpointGenerator();
    Scheduler scheduler = new Scheduler();
    Downloader downloader = new Downloader();

    public static void main( String[] args ) throws InterruptedException {

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

        executorService.awaitTermination(100000, TimeUnit.DAYS);

        System.out.println("This is the end");
    }
    public void setup() throws InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {

        ArrayList<String> urlList = endpointGenerator.getNextEndpoints("11312", "okta", "Google", null, null);
        scheduler.addEndPoints("Google",urlList);


        while(scheduler.hasNext("Google")){
            Thread.sleep(10000);
            ArrayList<String> urls = scheduler.getEndpoints( "Google" , 1);
            System.out.println("Urls is " + urls.get(0));
            downloader.submit(urls);
        }
    }
    public void consume() throws NamingException, IOException, TimeoutException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        while(true){
            Map<HttpGet, HttpResponse> x = Queue.queue.poll();

            if ( x != null){
                HttpGet request = x.entrySet().iterator().next().getKey();
                HttpResponse response = x.entrySet().iterator().next().getValue();

                ArrayList<String> urlList = endpointGenerator.getNextEndpoints("11312", "okta", "Google", request, response);
                scheduler.addEndPoints("Google",urlList);
            }
        }

    }
}
