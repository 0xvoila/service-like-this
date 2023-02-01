package org.freshworks.steps.box;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.freshworks.core.Annotations.FreshHierarchy;
import org.freshworks.core.model.RequestResponse;
import org.freshworks.steps.BaseStep;
import org.freshworks.steps.StepInterface;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;

// This is supposed to be the singleton objects
@FreshHierarchy(parentClass = BaseStep.class)
public class Usage implements StepInterface {

    ArrayList<String> listOfUsage = new ArrayList<>();

    String url = "https://api.box.com/2.0/events/?stream_type=admin_logs&event_type=ADD_LOGIN_ACTIVITY_DEVICE,ADMIN_LOGIN,CONTENT_ACCESS,COPY,DELETE,DOWNLOAD,ITEM_OPEN,LOGIN";
    String token = "Bearer 1!adpbMFcGz8NiyjzIHaMyhALMLrTWIrfJDZWuG8yoPd6TK3AW31A7ATB5L5piHUw_r21nbXOZCA49B7kpWY5X3k_e3_ZZ3hOG0XMl54x7X6vmT5mxvSyaPLs6VWTe85rw6Dtqq26H8Q8kflDb-KxOgI2ZRRtGj_rNkdrUqW17IaV2PLkdwEd5lvXnp_lCfhJz0uL0b9HDNbDn1O5F1h1nC2OETowj5qY4nklBgob9SzllxJX7-_4rMiEodRIrafhr7he24_-vsA5w_WoIlS7epXU7bcKbq7XU2p8uycb82RapS-w7LoXyflVlbDzpUsTaJjrYLontsTenMazKysh5QkfEBjBAbmTJ67FgB291Fh9FoEiMd9-sIUmKdz9trQQ0iOjWXSSCx9_mb74o3bfWI11AThtVWzfaleepP-su9orf08nX7Ifhf8gYBTTThyqNMoOC58Gl56dMOO7PLc8W5_Sksi_FI455px-8QvAvIMSqPLbx1L3GoBlslV7Pm_giFxh0KkjxQLHNNxBwgFq6IEK75bucwpDUAJJlREzyaw9WpZb9BsOvgk8_fgPo6Xlvlu4fYwlJOPvNu_UBNPiOULm4FclTAOa9FEHUqZaHeyXp_hCk7CnyhyHwHdicrI21OP72H1utOr3BtTm7SyC3yJ6TE1R8ILXdPvrM-bODv6-zDu7d9LGTJQ_YEzmazxkIHD1qshe95niDzipgzsMUZQ..";

    @Override
    public RequestResponse start() {

        RequestResponse requestResponse = new RequestResponse();
        try{
            HttpRequest request = requestResponse.getRequest();
            request = HttpRequest.newBuilder(new URI(url)).setHeader("Authorization",token).GET().build();
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
            JsonNode marker = node.get("next_stream_position");
            url = url + "&" + "stream_position=" + marker.asText();

            HttpRequest request = HttpRequest.newBuilder(new URI(url)).setHeader("Authorization",token).GET().build();
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
            JsonNode entriesArray = node.get("entries");
            if(entriesArray.isArray() && entriesArray.isEmpty()){
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
        listOfUsage.add(s);
    }

    @Override
    public ArrayList<String> getResult() {
        return listOfUsage;
    }

    @Override
    public JsonNode parseResponse(JsonNode jsonNode) {

        return jsonNode.get("entries");
    }
}
