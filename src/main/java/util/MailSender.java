package util;

import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fanyuanyuan on 2018/3/17.
 */
public class MailSender {
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

    private static String getRandom() {
        String num = "";
        for (int i = 0 ; i < 6 ; i ++) {
            num = num + String.valueOf((int) Math.floor(Math.random() * 9 + 1));
        }
        return num;
    }

    public static String sendEmail(String emailaddress){
        try {
            HtmlEmail email = new HtmlEmail();
            email.setHostName("smtp.qq.com");//需要修改，126邮箱为smtp.126.com,163邮箱为163.smtp.com，QQ为smtp.qq.com
            email.setCharset("UTF-8");
            email.addTo(emailaddress);

            email.setFrom("1750076014@qq.com", "天马行空的邮箱验证");
            email.setSSLOnConnect(true);
            email.setAuthentication("1750076014@qq.com", "rzebwsaxyjzsbjib");//此处填写邮箱地址和客户端授权码

            String code = getRandom();
            email.setSubject("来自天马行空的邮箱验证码");//此处填写邮件名，邮件名可任意填写
            email.setMsg("尊敬的先生/女士:\n    您好,您本次的验证码是:" + code);//此处填写邮件内容

            email.send();
            return code;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
