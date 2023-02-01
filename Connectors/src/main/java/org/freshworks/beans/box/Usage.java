package org.freshworks.beans.box;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import org.freshworks.beans.BaseBean;
import org.freshworks.core.model.RequestResponse;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("org.freshworks.beans.box.Usage")

public class Usage extends BaseBean {

    Source source;
    CreatedBy created_by;

    String created_at;

    JsonNode parentNode;

    public Usage(){

    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public CreatedBy getCreated_by() {
        return created_by;
    }

    public void setCreated_by(CreatedBy created_by) {
        this.created_by = created_by;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @Override
    public Boolean filter() {
        if(this.getSource().id == null){
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
