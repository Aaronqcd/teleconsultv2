package com.va.removeconsult.util;
import java.security.MessageDigest;
import java.util.Random;
public class MD5Utils {


    /*** 
     * MD5加码 生成32位md5码 
     */
    public static String string2MD5(String inStr){
        MessageDigest md5 = null;
        try{
            md5 = MessageDigest.getInstance("MD5");
        }catch (Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++){
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();

    }

    /**
     * 加密解密算法 执行一次加密，两次解密 
    */
    public static String convertMD5(String inStr){  

        char[] a = inStr.toCharArray();  
        for (int i = 0; i < a.length; i++){  
            a[i] = (char) (a[i] ^ 't');  
        }  
        String s = new String(a);  
        return s;  

    }  

    // 测试主函数  
    public static void main(String args[]) {
        String s = new String("123456");
        System.out.println("原始：" + s);  
        System.out.println("MD5后：" + string2MD5(s));  
        System.out.println("加密的：" + convertMD5(s));  
        System.out.println("解密的：" + convertMD5(convertMD5(s)));  
        System.out.println((int)((Math.random()*9+1)*100000));
    }

    /**
     * 复杂随机密码串
     *
     * @param  len 密码长度
     * @return string 随机串
     * */
    public static String getPsw(int len) {
        // 1、定义基本字符串baseStr和出参password
        String password = null;
        String baseStr = "0123456789abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^*_+|<>.";
        boolean flag = false;
        // 2、使用循环来判断是否是正确的密码
        while (!flag) {
            // 密码重置
            password = "";
            // 个数计数
            int a = 0, b = 0, c = 0, d = 0;
            for (int i = 0; i < len; i++) {
                int rand = (int) (Math.random() * baseStr.length());
                password += baseStr.charAt(rand);
                if (0 <= rand && rand < 10) {
                    a++;
                }
                if (10 <= rand && rand < 36) {
                    b++;
                }
                if (36 <= rand && rand < 62) {
                    c++;
                }
                if (62 <= rand && rand < baseStr.length()) {
                    d++;
                }
                if (a * b * c * d != 0) {
                    break;
                }
            }
            // 是否是正确的密码（4类中仅一类为0，其他不为0）
            flag = (a * b * c != 0 && d == 0) || (a * b * d != 0 && c == 0) || (a * c * d != 0 && b == 0)
                    || (b * c * d != 0 && a == 0);
        }
        return password;
    }
}