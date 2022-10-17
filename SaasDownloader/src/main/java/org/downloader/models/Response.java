package org.downloader.models;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;

public class Response {

    JsonNode response;
    int responseCode;

    HashMap<String, Object> headers;

    public HashMap<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, Object> headers) {
        this.headers = headers;
    }

    public JsonNode getResponse() {
        return response;
    }

    public void setResponse(JsonNode response) {
        this.response = response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
