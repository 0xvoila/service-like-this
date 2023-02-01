package org.freshworks.steps;

import com.fasterxml.jackson.databind.JsonNode;
import org.freshworks.core.model.RequestResponse;

import java.util.ArrayList;
import java.util.List;

public class BaseStep implements StepInterface{

    @Override
    public RequestResponse start() {
        return null;
    }

    @Override
    public Boolean filter(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        return null;
    }

    @Override
    public RequestResponse getNextRequest(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        return null;
    }

    @Override
    public Boolean isComplete(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        return null;
    }

    @Override
    public void saveResult(String s) {

    }

    @Override
    public List<String> getResult() {
        ArrayList<String> x = new ArrayList<String>();
        x.add("");
        return x;
    }

    @Override
    public JsonNode parseResponse(JsonNode jsonNode) {
        return null;
    }
}
