package org.transformer.models;

public class Factory {

    public static User createUser(RequestResponse requestResponse, String userId){
        User user = new User();
        user.setUserId(userId);
        user.setAccountName(requestResponse.getAccountName());
        user.setAppName(requestResponse.getAppName());
        user.setSyncId(requestResponse.getSyncId());
        user.setResourceName(requestResponse.getResourceName());
        return user;
    }

    public static Application createApplication(RequestResponse requestResponse, String appId){
        Application application = new Application();
        application.setApplicationId(appId);
        application.setAccountName(requestResponse.getAccountName());
        application.setAppName(requestResponse.getAppName());
        application.setSyncId(requestResponse.getSyncId());
        application.setResourceName(requestResponse.getResourceName());
        return application;
    }

    public static Usage createUsage(RequestResponse requestResponse, String userId, String appId){
        Usage usage = new Usage();
        usage.setUserId(userId);
        usage.setApplicationId(appId);
        usage.setAccountName(requestResponse.getAccountName());
        usage.setAppName(requestResponse.getAppName());
        usage.setSyncId(requestResponse.getSyncId());
        usage.setResourceName(requestResponse.getResourceName());
        return usage;
    }
}
