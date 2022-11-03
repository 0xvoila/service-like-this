package org.reconcilor.serde;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.reconcilor.models.*;

public class RequestResponseSerde {

    private RequestResponseSerde(){

    }

    public static Serde<RequestResponse> getRequestResponse(){
        return Serdes.serdeFrom(new JsonSerializer<RequestResponse>(), new JsonDeserializer<RequestResponse>(RequestResponse.class));
    }

    public static Serde<User> getUser(){
        return Serdes.serdeFrom(new JsonSerializer<User>(), new JsonDeserializer<User>(User.class));
    }

    public static Serde<Application> getApplication(){
        return Serdes.serdeFrom(new JsonSerializer<Application>(), new JsonDeserializer<Application>(Application.class));
    }

    public static Serde<Usage> getUsage(){
        return Serdes.serdeFrom(new JsonSerializer<Usage>(), new JsonDeserializer<Usage>(Usage.class));
    }

    public static Serde<Report> getReport(){
        return Serdes.serdeFrom(new JsonSerializer<Report>(), new JsonDeserializer<Report>(Report.class));
    }

}
