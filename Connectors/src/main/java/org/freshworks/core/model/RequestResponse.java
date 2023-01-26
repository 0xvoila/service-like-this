package org.freshworks.core.model;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RequestResponse {

    String connectorName;
    HttpRequest request;
    HttpResponse<String> response;


    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public HttpResponse<String> getResponse() {
        return response;
    }

    public void setResponse(HttpResponse<String> response) {
        this.response = response;
    }
}
