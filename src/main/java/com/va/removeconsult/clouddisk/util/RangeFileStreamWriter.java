package com.va.removeconsult.clouddisk.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class RangeFileStreamWriter {

	
	protected void writeRangeFileStream(HttpServletRequest request, HttpServletResponse response, File fo, String fname,
			String contentType) {
		long fileLength = fo.length();
		long startOffset = 0; 
		boolean hasEnd = false;
		long endOffset = 0; 
		long contentLength = 0; 
		String rangeBytes = "";
		
		response.setContentType(contentType);
		
		response.setCharacterEncoding("UTF-8");
		if (request.getHeader("User-Agent").toLowerCase().indexOf("safari") >= 0) {
			response.setHeader("Content-Disposition",
					"attachment; filename=\""
							+ new String(fname.getBytes(Charset.forName("UTF-8")), Charset.forName("ISO-8859-1"))
							+ "\"; filename*=utf-8''" + EncodeUtil.getFileNameByUTF8(fname));
		} else {
			response.setHeader("Content-Disposition", "attachment; filename=\"" + EncodeUtil.getFileNameByUTF8(fname)
					+ "\"; filename*=utf-8''" + EncodeUtil.getFileNameByUTF8(fname));
		}
		
		response.setHeader("Accept-Ranges", "bytes");
		
		if (request.getHeader("Range") != null && request.getHeader("Range").startsWith("bytes=")) {
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
			rangeBytes = request.getHeader("Range").replaceAll("bytes=", "");
			if (rangeBytes.endsWith("-")) {
				
				startOffset = Long.parseLong(rangeBytes.substring(0, rangeBytes.indexOf('-')).trim());
				
				contentLength = fileLength - startOffset;
			} else {
				hasEnd = true;
				startOffset = Long.parseLong(rangeBytes.substring(0, rangeBytes.indexOf('-')).trim());
				endOffset = Long.parseLong(rangeBytes.substring(rangeBytes.indexOf('-') + 1).trim());
				
				contentLength = endOffset - startOffset + 1;
			}
		} else { 
			contentLength = fileLength; 
		}
		response.setHeader("Content-Length", "" + contentLength);
		if (startOffset != 0) {
			
			String contentRange;
			if (!hasEnd) {
				contentRange = new StringBuffer("bytes ").append("" + startOffset).append("-")
						.append("" + (fileLength - 1)).append("/").append("" + fileLength).toString();
				response.setHeader("Content-Range", contentRange);
			} else {
				contentRange = new StringBuffer("bytes ").append(rangeBytes).append("/").append("" + fileLength)
						.toString();
			}
			response.setHeader("Content-Range", contentRange);
		}
		
		byte[] buf = new byte[ConfigureReader.instance().getBuffSize()];
		
		try (RandomAccessFile raf = new RandomAccessFile(fo, "r")) {
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			raf.seek(startOffset);
			if (!hasEnd) {
				
				int n = 0;
				while ((n = raf.read(buf)) != -1) {
					out.write(buf, 0, n);
				}
			} else {
				
				int n = 0;
				long readLength = 0;
				while (readLength < contentLength) {
					n = raf.read(buf);
					readLength += n;
					out.write(buf, 0, n);
				}
			}
			out.flush();
			out.close();
		} catch (IOException ex) {
			
		}
	}
}
