package com.hkafeidou.my_tomcat_simple.processor;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.hkafeidou.my_tomcat_simple.response.HttpResponse;

public class StaticResourceProcessor implements IProcessor {

    @Override
    public void process(ServletRequest request, ServletResponse response) {
        // TODO Auto-generated method stub
        try {
            ((HttpResponse) response).sendStaticResource();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
