package org.example;

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

public class Server extends AbstractVerticle {

    static ArrayList<Integer> backendServers = new ArrayList<>();

    public static void main(String args[]) {

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Server());
        backendServers.add(6000);
        backendServers.add(6001);
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

            int rnd = (int)(Math.random()*backendServers.size());
            ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress("127.0.0.1", backendServers.get(rnd)).usePlaintext();
            Channel channel = channelBuilder.build();
            org.example.DatabaseGrpcServiceGrpc.DatabaseGrpcServiceBlockingStub blockingStub = org.example.DatabaseGrpcServiceGrpc.newBlockingStub(channel);
            Record req = Record.newBuilder().setKey(Integer.parseInt(key)).setValue(value).build();
            blockingStub.createRecord(req);
//            database.insert(Integer.parseInt(key), value);

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
