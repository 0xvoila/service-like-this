package org.example;

import io.vertx.core.AbstractVerticle;

public class ArticleVerticle extends AbstractVerticle {

    public void start(){

    }

    public void stop(){

    }

    public void index(){
        System.out.println("This is from index");
    }

    public void create(){
        System.out.println("This is created");
    }
}
