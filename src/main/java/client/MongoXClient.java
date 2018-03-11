package client;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.*;

import static com.mongodb.client.model.Projections.*;

/**
 * Created by fanyuanyuan-iri on 2017/5/8.
 */
public class MongoXClient {
    private static final Logger logger = LoggerFactory.getLogger(MongoXClient.class);

    private String host;
    private Integer port;

    private String user = "mongo";
    private String auth;

    private MongoClient client;

    private final String userProfTable = "zs_user_info";

    private HashMap<String, IDC> idcMap = new HashMap();

    public IDC of(String k) {
        IDC idc = idcMap.get(k);
        if (idc == null) {
            idc = IDC.test;
        }
        return idc;
    }

    public enum IDC {
        bjcc,
        shbt,
        bjyt,
        test
    }

    private static final MongoXClient instance = new MongoXClient();

    public static MongoXClient getInstance() {
        return instance;
    }

    private MongoXClient() {
        init();
    }

    public void init() {
        idcMap.put("bjcc", IDC.bjcc);
        idcMap.put("shbt", IDC.shbt);
        idcMap.put("bjyt", IDC.bjyt);
        idcMap.put("test", IDC.test);

        getConfig(getIDC());

        createMongoClient();
    }


    public void createMongoClient() {
        String uri = "mongodb://" + user + ":" + auth + "@" + host + ":" + port + "/admin";
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

    public void close() {
        client.close();
    }

    public MongoDatabase getDataBase(String dbName) {
        if (client == null) {
            createMongoClient();
        }
        return client.getDatabase(dbName);
    }


    private String getDBName(String m2) {
        if (m2 == null || m2.length() != 32) {
            return null;
        }
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.append("_id", m2);
        String dbPrefix = "zs_user_m2_";
        String dbName = dbPrefix + m2.charAt(m2.length() - 1);
        return dbName;
    }

    private MongoCursor<Document> findIter(String m2, String k, Object v, final String... fieldNames) {
        BasicDBObject queryObj = new BasicDBObject();
        queryObj.append(k, v);

        String dbName = getDBName(m2);
        if(dbName==null){
            return null;
        }
        MongoCollection collect = getDataBase(dbName).getCollection(userProfTable);
        FindIterable<Document> findIterable =
                collect.find(queryObj).projection(
                        fields(
                                include(fieldNames), excludeId()));

        return findIterable.iterator();
    }

    public MongoCursor<Document> findIterByM2(String m2, final String... fieldNames) {
        BasicDBObject queryObj = new BasicDBObject();
        queryObj.append("_id", m2);
        String dbName = getDBName(m2);
        if(dbName==null){
            return null;
        }
        MongoCollection collect = getDataBase(dbName).getCollection(userProfTable);
        FindIterable<Document> findIterable =
                collect.find(queryObj).projection(
                        fields(
                                include(fieldNames), excludeId()));

        return findIterable.iterator();
    }

    private void updateOne(String m2, String field){
        BasicDBObject queryObj = new BasicDBObject();
        queryObj.append("_id", m2);
        String dbName = getDBName(m2);
        if(dbName==null){
            return ;
        }

        Document filter = new Document();
        filter.append("_id", m2);

        Document updateObj = new Document();
        updateObj.append("_id", m2);

        MongoCollection collect = getDataBase(dbName).getCollection(userProfTable);
        FindIterable<Document> findIterable = collect.find(queryObj);
        MongoCursor<Document> cur = findIterable.iterator();
        while (cur.hasNext()) {
            Document doc = cur.next();
            Set<String> kset = doc.keySet();
            for (String key : kset) {
                if(field.equals(key)){
                    continue;
                }
                updateObj.put(key, doc.get(key));
            }
        }

        System.out.println(updateObj.toString());
        UpdateResult result = collect.replaceOne(filter, updateObj);
//        logger.info("matched count = {}", result.getMatchedCount());
        System.out.println("matched count = " +  result.getMatchedCount());
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

        String idc = "test";
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
            case bjcc:

                break;
            case shbt:

                break;
            case bjyt:

                break;
            default:

        }
    }

    public static void main(String[] argc){
        MongoXClient mongoXClient = MongoXClient.getInstance();
    }
}
