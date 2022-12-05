package org.freshworks.connectors;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.freshworks.connectors.okta.Application;
import org.freshworks.connectors.okta.ServicePrincipal;
import org.freshworks.connectors.okta.Usage;
import org.freshworks.connectors.okta.User;

import static org.freshworks.Constants.JsonTypeInfo_As_PROPERTY;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY, property = JsonTypeInfo_As_PROPERTY)
@JsonSubTypes({

        @JsonSubTypes.Type(value = Application.class, name = "org.freshworks.connectors.okta.Application"),
        @JsonSubTypes.Type(value = ServicePrincipal.class, name = "org.freshworks.connectors.okta.ServicePrincipal"),
        @JsonSubTypes.Type(value = User.class, name = "org.freshworks.connectors.okta.User"),
        @JsonSubTypes.Type(value = Usage.class, name = "org.freshworks.connectors.okta.Usage"),
        @JsonSubTypes.Type(value = org.freshworks.connectors.box.User.class, name = "org.freshworks.connectors.box.User"),
        @JsonSubTypes.Type(value = org.freshworks.connectors.box.Application.class, name = "org.freshworks.connectors.box.Application")
})
public interface BaseConnector {

}
