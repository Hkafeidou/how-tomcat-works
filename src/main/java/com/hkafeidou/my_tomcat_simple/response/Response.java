package com.hkafeidou.my_tomcat_simple.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.hkafeidou.my_tomcat_infrastructure.infrstructure.my_const.HttpServerConst;
import com.hkafeidou.my_tomcat_simple.request.HttpRequest;
import com.hkafeidou.my_tomcat_simple.request.Request;

public class Response implements ServletResponse {
    //Request request;
    HttpRequest request;
    OutputStream output = null;
    PrintWriter writer = null;
    
    public Response(OutputStream output) {
        super();
        this.output = output;
    }
    
    /**
     * @param request the request to set
     */
    public void setRequest(ServletRequest request) {
        this.request = (HttpRequest)request;
    }
    
    
    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[HttpServerConst.DEFAULT_BUFF_SIZE];
        FileInputStream fileStream = null;
        try {
            File file = new File(HttpServerConst.DEFAULT_WEB_ROOT,request.getRequestURI());
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

    @Override
    public String getCharacterEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getContentType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * return the wirter
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        // TODO Auto-generated method stub
        if(null == writer) {
            writer = new PrintWriter(output, true);
        }
        
        return writer;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setContentLength(int len) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setContentLengthLong(long len) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setContentType(String type) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBufferSize(int size) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getBufferSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void resetBuffer() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isCommitted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setLocale(Locale loc) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }
   
    
    
}
