package com.hkafeidou.my_tomcat_simple.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.hkafeidou.my_tomcat_infrastructure.infrstructure.my_const.HttpServerConst;
import com.hkafeidou.my_tomcat_infrastructure.infrstructure.util.CookieTools;
import com.hkafeidou.my_tomcat_simple.request.HttpRequest;

public class HttpResponse implements HttpServletResponse {

    private static final int BUFFER_SIZE = 1024;
    HttpRequest request;
    OutputStream output;
    PrintWriter writer;
    protected byte[] buffer = new byte[BUFFER_SIZE];
    protected int bufferCount = 0;
    protected boolean committed = false;
    protected int contentCount = 0;
    protected int contentLength = -1;
    protected String contentType = null;
    protected String encoding = null;
    protected ArrayList cookies = new ArrayList();
    protected HashMap headers = new HashMap();
    protected final SimpleDateFormat format = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

    protected String message = getStatusMessage(HttpServletResponse.SC_OK);
    protected int status = HttpServletResponse.SC_OK;

    public HttpResponse(OutputStream output) {
        this.output = output;
    }

    public void finishResponse() {
        if (null != writer) {
            writer.flush();
            writer.close();
        }
    }

    protected String getStatusMessage(int status) {
        switch (status) {
            case SC_OK :
                return ("OK");
            case SC_ACCEPTED :
                return ("Accepted");
            case SC_BAD_GATEWAY :
                return ("Bad Gateway");
            case SC_BAD_REQUEST :
                return ("Bad Request");
            case SC_CONFLICT :
                return ("Conflict");
            case SC_CONTINUE :
                return ("Continue");
            case SC_CREATED :
                return ("Created");
            case SC_EXPECTATION_FAILED :
                return ("Expectation Failed");
            case SC_FORBIDDEN :
                return ("Forbidden");
            case SC_GATEWAY_TIMEOUT :
                return ("Gateway Timeout");
            case SC_GONE :
                return ("Gone");
            case SC_HTTP_VERSION_NOT_SUPPORTED :
                return ("HTTP Version Not Supported");
            case SC_INTERNAL_SERVER_ERROR :
                return ("Internal Server Error");
            case SC_LENGTH_REQUIRED :
                return ("Length Required");
            case SC_METHOD_NOT_ALLOWED :
                return ("Method Not Allowed");
            case SC_MOVED_PERMANENTLY :
                return ("Moved Permanently");
            case SC_MOVED_TEMPORARILY :
                return ("Moved Temporarily");
            case SC_MULTIPLE_CHOICES :
                return ("Multiple Choices");
            case SC_NO_CONTENT :
                return ("No Content");
            case SC_NON_AUTHORITATIVE_INFORMATION :
                return ("Non-Authoritative Information");
            case SC_NOT_ACCEPTABLE :
                return ("Not Acceptable");
            case SC_NOT_FOUND :
                return ("Not Found");
            case SC_NOT_IMPLEMENTED :
                return ("Not Implemented");
            case SC_NOT_MODIFIED :
                return ("Not Modified");
            case SC_PARTIAL_CONTENT :
                return ("Partial Content");
            case SC_PAYMENT_REQUIRED :
                return ("Payment Required");
            case SC_PRECONDITION_FAILED :
                return ("Precondition Failed");
            case SC_PROXY_AUTHENTICATION_REQUIRED :
                return ("Proxy Authentication Required");
            case SC_REQUEST_ENTITY_TOO_LARGE :
                return ("Request Entity Too Large");
            case SC_REQUEST_TIMEOUT :
                return ("Request Timeout");
            case SC_REQUEST_URI_TOO_LONG :
                return ("Request URI Too Long");
            case SC_REQUESTED_RANGE_NOT_SATISFIABLE :
                return ("Requested Range Not Satisfiable");
            case SC_RESET_CONTENT :
                return ("Reset Content");
            case SC_SEE_OTHER :
                return ("See Other");
            case SC_SERVICE_UNAVAILABLE :
                return ("Service Unavailable");
            case SC_SWITCHING_PROTOCOLS :
                return ("Switching Protocols");
            case SC_UNAUTHORIZED :
                return ("Unauthorized");
            case SC_UNSUPPORTED_MEDIA_TYPE :
                return ("Unsupported Media Type");
            case SC_USE_PROXY :
                return ("Use Proxy");
            case 207 : // WebDAV
                return ("Multi-Status");
            case 422 : // WebDAV
                return ("Unprocessable Entity");
            case 423 : // WebDAV
                return ("Locked");
            case 507 : // WebDAV
                return ("Insufficient Storage");
            default :
                return ("HTTP Response Status " + status);
        }
    }

