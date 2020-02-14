package com.hkafeidou.my_tomcat_simple.processor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.hkafeidou.my_tomcat_infrastructure.infrstructure.my_const.HttpServerConst;
import com.hkafeidou.my_tomcat_simple.request.Request;
import com.hkafeidou.my_tomcat_simple.request.RequestFacade;
import com.hkafeidou.my_tomcat_simple.response.Response;
import com.hkafeidou.my_tomcat_simple.response.ResponseFacade;

public class ServletProcessor implements IProcessor {

    @Override
    public void process(ServletRequest request, ServletResponse response) {
        // TODO Auto-generated method stub
        Request req = (Request)request;
        Response resp = (Response)response;
        
        String uri = req.getUri();//形如：/servlet/myservletName
        String servletName = uri.substring(uri.lastIndexOf("/")+1);
        URLClassLoader loader = null;
        //load class loader
        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(HttpServerConst.DEFAULT_WEB_ROOT);
            String repository = (new URL("file",null,classPath.getCanonicalPath()+File.separator)).toString();
            urls[0] = new URL(null,repository,streamHandler);
            loader = new URLClassLoader(urls);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //load servlet class
        Class myClass = null;
        try {
            myClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Servlet servlet = null;
        RequestFacade requestFacade = new RequestFacade(req);
        ResponseFacade responseFacade = new ResponseFacade(resp);
        try {
            servlet = (Servlet)myClass.getDeclaredConstructor().newInstance();
            servlet.service(requestFacade, responseFacade);
        } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }

}
