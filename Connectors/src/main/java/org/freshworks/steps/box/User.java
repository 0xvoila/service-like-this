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

// This is suppose to be the singleton objects
@FreshHierarchy(parentClass = ParentStep.class)
public class User extends AbstractStep {

    ArrayList<String> listOfUsers = new ArrayList<>();
    String url = "https://api.box.com/2.0/users?usemarker=true&fields=login,created_at,role,status,space_amount,space_used,max_upload_size&limit=5&user_type=managed";
    String token ;

    @Override
    public void setup() {
        // Here make the call to authentication API and store the token in some where
        this.token = "Bearer " + Authentication.getAccessToken();
        saveData("auth_token", this.token);
    }


    @Override
    public Optional<RequestResponse> startSync() {

        RequestResponse requestResponse = new RequestResponse();
        try{
            HttpRequest request = requestResponse.getRequest();
            request = HttpRequest.newBuilder(new URI(url)).GET().setHeader("Authorization",token).build();
            requestResponse.setRequest(request);
            return Optional.fromNullable(requestResponse);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
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
            JsonNode marker = node.get("next_marker");
            url = url + "&" + "marker=" + marker.asText();
            HttpRequest request = HttpRequest.newBuilder(new URI(url)).GET().setHeader("Authorization",token).build();
            requestResponse.setRequest(request);
            return Optional.fromNullable(requestResponse);
        }
        catch(Exception e){
            e.printStackTrace();
            return Optional.fromNullable(null);
        }
    }

    @Override
    public Optional<Boolean> isSyncComplete(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(currentRequest.getResponse().body());
            JsonNode marker = node.get("next_marker");
            if(marker == null){
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
