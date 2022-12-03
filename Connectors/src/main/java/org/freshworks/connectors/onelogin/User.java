package org.freshworks.connectors.onelogin;

import org.freshworks.connectors.BaseConnector;

public class User implements BaseConnector {

    ServicePrincipal servicePrincipal;

    String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User(ServicePrincipal servicePrincipal){
        this.servicePrincipal = servicePrincipal;
    }

    //    This is going to be like get saas object
    public String getUrl(String fromUrl){

        return fromUrl + "/" + this.servicePrincipal.groupId;
    }


    public Boolean isComplete(){
        return false;
    }
}
