package com.hkafeidou.my_tomcat_simple.processor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import com.hkafeidou.my_tomcat_infrastructure.infrstructure.my_const.HttpServerConst;
import com.hkafeidou.my_tomcat_infrastructure.infrstructure.util.RequestUtil;
import com.hkafeidou.my_tomcat_infrastructure.infrstructure.util.StringManager;
import com.hkafeidou.my_tomcat_simple.connector.http.HttpConnector;
import com.hkafeidou.my_tomcat_simple.connector.http.HttpHeader;
import com.hkafeidou.my_tomcat_simple.connector.http.HttpRequestLine;
import com.hkafeidou.my_tomcat_simple.connector.http.SocketInputStream;
import com.hkafeidou.my_tomcat_simple.request.HttpRequest;
import com.hkafeidou.my_tomcat_simple.response.HttpResponse;

public class HttpProcessor {
    
    private HttpConnector connector;
    private HttpRequest request;
    private HttpRequestLine requestLine = new HttpRequestLine();
    private HttpResponse response;
    
    protected String method= null;
    protected String queryString = null;
    
    protected StringManager sm = StringManager.getManager("com.hkafeidou.my_tomcat_simple.connector.http");
    
    public HttpProcessor(HttpConnector connector) {
        this.connector=connector;
    }
    
