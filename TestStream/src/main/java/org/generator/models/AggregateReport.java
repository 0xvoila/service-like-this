package org.generator.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class AggregateReport {

    String syncId;
    String url;
    ArrayList<HashMap<String, Integer>> report = new ArrayList<>();

    public AggregateReport(){

    }

    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<HashMap<String, Integer>> getReport() {
        return report;
    }

    public void setReport(ArrayList<HashMap<String, Integer>> report) {
        this.report = report;
    }

    public void incrementCountByStatus(String status){

        if ( this.report.isEmpty()){
            HashMap<String, Integer> x = new HashMap<String, Integer>();
            x.put(status, 1);
            this.report.add(x);
        }
        else{
            this.report.stream().map((value) -> {
                if ( value.containsKey(status)){
                    value.put(status, value.get(status) + 1 );
                }
                else{
                    value.put(status, 1 );
                }
                return value;
            }).forEach(System.out::println);
        }

    }
}
