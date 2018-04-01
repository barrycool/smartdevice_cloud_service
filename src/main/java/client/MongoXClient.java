package client;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ConstKey;

import java.net.InetAddress;
import java.util.*;

import static com.mongodb.client.model.Projections.*;
import static java.util.Arrays.asList;

/**
 * Created by fanyuanyuan-iri on 2017/5/8.
 */
public class MongoXClient {
    private static final Logger logger = LoggerFactory.getLogger(MongoXClient.class);

    private String host;
    private Integer port;

    private String user;
    private String passwd;

    private MongoClient client;

    private final String userTable = "user";
    private final String userDB = "user_info";

    private HashMap<String, IDC> idcMap = new HashMap();

    public IDC of(String k) {
        IDC idc = idcMap.get(k);
        if (idc == null) {
            idc = IDC.Other;
        }
        return idc;
    }

    public enum IDC {
        China,
        Korea,
        America,
        Other
    }

    private static final MongoXClient instance = new MongoXClient();

    public static MongoXClient getInstance() {
        return instance;
    }

    private MongoXClient() {
        init();
    }

    public void init() {
        idcMap.put("china", IDC.China);
        idcMap.put("korea", IDC.Korea);
        idcMap.put("america", IDC.America);
        idcMap.put("other", IDC.Other);

        getConfig(getIDC());

        createMongoClientV2();
    }


    public void createMongoClient() {
        String uri = "mongodb://" + user + ":" + passwd + "@" + host + ":" + port + "/admin";
        logger.info("mongoURI={}", uri);

        MongoClientOptions.Builder build = new MongoClientOptions.Builder();
        build.connectionsPerHost(100);
        build.threadsAllowedToBlockForConnectionMultiplier(50);
        build.maxWaitTime(1000 * 60 * 2);
        build.connectTimeout(1000 * 60 * 1);

        client = new MongoClient(new MongoClientURI(uri, build));
        if (client == null) {
            logger.error("create MongoXClient failed");
        }
    }

