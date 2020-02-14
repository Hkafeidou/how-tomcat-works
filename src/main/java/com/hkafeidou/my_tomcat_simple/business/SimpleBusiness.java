package com.hkafeidou.my_tomcat_simple.business;

import com.hkafeidou.my_infrastructure.business.IBusiness;
import com.hkafeidou.my_tomcat_infrastructure.infrstructure.my_const.HttpServerConst;
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
        simple();
    }
    
    private void simple() {
        HttpServer server = new HttpServer();
        server.await();
    }

}