package org.example.configitems.okta;

import org.example.connectors.okta.User;

public class S3Bucket {

    String bucketName;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(User bucketName) {
        this.bucketName = bucketName.getUserName();
    }
}

