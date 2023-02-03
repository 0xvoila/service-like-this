package org.freshworks.beans.box;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.freshworks.beans.BaseBean;

@Getter @Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("org.freshworks.beans.box.Usage")

public class Usage extends BaseBean {

    String created_at;

    Source source;

    Created_by created_by;

    JsonNode parentNode;


    @NoArgsConstructor
    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Source {

        String type;
        String id;
        String name;
        String login;

    }

    @NoArgsConstructor
    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Created_by {

        String type;
        String id;
        String name;
        String login;

    }

    @Override
    public Boolean filter() {
        if(this.getSource().getName() == null){
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
