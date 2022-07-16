package org.freshworks.MasterSlaveFileReplication;

import com.sun.source.tree.Tree;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.CRC32;

public class MasterSlaveLSM extends HttpServlet {

    TreeMap<Integer, String> lsm = null;
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        int id = Integer.parseInt(req.getParameter("record_id"));
        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");
        String record = id + "," + firstName + "," + lastName;
        CRC32 crc32 = new CRC32();
        crc32.update(record.getBytes(StandardCharsets.UTF_8));
        long crc32Value = crc32.getValue();
        record = record + "," + crc32Value + "\n";

        lock.writeLock().lock();

        if (this.lsm == null){
            this.lsm = new TreeMap<Integer, String>();
        }
        this.lsm.put(id, record);
        if (this.lsm.size() == 10000){
            flushLSM(this.lsm);
            this.lsm = null;
        }
        lock.writeLock().unlock();
    }

    public void flushLSM(TreeMap<Integer, String> lsm) throws IOException {

        long epoch = System.currentTimeMillis();
        String epochFileName = epoch + ".txt";
        FileOutputStream masterDb = new FileOutputStream(epochFileName);

        for(Map.Entry<Integer, String> entry: lsm.entrySet()){
            String record = entry.getKey() + "," + entry.getValue();
            masterDb.write(record.getBytes(StandardCharsets.UTF_8));
        }

        masterDb.flush();
        masterDb.close();
    }
}
