package com.hkafeidou.my_tomcat_simple.request;

import java.io.IOException;
import java.io.InputStream;

import com.hkafeidou.my_tomcat_infrastructure.infrstructure.my_const.HttpServerConst;

public class Request {
    private InputStream input ;
    private String uri;
    public Request(InputStream input) {
        super();
        this.input = input;
    }
    
    public void parse() {
        StringBuffer request=new StringBuffer(HttpServerConst.DEFAULT_BUFF_SIZE);
        int index=0;
        byte[] buffer = new byte[HttpServerConst.DEFAULT_BUFF_SIZE];
        
        try {
            index = input.read(buffer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            index=-1;
        }
        
        for(int j=0;j<index;j++) {
            request.append((char)buffer[j]);
        }
        System.out.println("the request is :" + request.toString());
        uri = parseUri(request.toString());
    }
    
    /**
     * url eg: GET / HTTP/1.1\r\n
     * @param requestString
     * @return
     */
    private String parseUri(String requestString) {
        int index1,index2;
        index1= requestString.indexOf(' ');
        if(index1!=-1) {
            index2 = requestString.indexOf(' ',index1+1);
            if(index2>index1) {
                return requestString.substring(index1+1,index2);
            }
        }
        return null;
    }
    
    public String getUri() {
        return this.uri;
    }
    
    
    
}
