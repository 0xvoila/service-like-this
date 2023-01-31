package org.freshworks.assets.box;

import org.freshworks.assets.BaseAsset;
import org.freshworks.beans.box.Application;

public class S3Bucket extends BaseAsset {

    String id;

    public String getId() {
        return id;
    }

    public void setId(Application application) {
        this.id = application.getId();
    }
}
