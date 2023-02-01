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
    String token = "Bearer 1!adpbMFcGz8NiyjzIHaMyhALMLrTWIrfJDZWuG8yoPd6TK3AW31A7ATB5L5piHUw_r21nbXOZCA49B7kpWY5X3k_e3_ZZ3hOG0XMl54x7X6vmT5mxvSyaPLs6VWTe85rw6Dtqq26H8Q8kflDb-KxOgI2ZRRtGj_rNkdrUqW17IaV2PLkdwEd5lvXnp_lCfhJz0uL0b9HDNbDn1O5F1h1nC2OETowj5qY4nklBgob9SzllxJX7-_4rMiEodRIrafhr7he24_-vsA5w_WoIlS7epXU7bcKbq7XU2p8uycb82RapS-w7LoXyflVlbDzpUsTaJjrYLontsTenMazKysh5QkfEBjBAbmTJ67FgB291Fh9FoEiMd9-sIUmKdz9trQQ0iOjWXSSCx9_mb74o3bfWI11AThtVWzfaleepP-su9orf08nX7Ifhf8gYBTTThyqNMoOC58Gl56dMOO7PLc8W5_Sksi_FI455px-8QvAvIMSqPLbx1L3GoBlslV7Pm_giFxh0KkjxQLHNNxBwgFq6IEK75bucwpDUAJJlREzyaw9WpZb9BsOvgk8_fgPo6Xlvlu4fYwlJOPvNu_UBNPiOULm4FclTAOa9FEHUqZaHeyXp_hCk7CnyhyHwHdicrI21OP72H1utOr3BtTm7SyC3yJ6TE1R8ILXdPvrM-bODv6-zDu7d9LGTJQ_YEzmazxkIHD1qshe95niDzipgzsMUZQ..";
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
