package client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class MySqlClient {

    private String conn_str = "jdbc:mysql://10.121.199.254:13456/zs_mp?user=zs_mp&password=7cdc14234d7c3add&zeroDateTimeBehavior=convertToNull";
    private static final Logger logger = LoggerFactory.getLogger(MySqlClient.class);
    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(conn_str);
        } catch (SQLException e) {
            logger.error("get mysql connection is fail !");
        }

        return conn;
    }

    public JSONArray query(String sql) {
        JSONArray array = new JSONArray();
        Connection conn = getConnection();
        if (null != conn) {
            try {
                PreparedStatement statement = conn.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery();
                ResultSetMetaData metaData = resultSet.getMetaData();

                int columnCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    JSONObject jsonObj = new JSONObject();
                    for (int i = 1; i <= columnCount; i++) {
                        String key = metaData.getColumnLabel(i);
                        String value = resultSet.getString(key);
                        jsonObj.put(key, value);
                    }
                    array.add(jsonObj);
                }

            } catch (SQLException e) {
                logger.error("get mysql data is fail !");
            }finally {
                close(conn);
            }
        }
        return array;
    }

    public void close(Connection conn) {
        if (null != conn) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("close mysql connection is fail !");
            }
        }
    }
}
