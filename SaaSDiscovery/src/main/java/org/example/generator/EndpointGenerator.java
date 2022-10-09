package org.example.generator;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.example.models.SaaSObject;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Properties;

public class EndpointGenerator {

    Properties properties;
    public EndpointGenerator(){

        this.properties = new Properties();
        this.properties.setProperty("okta","org.example.generator.OktaEndpointGenerator");
    }
    public ArrayList<SaaSObject> getNextEndpoints(SaaSObject saaSObject) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Class<?>  cl = Class.forName(this.properties.getProperty(saaSObject.getAppName()));
        EndpointGeneratorInterface c = (EndpointGeneratorInterface) cl.newInstance();
        ArrayList<SaaSObject> urlList = c.getNextEndpoints(saaSObject);
        return urlList;
    }
}
