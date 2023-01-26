package org.freshworks.connectors.onelogin;

import org.freshworks.connectors.BaseConnector;

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

    public String getNexturl(String thisURl, org.freshworks.connectors.okta.Application app ){

        return "http://okta.freshworks.com/page=2";
    }

}
