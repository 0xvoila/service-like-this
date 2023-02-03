package org.freshworks.steps;

import com.fasterxml.jackson.databind.JsonNode;
import org.freshworks.core.model.RequestResponse;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStep {

    ArrayList<String> list = new ArrayList<>();

    public abstract  RequestResponse setupSync();

    public abstract  Boolean isSetupSyncComplete(RequestResponse currentRequest);

    public abstract RequestResponse startSync();

    public abstract Boolean filter(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public abstract RequestResponse getNextSyncRequest(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public abstract Boolean isSyncComplete(RequestResponse currentRequest, JsonNode... parentJsonObject);

    public void saveSyncResult(String s){
        list.add(s);
    }

    public List<String> getSyncResult(){
        return list;
    }

    public abstract JsonNode parseSyncResponse(JsonNode jsonNode);

    public abstract void closeSync();
}
