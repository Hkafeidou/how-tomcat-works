package com.hkafeidou.my_tomcat_simple;

import com.hkafeidou.my_tomcat_simple.business.SimpleBusiness;
import com.hkafeidou.my_infrastructure.business.IBusiness;

public class MyTomcatSimpleApplication {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println("application start...");
        IBusiness business = new SimpleBusiness();
        business.businessRun();
        
        System.out.println("application over...");
    }

}
