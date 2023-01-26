package org.example.dev;

import com.google.inject.AbstractModule;

public class AppBinding extends AbstractModule {

    @Override
    protected void configure(){
        bind(ArticleVerticle.class).to(ArticleVerticleImpl.class);
    }
}
