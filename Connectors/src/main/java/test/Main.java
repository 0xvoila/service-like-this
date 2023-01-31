package test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.freshworks.beans.BaseBean;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Iterator;

import static java.lang.Class.forName;

public class Main {

    public static void main(String args[]){
        ObjectMapper objectMapper = new ObjectMapper();

        try{
            HttpRequest request = HttpRequest.newBuilder(new URI("http://localhost:4000/apps")).GET().build();
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode jNode = objectMapper.readTree(response.body());
            ObjectNode objectNode = objectMapper.createObjectNode();
//            objectNode.put("BaseBean", jNode);
//            System.out.println(objectNode.toString());

            Iterator<JsonNode> it = jNode.iterator();
            while(it.hasNext()){
                JsonNode j = it.next();
                ObjectNode o = (ObjectNode) j;
                o.put("type", "org.freshworks.connectors.box.Application");
                BaseBean bcs = objectMapper.readValue(o.toString(), BaseBean.class);

//                User user = new User();
//                user.setApplication(bcs);
            }



        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
