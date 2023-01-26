package org.freshworks.connectors.box;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.freshworks.connectors.BaseConnector;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("org.freshworks.connectors.box.Usage")

public class Usage implements BaseConnector {

    String id;
    String usage;
    String login;
    Application application;

    public Usage(){

    }

    public Usage(Application application){
        this.application = application;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNexturl(String thisURl, org.freshworks.connectors.okta.Application app ){

        return "http://okta.freshworks.com/page=2";
    }
}
