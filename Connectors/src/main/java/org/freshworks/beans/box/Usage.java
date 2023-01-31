package org.freshworks.beans.box;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import org.freshworks.beans.BaseBean;
import org.freshworks.core.model.RequestResponse;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("org.freshworks.beans.box.Usage")

public class Usage extends BaseBean {

    String id;
    String usage;
    String login;
    JsonNode parentNode;

    public Usage(){

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


    public static Boolean isComplete(RequestResponse requestResponse){
        return false;
    }

    @Override
    public Boolean filter() {
        if(this.id == null){
            return false;
        }
        else{
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

    public JsonNode getParentNode() {
        return parentNode;
    }
}
