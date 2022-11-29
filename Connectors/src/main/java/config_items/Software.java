package config_items;

import org.example.connectors.okta.Application;
import org.example.connectors.okta.User;

public class Software extends BaseConfigItem{

    String id;
    String userName;
    String appId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(User user) {
        this.userName = user.getUserName();
    }

    public String getId() {
        return id;
    }

    public void setId(Application application) {
        this.id = appId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
