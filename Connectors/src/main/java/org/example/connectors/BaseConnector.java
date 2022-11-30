package org.example.connectors;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.example.connectors.okta.Application;
import org.example.connectors.okta.ServicePrincipal;
import org.example.connectors.okta.User;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({

        @JsonSubTypes.Type(value = Application.class, name = "org.example.connectors.okta.Application"),
        @JsonSubTypes.Type(value = ServicePrincipal.class, name = "org.example.connectors.okta.ServicePrincipal"),
        @JsonSubTypes.Type(value = User.class, name = "org.example.connectors.okta.User")
})
public interface BaseConnector {

}
