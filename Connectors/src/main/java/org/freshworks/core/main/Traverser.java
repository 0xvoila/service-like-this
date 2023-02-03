package org.freshworks.core.main;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Optional;
import com.scalified.tree.TreeNode;
import org.freshworks.core.constants.Constants;
import org.freshworks.core.infra.Infra;
import org.freshworks.beans.BaseBean;
import org.freshworks.core.model.DiscoveryObject;
import org.freshworks.core.model.RequestResponse;
import org.freshworks.core.utils.Utility;
import org.freshworks.steps.ParentStep;
import org.freshworks.steps.AbstractStep;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;

public class Traverser {

    static HashMap<String, AbstractStep> singletonObjects = new HashMap<>();

    public static void traverse(TreeNode<String> node) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, URISyntaxException, IOException, InstantiationException {

        ObjectMapper objectMapper = new ObjectMapper();

        if(node.parent() == null && node.data().equals(ParentStep.class.getName())) {
        }
        else{
            AbstractStep parentTraverseObject = null;

            if(singletonObjects.get(node.parent().data()) != null){
                parentTraverseObject = singletonObjects.get(node.parent().data());
            }
            else{
                Class<?> parentCl = Class.forName(node.parent().data());
                parentTraverseObject = (AbstractStep) parentCl.getConstructor().newInstance();
                singletonObjects.put(node.parent().data(), parentTraverseObject);
            }

//          In this method, we process the first time methods of the traverse like sync set up
            setupSync(node);

            Iterator<String> it = parentTraverseObject.getSyncResult().iterator();
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

    private static void setupSync(TreeNode<String> node){

        try{
            Class<?> cl = Class.forName(node.data());
            ObjectMapper objectMapper = new ObjectMapper();

            AbstractStep abstractStep = null;
            if(singletonObjects.get(node.data()) != null){
                abstractStep = singletonObjects.get(node.data());
            }
            else{
                abstractStep = (AbstractStep) cl.getConstructor().newInstance();
                singletonObjects.put(node.data(), abstractStep);
            }

            while(true){
                Optional<RequestResponse> requestResponseOptional =  abstractStep.setupSync();
                if(requestResponseOptional.isPresent()){
                    getObject(requestResponseOptional.get());
                    Optional<Boolean> opt = abstractStep.isSetupSyncComplete(requestResponseOptional.get());
                    checkArgument(opt.isPresent(), "isSetupSyncComplete should return boolean. It returns null");
                    if(Boolean.TRUE.equals(opt.get())){
                        break;
                    }
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

    private static void process(TreeNode<String> node, JsonNode parentNodeData){

        try{
            Class<?> cl = Class.forName(node.data());
            ObjectMapper objectMapper = new ObjectMapper();

            AbstractStep abstractStep = null;
            if(singletonObjects.get(node.data()) != null){
                abstractStep = singletonObjects.get(node.data());
            }
            else{
                abstractStep = (AbstractStep) cl.getConstructor().newInstance();
                singletonObjects.put(node.data(), abstractStep);
            }

            while (true) {
                Optional<RequestResponse> requestResponseOptional = abstractStep.startSync();
                checkArgument(requestResponseOptional.isPresent(), "start sync request can not be null");

                RequestResponse requestResponse = requestResponseOptional.get();
                getObject(requestResponse);
                checkArgument(!requestResponse.getResponse().body().equals(""), "Requested response is empty from third party");

                JsonNode jNodeList = objectMapper.readTree(requestResponse.getResponse().body());
                Optional<JsonNode> jsonNodeOptional = abstractStep.parseSyncResponse(jNodeList);
                checkArgument(jsonNodeOptional.isPresent(), "parse Sync response can not be null. It must be value JSON node");

                jNodeList = jsonNodeOptional.get();
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
                        abstractStep.saveSyncResult(s);
                    }
                }

                Optional<Boolean> opt = abstractStep.isSyncComplete(requestResponse);
                checkArgument(opt.isPresent(), "isSyncComplete should return boolean value. It returns null");
                if(Boolean.FALSE.equals(opt.get())){
                    requestResponseOptional = abstractStep.getNextSyncRequest(requestResponse, parentNodeData);
                    checkArgument(requestResponseOptional.isPresent(), "Get next sync request can not be null");
                    requestResponse = requestResponseOptional.get();
                }
                else{
                    break;
                }
            }

            abstractStep.closeSync();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}
