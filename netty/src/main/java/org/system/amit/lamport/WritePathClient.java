package org.system.amit.lamport;

public class WritePathClient {

    public void client(){

        try{
            while(true){
                Mutation x = Global.writeQueue.poll();
                if ( x == null){
                    continue;
                }

                if (x.getCommand().equals("DELETE")){
                    x.setTombstone(true);
                    MemtableManager.write(x.getKey(), x );
                }
                else{
                    MemtableManager.write(x.getKey(), x);
                }

            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        finally {
            System.out.println("Error in write path of the client");
        }
    }
}
