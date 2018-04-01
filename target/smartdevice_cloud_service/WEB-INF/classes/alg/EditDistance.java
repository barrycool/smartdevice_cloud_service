package alg;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by yuanyuanfan on 2018/1/11.
 */
public class EditDistance {

    public static int getDistance(String strA, String strB){
        int distance=-1;
        if(null==strA||null==strB||strA.length()==0||strB.length()==0){
            return distance;
        }
        if (strA.equals(strB)) {
            return 0;
        }
        int lengthA=strA.length();
        int lengthB=strB.length();
        int length=Math.max(lengthA,lengthB);
        int array[][]=new int[length+1][length+1];
        for(int i=0;i<=length;i++){
            array[i][0]=i;

        }
        for(int j=0;j<=length;j++){
            array[0][j]=j;
        }
        for(int i=1;i<=lengthA;i++){
            for(int j=1;j<=lengthB;j++){
                array[i][j]=min(array[i-1][j]+1,
                        array[i][j-1]+1,
                        array[i-1][j-1]+(strA.charAt(i-1)==strB.charAt(j-1)?0:1));
            }
        }

        return array[lengthA][lengthB];

    }
    public static int  min(int a,int b, int c){
        return Math.min(Math.min(a,b),c);
    }


    public static Set<String> wordSet = new HashSet<String>();
    public static Set<String> wordSetV1 = new HashSet<String>();
    public static Set<String> wordSetV2 = new HashSet<String>();


    public static void load(String file, int i){
        try {
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                switch (i){
                    case 1:
                        wordSetV1.add(line);
                        break;
                    default:
                        wordSetV2.add(line);
                        break;
                }
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static void load(String file){
        try {
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                wordSet.add(line);
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }


    public static void run(String out){
        Map<String, Set<String>> wordMap = new HashMap<String, Set<String>>();
        for(String word : wordSetV1){
            if(word.length()<2){
                continue;
            }
            for(int i=2; i<word.length(); ++i){
                String sub = word.substring(0, i);
                if(wordSet.contains(sub) && getDistance(word, sub)<2){
                    Set<String> set = wordMap.get(sub);
                    if(set==null){
                        set = new HashSet<String>();
                    }
                    set.add(word);
                    wordMap.put(sub, set);
                }
            }
        }
        for(String word : wordSetV2){
            if(word.length()<2){
                continue;
            }
            for(int i=2; i<word.length(); ++i){
                String sub = word.substring(0, i);
                if(wordSet.contains(sub) && getDistance(word, sub)<2){
                    Set<String> set = wordMap.get(sub);
                    if(set==null){
                        set = new HashSet<String>();
                    }
                    set.add(word);
                    wordMap.put(sub, set);
                }
            }
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(out));
            for(Map.Entry<String, Set<String>> entry : wordMap.entrySet()){
                String k = entry.getKey();
                Set<String> v = entry.getValue();

                String tmp = "";
                for(String word : v){
                    tmp += word + ",";
                }
                bw.write(k + "," + tmp + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int calcHMDistance(byte[] doc1, byte[] doc2){

        int dist = 0;
        for(int i=0; i<doc1.length; ++i){
        }

        return dist;
    }

    public static void main(String[] argc){

//        String in = "/Users/yuanyuanfan/temp/dict/citys.txt";
//        EditDistance.load(in, 1);
//        String inV2 = "/Users/yuanyuanfan/temp/dict/segment.txt";
//        EditDistance.load(inV2);
////        EditDistance.load(inV2, 2);
//
//        String out = "/Users/yuanyuanfan/temp/dict/subword.txt";
//        EditDistance.run(out);
        System.out.println(Integer.toBinaryString(12));
    }

}
