package com.va.removeconsult.clouddisk.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.va.removeconsult.clouddisk.enumeration.AccountAuth;
import com.va.removeconsult.clouddisk.model.Node;
import com.va.removeconsult.clouddisk.pojo.VideoTranscodeThread;
import com.va.removeconsult.clouddisk.service.ResourceService;
import com.va.removeconsult.clouddisk.util.ConfigureReader;
import com.va.removeconsult.clouddisk.util.Docx2PDFUtil;
import com.va.removeconsult.clouddisk.util.FileBlockUtil;
import com.va.removeconsult.clouddisk.util.LogUtil;
import com.va.removeconsult.clouddisk.util.Txt2PDFUtil;
import com.va.removeconsult.clouddisk.util.VideoTranscodeUtil;
import com.va.removeconsult.dao.NodeDao;
import com.va.removeconsult.util.Utils;


@Service
public class ResourceServiceImpl implements ResourceService {

	@Resource
	private NodeDao nm;
	@Resource
	private FileBlockUtil fbu;
	@Resource
	private LogUtil lu;
	@Resource
	private Docx2PDFUtil d2pu;
	@Resource
	private Txt2PDFUtil t2pu;
	@Resource
	private VideoTranscodeUtil vtu;

	
	@Override
	public void getResource(HttpServletRequest request, HttpServletResponse response) {
		
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES)) {
			String fid = request.getParameter("fid");
			if (fid != null) {
				Node n = nm.queryById(fid);
				if (n != null) {
					File file = fbu.getFileFromBlocks(n);
					String suffix = "";
					if (n.getFileName().indexOf(".") >= 0) {
						suffix = n.getFileName().substring(n.getFileName().lastIndexOf(".")).trim().toLowerCase();
					}
					String contentType = "application/octet-stream";
					switch (suffix) {
					case ".mp4":
					case ".webm":
					case ".mov":
					case ".avi":
					case ".wmv":
					case ".mkv":
					case ".flv":
						contentType = "video/mp4";
						synchronized (VideoTranscodeUtil.videoTranscodeThreads) {
							VideoTranscodeThread vtt = VideoTranscodeUtil.videoTranscodeThreads.get(fid);
							if (vtt != null) {
								File f = new File(ConfigureReader.instance().getTemporaryfilePath(),
										vtt.getOutputFileName());
								if (f.isFile() && vtt.getProgress().equals("FIN")) {
									file = f;
								} else {
									try {
										response.sendError(500);
									} catch (IOException e) {
									}
									return;
								}
							}
						}
						break;
					case ".mp3":
						contentType = "audio/mpeg";
						break;
					case ".ogg":
						contentType = "audio/ogg";
						break;
					default:
						break;
					}
					sendResource(file, n.getFileName(), contentType, request, response);
					if (request.getHeader("Range") == null) {
						this.lu.writeDownloadFileEvent(request, n);
					}
					return;
				}
			}
		}
		try {
			
			response.sendError(404);
		} catch (IOException e) {
			
		}
	}

	
	private void sendResource(File resource, String fname, String contentType, HttpServletRequest request,
			HttpServletResponse response) {
		try (RandomAccessFile randomFile = new RandomAccessFile(resource, "r")) {
			long contentLength = randomFile.length();
			String range = request.getHeader("Range");
			long start = 0, end = 0;
			if (range != null && range.startsWith("bytes=")) {
				String[] values = range.split("=")[1].split("-");
				start = Long.parseLong(values[0]);
				if (values.length > 1) {
					end = Long.parseLong(values[1]);
				}
			}
			long requestSize = 0;
			if (end != 0 && end > start) {
				requestSize = end - start + 1;
			} else {
				requestSize = Long.MAX_VALUE;
			}
			byte[] buffer = new byte[ConfigureReader.instance().getBuffSize()];
			response.setContentType(contentType);
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("ETag", fname);
			response.setHeader("Last-Modified", new Date().toString());
			
			if (range == null) {
				response.setHeader("Content-length", contentLength + "");
			} else {
				
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
				long requestStart = 0, requestEnd = 0;
				String[] ranges = range.split("=");
				if (ranges.length > 1) {
					String[] rangeDatas = ranges[1].split("-");
					requestStart = Long.parseLong(rangeDatas[0]);
					if (rangeDatas.length > 1) {
						requestEnd = Long.parseLong(rangeDatas[1]);
					}
				}
				long length = 0;
				if (requestEnd > 0) {
					length = requestEnd - requestStart + 1;
					response.setHeader("Content-length", "" + length);
					response.setHeader("Content-Range",
							"bytes " + requestStart + "-" + requestEnd + "/" + contentLength);
				} else {
					length = contentLength - requestStart;
					response.setHeader("Content-length", "" + length);
					response.setHeader("Content-Range",
							"bytes " + requestStart + "-" + (contentLength - 1) + "/" + contentLength);
				}
			}
			ServletOutputStream out = response.getOutputStream();
			long needSize = requestSize;
			randomFile.seek(start);
			while (needSize > 0) {
				int len = randomFile.read(buffer);
				if (needSize < buffer.length) {
					out.write(buffer, 0, (int) needSize);
				} else {
					out.write(buffer, 0, len);
					if (len < buffer.length) {
						break;
					}
				}
				needSize -= buffer.length;
			}
			out.close();
		} catch (Exception e) {

		}
	}

	
	@Override
	public void getWordView(String fileId, HttpServletRequest request, HttpServletResponse response) {
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		
		if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES)) {
			if (fileId != null) {
				Node n = nm.queryById(fileId);
				if (n != null) {
					File file = fbu.getFileFromBlocks(n);
					
					String suffix = "";
					if (n.getFileName().indexOf(".") >= 0) {
						suffix = n.getFileName().substring(n.getFileName().lastIndexOf(".")).trim().toLowerCase();
					}
					if (".docx".equals(suffix)) {
						String contentType = "application/octet-stream";
						response.setContentType(contentType);
						
						try {
							d2pu.convertPdf(new FileInputStream(file), response.getOutputStream());
							return;
						} catch (Exception e) {
						}
					}
				}
			}
		}
		try {
			response.sendError(500);
		} catch (Exception e1) {
		}
	}

	
	@Override
	public void getTxtView(String fileId, HttpServletRequest request, HttpServletResponse response) {
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		
		if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES)) {
			if (fileId != null) {
				Node n = nm.queryById(fileId);
				if (n != null) {
					File file = fbu.getFileFromBlocks(n);
					
					String suffix = "";
					if (n.getFileName().indexOf(".") >= 0) {
						suffix = n.getFileName().substring(n.getFileName().lastIndexOf(".")).trim().toLowerCase();
					}
					if (".txt".equals(suffix)) {
						String contentType = "application/octet-stream";
						response.setContentType(contentType);
						
						try {
							t2pu.convertPdf(file, response.getOutputStream());

							return;
						} catch (Exception e) {
							e.printStackTrace();
							Utils.error(e.getMessage(), e);
						}
					}
				}
			}
		}
		try {
			response.sendError(500);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void getPdfView(String fileId, HttpServletRequest request, HttpServletResponse response) {
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		
		if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES)) {
			if (fileId != null) {
				Node n = nm.queryById(fileId);
				if (n != null) {
					File file = fbu.getFileFromBlocks(n);
					
					String suffix = "";
					if (n.getFileName().indexOf(".") >= 0) {
						suffix = n.getFileName().substring(n.getFileName().lastIndexOf(".")).trim().toLowerCase();
					}
					if (".pdf".equals(suffix)) {
						String contentType = "application/octet-stream";
						response.setContentType(contentType);
						
						try {

							OutputStream fout = response.getOutputStream();
							FileInputStream fin =  new FileInputStream(file);
							byte[] buffer = new byte[1024];
							while(fin.read(buffer)!=-1) {
								fout.write(buffer);
							}
							fin.close();
							
							return;
						} catch (Exception e) {
							e.printStackTrace();
							Utils.error(e.getMessage(), e);
						}
					}
				}
			}
		}
		try {
			response.sendError(500);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public String getVideoTranscodeStatus(HttpServletRequest request) {
		final String account = (String) request.getSession().getAttribute("ACCOUNT");
		if (ConfigureReader.instance().authorized(account, AccountAuth.DOWNLOAD_FILES)) {
			String fId = request.getParameter("fileId");
			if (fId != null) {
				try {
					return vtu.getTranscodeProcess(fId);
				} catch (Exception e) {
					e.printStackTrace();
					lu.writeException(e);
				}
			}
		}
		return "ERROR";
	}

}
