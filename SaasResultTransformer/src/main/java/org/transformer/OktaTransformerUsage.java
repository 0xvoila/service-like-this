package org.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import org.transformer.models.Factory;
import org.transformer.models.RequestResponse;
import org.transformer.models.Usage;

import java.util.ArrayList;
import java.util.function.Consumer;

public class OktaTransformerUsage  {


    public ArrayList<? extends Usage> transformUsage(RequestResponse saaSObject) {

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
