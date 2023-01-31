package org.freshworks.beans;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;


import static org.freshworks.Constants.JsonTypeInfo_As_PROPERTY;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY, property = JsonTypeInfo_As_PROPERTY)
@JsonSubTypes({

        @JsonSubTypes.Type(value = org.freshworks.beans.box.User.class, name = "org.freshworks.beans.box.User"),
        @JsonSubTypes.Type(value = org.freshworks.beans.box.Application.class, name = "org.freshworks.beans.box.Application"),
        @JsonSubTypes.Type(value = org.freshworks.beans.box.Usage.class, name = "org.freshworks.beans.box.Usage")
})
public abstract class BaseBean {

    public abstract Boolean filter();
    public abstract void transform();

    public abstract void setParentNode(JsonNode parentJSONNode);

}
