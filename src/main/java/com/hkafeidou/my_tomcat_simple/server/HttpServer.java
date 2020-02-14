package com.hkafeidou.my_tomcat_simple.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.hkafeidou.my_tomcat_infrastructure.infrstructure.my_const.HttpServerConst;
import com.hkafeidou.my_tomcat_simple.request.Request;
import com.hkafeidou.my_tomcat_simple.response.Response;

public class HttpServer {
    
    private boolean shutdown = false;
    
    public void await() {
        ServerSocket serverSocket = null;
        
        try {
            serverSocket = new ServerSocket(HttpServerConst.DEFAULT_PORT, 5, InetAddress.getByName(HttpServerConst.DEFAULT_SERVER_ADDR));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }
        
        while(!shutdown) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            
            try {
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();
                
                Request request = new Request(input);
                request.parse();
                
                Response response = new Response(output);
                response.setRequest(request);
                response.sendStaticResource();
                socket.close();
                
                shutdown=request.getUri().equals(HttpServerConst.DEFAULT_SHUTDOWN_COMMAND);
                
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                continue;
            }
            
        }
        
    }
    

}
