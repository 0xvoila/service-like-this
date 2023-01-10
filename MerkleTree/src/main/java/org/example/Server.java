package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Any;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.checkerframework.checker.units.qual.A;
import org.example.Record;

import java.util.ArrayList;
import java.util.HashMap;

public class Server extends AbstractVerticle {

    static HashMap<String, Integer> backendServers = new HashMap<>();

    public static void main(String args[]) {

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Server());
        backendServers.put("a",7000);
        backendServers.put("b",7001);
    }

    @Override
    public void start() throws Exception {
        // Create a Router
        Router router = Router.router(vertx);
        Database database = new Database();

        // Mount the handler for all incoming requests at every path and HTTP method
        router.route().handler(context -> {
            // Get the address of the request
            String address = context.request().connection().remoteAddress().toString();
            // Get the query parameter "name"
            MultiMap queryParams = context.queryParams();
            String key = queryParams.contains("key") ? queryParams.get("key") : "0";
            String value = queryParams.contains("value") ? queryParams.get("value") : "unknown";
            String server = queryParams.contains("server") ? queryParams.get("server") : "a";

            if ( !key.equals("0")){
                ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress("127.0.0.1", backendServers.get(server)).usePlaintext();
                Channel channel = channelBuilder.build();
                org.example.DatabaseGrpcServiceGrpc.DatabaseGrpcServiceBlockingStub blockingStub = org.example.DatabaseGrpcServiceGrpc.newBlockingStub(channel);
                HashMap<String, Object> data = new HashMap<>();
                data.put("key", key);
                data.put("value", value);
                data.put("timestamp", System.currentTimeMillis());
                ObjectMapper objectMapper = new ObjectMapper();
                Record req = null;
                try {
                    req = Record.newBuilder().setKey(Integer.parseInt(key)).setValue(objectMapper.writeValueAsString(data)).build();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                blockingStub.createRecord(req);
            }

            context.json(
                    new JsonObject()
                            .put("key", key)
                            .put("value", value)

            );

        });

        // Create the HTTP server
        vertx.createHttpServer()
                // Handle every request using the router
                .requestHandler(router)
                // Start listening
                .listen(8888)
                // Print the port
                .onSuccess(server ->
                        System.out.println(
                                "HTTP server started on port " + server.actualPort()
                        )
                );
    }
}
