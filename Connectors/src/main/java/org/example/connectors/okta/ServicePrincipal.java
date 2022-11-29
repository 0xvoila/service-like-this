package org.example.connectors.okta;

import org.example.connectors.BaseConnector;

public class ServicePrincipal implements BaseConnector {

    int groupId;

    Application application;

    public ServicePrincipal(Application application){
        this.application = application;
    }

    //    This is going to be like get saas object
    public String getUrl(String fromUrl){

        return fromUrl + "/" + this.application.appId;
    }


    public Boolean isComplete(){
        return false;
    }

}
