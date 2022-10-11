package org.example.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    ObjectMapper mapper = new ObjectMapper();

    public EndpointGenerator(){

        this.properties = new Properties();
        this.properties.setProperty("okta","org.example.generator.OktaEndpointGenerator");
    }
    public ArrayList<RequestResponse> getNextEndpoints(RequestResponse saaSObject) throws ClassNotFoundException, InstantiationException, IllegalAccessException, JsonProcessingException {

        Class<?>  cl = Class.forName(this.properties.getProperty(saaSObject.getAppName()));
        logger.info("generating end points for app " + properties.toString() );
        logger.info(mapper.writeValueAsString(saaSObject));

        EndpointGeneratorInterface c = (EndpointGeneratorInterface) cl.newInstance();
        ArrayList<RequestResponse> saaSObjectsList = c.getNextEndpoints(saaSObject);

        logger.info("following end points are generated");
        saaSObjectsList.stream().forEach(x -> {
            try {
                logger.info(mapper.writeValueAsString(x));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        return saaSObjectsList;
    }

}
