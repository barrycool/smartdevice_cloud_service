package servlet.test;

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
        while(true){
            out.write("hello server".getBytes());
            out.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            char[] buf = new char[1024];
            int len = br.read(buf);
            System.out.println(new String(buf, 0, len));
            Thread.sleep(5000);
        }

    }
}
