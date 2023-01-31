package org.freshworks.core.model;

import org.freshworks.beans.BaseBean;


public class DiscoveryObject {

    String beanName;
    BaseBean baseBean;

    public DiscoveryObject(){

    }
    public DiscoveryObject(String beanName, BaseBean baseBean){
        this.beanName = beanName;
        this.baseBean = baseBean;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public BaseBean getBaseBean() {
        return baseBean;
    }

    public void setBaseBean(BaseBean baseBean) {
        this.baseBean = baseBean;
    }
}
