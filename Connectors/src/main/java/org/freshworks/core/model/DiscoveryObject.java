package org.freshworks.core.model;

import org.freshworks.beans.BaseBean;


public class DiscoveryObject {

    String connectorName;
    BaseBean baseBean;

    public DiscoveryObject(){

    }
    public DiscoveryObject(String connectorName, BaseBean baseBean){
        this.connectorName = connectorName;
        this.baseBean = baseBean;
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public BaseBean getConnectorClass() {
        return baseBean;
    }

    public void setConnectorClass(BaseBean baseBean) {
        this.baseBean = baseBean;
    }
}
