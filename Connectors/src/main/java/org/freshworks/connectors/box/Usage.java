package org.freshworks.connectors.box;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.freshworks.connectors.BaseConnector;
import org.freshworks.core.model.RequestResponse;


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

    public static RequestResponse getNextRequest(RequestResponse requestResponse, Application app ){

        return requestResponse;
    }

    public static Boolean isComplete(RequestResponse requestResponse){
        return false;
    }
}
