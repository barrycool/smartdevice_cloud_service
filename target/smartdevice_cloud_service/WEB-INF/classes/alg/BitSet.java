package alg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yuanyuanfan on 2018/1/18.
 */
public class BitSet {
    private final int N = 8;
    private final int MAX = 10000;
    private byte[] flags = new byte[MAX];

    public  int check(int idx) {
        int i = idx/N;
        int j = idx%N;
        return ((1<<j) & flags[i]);
    }

    public void set(int idx) {
        int i = idx/N;
        int j = idx%N;
        flags[i] = (byte)((1<<j) | flags[i]);
    }

    public void set(Set<Integer> intSet) {
        for(int v : intSet){
            set(v);
        }
    }

    public void test(){
        String s = "";
        int idx = 0;
        for(int i=0; i<MAX; ++i){
            for(int j=0; j<N; ++j){
                if(((1<<j) & flags[i])>0){
                    s += idx+",";
                    idx = 0;
                }
                idx++;
            }
        }
        System.out.println(s);
    }


    public static void main(String[] argc) {

        Set<String> strSet = new HashSet<String>();
        Set<Integer> intSet = new HashSet<Integer>();
        List<Integer> intList = new ArrayList<Integer>();
        List<String> strList = new ArrayList<String>();

        for(int i=0; i<80000; ++i){
            if(i%10000==0){
                strSet.add(i+"");
                intSet.add(i);
            }
            intList.add(i);
            strList.add(i+"");
        }


        long start  = System.currentTimeMillis();
        for(String s : strList){
            if(!strSet.contains(s)){
                continue;
            }
        }
        long end  = System.currentTimeMillis();

        BitSet bitSet = new BitSet();
        bitSet.set(intSet);
        long startV2  = System.currentTimeMillis();
        for(int i : intList){
            if(bitSet.check(i)>0){
                continue;
            }
        }
        bitSet.test();
        long endV2  = System.currentTimeMillis();
        System.out.println((end-start) + "\t" + (endV2-startV2));
    }
}
