package org.reconcilor.models;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;


public class Request {

    String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    String url;
    HashMap<String, Object> queryParam = new HashMap<>();
    HashMap<String, Object> headers = new HashMap<>();
    String method = "GET";
    HashMap<String, Object> tags = new HashMap<>();

    public Request(){
        UUID uuid=UUID.randomUUID();
        this.uuid = uuid.toString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.uuid = Integer.toString(Objects.hashCode(url + getQueryParam().toString() + getHeaders().toString()));
    }

    public HashMap<String, Object> getQueryParam() {
        return queryParam;
    }

    public void setQueryParam(HashMap<String, Object> queryParam) {
        this.queryParam = queryParam;
        this.uuid = Integer.toString(Objects.hashCode(url + getQueryParam().toString() + getHeaders().toString()));
    }

    public HashMap<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, Object> headers) {
        this.headers = headers;
        this.uuid = Integer.toString(Objects.hashCode(url + getQueryParam().toString() + getHeaders().toString()));
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
        this.uuid = Integer.toString(Objects.hashCode(url + getQueryParam().toString() + getHeaders().toString()));
    }

    public HashMap<String, Object> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, Object> tags) {
        this.tags = tags;
        this.uuid = Integer.toString(Objects.hashCode(url + getQueryParam().toString() + getHeaders().toString()));
    }

    public void setTags(String key, Object value){
        this.tags.put(key, value);
        this.uuid = Integer.toString(Objects.hashCode(url + getQueryParam().toString() + getHeaders().toString()));
    }

}
