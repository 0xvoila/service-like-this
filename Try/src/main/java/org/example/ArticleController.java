package org.example.dev;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ArticleController {

    public void ArticleController(){

    }

    @Inject
    ArticleVerticle articleVerticle;
    public void index(){
        articleVerticle.index();
    }


    public void create(){
        articleVerticle.create();
    }
}
