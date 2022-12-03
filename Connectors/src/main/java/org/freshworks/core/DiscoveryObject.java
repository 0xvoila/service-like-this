package org.freshworks.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.freshworks.connectors.BaseConnector;
import org.freshworks.connectors.okta.Application;
import org.freshworks.connectors.okta.ServicePrincipal;

@JsonSubTypes({

        @JsonSubTypes.Type(value = Application.class, name = "org.freshworks.connectors.okta.Application"),
        @JsonSubTypes.Type(value = ServicePrincipal.class, name = "org.freshworks.connectors.okta.ServicePrincipal")
})

public class DiscoveryObject {

    String connectorName;
    BaseConnector connectorClass;

    public DiscoveryObject(){

    }
    public DiscoveryObject(String connectorName, BaseConnector connectorClass){
        this.connectorName = connectorName;
        this.connectorClass = connectorClass;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public BaseConnector getConnectorClass() {
        return connectorClass;
    }

    public void setConnectorClass(BaseConnector connectorClass) {
        this.connectorClass = connectorClass;
    }
}
