package org.freshworks.postman.box;

import com.fasterxml.jackson.databind.JsonNode;
import org.freshworks.core.Annotations.FreshHierarchy;
import org.freshworks.core.model.RequestResponse;
import org.freshworks.postman.BasePostman;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;

// This is suppose to be the singleton objects
@FreshHierarchy(parentClass = Application.class)
public class User extends BasePostman {

    ArrayList<String> listOfUsers = new ArrayList<>();

    @Override
    public RequestResponse start() {

        RequestResponse requestResponse = new RequestResponse();
        try{
            HttpRequest request = requestResponse.getRequest();
            request = HttpRequest.newBuilder(new URI("http://localhost:4000/usage")).GET().build();
            requestResponse.setRequest(request);
            return requestResponse;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Boolean filter(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        return null;
    }

    @Override
    public RequestResponse getNextUrl(RequestResponse requestResponse, JsonNode... parentJsonObject) {
        try{
            HttpRequest request = requestResponse.getRequest();
            request = HttpRequest.newBuilder(new URI("http://localhost:4000/users")).GET().build();
            requestResponse.setRequest(request);
            return requestResponse;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Boolean isComplete(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        if(this.listOfUsers.size() > 3){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void saveResult(String s) {
        listOfUsers.add(s);
    }

    @Override
    public ArrayList<String> getResult() {
        return listOfUsers;
    }
}
