package org.example.scheduler;

import org.example.models.RequestResponse;

import java.util.*;

public class Scheduler {

    HashMap<String, HashMap<String, Object>> database = new HashMap<>();
    HashMap<String, Stack<RequestResponse>> accountUrls = new HashMap<>();

    public Scheduler(){
        HashMap<String, Object> c = new HashMap<String, Object>();
        c.put("priority", 1);
        c.put("last_response_status", 200);
        c.put("api_threshold", 20 );
        c.put("server_response_time", 1000);
        database.put("Google", c);

        c.put("priority", 2);
        c.put("last_response_status", 500);
        c.put("api_threshold", 50 );
        c.put("server_response_time", 5000);
        database.put("Microsoft", c);
    }

    public void addEndPoints(String accountName, ArrayList<RequestResponse> urls){

//        Here use the bloom filter if url in urls has been traversed
        Stack<RequestResponse> x = accountUrls.get(accountName);

        if ( x == null ){
            x = new Stack<>();
            accountUrls.put(accountName, x);
            urls.stream().map(x::push).forEach(y -> System.out.println(y));
        }
        else{
            accountUrls.put(accountName, x);
            urls.stream().map(x::push).forEach(y -> System.out.println(y));
        }

    }

    public ArrayList<RequestResponse> getEndpoints(String accountName, int number){

        Stack<RequestResponse> x = accountUrls.get(accountName);
        ArrayList<RequestResponse> y = new ArrayList<>();

        if (x.size() >= number){
            for (int i=0; i<number; i++){
                y.add(x.pop());
            }
        }
        else{
            for (int i=0; i<x.size(); i++){
                y.add(x.pop());
            }
        }

        return y;
    }

//    This API, will let the engine know whether there are more urls to return right now. Remember it might be
    // possible to call that urls are not available to return now but may be available after X seconds.

    public Boolean hasNext(String accountName){
        Stack<RequestResponse> x = accountUrls.get(accountName);

        if ( x.empty()){
            return false;
        }
        else {
            return true;
        }
    }

}
