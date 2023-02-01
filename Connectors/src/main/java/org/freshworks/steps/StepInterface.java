package org.freshworks.steps;

import com.fasterxml.jackson.databind.JsonNode;
import org.freshworks.core.model.RequestResponse;

import java.util.List;

public interface StepInterface {

    public abstract RequestResponse start();

    public abstract Boolean filter(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public abstract RequestResponse getNextRequest(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public abstract Boolean isComplete(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public abstract void saveResult(String s);

    public abstract List<String> getResult();

    public abstract JsonNode parseResponse(JsonNode jsonNode);

}
