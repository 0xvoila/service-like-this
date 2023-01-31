package org.freshworks.assets.box;

import org.freshworks.assets.BaseAsset;
import org.freshworks.beans.box.Application;
import org.freshworks.beans.box.User;

public class Software extends BaseAsset {

    String id;

    public String getId() {
        return id;
    }

    public void setId(User user) {
        this.id = user.getId();
    }
}
