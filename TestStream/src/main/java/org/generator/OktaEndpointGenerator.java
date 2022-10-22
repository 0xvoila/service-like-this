package org.generator;

import com.fasterxml.jackson.databind.JsonNode;
import org.generator.models.Request;
import org.generator.models.RequestResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class OktaEndpointGenerator  implements EndpointGeneratorInterface {

    String seed_url;

    public OktaEndpointGenerator(){
        this.seed_url = "http://localhost:3000/apps";

    }
    @Override
    public ArrayList<RequestResponse> getNextEndpoints(RequestResponse saaSObject) {

        if (saaSObject.getRequest() == null && saaSObject.getResponse() == null){
//            It means that it is the starting point for this account
            Request request = new Request();
            request.setTags("domain", "app_sync");
            request.setTags("page", 0);

            HashMap<String, Object> xx = new HashMap<>();
            xx.put("_page", 0);

            request.setUrl(this.seed_url);
            request.setQueryParam(xx);

            saaSObject.setRequest(request);
            ArrayList<RequestResponse> x = new ArrayList<>();
            x.add(saaSObject);
            return x;
        }
        else{
            try{
                JsonNode actualObj = saaSObject.getResponse().getResponse();
//                  System.out.println(actualObj);
                    if (actualObj.isEmpty()){
                        String x = "http://localhost:3000/users";
                        HashMap<String, Object> params = new HashMap<>();
                        params.put("_page",1);
                        Request request = new Request();
                        request.setTags("domain", "user_sync");
                        request.setTags("page", 1);
                        request.setQueryParam(params);
                        request.setUrl(x);

                        saaSObject.setRequest(request);
                        ArrayList<RequestResponse> aa = new ArrayList<>();
                        aa.add(saaSObject);
                        return aa;

                    }
                    else {

                        int count = Integer.parseInt(saaSObject.getRequest().getQueryParam().get("_page").toString()) + 1;

                        Request request = new Request();
                        request.setTags("domain", "user_sync");
                        request.setTags("page", count);
                        request.setUrl(saaSObject.getRequest().getUrl());

                        HashMap<String, Object> params = saaSObject.getRequest().getQueryParam();
                        params.put("_page", count);
                        request.setQueryParam(params);

                        saaSObject.setRequest(request);
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
