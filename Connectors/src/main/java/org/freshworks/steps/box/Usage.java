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
public class Usage extends StepInterface {

    ArrayList<String> listOfUsage = new ArrayList<>();

    String url = "https://api.box.com/2.0/events/?stream_type=admin_logs&event_type=ADD_LOGIN_ACTIVITY_DEVICE,ADMIN_LOGIN,CONTENT_ACCESS,COPY,DELETE,DOWNLOAD,ITEM_OPEN,LOGIN";
    String token = "Bearer 1!ICgfok5JuBDMVNHG49w7E5epiHFB24oV3bv28r4Eh-j6eCGNTNMVJzSoG7mrWqLludgxPCQfOA3vNAFlot70j2j13mh3ANUGRFQN4K9V8E9nfZPQ2CzPaBMmf8d7VtNRWlFfSABLq2e5jweKJYB3UTSHWhtJ6RrFGpZMmdAwZZCKWwH_b61hAuNXetu2ZAdhQ55YGDq90PeSl3YJuID2MtCgAo2_JiCrXl5oPL_Yq4V1JWQ7gvub0ilTLtTLwqX1V6sN_muJ_lNUrN8g7Xjr9AsFfxpIJDds_1Exr4rqrQJ_F1c0J_qPR8jnC5mRt0DOiN-rwQ1FtJTiJABVWUZZ0_T318nNM6HgZmE5G4aSwTiwzr06skfUIeAuQdGs-yXsPicDf5uPIeJHjVVUfuGE2qGayiENtKroVRLrZf-UXP7_RYYeKZSPrS45kJEHSq3LbkRZcAfweu5jtv1VumhR4vXTUlI1aDOdAGwfcJ3zbBynR6xmWhs3DUKmqL6Vnccl-TeCdhDPEISg37A_1SLpZJ291p3DOxp7AMvSD-mIviFQvxb4xPKVcnspPBhQS_JlfZRobh12HjtSvCEutpc5fmUy_kG5QHCmP-kAxCHN6d9QughWQpn-reC4PPMlcPlqWf7ttsGuDcivWNNIBlXjzuZ4GF-hXvn-HaliEr5SP27KbKtgv3UxexIq-_C58_zW-TjUcICTQvp7hYtCOKKkxA..";

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
        return true;
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
    public JsonNode parseResponse(JsonNode jsonNode) {

        return jsonNode.get("entries");
    }
}