    public  void createMongoClientV2(){
        try {
            MongoCredential credential = MongoCredential.createCredential(user, userDB, passwd.toCharArray());
            ServerAddress serverAddress;
            serverAddress = new ServerAddress(host, port);
            List<ServerAddress> addrs = new ArrayList<ServerAddress>();
            addrs.add(serverAddress);
            List<MongoCredential> credentials = new ArrayList<MongoCredential>();
            credentials.add(credential);
            client = new MongoClient(addrs, credentials);
            if (client == null) {
                logger.error("create MongoXClient failed");
            }
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }


    public void close() {
        client.close();
    }

    public MongoDatabase getDataBase(String dbName) {
        if (client == null) {
            createMongoClient();
        }
        return client.getDatabase(dbName);
    }


    public String getDBName() {
       return userDB;
    }

    private MongoCursor<Document> findIter(String m2, String k, Object v, final String... fieldNames) {
        BasicDBObject queryObj = new BasicDBObject();
        queryObj.append(k, v);

        String dbName = getDBName();
        if(dbName==null){
            return null;
        }
        MongoCollection collect = getDataBase(dbName).getCollection(userTable);
        FindIterable<Document> findIterable =
                collect.find(queryObj).projection(
                        fields(
                                include(fieldNames), excludeId()));

        return findIterable.iterator();
    }

    public MongoCursor<Document> findIterByM2(String m2, final String... fieldNames) {
        BasicDBObject queryObj = new BasicDBObject();
        queryObj.append(ConstKey.userId, m2);
        String dbName = getDBName();
        if(dbName==null){
            return null;
        }
        MongoCollection collect = getDataBase(dbName).getCollection(userTable);
        FindIterable<Document> findIterable =
                collect.find(queryObj).projection(
                        fields(
                                include(fieldNames), excludeId()));

        return findIterable.iterator();
    }

    public void updateDoc(JSONObject updateInfo){

        String userId = updateInfo.getString(ConstKey.userId);

        BasicDBObject queryObj = new BasicDBObject();
        queryObj.append(ConstKey.userId, userId);
        String dbName = getDBName();
        if(dbName==null){
            return ;
        }

        Document filter = new Document();
        filter.append(ConstKey.userId, userId);

        Document updateObj = new Document();
        updateObj.append(ConstKey.userId, userId);

        MongoCollection collect = getDataBase(dbName).getCollection(userTable);
        FindIterable<Document> findIterable = collect.find(queryObj);
        MongoCursor<Document> cur = findIterable.iterator();
        while (cur.hasNext()) {
            Document doc = cur.next();
            Set<String> kset = doc.keySet();
            for (String key : kset) {
                String v = updateInfo.getString(key);
                if(v!=null){
                    updateObj.put(key, v);
                }else{
                    updateObj.put(key, doc.get(key));
                }
            }
        }
//        UpdateResult result = collect.replaceOne(filter, updateObj);
        collect.findOneAndUpdate(filter, updateObj);
//        logger.info("matched count = {}", result.getMatchedCount());
//        System.out.println("matched count = " +  result.getMatchedCount());
    }

    public void insertDoc(Document updateObj){
        String dbName = getDBName();
        MongoCollection collect = getDataBase(dbName).getCollection(userTable);
        try{
            collect.insertOne(updateObj);
        }catch (Exception e){
            return;
        }
    }

    /**
     * 获取本机所在机房
     *
     * @param
     * @return void
     */
    private IDC getIDC() {
        InetAddress iAddr = null;
        String addr = "";
        try {
            iAddr = InetAddress.getLocalHost();
            addr = iAddr.getHostName().toString();
        } catch (Exception e) {

        }

        String idc = "other";
        String[] parts = addr.split("\\.");
        if (parts.length > 2) {
            idc = parts[2];
        }
        logger.info("parts.size={},parts={}", parts.length, parts);
        logger.info("iddr={}", iAddr);
        logger.info("addr={}", addr);
        logger.info("idc={}", idc);
        return of(idc);
    }



    /**
     * 根据机房，索引类型选取对应的配置
     *
     * @param idc
     * @return
     */
    private void getConfig(IDC idc) {
        switch (idc) {
            case China:
                host = "www.ai-keys.com";
                port = 27017;
                user = "pangzi";
                passwd = "pangzi123";
                break;
            case Korea:
                host = "www.ai-keys.com";
                port = 27017;
                user = "pangzi";
                passwd = "pangzi123";
                break;
            case America:
                host = "www.ai-keys.com";
                port = 27017;
                user = "pangzi";
                passwd = "pangzi123";
                break;
            default:
                host = "www.ai-keys.com";
                port = 27017;
                user = "pangzi";
                passwd = "pangzi123";
                break;

        }
    }


    public JSONObject getUserInfo(String userId) {
        if (userId == null || userId.length() != 32) {
            return null;
        }
        JSONObject jsonObject = get(userId, ConstKey.userEmail, ConstKey.userName, ConstKey.userPasswd);

        return jsonObject;
    }

    public JSONObject get(String m2, String... fieldNames){
        JSONObject jsonResult = new JSONObject();

        MongoCursor<Document> cur = findIterByM2(m2, fieldNames);
        if(cur==null){
            return jsonResult;
        }
        while (cur.hasNext()) {
            Document doc = cur.next();
            insert(doc, jsonResult, fieldNames);
        }

        return jsonResult;
    }

    private void insert(Document doc, JSONObject jsonResult, String... fieldNames) {
        List<String> names = asList(fieldNames);
        for(String name : names){
            String info = doc.getString(name);
            if (info != null && info.length() != 0) {
                jsonResult.put(name, info);
            }
        }
    }



    public static void main(String[] argc){
        MongoXClient mongoXClient = MongoXClient.getInstance();
        JSONObject jsonUserInfo = new JSONObject();
        jsonUserInfo.put(ConstKey.userName, "1xx2xx3");
        jsonUserInfo.put(ConstKey.userPasswd, "123xxx");
        jsonUserInfo.put(ConstKey.userPhone, "sxs");
        jsonUserInfo.put(ConstKey.userEmail, "ssxxx");
        jsonUserInfo.put(ConstKey.RegisterCode, "sxs");
        jsonUserInfo.put("_id", "e5f485f9cd9363ab82d35f6b1855dc71");
        jsonUserInfo.put("user_id", "e5f485f9cd9363ab82d35f6b1855dc71");

        Document userDoc = new Document();
        for (Map.Entry<String, Object> entry : jsonUserInfo.entrySet()) {
//            userDoc.put(entry.getKey(), entry.getValue().toString());
            String key = entry.getKey();
            String value = (String)entry.getValue();
            userDoc.put(key, value);
        }
        mongoXClient.insertDoc(userDoc);
    }
}
