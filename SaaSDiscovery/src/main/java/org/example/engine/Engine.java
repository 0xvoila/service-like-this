package org.example.engine;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.example.Queue;
import org.example.downloader.Downloader;
import org.example.generator.EndpointGenerator;
import org.example.models.SaaSObject;
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

        executorService.awaitTermination(100, TimeUnit.DAYS);

        System.out.println("This is the end");
    }
    public void setup() throws InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {

        SaaSObject saaSObject = new SaaSObject("11312", "Google", "okta", null, null);
        ArrayList<SaaSObject> saaSObjectsList = endpointGenerator.getNextEndpoints(saaSObject);
        scheduler.addEndPoints("Google",saaSObjectsList);


        while(true){
            if(scheduler.hasNext("Google")){
                Thread.sleep(10);
                ArrayList<SaaSObject> saaSObjects = scheduler.getEndpoints( "Google" , 1);
                System.out.println("Urls is " + saaSObjects.get(0).getRequest().getURI());
                downloader.submit(saaSObjects);
            }

        }
    }
    public void consume() throws NamingException, IOException, TimeoutException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        while(true){
            SaaSObject x = Queue.queue.poll();

            if ( x != null){

                ArrayList<SaaSObject> urlList = endpointGenerator.getNextEndpoints(x);
                scheduler.addEndPoints("Google",urlList);
            }
        }

    }
}
