package org.freshworks.steps;

import com.fasterxml.jackson.databind.JsonNode;
import org.freshworks.core.model.RequestResponse;

import java.util.ArrayList;
import java.util.List;

public abstract class StepInterface {

    ArrayList<String> list = new ArrayList<>();

    public abstract RequestResponse start();

    public abstract Boolean filter(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public abstract RequestResponse getNextRequest(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public abstract Boolean isComplete(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public void saveResult(String s){
        list.add(s);
    }

    public List<String> getResult(){
        return list;
    }

    public abstract JsonNode parseResponse(JsonNode jsonNode);

}
