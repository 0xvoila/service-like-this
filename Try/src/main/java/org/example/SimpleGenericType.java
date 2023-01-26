package org.example.dev;

public class SimpleGenericType<T> {

    T elem;

    public SimpleGenericType(T e){
        this.elem = e;
    }
    public static void main(String args[]){

        SimpleGenericType<String> simpleGenericType = new SimpleGenericType<String>("amit");
        SimpleGenericType<Integer> simpleGenericType1 = new SimpleGenericType<Integer>(1);
        System.out.println(simpleGenericType.getElem());
        System.out.println(simpleGenericType1.getElem());
    }

    public T getElem(){
        return this.elem;
    }

}
