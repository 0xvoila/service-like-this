package org.example.generator;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.example.downloader.Downloader;
import org.example.models.SaaSObject;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Properties;

public class EndpointGenerator {

    Properties properties;
    Logger logger = Logger.getLogger(EndpointGenerator.class);

    public EndpointGenerator(){

        this.properties = new Properties();
        this.properties.setProperty("okta","org.example.generator.OktaEndpointGenerator");
    }
    public ArrayList<SaaSObject> getNextEndpoints(SaaSObject saaSObject) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Class<?>  cl = Class.forName(this.properties.getProperty(saaSObject.getAppName()));
        logger.info("generating end points for app " + properties.toString() );
        logger.info(saaSObject.toString());

        EndpointGeneratorInterface c = (EndpointGeneratorInterface) cl.newInstance();
        ArrayList<SaaSObject> saaSObjectsList = c.getNextEndpoints(saaSObject);

        logger.info("following end points are generated");
        saaSObjectsList.stream().forEach(x -> logger.info(x.toString()));

        return saaSObjectsList;
    }
}
