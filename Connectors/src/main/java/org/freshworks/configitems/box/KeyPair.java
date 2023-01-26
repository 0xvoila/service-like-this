package org.freshworks.configitems.box;

import config_items.BaseConfigItem;
import org.freshworks.connectors.box.Application;
import org.freshworks.connectors.box.Usage;
import org.freshworks.connectors.box.User;
import org.freshworks.core.Annotations.FreshLookup;

@FreshLookup(leftClass = User.class, rightClass = Usage.class, leftClassField = "id", rightClassField = "login", join_type = "inner")
public class KeyPair extends BaseConfigItem {

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
