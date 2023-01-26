package org.freshworks.connectors.box;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.github.javafaker.App;
import org.freshworks.connectors.BaseConnector;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("org.freshworks.connectors.box.Application")
public class Application implements BaseConnector {

    String id;

    String applicationName;

    public Application(){

    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNexturl(String thisURl, org.freshworks.connectors.okta.Application app ){

        return "http://okta.freshworks.com/page=2";
    }

    public Boolean filter(Application app ){
        if ( app.getId() == null){
            return false;
        }
        else {
            return true;
        }
    }

//    Core of the system will call your Application.getNextUrl();
}
