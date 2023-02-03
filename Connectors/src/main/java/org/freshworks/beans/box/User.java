package org.freshworks.beans.box;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.freshworks.beans.BaseBean;


@NoArgsConstructor
@Getter @Setter
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
