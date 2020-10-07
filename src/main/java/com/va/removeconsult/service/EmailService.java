package com.va.removeconsult.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.va.removeconsult.dto.MailModel;

@Service
public class EmailService {
    private static Logger logger = LoggerFactory.getLogger(EmailService.class);
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired 
    private MeetingService meetingService;
    @Value("${mail.sender}")
    private String senderEmail;
    
    @SuppressWarnings("rawtypes")
    public String sendShareMeetingInfoEmail(String meetingTitle,String meetingTime,String meetingAttends,
            String meetingAbsentee,String meetingConclusion){
        MailModel mail = new MailModel();
        //主题
        mail.setSubject(meetingTitle+"(会诊纪要)"); 
        //发送人
        mail.setEmailFrom(senderEmail);
        //附件
//        Map<String, String> attachments = new HashMap<String, String>();
//        attachments.put("清单.xlsx",filePath+"清单.xlsx");
//        mail.setAttachments(attachments);
        List<Map>attendsList = new ArrayList<>();
        List<Map>absenteeList = new ArrayList<>();
        if(StringUtils.isNotEmpty(meetingAttends)){
            attendsList=meetingService.getAttends(meetingAttends);
        }
        if(StringUtils.isNotEmpty(meetingAbsentee)){
            absenteeList=meetingService.getAttends(meetingAbsentee);
        }
        //与会人名称
        String[] attendsNames = new String[attendsList.size()];
        String[] attendEmails = new String[attendsList.size()];
        for (int i = 0; i < attendsList.size(); i++) {
            attendsNames[i] = (String)attendsList.get(i).get("name");
            attendEmails[i] = (String)attendsList.get(i).get("email");
        }
        String attendsNamesStr = StringUtils.join(attendsNames, ",");
        String attendEmailsStr = StringUtils.join(attendEmails, ";");
        //缺席人
        String[] absenteeNames = new String[absenteeList.size()];
        for (int i = 0; i < absenteeList.size(); i++) {
            absenteeNames[i] = (String)absenteeList.get(i).get("name");
        }
        String absenteeNamesStr = StringUtils.join(absenteeNames, ",");

        //内容
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body><b>你好,以下是会诊纪要</b>：<br />");
        builder.append("<b>会诊主题:</b>"+meetingTitle+"<br />");
        builder.append("<b>会诊时间：</b>"+meetingTime+"<br />");
        builder.append("<b>与会人：</b>"+attendsNamesStr+"<br />");
        builder.append("<b>缺席人：</b>"+absenteeNamesStr+"<br />");
        builder.append("<b>会诊结论：</b><br />" +"<p>"+meetingConclusion+"</p>");
        builder.append("</body></html>");
        String content = builder.toString();
        
        mail.setToEmails(attendEmailsStr);
        mail.setContent(content);
        //发送
        try{
            sendEmail(mail);
        } catch(Exception e){
            e.printStackTrace();
            return "false";
        }
        
        return "true";
    }
    
    /**
     * 
     * 发送邮件
     * @param mail
     */
    public void sendEmail(MailModel mail)  {
        
        // 建立邮件消息
        MimeMessage message = javaMailSender.createMimeMessage();
        
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            // 设置发件人邮箱
            if (mail.getEmailFrom()!=null) {
                messageHelper.setFrom(mail.getEmailFrom());
            } else {
                throw new RuntimeException("发件人邮箱不得为空！");
            }
            
            // 设置收件人邮箱
            if (mail.getToEmails()!=null) {
                String[] toEmailArray = mail.getToEmails().split(";");
                List<String> toEmailList = new ArrayList<String>();
                if (null == toEmailArray || toEmailArray.length <= 0) {
                    throw new RuntimeException("收件人邮箱不得为空！");
                } else {
                    for (String s : toEmailArray) {
                        if (s!=null&&!s.equals("")) {
                            toEmailList.add(s);
                        }
                    }
                    if (null == toEmailList || toEmailList.size() <= 0) {
                        throw new RuntimeException("收件人邮箱不得为空！");
                    } else {
                        toEmailArray = new String[toEmailList.size()];
                        for (int i = 0; i < toEmailList.size(); i++) {
                            toEmailArray[i] = toEmailList.get(i);
                        }
                    }
                }
                messageHelper.setTo(toEmailArray);
            } else {
                throw new RuntimeException("收件人邮箱不得为空！");
            }
            
            // 邮件主题
            if (mail.getSubject()!=null) {
                messageHelper.setSubject(mail.getSubject());
            } else {
                throw new RuntimeException("邮箱主题不得为空！");
            }
            
            // true 表示启动HTML格式的邮件
            messageHelper.setText(mail.getContent(), true);
            
            // 添加图片
            if (null != mail.getPictures()) {
                for (Iterator<Map.Entry<String, String>> it = mail.getPictures().entrySet()
                        .iterator(); it.hasNext();) {
                    Map.Entry<String, String> entry = it.next();
                    String cid = entry.getKey();
                    String filePath = entry.getValue();
                    if (null == cid || null == filePath) {
                        throw new RuntimeException("请确认每张图片的ID和图片地址是否齐全！");
                    }
                    
                    File file = new File(filePath);
                    if (!file.exists()) {
                        throw new RuntimeException("图片" + filePath + "不存在！");
                    }
                    
                    FileSystemResource img = new FileSystemResource(file);
                    messageHelper.addInline(cid, img);
                }
            }
            
            // 添加附件
            if (null != mail.getAttachments()) {
                for (Iterator<Map.Entry<String, String>> it = mail.getAttachments()
                        .entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, String> entry = it.next();
                    String cid = entry.getKey();
                    String filePath = entry.getValue();
                    if (null == cid || null == filePath) {
                        throw new RuntimeException("请确认每个附件的ID和地址是否齐全！");
                    }
                    
                    File file = new File(filePath);
                    if (!file.exists()) {
                        throw new RuntimeException("附件" + filePath + "不存在！");
                    }
                    
                    FileSystemResource fileResource = new FileSystemResource(file);
                    messageHelper.addAttachment(cid, fileResource);
                }
            }
            messageHelper.setSentDate(new Date());
            // 发送邮件
            javaMailSender.send(message);
            logger.info("------------发送邮件完成----------");
            
        } catch (MessagingException e) {
            
            e.printStackTrace();
        }
    }
}
