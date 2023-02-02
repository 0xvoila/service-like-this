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
    String token = "Bearer 1!KoXewoU0il4IuBHurOGLk5Sqsb-KA1qo6wdVFE1FhL9TspCcim6O2USgGFheiU8eYxXBRP5gtiOfNpNPc4HP2Yyf0qdJ5ZaGdI2fvas_TdRyVIWOBDlWiZoxl76lnPLe0ZGOX1SNsdrVUx5V688Dh0EMGWVL1UVK5hPTS7Wzt7rbhcGkbndzoRlvM03x-ObvzAya1nVoqKJyRUJSyOlvKr_x7G1yQhdJ_LJAP2b46nlEly_fX1kLp3MbFdZIM4-MM9Xlst0FgInklFm1IE_dPsPEh0aj46aDO0kOcOl5Etc4JcH5PMhyTczwD8DstKXSDL5g_GxoA4Iy5U1wnSwZGA3QFmthNYv4t2HsQn5Ri37hGoyXs-X8X8Dz1bI1ay75H57YSymfWT5ldeJn8zmE4ybYCCRmtFkzS4m0Ut0uHb1fZiHqikf42bdaT1JAwVoS1qcQHe1qukw-YsdtysquBTzC-oUA0enQLfQtTtUYBScjLHvM_laD1kjKiP_sUXIDEklrNs_cNBJegwP74P8YjWO90Nrg2c8IHX_lsW0T9Thny-7y-mKR0gUlIz8qgy_pawba6tSPFgPjSfCG9Ai1cNgwV1k-9T4x7syk8QnzR8aP-uFl2xqm1EVKAz9cQKdfTAtEs3d-b5et9Hpp43QaCrVln0IYjWy5qFqYpip4Au6OFXNE-9pSK2Cec7Wo2Vlw8sY7JMFE5j3XoxtVo5aVZA..";
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
