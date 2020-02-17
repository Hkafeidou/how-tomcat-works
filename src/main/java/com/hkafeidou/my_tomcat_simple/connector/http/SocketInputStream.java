package com.hkafeidou.my_tomcat_simple.connector.http;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import com.hkafeidou.my_tomcat_infrastructure.infrstructure.util.StringManager;

public class SocketInputStream extends InputStream {
    private static final String packageName = "com.hkafeidou.my_tomcat_simple.connector.http";
    private static final byte CR = (byte) '\r';
    private static final byte LF = (byte) '\n';
    private static final byte SP = (byte) ' ';
    private static final byte HT = (byte) '\t';
    private static final byte COLON = (byte) ':';
    private static final int LC_OFFSET = 'A' - 'a';

    protected byte buf[];
    /**
     * last valid byte
     */
    protected int count;
    /**
     * position in the buffer
     */
    protected int pos;
    protected InputStream stream;

    public SocketInputStream(InputStream input, int bufferSize) {
        // TODO Auto-generated constructor stub
        this.stream = input;
        buf = new byte[bufferSize];
    }

    protected static StringManager sm = StringManager.getManager(packageName);

    public void readRequestLine(HttpRequestLine requestLine)
            throws IOException {
        if (requestLine.methodEnd != 0) {
            requestLine.recycle();
        }
        int chr = 0;
        do {
            try {
                chr = read();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                chr = -1;
            }
        } while ((chr == CR) || (chr == LF));

        if (chr == -1) {
            throw new EOFException(
                    sm.getString("requestStream.readline.error"));
        }

        pos--;

        // method
        int maxRead = requestLine.method.length;
        int readStart = pos;
        int readCount = 0;

        boolean space = false;

        while (!space) {
            if (readCount >= maxRead) {
                if ((2 * maxRead) <= HttpRequestLine.INITIAL_METHOD_SIZE) {
                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(requestLine.method, 0, newBuffer, 0,
                            maxRead);
                    requestLine.method = newBuffer;
                    maxRead = requestLine.method.length;
                } else {
                    throw new IOException(
                            sm.getString("requestStream.readline.toolong"));
                }
            }

            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException(
                            sm.getString("requestStream.readline.error"));
                }
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == SP) {
                space = true;
            }
            requestLine.method[readCount] = (char) buf[pos];
            readCount++;
            pos++;
        }

        requestLine.methodEnd = readCount - 1;

        // uri
        maxRead = requestLine.uri.length;
        readStart = pos;
        readCount = 0;

        space = false;

        boolean eol = false;

        while (!space) {
            if (readCount >= maxRead) {
                if ((2 * maxRead) <= HttpRequestLine.MAX_URL_SIZE) {
                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(requestLine.uri, 0, newBuffer, 0, maxRead);
                    requestLine.uri = newBuffer;
                    maxRead = requestLine.uri.length;
                }
            }

            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException(
                            sm.getString("requestStream.readline.error"));
                }

                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == SP) {
                space = true;
            } else if ((buf[pos] == CR) || (buf[pos] == LF)) {
                eol = true;
                space = true;
            }
            requestLine.uri[readCount] = (char) buf[pos];
            readCount++;
            pos++;
        }

        requestLine.uriEnd = readCount - 1;

        // protocol
        maxRead = requestLine.protocol.length;
        readStart = pos;
        readCount = 0;
        while (!eol) {
            if (readCount >= maxRead) {
                if ((2 * maxRead) <= HttpRequestLine.MAX_PROTOCOL_SIZE) {
                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(requestLine.protocol, 0, newBuffer, 0,
                            maxRead);
                    requestLine.protocol = newBuffer;
                    maxRead = requestLine.protocol.length;
                } else {
                    throw new IOException(
                            sm.getString("requestStream.readline.toolong"));
                }
            }
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException(
                            sm.getString("requestStream.readline.error"));
                }
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == CR) {

            } else if (buf[pos] == LF) {
                eol = true;
            } else {
                requestLine.protocol[readCount] = (char) buf[pos];
                readCount++;
            }
            pos++;
        }
        requestLine.protocolEnd = readCount;
    }

    public void readHeader(HttpHeader header) throws IOException {
        if (header.nameEnd != 0) {
            header.recycle();
        }

        int chr = read();
        if (chr == CR || chr == LF) {
            if (chr == CR) {
                read();
            }
            header.nameEnd = 0;
            header.valueEnd = 0;
            return;
        } else {
            pos--;
        }

        int maxRead = header.name.length;
        int readStart = pos;
        int readCount = 0;

        // key：value
        // header name(key) 遇到“：” 跳出循环
        boolean colon = false;
        while (!colon) {
            if (readCount >= maxRead) {
                if ((2 * maxRead) <= HttpHeader.MAX_NAME_SIZE) {
                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(header.name, 0, newBuffer, 0, maxRead);
                    header.name = newBuffer;
                    maxRead = header.name.length;
                } else {
                    throw new IOException(
                            sm.getString("requestStream.readline.toolong"));
                }

            }

            // 例如：buff 10（缓冲区总容量10），count为6（上次读入缓冲区的个数为6），pos
            // 8（当前缓冲区位置为8），则没有有用信息，需要重新读入缓冲区
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException(
                            sm.getString("requestStream.readline.error"));
                }
                pos = 0;
                readStart = 0;
            }

            if (buf[pos] == COLON) {
                colon = true;
            }
            char val = (char) buf[pos];
            if ((val >= 'A') && (val <= 'Z')) {
                val = (char) (val - LC_OFFSET);
            }
            header.name[readCount] = val;
            readCount++;
            pos++;
        }
        header.nameEnd = readCount - 1;

        maxRead = header.value.length;
        readStart = pos;
        readCount = 0;

        int crPos = -2;

        boolean eol = false;
        boolean validLine = true;
        while (validLine) {
            boolean space = true;
            while (space) {
                if (pos >= count) {
                    int val = read();
                    if (val == -1) {
                        throw new IOException(
                                sm.getString("requestStream.readline.error"));
                    }
                    pos = 0;
                    readStart = 0;
                }
                if (buf[pos] == SP || buf[pos] == HT) {
                    pos++;
                } else {
                    space = false;
                }
            }

            while (!eol) {
                if (readCount >= maxRead) {
                    if ((2 * maxRead) <= HttpHeader.MAX_VALUE_SIZE) {
                        char[] newBuffer = new char[2 * maxRead];
                        System.arraycopy(header.value, 0, newBuffer, 0,
                                maxRead);
                        header.value = newBuffer;
                        maxRead = header.value.length;
                    } else {
                        throw new IOException(
                                sm.getString("requestStream.readline.toolong"));
                    }
                }

                if (pos >= count) {
                    int val = read();
                    if (val == -1) {
                        throw new IOException(
                                sm.getString("requestStream.readline.error"));
                    }
                    pos = 0;
                    readStart = 0;
                }
                if (buf[pos] == CR) {

                } else if (buf[pos] == LF) {
                    eol = true;
                } else {
                    int ch = buf[pos] & 0xff;
                    header.value[readCount] = (char) ch;
                    readCount++;
                }
                pos++;
            }

            int nextChr = read();
            if (nextChr != SP && nextChr != HT) {
                pos--;
                validLine = false;
            } else {
                eol = false;
                if (readCount >= maxRead) {
                    if ((2 * maxRead) <= HttpHeader.MAX_VALUE_SIZE) {
                        char[] newBuffer = new char[2 * maxRead];
                        System.arraycopy(header.value, 0, newBuffer, 0,
                                maxRead);
                        header.value = newBuffer;
                        maxRead = header.value.length;
                    } else {
                        throw new IOException(
                                sm.getString("requestStream.readline.toolong"));

                    }
                }
                header.value[readCount] = ' ';
                readCount++;
            }
        }
        header.valueEnd = readCount;
    }

    @Override
    public int read() throws IOException {
        // TODO Auto-generated method stub
        if (pos >= count) {
            fill();
            if (pos >= count) {
                return -1;
            }
        }

        return buf[pos++] & 0xff;
    }

    protected void fill() throws IOException {
        pos = 0;
        count = 0;
        int nRead = this.stream.read(buf, 0, buf.length);
        if (nRead > 0) {
            count = nRead;
        }
    }

    public int available() throws IOException {
        return (count-pos)+stream.available();
    }
    
    public void close() throws IOException {
        if(null == stream) {
            return ;
        }
        stream.close();
        stream=null;
        buf=null;
    }
    
}
