package org.freshworks.steps;

import com.fasterxml.jackson.databind.JsonNode;
import org.freshworks.core.model.RequestResponse;

import java.util.ArrayList;

public abstract class BaseStep {

    public abstract RequestResponse start();

    public abstract Boolean filter(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public abstract RequestResponse getNextUrl(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public abstract Boolean isComplete(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public abstract void saveResult(String s);

    public abstract ArrayList<String> getResult();

}
