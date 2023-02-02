package org.freshworks.core.main;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scalified.tree.TreeNode;
import org.freshworks.core.constants.Constants;
import org.freshworks.core.infra.Infra;
import org.freshworks.beans.BaseBean;
import org.freshworks.core.model.DiscoveryObject;
import org.freshworks.core.model.RequestResponse;
import org.freshworks.core.utils.Utility;
import org.freshworks.steps.BaseStep;
import org.freshworks.steps.StepInterface;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Iterator;

public class Traverser {

    static HashMap<String, StepInterface> singletonObjects = new HashMap<>();

    public static void traverse(TreeNode<String> node) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, URISyntaxException, IOException, InstantiationException {

        ObjectMapper objectMapper = new ObjectMapper();

        if(node.parent() == null && node.data().equals(BaseStep.class.getName())) {
        }
        else{
            StepInterface parentTraverseObject = null;

            if(singletonObjects.get(node.parent().data()) != null){
                parentTraverseObject = singletonObjects.get(node.parent().data());
            }
            else{
                Class<?> parentCl = Class.forName(node.parent().data());
                parentTraverseObject = (StepInterface) parentCl.getConstructor().newInstance();
                singletonObjects.put(node.parent().data(), parentTraverseObject);
            }

            Iterator<String> it = parentTraverseObject.getResult().iterator();
            while (it.hasNext()){
                process(node, objectMapper.readTree(it.next()));
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


    private static void process(TreeNode<String> node, JsonNode parentNodeData){

        try{
            Class<?> cl = Class.forName(node.data());
            ObjectMapper objectMapper = new ObjectMapper();

            StepInterface stepInterface = null;
            if(singletonObjects.get(node.data()) != null){
                stepInterface = singletonObjects.get(node.data());
            }
            else{
                stepInterface = (StepInterface) cl.getConstructor().newInstance();
                singletonObjects.put(node.data(), stepInterface);
            }

            while (true) {
                RequestResponse requestResponse = stepInterface.start();
                getObject(requestResponse);
                JsonNode jNodeList = objectMapper.readTree(requestResponse.getResponse().body());
                jNodeList = stepInterface.parseResponse(jNodeList);
                Iterator<JsonNode> iterator = jNodeList.iterator();
                while (iterator.hasNext()) {

                    JsonNode jNode = iterator.next();
                    ObjectNode o = (ObjectNode) jNode;
                    HashMap<String, String> nodeMetaData = Utility.getMetaDataByClass(cl);
                    o.put(Constants.JsonTypeInfo_As_PROPERTY, nodeMetaData.get("bean"));
                    BaseBean baseBean = objectMapper.readValue(o.toString(), BaseBean.class);

                    if (Boolean.TRUE.equals(baseBean.filter())) {
                        baseBean.transform();
                        baseBean.setParentNode(parentNodeData);
                        DiscoveryObject discoveryObject = new DiscoveryObject(nodeMetaData.get("bean"), baseBean);

                        // Here push this to queue for processing by Processor
                        Infra.kafka.add(objectMapper.writeValueAsString(discoveryObject));

                        // Here save this as well so that it can be used to process its child
                        String s = objectMapper.writeValueAsString(baseBean);
                        stepInterface.saveResult(s);
                    }
                }

                if(Boolean.FALSE.equals(stepInterface.isComplete(requestResponse))){
                    requestResponse = stepInterface.getNextRequest(requestResponse, parentNodeData);
                }
                else{
                    break;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
