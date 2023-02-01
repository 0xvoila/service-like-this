package org.freshworks.beans.box;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import org.freshworks.beans.BaseBean;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("org.freshworks.beans.box.User")
public class User extends BaseBean {

    String id;
    String login;
    String created_at;
    String role;
    String status;
    Long space_amount;
    Long space_used;
    Long max_upload_size;

    JsonNode parentNode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSpace_amount() {
        return space_amount;
    }

    public void setSpace_amount(Long space_amount) {
        this.space_amount = space_amount;
    }

    public Long getSpace_used() {
        return space_used;
    }

    public void setSpace_used(Long space_used) {
        this.space_used = space_used;
    }

    public Long getMax_upload_size() {
        return max_upload_size;
    }

    public void setMax_upload_size(Long max_upload_size) {
        this.max_upload_size = max_upload_size;
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
