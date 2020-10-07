package com.va.removeconsult.clouddisk.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import com.va.removeconsult.clouddisk.model.Node;
import com.va.removeconsult.clouddisk.pojo.VideoTranscodeThread;
import com.va.removeconsult.dao.NodeDao;

import ws.schild.jave.AudioAttributes;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.VideoAttributes;


@Component
public class VideoTranscodeUtil {

	private EncodingAttributes ea;
	@Resource
	private FileBlockUtil fbu;
	@Resource
	private NodeDao nm;

	public static Map<String, VideoTranscodeThread> videoTranscodeThreads = new HashMap<>();

	{
		AudioAttributes audio = new AudioAttributes();
		audio.setCodec("libmp3lame");
		audio.setBitRate(128000);
		audio.setChannels(2);
		audio.setSamplingRate(44100);
		VideoAttributes video = new VideoAttributes();
		video.setCodec("libx264");
		ea = new EncodingAttributes();
		ea.setFormat("MP4");
		ea.setVideoAttributes(video);
		ea.setAudioAttributes(audio);
	}

	
	public String getTranscodeProcess(String fId) throws Exception {
		synchronized (videoTranscodeThreads) {
			VideoTranscodeThread vtt = videoTranscodeThreads.get(fId);
			Node n = nm.queryById(fId);
			File f = fbu.getFileFromBlocks(n);
			if (vtt != null) {
				if ("FIN".equals(vtt.getProgress())) {
					String md5 = DigestUtils.md5Hex(new FileInputStream(f));
					if (md5.equals(vtt.getMd5()) && new File(ConfigureReader.instance().getTemporaryfilePath(), vtt.getOutputFileName()).isFile()) {
						return vtt.getProgress();
					}else{
						videoTranscodeThreads.remove(fId);
					}
				} else {
					return vtt.getProgress();
				}
			}
			String suffix = n.getFileName().substring(n.getFileName().lastIndexOf(".") + 1).toLowerCase();
			switch (suffix) {
			case "mp4":
			case "webm":
			case "mov":
			case "avi":
			case "wmv":
			case "mkv":
			case "flv":
				break;
			default:
				throw new IllegalArgumentException();
			}
			videoTranscodeThreads.put(fId, new VideoTranscodeThread(f, ea));
			return "0.0";
		}
	}

}
