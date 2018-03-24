package util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by fanyuanyuan on 2018/3/24.
 */
public class PrintUtil {

    private static final Logger logger = LoggerFactory.getLogger(PrintUtil.class);


    public static void print(JSONObject queryResult, HttpServletResponse response){
        try {
            if(queryResult==null){
                queryResult = new JSONObject();
            }
            PrintWriter printWriter = response.getWriter();
            printWriter.println(queryResult.toJSONString());
            printWriter.flush();
            printWriter.close();
        }catch (Exception e){
            logger.error("print failed, ERROR:{}", e);
        }
    }

    public static void print(JSONArray queryResult, HttpServletResponse response){
        try {
            if(queryResult==null){
                queryResult = new JSONArray();
            }
            PrintWriter printWriter = response.getWriter();
            printWriter.println(queryResult.toJSONString());
            printWriter.flush();
            printWriter.close();
        }catch (Exception e){
            logger.error("print failed, ERROR:{}", e);
        }
    }
}
