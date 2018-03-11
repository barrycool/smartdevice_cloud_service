package servlet;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanyuanfan on 2018/1/23.
 */
public class QueryResult {

    private int flag = 0;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getSimDocId() {
        return simDocId;
    }

    public void setSimDocId(int simDocId) {
        this.simDocId = simDocId;
    }

    public int simDocId = -1;


    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    private List<String> tags = new ArrayList<String>();

    public String toJSONString(){
        JSONObject jsob = new JSONObject();
        jsob.put("flag", flag);
        jsob.put("tags", tags.toString());
        jsob.put("simId", simDocId);

        return jsob.toJSONString();
    }



}
