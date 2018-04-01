package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanyuanyuan-iri on 2017/5/4.
 */

/**
 * 简单的kv字典
 * key只支持String和Integer类型
 * @param <K>
 */
public class HashDict<K> {
    private Map<K, Node> hashIndex = new HashMap<K, Node>();
    private String dictName;
    public HashDict(String file){
        dictName = file;
    }



    public boolean save(){
        try{
            FileOutputStream out = new FileOutputStream(dictName);
            for(Map.Entry<K, Node > entry : hashIndex.entrySet()){
                K k = entry.getKey();
                Node v = entry.getValue();
                String str = k  + Global.TAB + v.id + Global.TAB + v.weight +  "\n";
                out.write(str.getBytes(Global.ENCODE));
            }
            out.close();
        } catch (Exception e){
        }

        return true;
    }

    public void add(K k, int weight){
        if(hashIndex.containsKey(k)){
            return;
        }
        int id = hashIndex.size();
        hashIndex.put(k, new Node(id, weight));
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
                hashIndex.put((K)items[0], new Node(Integer.valueOf(items[1]), Integer.valueOf(items[2])));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public Node get(K k){
        return hashIndex.get(k);
    }
}
