package org.example.connectors.onelogin;

import org.example.connectors.BaseConnector;

public class Application implements BaseConnector {

    int appId;

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
