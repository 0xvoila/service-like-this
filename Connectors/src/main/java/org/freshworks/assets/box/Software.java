package org.freshworks.assets.box;

import org.freshworks.assets.BaseAsset;
import org.freshworks.beans.box.Usage;
import org.freshworks.beans.box.User;
import org.freshworks.core.Annotations.FreshLookup;

@FreshLookup(leftClass = User.class, rightClass = Usage.Source.class, leftClassField = "login", rightClassField = "login", join_type = "inner")
public class Software extends BaseAsset {

    String name;
    String created_at;


    public String getName() {
        return name;
    }

    public void setName(User user) {
        this.name = user.getLogin();
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Usage usage) {
        this.created_at = usage.getCreated_at();
    }
}
