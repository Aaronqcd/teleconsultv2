package com.va.removeconsult.util;

/**
 * @description: 密码强度校验
 * 1) 密码控制只能输入字母、数字、特殊符号(~!@#$%^&*()_+[]{}|\;:'",./<>?)
 * 2) 长度 6-20 位，必须包括大小写字母、数字、特殊符号中的2种
 * 3) 密码不能包含用户名信息
 * @author: wyx
 * @time: 2020年1月13日13:50:45
 **/

public class PassWordCheck {
    //数字
    public static final String REG_NUMBER = ".*\\d+.*";
    //小写字母
    public static final String REG_UPPERCASE = ".*[A-Z]+.*";
    //大写字母
    public static final String REG_LOWERCASE = ".*[a-z]+.*";
    //特殊符号(~!@#$%^&*()_+|<>,.?/:;'[]{}\)
    public static final String REG_SYMBOL = ".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*";

    public static String checkPasswordRule(String password){

        //密码为空及长度大于6位小于20位判断
        if (password == null || password.length() <8 || password.length()>18) return "密码长度不符合规则";

        int i = 0;

        if (password.matches(PassWordCheck.REG_NUMBER)) i++;
        if (password.matches(PassWordCheck.REG_LOWERCASE))i++;
        if (password.matches(PassWordCheck.REG_UPPERCASE)) i++;
        if (password.matches(PassWordCheck.REG_SYMBOL)) i++;


        if (i  < 3 )  return "密码过于简单";

        return "密码符合规则";
        
        
    }

}
