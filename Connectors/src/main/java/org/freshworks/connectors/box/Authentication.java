package org.freshworks.connectors.box;

public class Authentication {

    String project;
    String zone;
    String instance;

    public Authentication(){

    }
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }
}
