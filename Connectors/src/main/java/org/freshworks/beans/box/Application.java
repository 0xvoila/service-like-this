package org.freshworks.beans.box;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import org.freshworks.beans.BaseBean;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("org.freshworks.beans.box.Application")
public class Application extends BaseBean {

    String id;

    String applicationName;

    JsonNode parentNode;

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


    @Override
    public Boolean filter() {
        if ( this.getId() == null){
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void transform() {

    }

    @Override
    public void setParentNode(JsonNode parentJSONNode) {

        this.parentNode = parentJSONNode;
    }

}
