package org.freshworks.core.main;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalified.tree.TreeNode;
import org.freshworks.connectors.BaseConnector;
import org.freshworks.core.model.RequestResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.List;

public class Traverser {

    public static void traverse(TreeNode<String> node) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, URISyntaxException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        Class<?> cl = Class.forName(node.data());
        Method getNextMethod = null;

        if(node.parent() != null){
            getNextMethod = cl.getDeclaredMethod("getNextRequest", RequestResponse.class, Class.forName(node.parent().data()));
        }
        else{
            getNextMethod = cl.getDeclaredMethod("getNextRequest", RequestResponse.class);
        }

        Method isCompleteMethod = cl.getDeclaredMethod("isComplete", RequestResponse.class);
        RequestResponse requestResponse = new RequestResponse();
        requestResponse.setConnectorName(node.data());

        while(Boolean.FALSE.equals((Boolean)isCompleteMethod.invoke(null, requestResponse))){
            if(node.parent() != null){
                requestResponse = (RequestResponse) getNextMethod.invoke(null, requestResponse, Class.forName(node.parent().data()));
            }
            else{
                requestResponse = (RequestResponse) getNextMethod.invoke(null, requestResponse);
            }

            requestResponse.setConnectorName(node.data());
            requestResponse = getObject(requestResponse);

            List<? extends BaseConnector> listOfObjects = objectMapper.readValue(requestResponse.getResponse().body(), objectMapper.getTypeFactory().constructType(List.class, cl));
            listOfObjects.stream().forEach(s -> System.out.println(s));
        }

    }

    public static RequestResponse getObject(RequestResponse requestResponse) {
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> httpResponse = client.send(requestResponse.getRequest(), HttpResponse.BodyHandlers.ofString());
            requestResponse.setResponse(httpResponse);
            return requestResponse;
        }
        catch (Exception e){
            e.printStackTrace();
            return requestResponse;
        }
    }
}
