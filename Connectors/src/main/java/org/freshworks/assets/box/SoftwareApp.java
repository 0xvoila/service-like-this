package org.freshworks.assets.box;

import org.freshworks.assets.BaseAsset;
import org.freshworks.beans.box.Application;
import org.freshworks.beans.box.Usage;
import org.freshworks.beans.box.User;
import org.freshworks.core.Annotations.FreshLookup;

@FreshLookup(leftClass = User.class, rightClass = Usage.class, leftClassField = "id", rightClassField = "login", join_type = "inner")
public class SoftwareApp extends BaseAsset {

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
