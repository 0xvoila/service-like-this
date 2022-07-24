package org.system.amit.echo;

public class FileMessage {

    private String fileCommand;
    private String clientName;

    public FileMessage(String fileCommand, String clientName){
        this.fileCommand = fileCommand;
        this.clientName = clientName;
    }

    public String getClientName(){
        return clientName;
    }

    public String getFileCommand(){
        return fileCommand;
    }
}
