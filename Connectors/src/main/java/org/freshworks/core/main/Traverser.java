package org.freshworks.core.main;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scalified.tree.TreeNode;
import org.freshworks.Constant;
import org.freshworks.Infra;
import org.freshworks.beans.BaseBean;
import org.freshworks.core.model.DiscoveryObject;
import org.freshworks.core.model.RequestResponse;
import org.freshworks.postman.BasePostman;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Iterator;

public class Traverser {

    static HashMap<String, BasePostman> singletonObjects = new HashMap<>();

    public static void traverse(TreeNode<String> node, HashMap<String, String> syncConfig) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, URISyntaxException, IOException, InstantiationException {

        ObjectMapper objectMapper = new ObjectMapper();

        if(node.parent() == null) {
            process(node, null, syncConfig);
        }
        else{
            BasePostman parentTraverseObject = null;

            if(singletonObjects.get(node.parent().data()) != null){
                parentTraverseObject = singletonObjects.get(node.parent().data());
            }
            else{
                Class<?> parentCl = Class.forName(node.parent().data());
                parentTraverseObject = (BasePostman) parentCl.getConstructor().newInstance();
                singletonObjects.put(node.parent().data(), parentTraverseObject);
            }

            Iterator<String> it = parentTraverseObject.getResult().iterator();
            while (it.hasNext()){
                process(node, objectMapper.readTree(it.next()), syncConfig);
            }

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

    public static HashMap<String, String> getBeanAndAssetByPostManClass(Class<?> postManClass, HashMap<String, String> syncConfig){

        String postmanClassName = postManClass.getName().substring(postManClass.getName().lastIndexOf('.') + 1);

        HashMap<String, String> data = new HashMap<>();
        data.put("postman", Constant.POSTMAN_PATH + syncConfig.get("service") + "." + postmanClassName);
        data.put("bean", Constant.BEAN_PATH + syncConfig.get("service") + "." + postmanClassName);

        return data;
    }

    private static void process(TreeNode<String> node, JsonNode parentNodeData, HashMap<String, String> syncConfig){

        try{
            Class<?> cl = Class.forName(node.data());
            ObjectMapper objectMapper = new ObjectMapper();
            RequestResponse requestResponse = new RequestResponse();

            BasePostman basePostman = null;
            if(singletonObjects.get(node.data()) != null){
                basePostman = singletonObjects.get(node.data());
            }
            else{
                basePostman = (BasePostman) cl.getConstructor().newInstance();
                singletonObjects.put(node.data(), basePostman);
            }

            while (Boolean.FALSE.equals(basePostman.isComplete(requestResponse))) {
                requestResponse = basePostman.getNextUrl(requestResponse, parentNodeData);
                getObject(requestResponse);
                JsonNode jNodeList = objectMapper.readTree(requestResponse.getResponse().body());

                Iterator<JsonNode> iterator = jNodeList.iterator();
                while (iterator.hasNext()) {

                    JsonNode jNode = iterator.next();
                    ObjectNode o = (ObjectNode) jNode;
                    HashMap<String, String> nodeMetaData = getBeanAndAssetByPostManClass(cl, syncConfig);
                    o.put("type", nodeMetaData.get("bean"));
                    BaseBean baseBean = objectMapper.readValue(o.toString(), BaseBean.class);

                    if (baseBean.filter()) {
                        baseBean.transform();
                        baseBean.setParentNode(parentNodeData);
                        DiscoveryObject discoveryObject = new DiscoveryObject(nodeMetaData.get("bean"), baseBean);

                        // Here push this to queue for processing by Processor
                        Infra.kafka.add(discoveryObject);

                        // Here save this as well so that it can be used to process its child
                        String s = objectMapper.writeValueAsString(baseBean);
                        basePostman.saveResult(s);
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
