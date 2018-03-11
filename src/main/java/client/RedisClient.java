package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanyuanyuan-iri on 2017/5/8.
 */
public class RedisClient {
    private static final Logger logger = LoggerFactory.getLogger(RedisClient.class);

    private JedisPool pool;

    /**
     * @param host
     * @param auth
     * @param port
     */
    private RedisClient(String host, String auth, int port) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(300);
        config.setMaxIdle(3);
        config.setMaxWaitMillis(100);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);
        pool = new JedisPool(config, host, port, 1000, auth);
    }

    private static RedisClient jedisClient = null;

    public static RedisClient getJedisClient(String host, String auth, int port) {
        if (jedisClient == null) {
            return new RedisClient(host, auth, port);
        }
        return jedisClient;
    }

    /**
     * 种草记录浏览日志
     *
     * @param key
     * @param value
     */
    public void put(String key, String value, int overtime) {
        Jedis jd = pool.getResource();
        try {
            if (overtime > 0) {
                jd.setex(key, overtime, value);
            } else {
                jd.set(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("jedis set {} failed: {}", key, e.toString());
        } finally {
            if (jd != null) {
                jd.close();
            }
        }
    }


    public void putEX(String key, String value, int overtime) throws Exception {
        Jedis jd = null;
        try {
            jd = pool.getResource();
            if (overtime > 0) {
                jd.setex(key, overtime, value);
            } else {
                jd.set(key, value);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (jd != null) {
                jd.close();
            }
        }
    }

    public void put(byte[] key, byte[] value, int overtime) {
        Jedis jd = pool.getResource();
        try {
            if (overtime > 0) {
                jd.setex(key, overtime, value);
            } else {
                jd.set(key, value);
            }
        } catch (Exception e) {
            logger.error("jedis set {} failed: {}", key, e.toString());
        } finally {
            if (jd != null) {
                jd.close();
            }
        }
    }


    public void putEX(byte[] key, byte[] value, int overtime) throws Exception {
        Jedis jd = null;
        try {
            jd = pool.getResource();
            if (overtime > 0) {
                jd.setex(key, overtime, value);
            } else {
                jd.set(key, value);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (jd != null) {
                jd.close();
            }
        }

    }

    /**
     * 获取数据
     *
     * @param key
     * @return value
     */
    public String get(String key) {
        String value = null;
        Jedis jd = null;
        try {
            jd = pool.getResource();
            value = jd.get(key);
        } catch (Exception e) {
            logger.error("jedis get {} failed: {}", key, e.toString());
        } finally {
            if (jd != null) {
                jd.close();
            }
        }
        return value;
    }


    public String getEX(String key) throws Exception {
        String value = null;
        Jedis jd = null;

        try {
            jd = pool.getResource();
            value = jd.get(key);
        } catch (Exception e) {
            throw e;
        } finally {
            if (jd != null) {
                jd.close();
            }
        }
        return value;
    }

    public byte[] get(byte[] key) {
        byte[] v = null;
        Jedis jd = null;

        if (null == key || key.length == 0) {
            return "".getBytes();
        }

        try {
            jd = pool.getResource();
            v = jd.get(key);
        } catch (Exception e) {
            logger.error("jedis get {} failed: {}", key, e.toString());
        } finally {
            if (jd != null) {
                jd.close();
            }
        }
        return v;
    }


    public byte[] getEX(byte[] key) throws Exception {
        byte[] v = null;
        Jedis jd = null;

        if (null == key || key.length == 0) {
            return "".getBytes();
        }
        try {
            jd = pool.getResource();
            v = jd.get(key);
        } catch (Exception e) {
            throw e;
        } finally {
            if (jd != null) {
                jd.close();
            }
        }
        return v;
    }

    /**
     * 获取数据
     *
     * @param keys
     * @return value
     */
    public List<String> mget(String[] keys) {
        List<String> values = null;
        Jedis jd = pool.getResource();
        try {
            values = jd.mget(keys);
        } catch (Exception e) {
            logger.error("jedis mget {} failed: {}", keys.toString(), e.toString());
        } finally {
            if (jd != null) {
                jd.close();
            }
        }
        return values;
    }

    /**
     * 获取数据hash map类型
     *
     * @param key
     * @return value
     */
    public Map<String, String> hscan(String key) {
        ScanResult<Map.Entry<String, String>> value = null;
        List<Map.Entry<String, String>> vs = null;
        Map<String, String> ret = new HashMap<String, String>();
        String cursor = "0";
        Jedis jd = null;
        try {
            jd = pool.getResource();
            while (true) {

                value = jd.hscan(key, cursor);
                cursor = value.getStringCursor();
                vs = value.getResult();
                for (Map.Entry<String, String> e : vs) {
                    String k = e.getKey();
                    String v = e.getValue();
                    ret.put(k, v);
                }
                if (cursor.equals("0")) {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("jedis hscan {} failed: {}", key, e.toString());
        } finally {
            if (jd != null) {
                jd.close();
            }
        }
        return ret;
    }


    public Map<String, String> hscanEX(String key) throws Exception {
        ScanResult<Map.Entry<String, String>> value = null;
        List<Map.Entry<String, String>> vs = null;
        Map<String, String> ret = new HashMap<String, String>();
        String cursor = "0";
        Jedis jd = null;

        try {
            jd = pool.getResource();
            while (true) {
                value = jd.hscan(key, cursor);
                cursor = value.getStringCursor();
                vs = value.getResult();
                for (Map.Entry<String, String> e : vs) {
                    String k = e.getKey();
                    String v = e.getValue();
                    ret.put(k, v);
                }
                if (cursor.equals("0")) {
                    break;
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (jd != null) {
                jd.close();
            }
        }

        return ret;
    }


    /**
     * 获取数据hash map类型
     *
     * @param key
     * @return value
     */
    public Map<String, String> hgetall(String key) {
        Map<String, String> value = null;
        Jedis jd = pool.getResource();
        try {
            value = jd.hgetAll(key);
        } catch (Exception e) {
            logger.error("jedis hgetall {} failed: {}", key, e.toString());
        } finally {
            jd.close();
        }
        return value;
    }

    /**
     * 删除指定key
     *
     * @param key
     */
    public void delKey(byte[] key) {
        Jedis jd = pool.getResource();
        try {
            Long status = jd.del(key);
        } catch (Exception e) {
            logger.error("jedis del {} failed: {}", key, e.toString());
        } finally {
            jd.close();
        }

    }


}
