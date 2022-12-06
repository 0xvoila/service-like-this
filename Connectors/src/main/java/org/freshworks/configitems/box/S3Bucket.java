package org.freshworks.configitems.box;

import config_items.BaseConfigItem;
import org.freshworks.connectors.box.Application;
import org.freshworks.connectors.box.User;

public class S3Bucket extends BaseConfigItem {

    String id;

    public String getId() {
        return id;
    }

    public void setId(User user) {
        this.id = user.getId();
    }
}
