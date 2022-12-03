package org.freshworks.configitems.okta;

import org.freshworks.connectors.okta.User;

public class S3Bucket {

    String bucketName;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(User bucketName) {
        this.bucketName = bucketName.getUserName();
    }
}

