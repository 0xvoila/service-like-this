package org.example.models;

public class Report {

    int totalRequest = 0 ;
    int totalFailure = 0;
    int totalDelayed = 0;

    public Report(){

    }

    public int getTotalRequest() {
        return totalRequest;
    }

    public void setTotalRequest(int totalRequest) {
        this.totalRequest = totalRequest;
    }

    public int getTotalFailure() {
        return totalFailure;
    }

    public void setTotalFailure(int totalFailure) {
        this.totalFailure = totalFailure;
    }

    public int getTotalDelayed() {
        return totalDelayed;
    }

    public void setTotalDelayed(int totalDelayed) {
        this.totalDelayed = totalDelayed;
    }

    public void incrementFailure(){
        this.totalFailure = this.totalFailure + 1;
    }

    public void incrementSuccess(){
        this.totalFailure = this.totalFailure + 1;
    }

    public void incrementReceived(){
        this.totalFailure = this.totalFailure + 1;
    }
}
