package org.example.models;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.net.http.HttpRequest;

public class SaaSObject {

    String syncId;
    String accountName;
    String appName;
    HttpGet request;
    CloseableHttpResponse response;

    public SaaSObject(String syncId, String accountName, String appName, HttpGet request, CloseableHttpResponse response){
        this.syncId = syncId;
        this.accountName = accountName;
        this.appName = appName;
        this.request = request;
        this.response = response;
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

    public HttpGet getRequest(){
        return this.request;
    }

    public void setRequest(String url){
        this.request = new HttpGet(url);
    }

    public void setRequest(HttpGet request){
        this.request = request;
    }

    public HttpResponse getResponse(){
        return this.response;
    }

    public void setRequest(CloseableHttpResponse response){
        this.response = response;
    }

}
