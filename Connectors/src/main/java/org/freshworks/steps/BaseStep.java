package org.freshworks.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.freshworks.core.infra.Infra;
import org.freshworks.core.model.RequestResponse;

import java.util.ArrayList;
import java.util.List;

public class BaseStep extends StepInterface{

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
    public List<String> getResult() {
        ArrayList<String> x = new ArrayList<String>();
        x.add("");
        return x;
    }

    @Override
    public JsonNode parseResponse(JsonNode jsonNode) {
        return null;
    }

    public void save(String key, Object value){

        Infra.redis.put(key, value);

    }

    public Object get(String key){

        return Infra.redis.get(key);

    }
}
