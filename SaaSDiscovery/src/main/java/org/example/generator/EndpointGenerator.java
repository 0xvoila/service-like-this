package org.example.generator;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

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
    public ArrayList<String> getNextEndpoints(String syncId, String appName, String accountName, HttpGet request, HttpResponse response) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Class<?>  cl = Class.forName(this.properties.getProperty(appName));
        EndpointGeneratorInterface c = (EndpointGeneratorInterface) cl.newInstance();
        ArrayList<String> urlList = c.getNextEndpoints(syncId,accountName, request, response);
        return urlList;
    }
}
