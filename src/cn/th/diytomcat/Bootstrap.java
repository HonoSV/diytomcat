package cn.th.diytomcat;

import cn.hutool.core.util.NetUtil;
import cn.th.diytomcat.http.Request;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Bootstrap {
    public static void main(String[] args) {
        try {
            int port = 18080;
            if (!NetUtil.isUsableLocalPort(port)) {
                System.out.println(port + "端口已经被占用了，排查并关闭本端口的办法请用：\\r\\n https://how2j.cn/k/tomcat/tomcat-portfix/545.html");
                return;
            }

            ServerSocket ss = new ServerSocket(port);
            while (true) {
                Socket s = ss.accept();
                Request request = new Request(s);
                String requestString = request.getRequestString();
                System.out.println("浏览器的输入信息： \\r\\n" + requestString);
                System.out.println("uri:" + request.getUri());

                OutputStream os = s.getOutputStream();
                String response_head = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n\r\n";
                String responseString = "Hello DIY Tomcat from how2j.cn";
                responseString = response_head + responseString;
                os.write(responseString.getBytes());
                os.flush();

                s.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
