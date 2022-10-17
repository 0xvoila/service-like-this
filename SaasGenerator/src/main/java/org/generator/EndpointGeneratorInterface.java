package org.generator;

import org.generator.models.RequestResponse;

import java.util.ArrayList;

public interface EndpointGeneratorInterface {

    public ArrayList<RequestResponse> getNextEndpoints(RequestResponse saaSObject);
}
