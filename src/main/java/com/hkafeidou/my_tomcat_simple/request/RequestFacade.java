package com.hkafeidou.my_tomcat_simple.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.hkafeidou.my_tomcat_simple.response.Response;

public class RequestFacade implements ServletRequest {

    private ServletRequest request = null;
    
    public RequestFacade(ServletRequest _request) {
        this.request=_request;
    }
    
    
    
    
    @Override
    public Object getAttribute(String name) {
        // TODO Auto-generated method stub
        return request.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        // TODO Auto-generated method stub
        return request.getAttributeNames();
    }

    @Override
    public String getCharacterEncoding() {
        // TODO Auto-generated method stub
        return request.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String env)
            throws UnsupportedEncodingException {
        // TODO Auto-generated method stub
        request.setCharacterEncoding(env);
    }

    @Override
    public int getContentLength() {
        // TODO Auto-generated method stub
        return request.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
        // TODO Auto-generated method stub
        return request.getContentLengthLong();
    }

    @Override
    public String getContentType() {
        // TODO Auto-generated method stub
        return request.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // TODO Auto-generated method stub
        return request.getInputStream();
    }

    @Override
    public String getParameter(String name) {
        // TODO Auto-generated method stub
        return request.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        // TODO Auto-generated method stub
        return request.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        // TODO Auto-generated method stub
        return request.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        // TODO Auto-generated method stub
        return request.getParameterMap();
    }

    @Override
    public String getProtocol() {
        // TODO Auto-generated method stub
        return request.getProtocol();
    }

    @Override
    public String getScheme() {
        // TODO Auto-generated method stub
        return request.getScheme();
    }

    @Override
    public String getServerName() {
        // TODO Auto-generated method stub
        return request.getServerName();
    }

    @Override
    public int getServerPort() {
        // TODO Auto-generated method stub
        return request.getServerPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        // TODO Auto-generated method stub
        return request.getReader();
    }

    @Override
    public String getRemoteAddr() {
        // TODO Auto-generated method stub
        return request.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        // TODO Auto-generated method stub
        return request.getRemoteHost();
    }

    @Override
    public void setAttribute(String name, Object o) {
        // TODO Auto-generated method stub
        request.setAttribute(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        // TODO Auto-generated method stub
        request.removeAttribute(name);
    }

    @Override
    public Locale getLocale() {
        // TODO Auto-generated method stub
        return request.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        // TODO Auto-generated method stub
        return request.getLocales();
    }

    @Override
    public boolean isSecure() {
        // TODO Auto-generated method stub
        return request.isSecure();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        // TODO Auto-generated method stub
        return request.getRequestDispatcher(path);
    }

    @Override
    public String getRealPath(String path) {
        // TODO Auto-generated method stub
        return request.getRealPath(path);
    }

    @Override
    public int getRemotePort() {
        // TODO Auto-generated method stub
        return request.getRemotePort();
    }

    @Override
    public String getLocalName() {
        // TODO Auto-generated method stub
        return request.getLocalName();
    }

    @Override
    public String getLocalAddr() {
        // TODO Auto-generated method stub
        return request.getLocalAddr();
    }

    @Override
    public int getLocalPort() {
        // TODO Auto-generated method stub
        return request.getLocalPort();
    }

    @Override
    public ServletContext getServletContext() {
        // TODO Auto-generated method stub
        return request.getServletContext();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        // TODO Auto-generated method stub
        return request.startAsync();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest,
            ServletResponse servletResponse) throws IllegalStateException {
        // TODO Auto-generated method stub
        return request.startAsync(servletRequest, servletResponse);
    }

    @Override
    public boolean isAsyncStarted() {
        // TODO Auto-generated method stub
        return request.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported() {
        // TODO Auto-generated method stub
        return request.isAsyncSupported();
    }

    @Override
    public AsyncContext getAsyncContext() {
        // TODO Auto-generated method stub
        return request.getAsyncContext();
    }

    @Override
    public DispatcherType getDispatcherType() {
        // TODO Auto-generated method stub
        return request.getDispatcherType();
    }
    
}
