package org.freshworks.connectors.okta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.freshworks.connectors.BaseConnector;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("org.example.connectors.okta.Application")
public class Application implements BaseConnector {

    int appId;
    String appName;


    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Application(){

    }

//    This is going to be like get saas object
    public String getUrl(String fromUrl){

        return fromUrl;
    }

    public Boolean isComplete(){
        return false;
    }



}
