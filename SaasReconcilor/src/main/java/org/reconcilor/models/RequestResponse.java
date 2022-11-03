package org.reconcilor.models;

import java.util.HashMap;

public class RequestResponse {

    String syncId;
    String accountName;
    String appName;

    String resourceName;

    Request request;
    Response response;

    Application application;

    User user;

    Usage usage;

    HashMap<String, Object> tags = new HashMap<>();

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }


    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public HashMap<String, Object> getTags() {
        return tags;
    }

    public void setTag(String key, Object value){
        this.tags.put(key, value);
    }
    public void setTags(HashMap<String, Object> tags) {
        this.tags = tags;
    }

    public RequestResponse(){

    }
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

    public RequestResponse(String syncId, String accountName, String appName, String resourceName){
        this.syncId = syncId;
        this.accountName = accountName;
        this.appName = appName;
        this.resourceName = resourceName;
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
