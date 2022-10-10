package org.example.generator;

import org.apache.log4j.Logger;
import org.example.models.RequestResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EndpointGenerator {

    Properties properties;
    Logger logger = Logger.getLogger(EndpointGenerator.class);

    public EndpointGenerator(){

        this.properties = new Properties();
        this.properties.setProperty("okta","org.example.generator.OktaEndpointGenerator");
    }
    public ArrayList<RequestResponse> getNextEndpoints(RequestResponse saaSObject) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Class<?>  cl = Class.forName(this.properties.getProperty(saaSObject.getAppName()));
        logger.info("generating end points for app " + properties.toString() );
        logger.info(saaSObject.toString());

        EndpointGeneratorInterface c = (EndpointGeneratorInterface) cl.newInstance();
        ArrayList<RequestResponse> saaSObjectsList = c.getNextEndpoints(saaSObject);

        logger.info("following end points are generated");
        saaSObjectsList.stream().forEach(x -> logger.info(x.toString()));

        return saaSObjectsList;
    }

    public HashMap<String, String> queryToMap(String queryString){

        HashMap<String, String> y = new HashMap<>();

        Stream<String> s = Stream.of(queryString.split("&"));
        s.map(param -> param.split("=")).forEach( x -> y.put(x[0], x[1].toString()));
        return y;
    }

    public String mapToQuery(HashMap<String, String> map){

        return map.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));
    }
}
