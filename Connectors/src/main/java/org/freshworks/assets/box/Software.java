package org.freshworks.assets.box;

import com.github.javafaker.App;
import org.freshworks.assets.BaseAsset;
import org.freshworks.beans.box.Application;
import org.freshworks.beans.box.Usage;
import org.freshworks.beans.box.User;
import org.freshworks.core.Annotations.FreshLookup;

@FreshLookup(leftClass = User.class, rightClass = Usage.class, leftClassField = "id", rightClassField = "id", join_type = "inner")
public class Software extends BaseAsset {

    String id;

    String name;

    String appName;

    public String getId() {
        return id;
    }

    public void setId(User user) {
        this.id = user.getId();
    }

    public String getName() {
        return name;
    }

    public void setName(Usage usage) {
        this.name = usage.getUsage();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(Application app) {
        this.appName = app.getApplicationName();
    }
}
