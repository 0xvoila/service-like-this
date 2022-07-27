package org.system.amit.lamport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;

public class IMutation implements Serializable {

    String command;
    String key;

    int timestamp;

    public IMutation(String command, String key, int timestamp){
        this.command = command;
        this.key = key;
        this.timestamp = timestamp;
    }

    public IMutation(){

    }

    public String getKey(){
        return this.key;
    }

    public String getCommand(){
        return this.command;
    }

    public int getTimestamp(){
        return  this.timestamp;
    }

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

//      first write the command
        int totalBytes = 4 + this.command.getBytes().length + 4 + this.key.getBytes().length + 4 +  4 ;

        outputStream.write(totalBytes);
        outputStream.write(this.command.length());
        outputStream.write(this.command.getBytes());
        outputStream.write(this.key.length());
        outputStream.write(this.key.getBytes());
        outputStream.write(4);
        outputStream.write(this.timestamp);


        return outputStream.toByteArray();
    }

    public Mutation fromByteArray(byte[] byteArray) throws IOException {

        Mutation mutation = new Mutation();
        int index = 0;

        byte[] commandLengthArray = new byte[4];
        System.arraycopy(byteArray,index,  commandLengthArray, 0, 4 );
        int commandLength = new BigInteger(commandLengthArray).intValue();
        index = index + 4;

        byte[] commandArray = new byte[commandLength];
        System.arraycopy(byteArray, index,  commandArray, 0, commandLength);
        String command = new String(commandArray);
        mutation.command = command;
        index = index + commandLength;

        byte[] keyLengthArray = new byte[4];
        System.arraycopy(byteArray, index,  keyLengthArray, 0, 4);
        int keyLength = new BigInteger(keyLengthArray).intValue();
        index = index + 4;


        byte[] keyArray = new byte[keyLength];
        System.arraycopy(byteArray, index,  keyArray, 0, keyLength);
        String key = new String(commandArray);
        mutation.key = key;
        index = index + keyLength;

        byte[] valueLengthArray = new byte[4];
        System.arraycopy(byteArray,index,  valueLengthArray, 0, 4 );
        int valueLength = new BigInteger(valueLengthArray).intValue();
        index = index + 4;

        byte[] valueArray = new byte[valueLength];
        System.arraycopy(byteArray, index,  valueArray, 0, valueLength);
        String value = new String(commandArray);
        mutation.value = value;
        index = index + valueLength;

        byte[] timestampLengthArray = new byte[4];
        System.arraycopy(byteArray, index,  timestampLengthArray, 0, 4 );
        int timestampLength = new BigInteger(timestampLengthArray).intValue();
        index = index + 4;

        byte[] timestampArray = new byte[timestampLength];
        System.arraycopy(byteArray, index,  timestampArray, 0, valueLength);
        int timestamp = new BigInteger(timestampArray).intValue();
        mutation.timestamp = timestamp;
        index = index + 4;

        return mutation;
    }

}
