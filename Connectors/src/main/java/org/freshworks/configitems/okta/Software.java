package org.freshworks.configitems.okta;

import org.freshworks.connectors.okta.Application;
import org.freshworks.connectors.okta.Usage;
import org.freshworks.connectors.okta.User;

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


    public void setUserName(User user, Usage usage) {
        this.userName = user.getUserName() + usage.getUserUsage();
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
