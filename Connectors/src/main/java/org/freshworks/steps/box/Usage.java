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
    String token = "Bearer 1!ekqAD_7PqjFrSURe5Vea5s7l4_DFeJAe4s0m-MsbUnp9Oq0emYAINE9m09FK-qGRrw5QjhX_4-4GXGumjUZoghbDfcmoU8KXjTMoS6MKnCSi3pT4VVrTUIVEoWvQWLyCVABi8jywKlKlflMrpJxzN3thVM6Z4yjBh3XSW84EamaJqFORnMswOEQaJrC4i8NNWUv9dDm-A6Y6tFlwU5EjKJywS4eIWVopPiVxR-3-FdHBqzWepTcn_YO9SpYFJMIqv9-aEbH6fYmN9gLwPodiekO8zGDCdph-8dvkYMAv5VbSpO8la0Wk9uPA67o7aPQwHpcBif7YAYJuevkPynUuo7RsHnz9944dL5Q53jCNDkSimcd-ARp7eawsU3n8BWDPaZSS-YKmuZbbcHiMEToouk-Rnx0TKKZg_UEaNnXF7Hh4cZNgftOnkSteohffbbjAuaoOsH0g-flZQJMINdz17lLrjsQiGatf4UGXat5lTsMCg-s7DliCe0pMJRN0_mB8Yk3C5z8Q4UeultMcIY1ScSMu0bDwq11dz-mchBnk2rIs4BTgzd0V_B5hOV4IfTUTS_NS0AmWrAnVgyHXGxaxbaTVEksb8LuNK4bOl7U7LMznvFBYKK_HNpjIdha-C3Hs190kZpigu050tIJZlrfCZ9m0-Gx7ZY4paRt1aYfHI_5XvI3g4_FkxLzt9ilNka0gFPs3Ru341Fwthv4Ee9yEQA..";

    @Override
    public Optional<RequestResponse> setupSync() {
        return Optional.fromNullable(null);
    }

    @Override
    public Optional<Boolean> isSetupSyncComplete(RequestResponse currentRequest) {
        return Optional.fromNullable(true);
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
