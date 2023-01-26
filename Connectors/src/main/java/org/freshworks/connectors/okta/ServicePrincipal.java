package org.freshworks.connectors.okta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.freshworks.connectors.BaseConnector;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("org.freshworks.connectors.okta.ServicePrincipal")
public class ServicePrincipal implements BaseConnector {

    int id;
    String servicePrincipalName;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServicePrincipalName() {
        return servicePrincipalName;
    }

    public void setServicePrincipalName(String servicePrincipalName) {
        this.servicePrincipalName = servicePrincipalName;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    Application application;

    public ServicePrincipal(){

    }
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

    public String getNexturl(String thisURl, Application app ){

        return "http://okta.freshworks.com/page=2";
    }
}
