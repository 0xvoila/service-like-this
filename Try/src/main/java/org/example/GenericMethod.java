package org.example.dev;

public class GenericMethod {

    public <T1 extends Number, T2 extends  Number> int compare( T1 object1, T2 object2)
    {
        return 1;
    }

    public static void main(String args[]){

        GenericMethod genericMethod = new GenericMethod();
        System.out.println(genericMethod.compare(1,2)); // It will work
//        genericMethod.compare(1, "amit");  // Because we have upper bound on what could be the type at most
    }
}
