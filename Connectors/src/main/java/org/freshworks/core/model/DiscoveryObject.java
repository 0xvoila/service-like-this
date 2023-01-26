package org.freshworks.core.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import org.freshworks.connectors.BaseConnector;
import org.freshworks.connectors.box.Usage;
import org.freshworks.connectors.box.User;
import org.freshworks.connectors.okta.Application;
import org.freshworks.connectors.okta.ServicePrincipal;



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
