package com.hkafeidou.my_tomcat_simple.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import com.hkafeidou.my_tomcat_infrastructure.infrstructure.my_const.HttpServerConst;
import com.hkafeidou.my_tomcat_simple.request.Request;

public class Response {
    Request request;
    OutputStream output = null;
    public Response(OutputStream output) {
        super();
        this.output = output;
    }
    
    /**
     * @param request the request to set
     */
    public void setRequest(Request request) {
        this.request = request;
    }
    
    
    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[HttpServerConst.DEFAULT_BUFF_SIZE];
        FileInputStream fileStream = null;
        try {
            File file = new File(HttpServerConst.DEFAULT_WEB_ROOT,request.getUri());
            if(file.exists()) {
                fileStream = new FileInputStream(file);
                int ch = fileStream.read(bytes,0,HttpServerConst.DEFAULT_BUFF_SIZE);
                while(ch!=-1) {
                    output.write(bytes,0,ch);
                    ch = fileStream.read(bytes,0,HttpServerConst.DEFAULT_BUFF_SIZE);
                }
            }else {
                String errorMessage = "HTTP/1.1 404 File not Fount \r\n"
                                        +"Content-Type:text/html\r\n"
                                        +"Content-Length:23\r\n"
                                        +"\r\n"
                                        +"<h1>File Not Fount</h1>";
                output.write(errorMessage.getBytes());
                                        
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println(e.toString());
            e.printStackTrace();
            
        }finally {
            if(fileStream!=null) {
                fileStream.close();
            }
        }
    }
    
}
