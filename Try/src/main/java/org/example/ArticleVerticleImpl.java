package org.example.dev;

import io.vertx.core.AbstractVerticle;

public class ArticleVerticleImpl extends AbstractVerticle implements ArticleVerticle{

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
