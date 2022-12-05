package org.freshworks.configitems.box;

import config_items.BaseConfigItem;
import org.freshworks.connectors.box.Application;
import org.freshworks.connectors.box.User;

public class Software extends BaseConfigItem {

    String id;
    String softwareName;

    String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(User user) {
        this.userName = user.getName();
    }

    public Software(){

    }

    public String getId() {
        return id;
    }

    public void setId(Application application) {
        this.id = application.getId();
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public void setSoftwareName(Application application) {
        this.softwareName = application.getApplicationName();
    }
}
