package org.example;

public class GenericType<T> {

    T elem;

    public GenericType(T e){
        this.elem = e;
    }
    public static void main(String args[]){

        GenericType<String> genericType = new GenericType<String>("amit");
        GenericType<Integer> genericType1 = new GenericType<Integer>(1);
        System.out.println(genericType.getElem());
        System.out.println(genericType1.getElem());
    }

    public T getElem(){
        return this.elem;
    }
}
