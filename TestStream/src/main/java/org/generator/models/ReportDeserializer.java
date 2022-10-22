package org.generator.models;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class ReportDeserializer extends StdDeserializer<ArrayList<HashMap<String, Integer>>> {

    public ReportDeserializer(){
        this(null);

    }
    protected ReportDeserializer(Class<?> vc) {
        super(vc);
    }


    @Override
    public ArrayList<HashMap<String, Integer>> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        for (JsonNode n:
                node) {
            System.out.println( "Amit " + n.get("200").asText());
        }
        return null;
    }
}
