package com.nowcoder.nowcommunity.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    /**
     * 生成不带-的随机字符串
     * @return
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }


    // MD5加密
    // 简单密码加密(容易被破解)：hello -> abc123def456
    // 简单密码解密 + 随机字符串拼串:hello + 3e4a8
    public static String md5(String key){
        // 不对非法字符串进行机密
        if(StringUtils.isBlank(key)){
            return null;
        }

        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 注册页面信息检查
     * @param code 状态码
     * @param msgName 信息名称
     * @param msg 信息内容
     * @return
     */
    public static String getRegisterCheck(int code,String msgName, String msg){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put(msgName,msg);
        return json.toJSONString();
    }

    /**
     * 密码强度检查工具类
     * @param passwordStr
     * @return
     */
    public static String checkPassword(String passwordStr) {
        if (passwordStr != null && !"".equals(passwordStr) && (passwordStr.length() < 6 || passwordStr.length() > 20)) {
            return "密码为 6-20 位字母、数字或英文字符!";
        }

        // Z = 字母       S = 数字           T = 特殊字符
        String regexZ = "[A-Za-z]+";
        String regexS = "^\\d+$";
        String regexT = "[~!@#$%^&*.]+";
        String regexZT = "[a-zA-Z~!@#$%^&*.]+";
        String regexZS = "[0-9A-Za-z]+";
        String regexST = "[\\d~!@#$%^&*.]*";
        String regexZST = "[\\da-zA-Z~!@#$%^&*.]+";

        if (passwordStr.matches(regexZ)){
            return "密码强度弱，建议密码为数字、字母、字符组合";
        }
        if (passwordStr.matches(regexS)){
            return "密码强度弱，建议密码为数字、字母、字符组合";
        }
        if (passwordStr.matches(regexT)){
            return "密码强度弱，建议密码为数字、字母、字符组合";
        }
        if (passwordStr.matches(regexZT)){
            return "密码强度中，建议密码为数字、字母、字符组合";
        }
        if (passwordStr.matches(regexZS)){
            return "密码强度中，建议密码为数字、字母、字符组合";
        }
        if (passwordStr.matches(regexST)){
            return "密码强度中，建议密码为数字、字母、字符组合";
        }
        if (passwordStr.matches(regexZST)) {
            return "密码强度：强";
        }
        return "不知道是啥";
    }

    /**
     * 返回显示信息的json字符串
     * @param code 状态码
     *             200：success
     *             404: 未知异常错误
     *             400 : 常规错误
     * @param msg 信息
     * @param map
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String,Object> map){
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map != null && !map.isEmpty()){
            for(String key : map.keySet()){
                json.put(key,map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code,String msg){
        return getJSONString(code,msg,null);
    }

    /**
     * 判断是否为允许的上传文件类型,true表示允许
     */
    public static boolean checkFile(String fileName) {
        //设置允许上传文件类型
        String suffixList = "jpg,png,bmp,jpeg";
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 文件后缀名不能包含空格
        if(StringUtils.isBlank(suffix)){
            return false;
        }

        if (suffixList.contains(suffix.toLowerCase())) {
            return true;
        }
        return false;
    }
}
