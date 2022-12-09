package org.freshworks.configitems.box;

import config_items.BaseConfigItem;
import org.freshworks.connectors.box.Application;
import org.freshworks.connectors.box.Usage;
import org.freshworks.connectors.box.User;
import org.freshworks.core.Annotations.FreshworksLookup;

@FreshworksLookup(leftClass = User.class, rightClass = Usage.class, leftClassField = "id", rightClassField = "login", join_type = "inner")
public class SoftwareApp extends BaseConfigItem {

    String id;
    String softwareName;

    String usage;

    String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(User user) {
        this.userName = user.getName();
    }

    public SoftwareApp(){

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

    public String getUsage() {

        return usage;
    }

    public void setUsage(Usage usage, User user) {
        this.usage = user.getAddress() + " / " + usage.getUsage();
    }
}
