package org.example.connectors.onelogin;

import org.example.connectors.BaseConnector;

public class ServicePrincipal implements BaseConnector {

    int groupId;
    String principleName;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getPrincipleName() {
        return principleName;
    }

    public void setPrincipleName(String principleName) {
        this.principleName = principleName;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    Application application;

    public ServicePrincipal(Application application){
        this.application = application;
    }

    //    This is going to be like get saas object
    public String getUrl(String fromUrl){

        return fromUrl + "/" + this.application.appId;
    }


    public Boolean isComplete(){
        return false;
    }

}
