package org.freshworks.beans.box;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import org.freshworks.beans.BaseBean;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("org.freshworks.beans.box.Usage")

public class Usage extends BaseBean {

    String created_at;

    Source source;

    Created_by created_by;

    JsonNode parentNode;

    public Usage(){

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Source {

        String type;
        String id;
        String name;
        String login;

        public Source() {

        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Created_by {

        String type;
        String id;
        String name;
        String login;

        public Created_by(){

        }
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }
    }



    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Created_by getCreated_by() {
        return created_by;
    }

    public void setCreated_by(Created_by created_by) {
        this.created_by = created_by;
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
