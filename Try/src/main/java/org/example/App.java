package org.example.dev;


import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

import java.util.Optional;

/**
 * Hello world!
 *
 */
public class App 
{
    public static Injector injector;
    
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        injector = Guice.createInjector(new AppBinding());
//        Vertx vertx = Vertx.vertx();
//        HttpServer server = vertx.createHttpServer();
//        server.requestHandler(App::router);
//        vertx.deployVerticle("ArticleVerticle");
//        server.listen(9090);
    }

     public static void router(HttpServerRequest request){

        Optional<String> param = Optional.ofNullable(request.getParam("name"));
        if (param.isPresent() && param.get().equals("index")){
            ArticleController articleController = injector.getInstance(ArticleController.class);
            System.out.println(articleController);
            articleController.index();
            request.response().end("INDEX");
        }
        else {
            ArticleController articleController = injector.getInstance(ArticleController.class);
            articleController.create();
            request.response().end("NON INDEX");
        }
    }
}
