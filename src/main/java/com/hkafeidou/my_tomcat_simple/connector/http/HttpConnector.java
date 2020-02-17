package com.hkafeidou.my_tomcat_simple.connector.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.hkafeidou.my_tomcat_infrastructure.infrstructure.my_const.HttpServerConst;
import com.hkafeidou.my_tomcat_simple.processor.HttpProcessor;

public class HttpConnector implements Runnable {

    boolean stoped = false;
    
    private String scheme="http";
    public String getScheme() {
        return scheme;
    }
    
    @Override
    public void run() {
        // TODO Auto-generated method stub
        ServerSocket serverSocket = null;
        int port = HttpServerConst.DEFAULT_PORT;
        
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName(HttpServerConst.DEFAULT_SERVER_ADDR));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }
        while(!stoped) {
            Socket socket = null;
            
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                continue;
            }
            
            HttpProcessor processor = new HttpProcessor(this);
            processor.process(socket);
            
        }
        
    }
    
    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }
    
}
