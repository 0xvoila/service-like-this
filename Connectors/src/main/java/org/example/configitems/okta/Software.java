package org.example.configitems.okta;

import org.example.App;
import org.example.connectors.okta.Application;
import org.example.connectors.okta.ServicePrincipal;
import org.example.connectors.okta.User;

public class Software extends config_items.Software {

    String id;
    String userName;

    String usage;

    @Override
    public String getAppId() {
        return appId;
    }


    public void setAppId(Application application) {
        this.appId = application.getAppName();
    }

    String appId;


    public String getUserName() {
        return userName;
    }

    public void setUserName(User user) {
        this.userName = user.getUserName();
//        System.out.println("Setting up the username to "  + this.userName);
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(Application usage) {
        this.usage = usage.getAppName();
    }

    public String getId() {
        return id;
    }

    public void setId(Application application) {
        this.id = application.getAppId() + "23";
//        System.out.println("Setting up id of the software " + this.id);
    }

}
