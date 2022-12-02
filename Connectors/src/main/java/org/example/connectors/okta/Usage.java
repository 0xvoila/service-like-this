package org.example.connectors.okta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.example.connectors.BaseConnector;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("org.example.connectors.okta.User")
public class Usage implements BaseConnector {

    ServicePrincipal servicePrincipal;

    int id;

    String userUsage;

    public Usage(){

    }

    public ServicePrincipal getServicePrincipal() {

        return servicePrincipal;
    }

    public void setServicePrincipal(ServicePrincipal servicePrincipal) {
        this.servicePrincipal = servicePrincipal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserUsage() {
        return userUsage;
    }

    public void setUserUsage(String userUsage) {
        this.userUsage = userUsage;
    }

    public Usage(ServicePrincipal servicePrincipal){
        this.servicePrincipal = servicePrincipal;
    }

    //    This is going to be like get saas object
    public String getUrl(String fromUrl){

        return fromUrl + "/" + this.servicePrincipal.id;
    }


    public Boolean isComplete(){
        return false;
    }
}
