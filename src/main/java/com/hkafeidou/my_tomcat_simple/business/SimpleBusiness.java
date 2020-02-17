package com.hkafeidou.my_tomcat_simple.business;

import com.hkafeidou.my_infrastructure.business.IBusiness;
import com.hkafeidou.my_tomcat_infrastructure.infrstructure.my_const.HttpServerConst;
import com.hkafeidou.my_tomcat_simple.connector.http.HttpConnector;
import com.hkafeidou.my_tomcat_simple.server.HttpServer;

/**
 * simple business
 * @author Hao
 *
 */
public class SimpleBusiness implements IBusiness {

    @Override
    public void businessRun() {
        // TODO Auto-generated method stub
//        simple();
        
        //使用connector 操作
        connector();
    }
    
    private void connector() {
        HttpConnector connector = new HttpConnector();
        connector.start();
    }
    
    private void simple() {
        HttpServer server = new HttpServer();
        server.await();
    }

}