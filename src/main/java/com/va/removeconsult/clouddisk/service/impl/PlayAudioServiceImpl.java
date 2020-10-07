package com.va.removeconsult.clouddisk.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.va.removeconsult.clouddisk.enumeration.AccountAuth;
import com.va.removeconsult.clouddisk.model.Node;
import com.va.removeconsult.clouddisk.pojo.AudioInfoList;
import com.va.removeconsult.clouddisk.service.PlayAudioService;
import com.va.removeconsult.clouddisk.util.AudioInfoUtil;
import com.va.removeconsult.clouddisk.util.ConfigureReader;
import com.va.removeconsult.dao.NodeDao;

@Service
public class PlayAudioServiceImpl implements PlayAudioService
{
    @Resource
    private NodeDao fm;
    @Resource
    private AudioInfoUtil aiu;
    @Resource
    private Gson gson;
    
    private AudioInfoList foundAudios(final HttpServletRequest request) {
        final String fileId = request.getParameter("fileId");
        if (fileId != null && fileId.length() > 0) {
            final String account = (String)request.getSession().getAttribute("ACCOUNT");
            if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES)) {
                final List<Node> blocks = (List<Node>)this.fm.queryBySomeFolder(fileId);
                return this.aiu.transformToAudioInfoList(blocks, fileId);
            }
        }
        return null;
    }
    
    
    public String getAudioInfoListByJson(final HttpServletRequest request) {
        final AudioInfoList ail = this.foundAudios(request);
        if (ail != null) {
            return gson.toJson((Object)ail);
        }
        return "ERROR";
    }
}