    public void process(Socket socket) {
        SocketInputStream input =null;
        OutputStream output = null;
        
        try {
            input = new SocketInputStream(socket.getInputStream(), HttpServerConst.DEFAULT_BUFF_SIZE);
            
            output = socket.getOutputStream();
            request = new HttpRequest(input);
            
            response = new HttpResponse(output);
            response.setRequest(request);
            response.setHeader("Server", "Hkafeidou");
            
            parseRequest(input,output);
            parseHeaders(input);
            IProcessor processor = null;
            if(request.getRequestURI().startsWith(HttpServerConst.MY_SERVER_KEY)) {
                processor = new ServletProcessor();
            }else {
                processor = new StaticResourceProcessor();
            }
            processor.process(request, response);
            
            socket.close();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ServletException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }
    
    private void parseHeaders(SocketInputStream input) throws IOException, ServletException {
        while(true) {
            HttpHeader header = new HttpHeader();
            input.readHeader(header);
            if(header.nameEnd==0) {
                if(header.valueEnd==0) {
                    return ;
                }else {
                    throw new ServletException( sm.getString("httpProcessor.parseHeaders.colon"));
                }
            }
            
            String name = new String(header.name,0,header.nameEnd);
            String value = new String(header.value,0,header.valueEnd);
            request.addHeaders(name, value);
            
            if(name.equals("cookie")) {
                Cookie[] cookie = RequestUtil.parseCookieHeader(value);
                for(int i=0;i<cookie.length;i++) {
                    if(cookie[i].getName().equals("jsesessionid")) {
                        if(!request.isRequestedSessionIdFromCookie()) {
                            request.setRequestedSessionId(cookie[i].getValue());
                            request.setRequestedSessionCookie(true);
                            request.setRequestedSessionURL(false);
                        }
                    }
                    request.addCookie(cookie[i]);
                    
                }
            }else if(name.equals("content-length")) {
                int n = -1;
                try {
                    n = Integer.parseInt(value);
                } catch (Exception e) {
                    throw new ServletException(
                            sm.getString("httpProcessor.parseHeaders.contentLength"));
                }
                request.setContentLength(n);
            }else if(name.equals("content-type")) {
                request.setContentType(value);
            }
            
        }
    }
    
    
    /**
     * parse request info :GET /myApp/ModernServlet?userName=tarzan&password=pwd HTTP/1.1
     * @param input
     * @param output
     * @throws IOException
     * @throws ServletException
     */
    private void parseRequest(SocketInputStream input ,OutputStream output) throws IOException, ServletException {
        input.readRequestLine(requestLine);
        String method = new String(requestLine.method,0,requestLine.methodEnd);
        String uri = null;
        String protocol = new String (requestLine.protocol,0,requestLine.protocolEnd);
        if(method.length()<1) {
            throw new ServletException("Missing HTTP request method");
        }else if(requestLine.uriEnd<1) {
            throw new ServletException("Missing HTTP request URI");
        }
        
        int question = requestLine.indexOf("?");
        if(question>0) {
            request.setQueryString(new String(requestLine.uri,question+1,requestLine.uriEnd-question-1));;
            uri = new String(requestLine.uri,0,question);
        }else {
            request.setQueryString(null);
            uri = new String(requestLine.uri,0,requestLine.uriEnd);
        }
        
        if(!uri.startsWith("/")) {
            int pos = uri.indexOf("://");
            if(pos!=-1) {
                pos = uri.indexOf('/',pos+3);
                if(pos==-1) {
                    uri="";
                }else {
                    uri= uri.substring(pos);
                }
            }
        }
        
        String match = ";jsessionid=";
        int semicolon = uri.indexOf(match);
        if(semicolon>=0) {
            String rest = uri.substring(semicolon+match.length());
            int semicolon2 = rest.indexOf(";");
            if(semicolon2>=0) {
                request.setRequestedSessionId(rest.substring(0,semicolon2));
                rest = rest.substring(semicolon2);
            }else {
                request.setRequestedSessionId(rest);
                rest = "";
            }
            request.setRequestedSessionURL(true);
            uri=uri.substring(0,semicolon)+rest;
            
        }else {
            request.setRequestedSessionId(null);
            request.setRequestedSessionURL(false);
        }
        
        String  normalizedUri = normalize(uri);
        request.setMethod(method);
        request.setProtocol(protocol);
        if(null != normalizedUri) {
            request.setRequestURI(uri);
        }else {
            request.setRequestURI(uri);
        }
        
        if(null == normalizedUri) {
            throw new ServletException("Invalid URI: " + uri + "'");
        }
        
    }
    
    /**
     * 将url转换为正常的url ，存在浏览器自动编码 ，或者斜杠问题
     * @param path
     * @return
     */
    protected String normalize(String path) {
        if (path == null)
            return null;
        // Create a place for the normalized path
        String normalized = path;

        // Normalize "/%7E" and "/%7e" at the beginning to "/~"
        if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
            normalized = "/~" + normalized.substring(4);

        // Prevent encoding '%', '/', '.' and '\', which are special reserved
        // characters
        if ((normalized.indexOf("%25") >= 0)
                || (normalized.indexOf("%2F") >= 0)
                || (normalized.indexOf("%2E") >= 0)
                || (normalized.indexOf("%5C") >= 0)
                || (normalized.indexOf("%2f") >= 0)
                || (normalized.indexOf("%2e") >= 0)
                || (normalized.indexOf("%5c") >= 0)) {
            return null;
        }

        if (normalized.equals("/."))
            return "/";

        // Normalize the slashes and add leading slash if necessary
        if (normalized.indexOf('\\') >= 0)
            normalized = normalized.replace('\\', '/');
        if (!normalized.startsWith("/"))
            normalized = "/" + normalized;

        // Resolve occurrences of "//" in the normalized path
        while (true) {
            int index = normalized.indexOf("//");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index)
                    + normalized.substring(index + 1);
        }

        // Resolve occurrences of "/./" in the normalized path
        while (true) {
            int index = normalized.indexOf("/./");
            if (index < 0)
                break;
            normalized = normalized.substring(0, index)
                    + normalized.substring(index + 2);
        }

        // Resolve occurrences of "/../" in the normalized path
        while (true) {
            int index = normalized.indexOf("/../");
            if (index < 0)
                break;
            if (index == 0)
                return (null); // Trying to go outside our context
            int index2 = normalized.lastIndexOf('/', index - 1);
            normalized = normalized.substring(0, index2)
                    + normalized.substring(index + 3);
        }

        // Declare occurrences of "/..." (three or more dots) to be invalid
        // (on some Windows platforms this walks the directory tree!!!)
        if (normalized.indexOf("/...") >= 0)
            return (null);

        // Return the normalized path that we have completed
        return (normalized);

    }
}
