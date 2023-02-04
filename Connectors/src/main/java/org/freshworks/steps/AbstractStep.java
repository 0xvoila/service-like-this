package org.freshworks.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import org.freshworks.core.infra.Infra;
import org.freshworks.core.model.RequestResponse;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractStep {

    ArrayList<String> list = new ArrayList<>();

    public abstract void setup();


    public abstract Optional<RequestResponse> startSync();

    public abstract Optional<Boolean> filter(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public abstract Optional<RequestResponse> getNextSyncRequest(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public abstract Optional<Boolean> isSyncComplete(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public void saveSyncResult(String s){
        list.add(s);
    }

    public List<String> getSyncResult(){
        return list;
    }

    public abstract Optional<JsonNode> parseSyncResponse(JsonNode jsonNode);

    public abstract void closeSync();

    public void saveData(String key, Object value){
        Infra.redis.put(key, value);
    }

    public Object getData(String key){
        return Infra.redis.get(key);
    }
}
