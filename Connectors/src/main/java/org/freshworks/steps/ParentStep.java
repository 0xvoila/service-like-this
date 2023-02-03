package org.freshworks.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Optional;
import org.freshworks.core.infra.Infra;
import org.freshworks.core.model.RequestResponse;

import java.util.ArrayList;
import java.util.List;

public class ParentStep extends AbstractStep {

    @Override
    public Optional<RequestResponse> setupSync() {
        return Optional.fromNullable(null);
    }

    @Override
    public Optional<Boolean> isSetupSyncComplete(RequestResponse currentRequest) {
        return Optional.fromNullable(null);
    }

    @Override
    public Optional<RequestResponse> startSync() {
        return Optional.fromNullable(null);
    }

    @Override
    public Optional<Boolean> filter(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        return Optional.fromNullable(null);
    }

    @Override
    public Optional<RequestResponse> getNextSyncRequest(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        return Optional.fromNullable(null);
    }

    @Override
    public Optional<Boolean> isSyncComplete(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        return Optional.fromNullable(null);
    }


    @Override
    public List<String> getSyncResult() {
        ArrayList<String> x = new ArrayList<String>();
        x.add("");
        return x;
    }

    @Override
    public Optional<JsonNode> parseSyncResponse(JsonNode jsonNode) {
        return Optional.fromNullable(null);
    }

    @Override
    public void closeSync() {

    }

    public void save(String key, Object value){

        Infra.redis.put(key, value);

    }

    public Object get(String key){

        return Infra.redis.get(key);

    }
}
