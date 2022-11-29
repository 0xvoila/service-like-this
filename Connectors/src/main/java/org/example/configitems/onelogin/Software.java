package org.example.configitems.onelogin;

import org.example.connectors.onelogin.Application;
import org.example.connectors.onelogin.ServicePrincipal;


public class Software extends config_items.Software {

    String id;
    String userName;
    String appId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(ServicePrincipal servicePrincipal) {
        this.userName = servicePrincipal.getPrincipleName();
    }

    public String getId() {
        return id;
    }

    public void setId(Application application) {
        this.id = appId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
