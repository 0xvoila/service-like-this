package org.example.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;
import org.example.models.SaaSObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OktaEndpointGenerator implements EndpointGeneratorInterface {

    String seed_url;

    public OktaEndpointGenerator(){
        this.seed_url = "http://localhost:3000/apps?_page=0";

    }
    @Override
    public ArrayList<SaaSObject> getNextEndpoints(SaaSObject saaSObject) {

        if (saaSObject.getRequest() == null && saaSObject.getRequest() == null){
//            It means that it is the starting point for this account
            saaSObject.setRequest(this.seed_url);
            ArrayList<SaaSObject> x = new ArrayList<>();
            x.add(saaSObject);
            return x;
        }
        else{
            try{

                HttpEntity entity = saaSObject.getResponse().getEntity();

                if ( entity != null){
                    String body = EntityUtils.toString(entity);
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode actualObj = mapper.readTree(body);
//                    System.out.println(actualObj);
                    if (actualObj.isEmpty()){
                        String x = "http://localhost:3000/users?_page=1";

                        saaSObject.setRequest(x);
                        ArrayList<SaaSObject> aa = new ArrayList<>();
                        aa.add(saaSObject);
                        return aa;

                    }
                    else {
                        URI uri = saaSObject.getRequest().getURI();
                        HashMap<String, String> x = queryToMap(uri.getQuery());

                        int count = Integer.parseInt(x.get("_page")) + 1;
                        x.put("_page", Integer.toString(count));
                        String queryString = mapToQuery(x);

                        URI newURI = new URIBuilder().setScheme(uri.getScheme()).setHost(uri.getHost()).setPort(3000).setPath(uri.getPath()).setCustomQuery(queryString).build();
                        saaSObject.getRequest().setURI(newURI);

                        ArrayList<SaaSObject> res = new ArrayList<SaaSObject>();
                        res.add(saaSObject);
                        return res;
                    }
                }
            }
            catch(Exception e){
                System.out.println(e.getMessage());
                return null;
            }

            return null;
        }
    }

    public HashMap<String, String> queryToMap(String queryString){

        HashMap<String, String> y = new HashMap<>();

        Stream<String> s = Stream.of(queryString.split("&"));
        s.map(param -> param.split("=")).forEach( x -> y.put(x[0], x[1].toString()));
        return y;
    }

    public String mapToQuery(HashMap<String, String> map){

        return map.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));
    }
}
