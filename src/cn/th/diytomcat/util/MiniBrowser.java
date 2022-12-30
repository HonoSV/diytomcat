package cn.th.diytomcat.util;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class MiniBrowser {
    public static byte[] getHttpBytes(String url, boolean gzip) {
        byte[] result = null;
        try {
            URL u = new URL(url);
            Socket client = new Socket();
            int port = u.getPort();
            if (-1 == port) {port = 80;}
            InetSocketAddress inetSocketAddress = new InetSocketAddress(u.getHost(), port);
            client.connect(inetSocketAddress, 1000);

            HashMap<String, Object> requestHeaders = new HashMap<>();
            requestHeaders.put("Host", u.getHost() + ":" + port);
            requestHeaders.put("Accept", "text/html");
            requestHeaders.put("Connection", "close");
            requestHeaders.put("User-Agent", "my mini browser / java8");
            if (gzip) {requestHeaders.put("Accept-Encoding", "gzip");}

            String path = u.getPath();
            if (path.length() == 0) {path = "/";}

            String firstLine = "GET" + path + " HTTP/1.1\r\n";

            StringBuffer sb = new StringBuffer();
            sb.append(firstLine);
            for (String each : requestHeaders.keySet()) {
                String headerLine = each + ":" + requestHeaders.get(each) + "\r\n";
                sb.append(headerLine);
            }

            PrintWriter printWriter = new PrintWriter(client.getOutputStream(), true);
            printWriter.println(sb);
            InputStream is = client.getInputStream();

            result = readBytes(is);
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                result = e.toString().getBytes("utf-8");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }

    public static byte[] readBytes(InputStream is) throws IOException {
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            int len = is.read(buffer);
            if (-1 == len) {break;}
            byteArrayOutputStream.write(buffer, 0, len);
            if (len != bufferSize) {break;}
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] getContentBytes(String url, boolean gzip) {
        byte[] response = getHttpBytes(url, gzip);
        byte[] doubleReturns = "\r\n\r\n".getBytes();

        int pos = -1;
        for (int i = 0; i < response.length - doubleReturns.length; i++) {
            byte[] temp = Arrays.copyOfRange(response, i, i + doubleReturns.length);
            if (Arrays.equals(doubleReturns, temp)) {
                pos = i + doubleReturns.length;
                break;
            }
        }

        if (pos == -1) {
            return null;
        }

        return Arrays.copyOfRange(response, pos, response.length);
    }

    public static String getHttpString(String url,boolean gzip) {
        byte[] bytes=getHttpBytes(url,gzip);
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }

    public static String getContentString(String url, boolean gzip) {
        byte[] result = getContentBytes(url, gzip);
        if(null==result)
            return null;
        return new String(result, StandardCharsets.UTF_8).trim();
    }

    public static byte[] getHttpBytes(String url) {
        return getHttpBytes(url, false);
    }

    public static byte[] getContentBytes(String url) {
        return getContentBytes(url, false);
    }

    public static String getHttpString(String url) {
        return getHttpString(url, false);
    }

    public static String getContentString(String url) {
        return getContentString(url, false);
    }

    public static void main(String[] args) throws Exception {
        String url = "http://static.how2j.cn/diytomcat.html";
        String contentString= getContentString(url,false);
        System.out.println(contentString);
        String httpString= getHttpString(url,false);
        System.out.println(httpString);
    }
}
