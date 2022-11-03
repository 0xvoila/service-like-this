package org.transformer;

import org.transformer.models.RequestResponse;

import java.util.ArrayList;

public interface TransformerInterface {

    public ArrayList<? extends Object> transform(RequestResponse saaSObject);


}