    protected void sendHeaders() {
        if (isCommitted()) {
            return;
        }
        OutputStreamWriter osr = null;
        try {
            osr = new OutputStreamWriter(getStream(), getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            osr = new OutputStreamWriter(getStream());
        }
        final PrintWriter outputWriter = new PrintWriter(osr);
        outputWriter.print(this.getProtocol());
        outputWriter.print(" ");
        outputWriter.print(status);
        if (message != null) {
            outputWriter.print(" ");
            outputWriter.print(message);
        }
        outputWriter.print("\r\n");

        if (getContentType() != null) {
            outputWriter.print("Content-Type: " + getContentType() + "\r\n");
        }

        if (getContentLength() >= 0) {
            outputWriter
                    .print("Content-Length: " + getContentLength() + "\r\n");
        }

        synchronized (headers) {
            Iterator names = headers.keySet().iterator();
            while (names.hasNext()) {
                String name = (String) names.next();
                ArrayList values = (ArrayList) headers.get(name);
                Iterator items = values.iterator();
                while (items.hasNext()) {
                    String value = (String) items.next();
                    outputWriter.print(name);
                    outputWriter.print(": ");
                    outputWriter.print(value);
                    outputWriter.print("\r\n");
                }
            }
        }
        synchronized (cookies) {
            Iterator items = cookies.iterator();
            while (items.hasNext()) {
                Cookie cookie = (Cookie) items.next();
                outputWriter.print(CookieTools.getCookieHeaderName(cookie));
                outputWriter.print(": ");
                outputWriter.print(CookieTools.getCookieHeaderValue(cookie));
                outputWriter.print("\r\n");
            }
        }
        outputWriter.print("\r\n");
        outputWriter.flush();

        committed = true;
    }

    public int getContentLength() {
        return contentLength;
    }

    protected String getProtocol() {
        return request.getProtocol();
    }
    public OutputStream getStream() {
        return this.output;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            /* request.getUri has been replaced by request.getRequestURI */
            File file = new File(HttpServerConst.DEFAULT_WEB_ROOT,
                    request.getRequestURI());
            fis = new FileInputStream(file);
            /*
             * HTTP Response = Status-Line (( general-header | response-header |
             * entity-header ) CRLF) CRLF [ message-body ] Status-Line =
             * HTTP-Version SP Status-Code SP Reason-Phrase CRLF
             */
            int ch = fis.read(bytes, 0, BUFFER_SIZE);
            while (ch != -1) {
                output.write(bytes, 0, ch);
                ch = fis.read(bytes, 0, BUFFER_SIZE);
            }
        } catch (FileNotFoundException e) {
            String errorMessage = "HTTP/1.1 404 File Not Found\r\n"
                    + "Content-Type: text/html\r\n" + "Content-Length: 23\r\n"
                    + "\r\n" + "<h1>File Not Found</h1>";
            output.write(errorMessage.getBytes());
        } finally {
            if (fis != null)
                fis.close();
        }
    }

