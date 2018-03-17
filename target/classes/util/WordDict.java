package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanyuanfan on 2018/1/17.
 */
public class WordDict<K> {

    private static final Logger logger = LoggerFactory.getLogger(WordDict.class);


    private Map<K, Integer> hashIndex = new HashMap<K, Integer>();
    private String dictName;
    public WordDict(String file){
        dictName = file;
    }

    public boolean save(){
        try{
            FileOutputStream out = new FileOutputStream(dictName);
            for(Map.Entry<K, Integer > entry : hashIndex.entrySet()){
                K k = entry.getKey();
                Integer v = entry.getValue();
                String str = k  + Global.TAB + v +  "\n";
                out.write(str.getBytes(Global.ENCODE));
            }
            out.close();
        } catch (Exception e){
            logger.error("open {} failed, error={}", dictName, e);
        }

        return true;
    }

    public int add(K k){
        Integer id = hashIndex.get(k);
        if(id!=null){
            return id;
        }
        id = hashIndex.size();
        hashIndex.put(k, id);
        return id;
    }

    public boolean load(){
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(dictName), Global.ENCODE));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String[] items = line.split(Global.TAB);
                if(items.length!=3){
                    continue;
                }
                hashIndex.put((K)items[0], Integer.valueOf(items[1]));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("open {} failed, error={}", dictName, e);
        }
        return true;
    }

    public Integer get(K k){
        return hashIndex.get(k);
    }
}
