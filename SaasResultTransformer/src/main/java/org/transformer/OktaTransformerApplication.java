package org.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import org.transformer.models.*;

import java.util.ArrayList;
import java.util.function.Consumer;

public class OktaTransformerApplication {

    public ArrayList<? extends Application> transformApplication(RequestResponse saaSObject) {

        ArrayList<Application> applicationList = new ArrayList<>();
        if (saaSObject.getRequest() == null && saaSObject.getResponse() == null){
            return applicationList;
        }
        else{
            try{
                JsonNode actualObj = saaSObject.getResponse().getResponse();

                if (actualObj.isEmpty()){
//
                    return applicationList;
                }
                else {
                    Consumer<JsonNode> data = (JsonNode node) -> {
                        Application application = Factory.createApplication(saaSObject,node.get("id").asText());
                        application.setApplicationName(node.get("name").asText());
                        applicationList.add(application);
                    };
                    actualObj.forEach(data);

                }
            }
            catch(Exception e){
                System.out.println(e.getMessage());
                return null;
            }
        }
        return applicationList;
    }

    public ArrayList<Usage> transformUsage(RequestResponse saaSObject) {

        ArrayList<Usage> usageList = new ArrayList<>();
        if (saaSObject.getRequest() == null && saaSObject.getResponse() == null){
            return usageList;
        }
        else{
            try{
                JsonNode actualObj = saaSObject.getResponse().getResponse();

                if (actualObj.isEmpty()){
//
                    return usageList;
                }
                else {
                    Consumer<JsonNode> data = (JsonNode node) -> {
                        Usage usage = Factory.createUsage(saaSObject,node.get("user_id").asText(), node.get("app_id").asText());
                        usage.setUtilisation(node.get("usage").asText());
                        usageList.add(usage);
                    };
                    actualObj.forEach(data);

                }
            }
            catch(Exception e){
                System.out.println(e.getMessage());
                return null;
            }
        }
        return usageList;
    }
}
