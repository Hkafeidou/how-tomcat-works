package com.hkafeidou.my_tomcat_simple.processor;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public interface IProcessor {
    void process(ServletRequest request,ServletResponse response);
}
