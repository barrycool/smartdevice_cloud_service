package alg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanyuanfan on 2017/8/15.
 */
public class ModuleDict {
    private static final Logger logger = LoggerFactory.getLogger(ModuleDict.class);
    private Map<Long, IPC> ipcMap = new HashMap<Long, IPC>();

    private ModuleDict(){
        loadIp2City();
    }

    public class C implements  Comparable<C>{
        public long start=0;
        public long end=0;
        public String city;
        public int compareTo(C o) {
            int d = (int)(this.start-o.start);
            return d;
        }
        public C(long s, long e, String c){
            this.start = s;
            this.end = e;
            this.city = c;
        }
    }

    public class IPC{
        public ArrayList<C> cArrayList = new ArrayList<C>();
        public int  binSearch(int start, int end, long ipx){
            while(start<=end) {
                int mid = (start+end)/2;
                C c = cArrayList.get(mid);
                if(ipx>=c.start && ipx<=c.end){
                    return mid;
                }else if (ipx < c.start) {
                    end = mid-1;
                } else {
                    start = mid+1;
                }
            }
            return -1;
        }

        public  C search(long ipx){
            int idx = binSearch(0, cArrayList.size()-1, ipx);
            if(idx!=-1){
                return cArrayList.get(idx);
            }
            return null;
        }
    }

    public static Long ip2LongEx(String ip) {
        Long ips = 0L;
        if (ip == null) {
            return ips;
        }

        try {
            String[] numbers = ip.split("\\.");
            if (numbers.length != 4) {
                return ips;
            }
            ips += Long.valueOf(numbers[0]) << 24;
            ips += Long.valueOf(numbers[1]) << 16;
            ips += Long.valueOf(numbers[2]) << 8;
            ips += Long.valueOf(numbers[3]);
        } catch (Exception e) {
            logger.warn("ip format failed, ip={}", ip);
        }
        return ips;

    }

    public String ip2city(String ip){
        Long ipx = ip2LongEx(ip);
        IPC ipc = ipcMap.get(ipx>>24);
        if(ipc==null){
            return "null";
        }
        C c = ipc.search(ipx);
        if(c!=null){
            return c.city;
        }
        return "null";
    }


    public boolean loadIp2City(){
        try {
//            String filename = "/Users/yuanyuanfan/temp/ip_standard.txt";
            String filename = "/Users/yuanyuanfan/Documents/doc/ip_standard.txt";
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String[] items = line.split("\t");
                Long start = Long.valueOf(items[0]);
                Long end = Long.valueOf(items[1]);
                IPC ipc = ipcMap.get(start>>24);
                if(ipc==null){
                    ipc = new IPC();
                }
                ipc.cArrayList.add(new C(start, end, items[2]+"_"+items[3]+"_"+items[4]));
                ipcMap.put(start>>24, ipc);
            }
            for(Map.Entry<Long, IPC> entry : ipcMap.entrySet()){
                Collections.sort(entry.getValue().cArrayList);
            }
            reader.close();
        } catch (Exception e) {
        }

        return true;

    }

    private static ModuleDict moduleDict = new ModuleDict();

    public static ModuleDict getModuleDict(){
        return moduleDict;
    }

    public static void main(String[] argc){
        ModuleDict moduleDict = ModuleDict.getModuleDict();
        System.out.println(moduleDict.ip2city("123.59.118.51"));
    }

}
