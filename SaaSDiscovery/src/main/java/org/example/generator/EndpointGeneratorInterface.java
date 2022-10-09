package org.example.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.example.models.SaaSObject;

import java.io.IOException;
import java.util.ArrayList;

public interface EndpointGeneratorInterface {

    public ArrayList<SaaSObject> getNextEndpoints(SaaSObject saaSObject);
}
