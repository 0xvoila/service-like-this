package org.generator.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.HashMap;

public class JacksonTest {

    String syncId;
    int status;

    HashMap<String, String> properties;

    String name;

    public JacksonTest(){

    }

    public JacksonTest(String syncId, int status){
        this.syncId = syncId;
        this.status = status;
        this.properties = new HashMap<>();
        this.name = "amit";
        this.custom = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<HashMap<String, Integer>> getReport() {
        return report;
    }

    public void setReport(ArrayList<HashMap<String, Integer>> report) {
        this.report = report;
    }

    public int getCustom() {
        return custom;
    }

    public void setCustom(int custom) {
        this.custom = custom;
    }

    @JsonDeserialize(using = ReportDeserializer.class)
    ArrayList<HashMap<String, Integer>> report;

    @JsonSerialize(using = CustomSerializer.class)
    int custom;


    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }

    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @JsonAnyGetter
    public HashMap<String, String> getProperties(){
        return properties;
    }

    @JsonGetter("name")
    public String hola(){
        return this.name;
    }

//    @JsonValue
//    public String ser(){
//        return "{\"name\":\"amit\"}";
//    }
}
