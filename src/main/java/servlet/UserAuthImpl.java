package servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.MailSender;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fanyuanyuan on 2018/3/17.
 */
public class UserAuthImpl {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthImpl.class);


    private ConcurrentHashMap<String, String> sendUserCodeMap = new ConcurrentHashMap<>();

    public boolean sendCode(String userMailAddr){
        String code = MailSender.sendEmail(userMailAddr);
        if(code!=null){
            sendUserCodeMap.put(userMailAddr, code);
        }else{
            logger.error("获取验证码失败,addr={}", userMailAddr);
            return false;
        }
        return true;
    }

    public boolean verify(String userMailAddr, String code){
        String hist = sendUserCodeMap.get(userMailAddr);
        if(hist==null || code==null || !hist.equals(code)){
            return false;
        }
        return true;
    }



}
