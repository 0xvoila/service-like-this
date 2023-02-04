package org.freshworks.steps.box;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import org.freshworks.core.Annotations.FreshHierarchy;
import org.freshworks.core.model.RequestResponse;
import org.freshworks.steps.ParentStep;
import org.freshworks.steps.AbstractStep;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;

// This is supposed to be the singleton objects
@FreshHierarchy(parentClass = ParentStep.class)
public class Usage extends AbstractStep {

    ArrayList<String> listOfUsage = new ArrayList<>();

    String url = "https://api.box.com/2.0/events/?stream_type=admin_logs&event_type=ADD_LOGIN_ACTIVITY_DEVICE,ADMIN_LOGIN,CONTENT_ACCESS,COPY,DELETE,DOWNLOAD,ITEM_OPEN,LOGIN";
    String token ;

    @Override
    public void setup() {

        if(getData("auth_token") == null){
            this.token = "Bearer " + Authentication.getAccessToken();
            saveData("auth_token",this.token);
        }
        else{
            this.token = (String)getData("auth_token");
        }
    }

    @Override
    public Optional<RequestResponse> startSync() {

        RequestResponse requestResponse = new RequestResponse();
        try{
            HttpRequest request = requestResponse.getRequest();
            request = HttpRequest.newBuilder(new URI(url)).setHeader("Authorization",token).GET().build();
            requestResponse.setRequest(request);
            return Optional.fromNullable(requestResponse);
        }
        catch(Exception e){
            e.printStackTrace();
            return Optional.fromNullable(null);
        }
    }

    @Override
    public Optional<Boolean> filter(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        return Optional.fromNullable(true);
    }

    @Override
    public Optional<RequestResponse> getNextSyncRequest(RequestResponse requestResponse, JsonNode... parentJsonObject) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(requestResponse.getResponse().body());
            JsonNode marker = node.get("next_stream_position");
            url = url + "&" + "stream_position=" + marker.asText();

            HttpRequest request = HttpRequest.newBuilder(new URI(url)).setHeader("Authorization",token).GET().build();
            requestResponse.setRequest(request);
            return Optional.fromNullable(requestResponse);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Optional<Boolean> isSyncComplete(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(currentRequest.getResponse().body());
            JsonNode entriesArray = node.get("entries");
            if(entriesArray.isArray() && entriesArray.isEmpty()){
                return Optional.fromNullable(true);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return Optional.fromNullable(false);
    }

    @Override
    public Optional<JsonNode> parseSyncResponse(JsonNode jsonNode) {

        return Optional.fromNullable(jsonNode.get("entries"));
    }

    @Override
    public void closeSync() {

    }
}
