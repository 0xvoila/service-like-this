package org.transformer;

import com.fasterxml.jackson.databind.JsonNode;
import org.transformer.models.*;

import java.util.ArrayList;
import java.util.function.Consumer;

public class OktaTransformerUser implements TransformerInterface {

    public  ArrayList<? extends Object> transform(RequestResponse saaSObject) {

        ArrayList<User> userList = new ArrayList<>();
        if (saaSObject.getRequest() == null && saaSObject.getResponse() == null){
            return userList;
        }
        else{
            try{
                JsonNode actualObj = saaSObject.getResponse().getResponse();

                    if (actualObj.isEmpty()){
//
                        return userList;
                    }
                    else {
                        Consumer<JsonNode> data = (JsonNode node) -> {
                            User user = Factory.createUser(saaSObject,node.get("id").asText());
                            user.setApplicationId(node.get("app_id").asText());
                            userList.add(user);
                        };
                        actualObj.forEach(data);

                    }
            }
            catch(Exception e){
                System.out.println(e.getMessage());
                return null;
            }
        }
        return userList;
    }
}
