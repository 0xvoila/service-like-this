package org.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.ibm.etcd.api.RangeResponse;
import com.ibm.etcd.client.kv.KvClient;
import org.downloader.contants.Constants;
import org.downloader.models.RequestResponse;

public class EtcdClient {

    static ObjectMapper mapper = new ObjectMapper();
    static KvClient kvClient = com.ibm.etcd.client.EtcdClient.forEndpoint(Constants.ETC_HOST, Constants.ETC_PORT).withPlainText().build().getKvClient();

    public static RangeResponse getKey(String key) {
        try{
            RangeResponse response = kvClient.get(ByteString.copyFromUtf8((key))).sync();
            return response;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
