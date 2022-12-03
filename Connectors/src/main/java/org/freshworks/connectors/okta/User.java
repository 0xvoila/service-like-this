package org.freshworks.connectors.okta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.freshworks.connectors.BaseConnector;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("org.example.connectors.okta.User")
public class User implements BaseConnector {

    ServicePrincipal servicePrincipal;

    int id;

    String userName;

    public User(){

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User(ServicePrincipal servicePrincipal){
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
