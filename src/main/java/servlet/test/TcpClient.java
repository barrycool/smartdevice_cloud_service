package servlet.test;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by fanyuanyuan on 2018/4/22.
 */
public class TcpClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost",12345);
        OutputStream out = socket.getOutputStream();

        JSONObject jsob = new JSONObject();
        jsob.put("deviceId", "12323");
        jsob.put("name_space'", "Alexa");
        jsob.put("name", "ReportState");
        jsob.put("token", "2222");
        out.write(jsob.toString().getBytes());
        out.flush();
        while(true){
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            char[] buf = new char[1024];
            int len = br.read(buf);
            System.out.println(new String(buf, 0, len));
            Thread.sleep(5000);
            JSONObject jsob2 = new JSONObject();
            jsob2.put("deviceIdxx", "12322");
            out.write(jsob2.toString().getBytes());
            out.flush();
        }

    }
}
