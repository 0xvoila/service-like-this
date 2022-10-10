package org.example.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
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

    Logger logger = Logger.getLogger(Engine.class);

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



//        System.out.println("This is the end");
    }
    public void setup() throws InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {

        SaaSObject saaSObject = new SaaSObject("11312", "Google", "okta", null, null);
        logger.info("Got new sync request");
        logger.info(saaSObject.toString());


        ArrayList<SaaSObject> saaSObjectsList = endpointGenerator.getNextEndpoints(saaSObject);
        logger.info("Endpoints generated for this sync are ");
        saaSObjectsList.stream().forEach(x -> logger.info(x.toString()));

        scheduler.addEndPoints("Google",saaSObjectsList);
        logger.info("added these end points to scheduler");
        logger.info(saaSObject.toString());


        while(true){
            if(scheduler.hasNext("Google")){
                Thread.sleep(10);
                ArrayList<SaaSObject> saaSObjects = scheduler.getEndpoints( "Google" , 1);
                logger.info("got these end points from scheduler to run ");
                saaSObjectsList.stream().forEach(x -> logger.info(x.toString()));
//                System.out.println("Urls is " + saaSObjects.get(0).getRequest().getURI());
                downloader.submit(saaSObjects);
                logger.info("submitted the endpoints to the downloader service");
            }

        }
    }
    public void consume() throws NamingException, IOException, TimeoutException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        while(true){
            SaaSObject x = Queue.queue.poll();

            if ( x != null){
                logger.info("below end points are downloaded from downloader service");
                logger.info(x.toString());

                ArrayList<SaaSObject> saaSObjectsList = endpointGenerator.getNextEndpoints(x);
                logger.info("submitting below endpoint to generator service to generate end points");
                logger.info(x.toString());
                logger.info("below endpoints are generated");
                saaSObjectsList.stream().forEach(y -> logger.info(y.toString()));

                scheduler.addEndPoints("Google",saaSObjectsList);
                logger.info("below endpoints are added to scheduler for schedule");
                saaSObjectsList.stream().forEach(y -> logger.info(y.toString()));

            }
        }

    }
}
