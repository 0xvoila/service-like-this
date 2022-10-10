package org.example.generator;

import org.example.models.RequestResponse;

import java.util.ArrayList;

public interface EndpointGeneratorInterface {

    public ArrayList<RequestResponse> getNextEndpoints(RequestResponse saaSObject);
}
