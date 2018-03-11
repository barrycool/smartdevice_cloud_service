package alg;

/**
 * Created by yuanyuanfan on 2018/1/16.
 */
public class BitMap {

    public static int calcHMDistance(byte[] src, byte[] des) {
        int dist = 0;
        int len = src.length;
        for (int i = 0; i < len; ++i) {
            for(int j=0; j<8; ++j){
                if((((1<<j)&src[i])^((1<<j)&des[i]))>0){
                    dist++;
                }
            }
        }
        return dist;
    }

    public static int calcHMDistance(int[] src, int[] des) {
        int dist = 0;
        int len = src.length;
        for (int i = 0; i < len; ++i) {
            if(src[i]!=des[i]){
                dist++;
            }
        }
        return dist;
    }

    public static byte[] int2byte(int id){
        byte[] ba = new byte[4];
        int j = 0;
        for(int i=3; i>=0; i--){
            ba[j++] = (byte)((id >> i*8) & 0xFF);
        }
        return ba;
    }

    public static byte[] int2byte(int[] ids){
        int len = ids.length/8;
        byte[] ba = new byte[len];
        for(int i=0; i<len; i++){
            int pos = i*8;
            byte b = (byte)((ids[pos])|(ids[pos+1]<<1)|(ids[pos+2]<<2)|(ids[pos+3]<<3)|
                    (ids[pos+4]<<4)|(ids[pos+5]<<5)|(ids[pos+6]<<6)|(ids[pos+7]<<7));
            ba[i] = b;
        }
        return ba;
    }

    public static int[] set(byte b, int w) {
        int[] ia = new int[8];
        int j = 0;
        for(int i=7; i>=0; i--){
            ia[j++] = ((b >> i) & 1)*w;
//            ia[j++] = ((b >> i) & 1);
        }
        return ia;
    }

    public static int[] set(int id, int w) {
        int[] ia = new int[32];
        byte[] ba =  int2byte(id);
        int i = 0;
        for(byte b : ba){
            int[] t = set(b, w);
            System.arraycopy(t, 0, ia, (i++)*8, t.length);
        }
        return ia;
    }

    public static String i2s(int[]x){
        String s = "";
        for(int i : x){
            s += i ;
        }
        return s;
    }
    public static void main(String[] argc){
        int[] x = {0,0,0,1,1,0,0,0,1,1,1,1,1,1,1,0,1};
        int[] y = {1,0,0,1,1,0,0,0,1,1,1,1,1,0,0,0,1};
        byte[] ba = BitMap.int2byte(x);
        byte[] bb = BitMap.int2byte(y);
        System.out.println(BitMap.calcHMDistance(ba,bb));
        String s = "";
        for(byte b : ba ){
            for(int i=0; i<8; i++){
                if(((1<<i)&b)>0){
                    s += "1";
                }else{
                    s += "0";
                }
            }
        }
        System.out.println(s);
        int[] xx = {76,17,15,14,13};
        int[] yy = {76,17,15,14,13};
        for(int aa : xx){
            System.out.println(i2s(BitMap.set(aa, 2)));
        }
    }
}
