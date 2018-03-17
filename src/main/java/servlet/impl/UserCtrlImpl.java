package servlet;

import client.MongoXClient;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.MailSender;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

/**
 * Created by fanyuanyuan on 2018/3/17.
 */
public class UserCtrlImpl {

    private static final Logger logger = LoggerFactory.getLogger(UserCtrlImpl.class);


    private ConcurrentHashMap<String, String> sendUserCodeMap = new ConcurrentHashMap<>();

    private MongoXClient mongoXClient = MongoXClient.getInstance();


    public boolean sendCode(String userMailAddr) {
        String code = MailSender.sendEmail(userMailAddr);
        if (code != null) {
            sendUserCodeMap.put(userMailAddr, code);
        } else {
            logger.error("获取验证码失败,addr={}", userMailAddr);
            return false;
        }
        return true;
    }

    public boolean verify(String userMailAddr, String code) {
        String hist = sendUserCodeMap.get(userMailAddr);
        if (hist == null || code == null || !hist.equals(code)) {
            return false;
        }
        return true;
    }


    public JSONObject get(String m2, String... fieldNames) {
        JSONObject jsonResult = new JSONObject();

        MongoCursor<Document> cur = mongoXClient.findIterByM2(m2, fieldNames);
        if (cur == null) {
            return jsonResult;
        }
        while (cur.hasNext()) {
            Document doc = cur.next();
            insertQueryResult(doc, jsonResult, fieldNames);
        }

        return jsonResult;
    }

    private void insertQueryResult(Document doc, JSONObject jsonResult, String... fieldNames) {
        List<String> names = asList(fieldNames);
        for (String name : names) {
            String info = doc.getString(name);
            if (info != null && info.length() != 0) {
                jsonResult.put(name, info);
            }
        }
    }

    public void insertNewUser(JSONObject jsonUserInfo){
        Document userDoc = new Document();

        for(Map.Entry<String, Object> entry : jsonUserInfo.entrySet()){
            userDoc.put(entry.getKey(), entry.getValue().toString());
        }

        mongoXClient.insertDoc(userDoc);
    }

    public void updateUserInfo(JSONObject jsonUserInfo){
        mongoXClient.updateDoc(jsonUserInfo);
    }



    public static void main(String[] argc){
        UserCtrlImpl userCtrl = new UserCtrlImpl();
    }


}
