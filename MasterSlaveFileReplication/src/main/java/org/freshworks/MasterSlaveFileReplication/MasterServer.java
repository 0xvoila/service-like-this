package org.freshworks.MasterSlaveFileReplication;


import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

public class MasterServer extends HttpServlet implements Runnable{

    FileOutputStream masterDb = null;
    FileOutputStream replicatedDb = null;
    static long dbRecordCount = 0;
    static long dbReplicaRecordCount = 0;

    public  MasterServer() throws FileNotFoundException {

        System.out.println("Calling from constructor");
        masterDb = new FileOutputStream("./master_db.txt");
        replicatedDb = new FileOutputStream("./replicated_db.txt");
    }

    public void init(ServletConfig servletConfig) throws ServletException{
        super.init(servletConfig);
        //        Here we will start the new thread to read from masterDB and send it to slave files
        Thread replicationThread = new Thread(this);
        replicationThread.start();
    }

    public void run(){

        System.out.println("Calling from replication method");
        try
        {
            File file=new File("./master_db.txt");    //creates a new file instance
            FileReader fr=new FileReader(file);   //reads the file
            BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
            StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters
            String line;
            while(true){
                while((line=br.readLine())!=null)
                {
                    replicatedDb.write(line.getBytes(StandardCharsets.UTF_8));
                    replicatedDb.write(10);
                    dbReplicaRecordCount = dbReplicaRecordCount + 1;

                    //        Force flush data to the disk after 10000 records are inserted
                    if (dbReplicaRecordCount % 10000 == 0){
                        replicatedDb.flush();
                    }

                }
                Thread.sleep(1000);
            }

        }
        catch(IOException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        String id = req.getParameter("record_id");
        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        String record = id + "," + firstName + "," + lastName + "\n";
        CRC32 crc32 = new CRC32();
        crc32.update(record.getBytes(StandardCharsets.UTF_8));
        long crc32Value = crc32.getValue();
        record = record + "," + crc32Value;

        masterDb.write(record.getBytes(StandardCharsets.UTF_8));
        dbRecordCount = dbRecordCount + 1;

//        Force flush data to the disk after 10000 records are inserted
        if (dbRecordCount % 10000 == 0){
            masterDb.flush();
        }

    }


}
