package org.example.models;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

public class Request {

    String url;
    HashMap<String, Object> queryParam = new HashMap<>();
    HashMap<String, Object> headers = new HashMap<>();
    String method = "GET";
    HashMap<String, Object> tags = new HashMap<>();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HashMap<String, Object> getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(HashMap<String, Object> queryParam) {
        this.queryParam = queryParam;
    }

    public HashMap<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, Object> headers) {
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public HashMap<String, Object> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, Object> tags) {
        this.tags = tags;
    }

    public void setTags(String key, Object value){
        this.tags.put(key, value);
    }

}
