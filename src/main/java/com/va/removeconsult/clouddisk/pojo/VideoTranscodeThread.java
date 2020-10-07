package com.va.removeconsult.clouddisk.pojo;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import com.va.removeconsult.clouddisk.util.ConfigureReader;

import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderProgressListener;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;


public class VideoTranscodeThread {

	private String md5;
	private String progress;
	private Encoder encoder;
	private String outputFileName;

	public VideoTranscodeThread(File f, EncodingAttributes ea) throws Exception {
		
		md5 = DigestUtils.md5Hex(new FileInputStream(f));
		progress = "0.0";
		MultimediaObject mo = new MultimediaObject(f);
		encoder = new Encoder();
		Thread t = new Thread(() -> {
			try {
				outputFileName="video_"+UUID.randomUUID().toString()+".mp4";
				encoder.encode(mo, new File(ConfigureReader.instance().getTemporaryfilePath(), outputFileName),
						ea, new EncoderProgressListener() {
							public void sourceInfo(MultimediaInfo arg0) {
							}

							public void progress(int arg0) {
								progress = (arg0 / 10.00) + "";
							}

							public void message(String arg0) {
							}
						});
				progress = "FIN";
			} catch (Exception e) {

			}
		});
		t.start();
	}

	public String getMd5() {
		return md5;
	}

	public String getProgress() {
		return progress;
	}

	public String getOutputFileName() {
		return outputFileName;
	}
	
	
	public void abort() {
		if(encoder!=null) {
			encoder.abortEncoding();
		}
		File f=new File(ConfigureReader.instance().getTemporaryfilePath(),outputFileName);
		if(f.exists()) {
			f.delete();
		}
	}

}