    public void write(int b) throws IOException {
        if (bufferCount >= buffer.length) {
            flushBuffer();
        }
        buffer[bufferCount++] = (byte) b;
        contentCount++;
    }

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }
    public void write(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        if (len <= (buffer.length - bufferCount)) {
            System.arraycopy(b, off, buffer, bufferCount, len);
            bufferCount += len;
            contentCount += len;
            return;
        }

        flushBuffer();
        int iterations = len / buffer.length;
        int leftoverStart = iterations * buffer.length;
        int leftoverLen = len - leftoverStart;
        for (int i = 0; i < iterations; i++) {
            write(b, off + leftoverStart, leftoverLen);
        }
    }
    
    @Override
    public String getCharacterEncoding() {
        // TODO Auto-generated method stub
        if (encoding == null)
            return "UTF-8";
        else
            return encoding;
    }

    @Override
    public String getContentType() {
        // TODO Auto-generated method stub
        return contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        // TODO Auto-generated method stub
        ResponseStream newStream = new ResponseStream(this);
        newStream.setCommit(false);
        OutputStreamWriter osr = new OutputStreamWriter(newStream,getCharacterEncoding());
        writer = new ResponseWriter(osr);
        
        return writer;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setContentLength(int len) {
        // TODO Auto-generated method stub
        if (isCommitted()) {
            return ;
        }
        this.contentLength=len;
    }

    @Override
    public void setContentLengthLong(long len) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setContentType(String type) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBufferSize(int size) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getBufferSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        // TODO Auto-generated method stub
        if (bufferCount > 0) {
            try {
                output.write(buffer, 0, bufferCount);
            } finally {
                bufferCount = 0;
            }
        }
    }

    @Override
    public void resetBuffer() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isCommitted() {
        // TODO Auto-generated method stub
        return committed;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLocale(Locale loc) {
        // TODO Auto-generated method stub
        if (isCommitted()) {
            return ;
        }
        
        String language = loc.getLanguage();
        if ((language != null) && (language.length() > 0)) {
          String country = loc.getCountry();
          StringBuffer value = new StringBuffer(language);
          if ((country != null) && (country.length() > 0)) {
            value.append('-');
            value.append(country);
          }
          setHeader("Content-Language", value.toString());
        }
    }

    @Override
    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addCookie(Cookie cookie) {
        // TODO Auto-generated method stub
        if (isCommitted()) {
            return;
        }
        synchronized (cookies) {
            cookies.add(cookie);
        }
    }

    @Override
    public boolean containsHeader(String name) {
        // TODO Auto-generated method stub
        synchronized (headers) {
            return (headers.get(name) != null);
        }
    }

    @Override
    public String encodeURL(String url) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        // TODO Auto-generated method stub
        return encodeURL(url);
    }

    @Override
    public String encodeRedirectUrl(String url) {
        // TODO Auto-generated method stub
        return encodeRedirectURL(url);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendError(int sc) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void sendRedirect(String location) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setDateHeader(String name, long date) {
        // TODO Auto-generated method stub
        if (isCommitted()) {
            return ;
        }
        setHeader(name, format.format(new Date(date)));
    }

    @Override
    public void addDateHeader(String name, long date) {
        // TODO Auto-generated method stub
        if (isCommitted()) {
            return;
        }
        addHeader(name, format.format(new Date(date)));
    }

    @Override
    public void setHeader(String name, String value) {
        // TODO Auto-generated method stub
        if (isCommitted()) {
            return;
        }
        
        ArrayList values = new ArrayList();
        values.add(value);
        synchronized (headers) {
          headers.put(name, values);
        }
        String match = name.toLowerCase();
        if (match.equals("content-length")) {
          int contentLength = -1;
          try {
            contentLength = Integer.parseInt(value);
          }
          catch (NumberFormatException e) {
            ;
          }
          if (contentLength >= 0)
            setContentLength(contentLength);
        }
        else if (match.equals("content-type")) {
          setContentType(value);
        }
    }

    @Override
    public void addHeader(String name, String value) {
        // TODO Auto-generated method stub
        if (isCommitted()) {
            return;
        }

        synchronized (headers) {
            ArrayList values = (ArrayList) headers.get(name);
            if (values == null) {
                values = new ArrayList();
                headers.put(name, values);
            }

            values.add(value);
        }
    }

    @Override
    public void setIntHeader(String name, int value) {
        // TODO Auto-generated method stub
        if (isCommitted()) {
            return ;
        }
        setHeader(name, ""+value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        // TODO Auto-generated method stub
        if (isCommitted()) {
            return;
        }

        addHeader(name, "" + value);
    }

    @Override
    public void setStatus(int sc) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStatus(int sc, String sm) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getStatus() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getHeader(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        // TODO Auto-generated method stub
        return null;
    }

}
