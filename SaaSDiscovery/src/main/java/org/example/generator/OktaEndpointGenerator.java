package org.example.generator;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.models.RequestResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class OktaEndpointGenerator extends EndpointGenerator implements EndpointGeneratorInterface {

    String seed_url;

    public OktaEndpointGenerator(){
        this.seed_url = "http://localhost:3000/apps?_page=0";

    }
    @Override
    public ArrayList<RequestResponse> getNextEndpoints(RequestResponse saaSObject) {

        if (saaSObject.getRequest() == null && saaSObject.getRequest() == null){
//            It means that it is the starting point for this account
            saaSObject.setRequest(this.seed_url);
            ArrayList<RequestResponse> x = new ArrayList<>();
            x.add(saaSObject);
            return x;
        }
        else{
            try{
                JsonNode actualObj = saaSObject.getResponseBody();
//                  System.out.println(actualObj);
                    if (actualObj.isEmpty()){
                        String x = "http://localhost:3000/users?_page=1";

                        saaSObject.setRequest(x);
                        ArrayList<RequestResponse> aa = new ArrayList<>();
                        aa.add(saaSObject);
                        return aa;

                    }
                    else {

                        URI uri = saaSObject.getRequest().getURI();
                        HashMap<String, String> x = queryToMap(uri.getQuery());
                        int count = Integer.parseInt(saaSObject.getRequestParam().get("_page")) + 1;
                        saaSObject.setRequestParameter("_page", count);
                        ArrayList<RequestResponse> res = new ArrayList<RequestResponse>();
                        res.add(saaSObject);
                        return res;
                    }
            }
            catch(Exception e){
                System.out.println(e.getMessage());
                return null;
            }
        }
    }


}
