package com.hkafeidou.my_tomcat_simple.response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

public class ResponseFacade implements ServletResponse {

    private ServletResponse response = null;
    
    
    public ResponseFacade(ServletResponse response) {
        super();
        this.response = response;
    }

    @Override
    public String getCharacterEncoding() {
        // TODO Auto-generated method stub
        return response.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        // TODO Auto-generated method stub
        return response.getContentType();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // TODO Auto-generated method stub
        return response.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        // TODO Auto-generated method stub
        return response.getWriter();
    }

    @Override
    public void setCharacterEncoding(String charset) {
        // TODO Auto-generated method stub
        response.getCharacterEncoding();
    }

    @Override
    public void setContentLength(int len) {
        // TODO Auto-generated method stub
        response.setContentLength(len);
    }

    @Override
    public void setContentLengthLong(long len) {
        // TODO Auto-generated method stub
        response.setContentLengthLong(len);
    }

    @Override
    public void setContentType(String type) {
        // TODO Auto-generated method stub
        response.setContentType(type);
    }

    @Override
    public void setBufferSize(int size) {
        // TODO Auto-generated method stub
        response.setBufferSize(size);
    }

    @Override
    public int getBufferSize() {
        // TODO Auto-generated method stub
        return response.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
        // TODO Auto-generated method stub
        response.flushBuffer();
    }

    @Override
    public void resetBuffer() {
        // TODO Auto-generated method stub
        response.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        // TODO Auto-generated method stub
        return response.isCommitted();
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        response.reset();
    }

    @Override
    public void setLocale(Locale loc) {
        // TODO Auto-generated method stub
        response.setLocale(loc);
    }

    @Override
    public Locale getLocale() {
        // TODO Auto-generated method stub
        return response.getLocale();
    }
    
}
