package org.example.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.util.ArrayList;

public interface EndpointGeneratorInterface {

    public ArrayList<String> getNextEndpoints(String syncId, String accountName, HttpGet request, HttpResponse response);
}
