package org.freshworks.postman.box;

import com.fasterxml.jackson.databind.JsonNode;
import org.freshworks.core.Annotations.FreshHierarchy;
import org.freshworks.core.model.RequestResponse;
import org.freshworks.postman.BasePostman;

import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;


// This is suppose to be the singleton objects

@FreshHierarchy(childClass = {User.class, Usage.class})
public class Application extends BasePostman {

    ArrayList<String> listOfApplication = new ArrayList<>();

    @Override
    public Boolean filter(RequestResponse currentRequest, JsonNode... parentJsonObject) {
        return null;
    }

    @Override
    public RequestResponse getNextUrl(RequestResponse requestResponse, JsonNode... parentJsonObject) {

        try{
            HttpRequest request = requestResponse.getRequest();
            request = HttpRequest.newBuilder(new URI("http://localhost:4000/apps")).GET().build();
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
        if(this.listOfApplication.size() > 4){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void saveResult(String s) {
        listOfApplication.add(s);
    }

    @Override
    public ArrayList<String> getResult( ) {
        return listOfApplication;
    }


}
