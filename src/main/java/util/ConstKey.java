package util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanyuanyuan on 2018/3/20.
 */
public class ConstKey {
    //rediskey前缀:用户设备开关状态的
    public static final String redis_key_prefix_user_device_connect_status = "user_device_connnect_status:";
    public static final int user_device_connect_status_over_time = 0;


    public static final String redis_key_prefix_user_device_status = "user_device_status:";
    public static final int user_device_status_over_time = 3600;

    //rediskey前缀:用户设备列表
    public static final String redis_key_prefix_user_device_list = "user_device_list:";
    public static final int user_device_list_over_time = 0;

    //rediskey前缀:用户token对应关系
    public static final String redis_key_prefix_user_token = "user_token:";
    public static final int user_token_over_time = 3600*24*2;


    //rediskey前缀:user_id对应用户信息
    public static final String redis_key_prefix_user_id = "user_id:";
    public static final int user_id_over_time = 0;


    //rediskey前缀:注册码
    public static final String redis_key_prefix_register_code = "user_register_code:";
    public static final int register_code_over_time = 60*30;

    //rediskey前缀:登陆用户名对应用户信息
    public static final String redis_key_prefix_user_login_name = "user_login_name:";
    public static final int user_login_name_over_time = 0;


    public static final String nameSpace = "name_space";
    public static final String name = "name";
    public static final String token = "token";
    public static final String properties = "properties";
    public static final String value = "value";
    public static final String devices = "devices";

    public static final String  userId = "userId";
    public static final String  deviceId = "deviceId";
    public static final String  deviceType = "deviceType";
    public static final String  friendlyName = "friendlyName";
    public static final String  manufactureName = "manufacturerName";
    public static final String  modelName = "modelName";
    public static final String  userAccount = "userAccount";
    public static final String  code = "code";
    public static final String  msg = "msg";
    public static final String  result = "result";
    public static final String  userName = "userName";
    public static final String  userPasswd = "passwd";
    public static final String  userPhone = "userPhone";
    public static final String  userEmail= "userEmail";
    public static final String  RegisterCode= "RegisterCode";
    public static final String  refreshToken= "refreshToken";
    public static final String  expiresDate= "expiresDate";
    public static final String  userInfo= "userInfo";
    public static final String  loginName= "loginName";


    public static final String  onLine= "1";
    public static final String  offLine= "0";
    public static final String  Failed= "Failed";
    public static final String  OK= "OK";














}
