package org.freshworks.steps;

import com.fasterxml.jackson.databind.JsonNode;
import org.freshworks.core.infra.Infra;
import org.freshworks.core.model.RequestResponse;

import java.util.ArrayList;
import java.util.List;

public class ParentStep extends AbstractStep {

    @Override
    public RequestResponse startSync() {
        return null;
    }

    @Override
    public Boolean filter(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        return null;
    }

    @Override
    public RequestResponse getNextSyncRequest(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        return null;
    }

    @Override
    public Boolean isSyncComplete(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        return null;
    }


    @Override
    public List<String> getSyncResult() {
        ArrayList<String> x = new ArrayList<String>();
        x.add("");
        return x;
    }

    @Override
    public JsonNode parseSyncResponse(JsonNode jsonNode) {
        return null;
    }

    public void save(String key, Object value){

        Infra.redis.put(key, value);

    }

    public Object get(String key){

        return Infra.redis.get(key);

    }
}
