package asr.realtime.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import asr.realtime.config.AsrBaseConfig;
import asr.realtime.config.AsrInternalConfig;
import asr.realtime.config.AsrPersonalConfig;
import asr.realtime.http.synchronize.RequestSender;
import asr.realtime.model.enums.ResponseEncode;
import asr.realtime.model.enums.SdkRole;
import asr.realtime.model.enums.VoiceFormat;
import asr.realtime.model.response.VoiceResponse;
import asr.realtime.utils.CrossOrigin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 实时语音识别
 * @author BMKJ
 */
@Controller
@CrossOrigin
@RequestMapping("/ASR")
public class ASRController {
    RequestSender requestSender = new RequestSender();
    VoiceResponse voiceResponse = new VoiceResponse();

    static {
        initBaseParameters();
    }

    @RequestMapping(value = "/realtime", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String speechRecognize(@RequestParam(value = "file",required = false)MultipartFile file, HttpServletRequest request) throws IOException {
        voiceResponse = requestSender.sendFromBytes(file.getBytes());
        return printReponse(voiceResponse);
    }


    private String printReponse(VoiceResponse voiceResponse) {
        if (voiceResponse != null && voiceResponse.getCode() == 0) {
            JSONObject jsonObject = JSONObject.parseObject(voiceResponse.getOriginalText());
            if (jsonObject.get("result_list") != null){
                JSONArray resultList  = JSONArray.parseArray(jsonObject.get("result_list").toString());
                return JSONObject.parseObject(String.valueOf(resultList.get(0))).get("voice_text_str").toString();
            }else {
                return "";
            }
        } else {
            return "识别错误请联系网站管理！！！";
        }
    }

    /**
     * 初始化基础参数, 请将下面的参数值配置成你自己的值。
     * <p>
     * 参数获取方法可参考： <a href="https://cloud.tencent.com/document/product/441/6203">签名鉴权 获取签名所需信息</a>
     */
    private static void initBaseParameters() {
        // optional，根据自身需求配置值
        AsrInternalConfig.setSdkRole(SdkRole.VAD); // VAD版用户请务必赋值为 SdkRole.VAD
        AsrPersonalConfig.responseEncode = ResponseEncode.UTF_8;
        AsrPersonalConfig.engineModelType = "8k_zh";
        AsrPersonalConfig.voiceFormat = VoiceFormat.wav;
        AsrBaseConfig.PRINT_CUT_REQUEST = true; // 是否打印中间的每个分片请求到控制台
        AsrBaseConfig.PRINT_CUT_RESPONSE = true; // 是否打印中间的每个分片请求的结果到控制台
    }
}
