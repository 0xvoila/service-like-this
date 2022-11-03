package org.transformer.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<T> {

    static ObjectMapper mapper = new ObjectMapper();
    private Class<T> targetClass;

    public JsonDeserializer(Class<T> x){
        this.targetClass = x;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public T deserialize(String s, byte[] bytes) {

        try {
            if (bytes.length < 2){
                return null;
            }
            return mapper.readValue(bytes, this.targetClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
