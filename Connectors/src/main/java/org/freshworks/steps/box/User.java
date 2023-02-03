package org.freshworks.steps.box;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONArray;
import org.freshworks.core.Annotations.FreshHierarchy;
import org.freshworks.core.model.RequestResponse;
import org.freshworks.steps.BaseStep;
import org.freshworks.steps.StepInterface;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;

// This is suppose to be the singleton objects
@FreshHierarchy(parentClass = BaseStep.class)
public class User implements StepInterface {

    ArrayList<String> listOfUsers = new ArrayList<>();
    String url = "https://api.box.com/2.0/users?usemarker=true&fields=login,created_at,role,status,space_amount,space_used,max_upload_size&limit=5&user_type=managed";
    String token = "Bearer 1!jvy5venA7ak9r2S_hAUlbEotRflIR4q_JjFw-bclrQnCS2_SOLT46aYfv3odSty3f7AVU2nl432C4P3MEegjrZ81qZ6L-wv-jLg1zs8GleaaZx8cCwsG6SUMGGeCQPAUl9Aa8XvjDzpayO4qfKV4xglUA5fPGRhJRF3p4Z5-j8Bpbuk_v75bQicMcB-fX6FVJfYmrPxPk7fKk1uWPqlgK74-iSxoJpb5l0Am1-vAT4slV-uIbG01_b_Ywlc9puI8wZ6elAtg5EKtpKp0YwCqe1G_MCnrqBrekBAESu9BnV2btXXnlsDxBxvf03_qYTGmPON-WsHFzdw_tA5LLARnSlFwinG1or2AkE5hHAdEeS9qjctqpS--OuZLW_CJhkCroXDdE4dH0Fq6pJGuDVn5Wy_uHT00L_nUrc2IuVtWystZZLFXdWKs_xYwaTnF7LmyYLw87xeYsF_gtTBN9CXofUJy3DDnORMplG53pfDBwVhDvSjoeX4nAeNCvsG2laJJBB0VhizgdAvpKVs6lqrpPs0KMzB7_4vJAnTjkdtgLqCK1FSNxKe4FGxxxev0ys0TZ8nWQjtVvoytsF8o8q7xXdOM3y1kq8bJw9tSwWfRy-GHi3A2fcEzKSs_3R0FZgB6fWFH6zSjgPgfr8jTbJlgo8Dayog1OrK2jotzHvscmjZ2d1bBEqP0Bh4u56Cycd8HxqPjKhxL0UPJADroa8Ul8w..";

    @Override
    public RequestResponse start() {

        RequestResponse requestResponse = new RequestResponse();
        try{
            HttpRequest request = requestResponse.getRequest();
            request = HttpRequest.newBuilder(new URI(url)).GET().setHeader("Authorization",token).build();
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
    public RequestResponse getNextRequest(RequestResponse requestResponse, JsonNode... parentJsonObject) {
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(requestResponse.getResponse().body());
            JsonNode marker = node.get("next_marker");
            url = url + "&" + "marker=" + marker.asText();
            HttpRequest request = HttpRequest.newBuilder(new URI(url)).GET().setHeader("Authorization",token).build();
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
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(currentRequest.getResponse().body());
            JsonNode marker = node.get("next_marker");
            if(marker == null){
                return true;
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void saveResult(String s) {
        listOfUsers.add(s);
    }

    @Override
    public ArrayList<String> getResult() {
        return listOfUsers;
    }

    @Override
    public JsonNode parseResponse(JsonNode jsonNode) {

        return jsonNode.get("entries");
    }
}
