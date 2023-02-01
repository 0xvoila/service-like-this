package test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.freshworks.beans.BaseBean;
import org.freshworks.beans.box.Usage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Iterator;

import static java.lang.Class.forName;

public class Main {

    public static void main(String args[]) {
        ObjectMapper objectMapper = new ObjectMapper();

        String s = "{\"source\": {\"type\": \"user\",\"id\": \"20341833468\",\"name\": \"Mohankumar M\",\"login\": \"mohan.fwtest@gmail.com\"},\"created_by\": {\"type\": \"user\",\"id\": \"20341833468\",\"name\": \"Mohankumar M\",\"login\": \"mohan.fwtest@gmail.com\"},\"action_by\": null,\"created_at\": \"2022-08-24T05:27:29-07:00\",\"event_type\": \"ADD_LOGIN_ACTIVITY_DEVICE\",\"ip_address\": \"163.116.195.118\",\"session_id\": null,\"@class\":\"org.freshworks.beans.box.Usage\" ,\"additional_details\": null}";

        try{
            Usage usage = objectMapper.readValue(s, Usage.class);
            System.out.println(usage.getCreated_at());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
