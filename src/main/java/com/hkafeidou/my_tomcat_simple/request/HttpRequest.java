package com.hkafeidou.my_tomcat_simple.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import com.hkafeidou.my_tomcat_infrastructure.infrstructure.my_const.HttpServerConst;
import com.hkafeidou.my_tomcat_infrastructure.infrstructure.util.ParameterMap;
import com.hkafeidou.my_tomcat_infrastructure.infrstructure.util.RequestUtil;

public class HttpRequest implements HttpServletRequest {

    private String contentType;
    private int contentLength;
    private InetAddress inetAddress;
    private InputStream input;
    private String method;
    private String protocol;
    private String queryString;
    private String requestURI;
    private String serverName;
    private int serverPort;
    private Socket socket;
    private boolean requestedSessionCookie;
    private String requestedSessionId;
    private boolean requestedSessionURL;

    protected HashMap attributes = new HashMap();
    protected String authorization = null;

    protected String contextPath = "";
    protected ArrayList cookies = new ArrayList<>();
    protected static ArrayList empty = new ArrayList<>();

    protected SimpleDateFormat formats[] = {
            new SimpleDateFormat("EEE,dd MM yyyy HH:mm:ss zzz", Locale.US),
            new SimpleDateFormat("EEEEEE,dd-MMM-yy HH:mm:ss zzz", Locale.US),
            new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy", Locale.US)};
    protected HashMap headers = new HashMap();
    protected ParameterMap parameters = null;
    protected boolean parsed = false;
    protected String pathInfo = null;
    protected BufferedReader reader = null;

    protected ServletInputStream stream = null;
    public HttpRequest(InputStream input) {
        this.input = input;
    }
    public void addHeaders(String name, String value) {
        name = name.toLowerCase();
        synchronized (headers) {
            ArrayList values = (ArrayList) headers.get(name);
            if (null == values) {
                values = new ArrayList();
                headers.put(name, values);
            }
            values.add(value);
        }
    }

    protected void parseParameters() {
        if (parsed) {
            return;
        }
        ParameterMap results = parameters;
        if (null == results) {
            results = new ParameterMap();
        }
        results.setLocked(false);
        String encoding = getCharacterEncoding();
        if (null == encoding) {
            encoding = HttpServerConst.DEFAULT_ENCODING;
        }
        //从请求url中获取参数
        String queryString = getQueryString();
        try {
            RequestUtil.parseParameters(results, queryString, encoding);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //从请求体重获取参数
        String contentType = getContentType();
        if (null == contentType) {
            contentType = "";
        }
        int semicolon = contentType.indexOf(";");
        if (semicolon >= 0) {
            contentType = contentType.substring(0, semicolon).trim();
        } else {
            contentType = contentType.trim();
        }

        if ("POST".equals(getMethod()) && getContentLength() > 0
                && "application/x-www-form-urlencoded".equals(contentType)) {
            try {
                int max = getContentLength();
                int len = 0;
                byte[] buf = new byte[getContentLength()];
                ServletInputStream is = getInputStream();
                while (len < max) {
                    int next = is.read(buf, len, max - len);
                    if (next < 0) {
                        break;
                    }
                    len += next;
                }
                is.close();
                if (len < max) {
                    throw new RuntimeException("Content length mismatch");
                }
                RequestUtil.parseParameters(results, buf, encoding);
            } catch (UnsupportedEncodingException e) {
                // TODO: handle exception
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new RuntimeException("Content read fail");
            }
        }
        results.setLocked(true);
        parsed = true;
        parameters = results;

    }

    public ServletInputStream getInputStream() {
        if (reader != null) {
            throw new IllegalStateException("getInputStream has been called");
        }
        if (null == stream) {
            stream = createInputStream();
        }

        return stream;
    }

    public void addCookie(Cookie cookie) {
        synchronized (cookies) {
            cookies.add(cookie);
        }
    }

    public ServletInputStream createInputStream() {
        return new RequestStream(this);
    }

    public InputStream getStream() {
        return input;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getMethod() {
        return method;
    }

    public String getContentType() {
        return contentType;
    }

    public String getCharacterEncoding() {
        return null;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setRequestedSessionId(String requestedSessionId) {
        this.requestedSessionId = requestedSessionId;
    }

    public void setRequestedSessionURL(boolean flag) {
        requestedSessionURL = flag;
    }

    public void setRequestedSessionCookie(boolean flag) {
        this.requestedSessionCookie = flag;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPathInfo(String path) {
        this.pathInfo = path;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setContentLength(int length) {
        this.contentLength = length;
    }

    public void setContentType(String type) {
        this.contentType = type;
    }

    public void setInet(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public void setContextPath(String path) {
        if (path == null)
            this.contextPath = "";
        else
            this.contextPath = path;
    }

    @Override
    public Object getAttribute(String name) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Enumeration<String> getAttributeNames() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void setCharacterEncoding(String env)
            throws UnsupportedEncodingException {
        // TODO Auto-generated method stub

    }
    @Override
    public long getContentLengthLong() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public String getParameter(String name) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Enumeration<String> getParameterNames() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String[] getParameterValues(String name) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Map<String, String[]> getParameterMap() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getProtocol() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getScheme() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getServerName() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public int getServerPort() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public BufferedReader getReader() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getRemoteAddr() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getRemoteHost() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void setAttribute(String name, Object o) {
        // TODO Auto-generated method stub

    }
    @Override
    public void removeAttribute(String name) {
        // TODO Auto-generated method stub

    }
    @Override
    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Enumeration<Locale> getLocales() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public boolean isSecure() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getRealPath(String path) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public int getRemotePort() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public String getLocalName() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getLocalAddr() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public int getLocalPort() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public ServletContext getServletContext() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public AsyncContext startAsync(ServletRequest servletRequest,
            ServletResponse servletResponse) throws IllegalStateException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public boolean isAsyncStarted() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isAsyncSupported() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public AsyncContext getAsyncContext() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public DispatcherType getDispatcherType() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getAuthType() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Cookie[] getCookies() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public long getDateHeader(String name) {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public String getHeader(String name) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Enumeration<String> getHeaders(String name) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Enumeration<String> getHeaderNames() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public int getIntHeader(String name) {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public String getPathInfo() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getPathTranslated() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getContextPath() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getRemoteUser() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public boolean isUserInRole(String role) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public Principal getUserPrincipal() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getRequestedSessionId() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getRequestURI() {
        // TODO Auto-generated method stub
        return requestURI;
    }
    @Override
    public StringBuffer getRequestURL() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getServletPath() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public HttpSession getSession(boolean create) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public HttpSession getSession() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String changeSessionId() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public boolean isRequestedSessionIdValid() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isRequestedSessionIdFromCookie() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isRequestedSessionIdFromURL() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isRequestedSessionIdFromUrl() {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean authenticate(HttpServletResponse response)
            throws IOException, ServletException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public void login(String username, String password)
            throws ServletException {
        // TODO Auto-generated method stub

    }
    @Override
    public void logout() throws ServletException {
        // TODO Auto-generated method stub

    }
    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Part getPart(String name) throws IOException, ServletException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass)
            throws IOException, ServletException {
        // TODO Auto-generated method stub
        return null;
    }

}
