package cn.th.diytomcat.http;

import cn.hutool.core.util.StrUtil;
import cn.th.diytomcat.util.MiniBrowser;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Request {
    private Socket socket;
    private String requestString;
    private String uri;

    public Request(Socket socket) throws IOException {
        this.socket = socket;
        parseRequestString();
        if (StrUtil.isEmpty(requestString)) {
            return;
        }
        parseUri();
    }

    private void parseUri() {
        String temp = StrUtil.subBetween(requestString, " ", " ");
        if (!StrUtil.contains(temp, '?')) {
            uri = temp;
            return;
        }
        uri = StrUtil.subBefore(temp, '?', false);
    }

    private void parseRequestString() throws IOException {
        InputStream inputStream = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(inputStream);
        this.requestString =  new String(bytes, StandardCharsets.UTF_8);
    }

    public String getRequestString() {
        return requestString;
    }

    public String getUri() {
        return uri;
    }
}
