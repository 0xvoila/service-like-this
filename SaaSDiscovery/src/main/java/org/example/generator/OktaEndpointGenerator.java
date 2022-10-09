package org.example.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OktaEndpointGenerator implements EndpointGeneratorInterface {

    ArrayList<String> seed_url;

    public OktaEndpointGenerator(){
        seed_url = new ArrayList<>();
        seed_url.add("http://localhost:3000/apps?_page=0");
    }
    @Override
    public ArrayList<String> getNextEndpoints(String syncId, String accountName, HttpGet request, HttpResponse response) {

        if (request == null && response == null){
//            It means that it is the starting point for this account
            return this.seed_url;
        }
        else{
            try{

                HttpEntity entity = response.getEntity();

                if ( entity != null){
                    String body = EntityUtils.toString(entity);
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode actualObj = mapper.readTree(body);

                    if (actualObj.isEmpty()){
                        return new ArrayList<String>();
                    }
                    else {
                        URI uri = request.getURI();
                        HashMap<String, String> x = queryToMap(uri.getQuery());

                        int count = Integer.parseInt(x.get("_page")) + 1;
                        x.put("_page", Integer.toString(count));
                        String queryString = mapToQuery(x);

                        URI newURI = new URIBuilder().setScheme(uri.getScheme()).setHost(uri.getHost()).setPath(uri.getPath()).setCustomQuery(queryString).build();
                        request.setURI(newURI);

                        ArrayList<String> res = new ArrayList<String>();
                        res.add(newURI.toString());
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
