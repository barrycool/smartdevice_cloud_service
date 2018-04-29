package servlet.test;


import client.RedisTools;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import servlet.QueryPack;
import servlet.test.MyTcpHandler;
import util.ConstKey;
import util.RedisUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanyuanyuan on 2018/4/22.
 */

public class TcpServer {
    private Map<String, MyTcpHandler> mapTcpHandler = new HashMap<String, MyTcpHandler>();
    private ServerSocket serverSocket;

    private static TcpServer tcpServer = new TcpServer();

    public static TcpServer getTcpServer() {
        return tcpServer;
    }

    private TcpServer() {
        try {
            serverSocket = new ServerSocket(12345);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.start();
    }

    public void setDeviceStatus(String deviceId){
        String redisKey = RedisUtil.getRedisKey_DevStatus(deviceId);
        RedisTools.set(redisKey, "ON", ConstKey.user_device_status_over_time);
    }

    public void accept() {
        try {
            Socket socket = serverSocket.accept();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            char[] buf = new char[2048];
            int len = bufferedReader.read(buf);
            if (len > 0) {
                String data = new String(buf, 0, len);
                JSONObject jsonResult = QueryPack.postQueryPack(data);
                String deviceId = jsonResult.getString(ConstKey.deviceId);
                MyTcpHandler myTcpHandler = new MyTcpHandler(socket, bufferedReader);
                mapTcpHandler.put(deviceId, myTcpHandler);

                setDeviceStatus(deviceId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject onEvent(JSONObject jsonReq) {
        try {
            String deviceId = jsonReq.getString(ConstKey.deviceId);
            MyTcpHandler myTcpHandler = mapTcpHandler.get(deviceId);
            if(myTcpHandler==null){
                return null;
            }
            send(myTcpHandler, jsonReq);
            return myTcpHandler.recv();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void send(MyTcpHandler myTcpHandler, JSONObject jsonReq) {
        try {
            byte[] p = (jsonReq.toJSONString() + "\n").getBytes();
            myTcpHandler.write(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Thread t;

    private void start() {
        if (t == null) {
            t = new Thread() {
                public void run() {
                    while (true) {
                        accept();
                    }
                }
            };
            t.start();
        }
    }


}
