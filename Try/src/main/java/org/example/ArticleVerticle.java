package org.example.dev;

import io.vertx.core.AbstractVerticle;

public interface ArticleVerticle {

    public void start();

    public void stop();

    public void index();

    public void create();
}
