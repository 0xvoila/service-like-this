package org.freshworks.assets.box;

import org.freshworks.assets.BaseAsset;
import org.freshworks.beans.box.Application;
import org.freshworks.beans.box.Usage;
import org.freshworks.beans.box.User;
import org.freshworks.core.Annotations.FreshLookup;

@FreshLookup(leftClass = User.class, rightClass = Usage.class, leftClassField = "id", rightClassField = "login", join_type = "inner")
public class KeyPair extends BaseAsset {

    String id;
    String key;
    String value;

    public String getId() {
        return id;
    }

    public void setId(Application app) {
        this.id = app.getId();
    }

    public String getKey() {
        return key;
    }

    public void setKey(User user) {
        this.key = user.getName();
    }

    public String getValue() {
        return value;
    }

    public void setValue(Usage usage) {
        this.value = usage.getUsage();
    }
}
