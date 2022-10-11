package org.example.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequestResponse {

    String syncId;
    String accountName;
    String appName;
    Request request;
    Response response;

    HashMap<String, Object> tags;

    public RequestResponse(String syncId, String accountName, String appName, Request request, Response response, HashMap<String, Object> x ){
        this.syncId = syncId;
        this.accountName = accountName;
        this.appName = appName;
        this.request = request;
        this.response = response;
        this.tags = x;
    }

    public RequestResponse(String syncId, String accountName, String appName, HashMap<String, Object> x ){
        this.syncId = syncId;
        this.accountName = accountName;
        this.appName = appName;
        this.tags = x;
    }

    public RequestResponse(String syncId, String accountName, String appName ){
        this.syncId = syncId;
        this.accountName = accountName;
        this.appName = appName;
    }

    public String getAccountName(){
        return  this.accountName;
    }

    public void setAccountName(String accountName){
        this.accountName = accountName;
    }
    public String getAppName(){
        return this.appName;
    }

    public void setAppName(String appName){
        this.appName = appName;
    }

    public String getSyncId(){
        return this.syncId;
    }

    public void setSyncId(String syncId){
        this.syncId = syncId;
    }

    public Request getRequest(){
        return this.request;
    }

    public void setRequest(Request request){
        this.request = request;
    }

    public Response getResponse(){
        return this.response;
    }

    public void setResponse(Response response){
        this.response = response;
    }

//    public JsonNode  getResponseBody() throws IOException {
//        if (this.response.getEntity() != null){
//            ObjectMapper mapper = new ObjectMapper();
//            String body = EntityUtils.toString(this.response.getEntity());
//            JsonNode actualObj = mapper.readTree(body);
//            return actualObj;
//        }
//        else {
//            return null;
//        }
//    }
//    public int getResponseCode(){
//        return this.response.getStatusLine().getStatusCode();
//    }
//    public HashMap<String, Object> getTags(){
//        return this.tags;
//    }
//
//    public void setTags(HashMap<String, Object> x){
//        this.tags = x;
//    }
//
//    public void setTags(String key, Object value){
//        this.tags.put(key, value);
//    }
//
//    public void setTagEntry(Map.Entry<String, Object> x){
//     this.tags.put(x.getKey(), x.getValue()) ;
//    }
//
//    public HashMap<String, String> getRequestParam(){
//        HashMap<String, String> y = new HashMap<>();
//        String queryString = this.request.getURI().getQuery().toString();
//        Stream<String> s = Stream.of(queryString.split("&"));
//        s.map(param -> param.split("=")).forEach( x -> y.put(x[0], x[1].toString()));
//        return y;
//    }
//
//    public void setRequestParameter(String key, Object value) throws URISyntaxException {
//        HashMap<String, String> y = new HashMap<>();
//        String queryString = this.request.getURI().getQuery().toString();
//        String beforePath = this.request.getURI().toString().split("\\?")[0];
//
//        Stream<String> s = Stream.of(queryString.split("&"));
//        s.filter(param -> !param.equals(key)).map(param -> param.split("=")).forEach( x -> y.put(x[0], x[1].toString()));
//        y.put(key, value.toString());
//        String v = mapToQuery(y);
//        URI uri = new URIBuilder(beforePath).setCustomQuery(v).build();
//        this.request.setURI(uri);
//    }
//    public String mapToQuery(HashMap<String, String> map){
//
//        return map.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining("&"));
//    }
//
//    public String toString(){
//        ObjectMapper mapper = new ObjectMapper();
//        String requestStr = "";
////        String responseStr = "";
//        try {
//            requestStr = mapper.writeValueAsString(this.request);
////            responseStr = mapper.writeValueAsString(this.response);
//
//        } catch (JsonProcessingException e) {
//            System.out.println(e.getMessage());
//        }
//
//        return "{\"syncId\":" + syncId + "," + "\"accountName\":" + accountName + "," + "\"appName\":" + appName + "," + "\"request\":" + requestStr + "\"}";
//    }
}
