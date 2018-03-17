package util;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by fanyuanyuan on 2018/3/7.
 */
public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url 发送请求的URL
     * @return data 所代表远程资源的响应结果
     */
    public static String sendGet(String url, int connTimeout, int sockTimeout) {
        logger.info("cpc_url={}", url);
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(sockTimeout)
                .setConnectTimeout(connTimeout)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse resp = null;
        String res = "";
        try {
            resp = httpClient.execute(httpGet);
            HttpEntity entity = resp.getEntity();
            res = EntityUtils.toString(entity);
        } catch (IOException e) {
            logger.info("request failed, error={}", e);
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (IOException e) {
                }
            }
        }
        return res;
    }

    public static String sendPost(String url) {
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.info("request failed, error={}", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {

            }
        }
        return result;
    }

    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)  realUrl.openConnection();
            connection.setConnectTimeout(100);
            connection.setReadTimeout(250);
            connection.connect();

            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.info("request failed, error={}", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {

            }
        }
        return result;
    }

}
