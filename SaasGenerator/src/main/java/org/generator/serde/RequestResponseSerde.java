package org.generator.serde;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.generator.models.Report;
import org.generator.models.RequestResponse;

public class RequestResponseSerde {

    private RequestResponseSerde(){

    }

    public static Serde<RequestResponse> getRequestResponse(){
        return Serdes.serdeFrom(new JsonSerializer<RequestResponse>(), new JsonDeserializer<RequestResponse>(RequestResponse.class));
    }

    public static Serde<Report> getReport(){
        return Serdes.serdeFrom(new JsonSerializer<Report>(), new JsonDeserializer<Report>(Report.class));
    }
}
