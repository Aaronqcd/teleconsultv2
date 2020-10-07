package web.util;

import com.alibaba.fastjson.JSONObject;
import com.va.removeconsult.clouddisk.util.EncodeUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录接口rsa验签
 *
 * @author longmao
 * @date 2020-05-25
 */
public class LoginRsaValid {

    public static Map<String, String> valid(String data) {
        try {
            Map<String, String> map = new HashMap<>();
            //得到验签原文
            String rsaContent = URLDecoder.decode(data, "utf-8");
            rsaContent = data.replace(" ", "+").replace("%2F", "/");
            //验签
            String rsaDecode = RsaUtil.decryptByPriKey(rsaContent);
            JSONObject jsonRsa = JSONObject.parseObject(rsaDecode);
            map.put("password", jsonRsa.getString("password"));
            map.put("remember", jsonRsa.getString("remember"));
            map.put("type", jsonRsa.getString("type"));
            map.put("code", jsonRsa.getString("code"));
            map.put("user", jsonRsa.getString("user"));
            map.put("username", jsonRsa.getString("username"));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
