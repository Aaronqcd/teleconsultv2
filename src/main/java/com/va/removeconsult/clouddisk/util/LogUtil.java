package com.va.removeconsult.clouddisk.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.va.removeconsult.clouddisk.enumeration.LogLevel;
import com.va.removeconsult.clouddisk.model.Folder;
import com.va.removeconsult.clouddisk.model.Node;
import com.va.removeconsult.dao.FolderDao;
import com.va.removeconsult.dao.NodeDao;

@Component
public class LogUtil {

	@Resource
	private FolderUtil fu;
	@Resource
	private FolderDao fm;
	@Resource
	private NodeDao fim;

	private String sep = "";
	private String logs = "";

	public LogUtil() {
		sep = File.separator;
		logs = ConfigureReader.instance().getPath() + sep + "logs";
		File l = new File(logs);
		if (!l.exists()) {
			l.mkdir();
		} else {
			if (!l.isDirectory()) {
				l.delete();
				l.mkdir();
			}
		}
	}

	
	public void writeException(Exception e) {
		if (ConfigureReader.instance().inspectLogLevel(LogLevel.Runtime_Exception)) {
			writeToLog("Exception", "[" + e + "]:" + e.getMessage());
		}
	}

	
	public void writeCreateFolderEvent(HttpServletRequest request, Folder f) {
		if (ConfigureReader.instance().inspectLogLevel(LogLevel.Event)) {
			String account = (String) request.getSession().getAttribute("ACCOUNT");
			if (account == null || account.length() == 0) {
				account = "Anonymous";
			}
			String a = account;
			Thread t = new Thread(() -> {
				List<Folder> l = fu.getParentList(f.getFolderId());
				String pl = new String();
				for (Folder i : l) {
					pl = pl + i.getFolderName() + "/";
				}
				String content = ">ACCOUNT [" + a + "]\r\n>OPERATE [Create new folder]\r\n>PATH [" + pl + "]\r\n>NAME ["
						+ f.getFolderName() + "], CONSTRAINT [" + f.getFolderConstraint() + "]";
				writeToLog("Event", content);
			});
			t.start();
		}
	}

	
	public void writeRenameFolderEvent(HttpServletRequest request, Folder f, String newName, String newConstraint) {
		if (ConfigureReader.instance().inspectLogLevel(LogLevel.Event)) {
			String account = (String) request.getSession().getAttribute("ACCOUNT");
			if (account == null || account.length() == 0) {
				account = "Anonymous";
			}
			String a = account;
			Thread t = new Thread(() -> {
				List<Folder> l = fu.getParentList(f.getFolderId());
				String pl = new String();
				for (Folder i : l) {
					pl = pl + i.getFolderName() + "/";
				}
				String content = ">ACCOUNT [" + a + "]\r\n>OPERATE [Edit folder]\r\n>PATH [" + pl + "]\r\n>NAME ["
						+ f.getFolderName() + "]->[" + newName + "], CONSTRAINT [" + f.getFolderConstraint() + "]->["
						+ newConstraint + "]";
				writeToLog("Event", content);
			});
			t.start();
		}
	}

	
	public void writeDeleteFolderEvent(HttpServletRequest request, Folder f, List<Folder> l) {
		if (ConfigureReader.instance().inspectLogLevel(LogLevel.Event)) {
			String account = (String) request.getSession().getAttribute("ACCOUNT");
			if (account == null || account.length() == 0) {
				account = "Anonymous";
			}
			String a = account;
			Thread t = new Thread(() -> {
				String pl = new String();
				for (Folder i : l) {
					pl = pl + i.getFolderName() + "/";
				}
				String content = ">ACCOUNT [" + a + "]\r\n>OPERATE [Delete folder]\r\n>PATH [" + pl + "]\r\n>NAME ["
						+ f.getFolderName() + "]";
				writeToLog("Event", content);
			});
			t.start();
		}
	}

	
	public void writeDeleteFileEvent(HttpServletRequest request, Node f) {
		if (ConfigureReader.instance().inspectLogLevel(LogLevel.Event)) {
			String account = (String) request.getSession().getAttribute("ACCOUNT");
			if (account == null || account.length() == 0) {
				account = "Anonymous";
			}
			String a = account;
			Thread t = new Thread(() -> {
				Folder folder = fm.queryById(f.getFileParentFolder());
				List<Folder> l = fu.getParentList(folder.getFolderId());
				String pl = new String();
				for (Folder i : l) {
					pl = pl + i.getFolderName() + "/";
				}
				String content = ">ACCOUNT [" + a + "]\r\n>OPERATE [Delete file]\r\n>PATH [" + pl
						+ folder.getFolderName() + "]\r\n>NAME [" + f.getFileName() + "]";
				writeToLog("Event", content);
			});
			t.start();
		}
	}

	
	public void writeUploadFileEvent(Node f, String account) {
		if (ConfigureReader.instance().inspectLogLevel(LogLevel.Event)) {
			if (account == null || account.length() == 0) {
				account = "Anonymous";
			}
			String a = account;
			Thread t = new Thread(() -> {
				Folder folder = fm.queryById(f.getFileParentFolder());
				List<Folder> l = fu.getParentList(folder.getFolderId());
				String pl = new String();
				for (Folder i : l) {
					pl = pl + i.getFolderName() + "/";
				}
				String content = ">ACCOUNT [" + a + "]\r\n>OPERATE [Upload file]\r\n>PATH [" + pl
						+ folder.getFolderName() + "]\r\n>NAME [" + f.getFileName() + "]";
				writeToLog("Event", content);
			});
			t.start();
		}
	}

	
	public void writeDownloadFileEvent(HttpServletRequest request, Node f) {
		if (ConfigureReader.instance().inspectLogLevel(LogLevel.Event)) {
			String account = (String) request.getSession().getAttribute("ACCOUNT");
			if (account == null || account.length() == 0) {
				account = "Anonymous";
			}
			String a = account;
			Thread t = new Thread(() -> {
				Folder folder = fm.queryById(f.getFileParentFolder());
				List<Folder> l = fu.getParentList(folder.getFolderId());
				String pl = new String();
				for (Folder i : l) {
					pl = pl + i.getFolderName() + "/";
				}
				String content = ">ACCOUNT [" + a + "]\r\n>OPERATE [Download file]\r\n>PATH [" + pl
						+ folder.getFolderName() + "]\r\n>NAME [" + f.getFileName() + "]";
				writeToLog("Event", content);
			});
			t.start();
		}
	}
	
	
	public void writeDownloadFileByKeyEvent(Node f) {
		if (ConfigureReader.instance().inspectLogLevel(LogLevel.Event)) {
			Thread t = new Thread(() -> {
				Folder folder = fm.queryById(f.getFileParentFolder());
				List<Folder> l = fu.getParentList(folder.getFolderId());
				String pl = new String();
				for (Folder i : l) {
					pl = pl + i.getFolderName() + "/";
				}
				String content = ">OPERATE [Download file By Shared URL]\r\n>PATH [" + pl
						+ folder.getFolderName() + "]\r\n>NAME [" + f.getFileName() + "]";
				writeToLog("Event", content);
			});
			t.start();
		}
	}
	
	
	public void writeShareFileURLEvent(HttpServletRequest request, Node f) {
		if (ConfigureReader.instance().inspectLogLevel(LogLevel.Event)) {
			String account = (String) request.getSession().getAttribute("ACCOUNT");
			if (account == null || account.length() == 0) {
				account = "Anonymous";
			}
			String a = account;
			Thread t = new Thread(() -> {
				Folder folder = fm.queryById(f.getFileParentFolder());
				List<Folder> l = fu.getParentList(folder.getFolderId());
				String pl = new String();
				for (Folder i : l) {
					pl = pl + i.getFolderName() + "/";
				}
				String content = ">ACCOUNT [" + a + "]\r\n>OPERATE [Share Download file URL]\r\n>PATH [" + pl
						+ folder.getFolderName() + "]\r\n>NAME [" + f.getFileName() + "]";
				writeToLog("Event", content);
			});
			t.start();
		}
	}

	
	public void writeRenameFileEvent(HttpServletRequest request, Node f, String newName) {
		if (ConfigureReader.instance().inspectLogLevel(LogLevel.Event)) {
			String account = (String) request.getSession().getAttribute("ACCOUNT");
			if (account == null || account.length() == 0) {
				account = "Anonymous";
			}
			String a = account;
			Thread t = new Thread(() -> {
				Folder folder = fm.queryById(f.getFileParentFolder());
				List<Folder> l = fu.getParentList(folder.getFolderId());
				String pl = new String();
				for (Folder i : l) {
					pl = pl + i.getFolderName() + "/";
				}
				String content = ">ACCOUNT [" + a + "]\r\n>OPERATE [Rename file]\r\n>PATH [" + pl
						+ folder.getFolderName() + "]\r\n>NAME [" + f.getFileName() + "]->[" + newName + "]";
				writeToLog("Event", content);
			});
			t.start();
		}
	}

	
	public void writeMoveFileEvent(HttpServletRequest request, Node f) {
		if (ConfigureReader.instance().inspectLogLevel(LogLevel.Event)) {
			String account = (String) request.getSession().getAttribute("ACCOUNT");
			if (account == null || account.length() == 0) {
				account = "Anonymous";
			}
			String a = account;
			Thread t = new Thread(() -> {
				Folder folder = fm.queryById(f.getFileParentFolder());
				List<Folder> l = fu.getParentList(folder.getFolderId());
				String pl = new String();
				for (Folder i : l) {
					pl = pl + i.getFolderName() + "/";
				}
				String content = ">ACCOUNT [" + a + "]\r\n>OPERATE [Move file]\r\n>NEW PATH [" + pl
						+ folder.getFolderName() + "/" + f.getFileName() + "]";
				writeToLog("Event", content);
			});
			t.start();
		}
	}

	public void writeMoveFileEvent(HttpServletRequest request, Folder f) {
		if (ConfigureReader.instance().inspectLogLevel(LogLevel.Event)) {
			String account = (String) request.getSession().getAttribute("ACCOUNT");
			if (account == null || account.length() == 0) {
				account = "Anonymous";
			}
			String a = account;
			Thread t = new Thread(() -> {
				Folder folder = fm.queryById(f.getFolderParent());
				List<Folder> l = fu.getParentList(folder.getFolderId());
				String pl = new String();
				for (Folder i : l) {
					pl = pl + i.getFolderName() + "/";
				}
				String content = ">ACCOUNT [" + a + "]\r\n>OPERATE [Move Folder]\r\n>NEW PATH [" + pl
						+ folder.getFolderName() + "/" + f.getFolderName() + "]";
				writeToLog("Event", content);
			});
			t.start();
		}
	}

	private void writeToLog(String type, String content) {
		String t = ServerTimeUtil.accurateToLogName();
		File f = new File(logs, t + ".klog");
		FileWriter fw = null;
		if (f.exists()) {
			try {
				fw = new FileWriter(f, true);
				fw.write("\r\n\r\nTIME:\r\n" + ServerTimeUtil.accurateToSecond() + "\r\nTYPE:\r\n" + type
						+ "\r\nCONTENT:\r\n" + content);
				fw.close();
			} catch (Exception e1) {
				System.out.println("KohgylwIFT:[Log]Cannt write to file,message:" + e1.getMessage());
			}
		} else {
			try {
				fw = new FileWriter(f, false);
				fw.write("TIME:\r\n" + ServerTimeUtil.accurateToSecond() + "\r\nTYPE:\r\n" + type + "\r\nCONTENT:\r\n"
						+ content);
				fw.close();
			} catch (IOException e1) {
				System.out.println("KohgylwIFT:[Log]Cannt write to file,message:" + e1.getMessage());
			}
		}
	}

	
	public void writeDownloadCheckedFileEvent(HttpServletRequest request, List<String> idList) {
		if (ConfigureReader.instance().inspectLogLevel(LogLevel.Event)) {
			String account = (String) request.getSession().getAttribute("ACCOUNT");
			if (account == null || account.length() == 0) {
				account = "Anonymous";
			}
			String a = account;
			Thread t = new Thread(() -> {
				StringBuffer content = new StringBuffer(
						">ACCOUNT [" + a + "]\r\n>OPERATE [Download checked file]\r\n----------------\r\n");
				for (String fid : idList) {
					Node f = fim.queryById(fid);
					if (f != null) {
						Folder folder = fm.queryById(f.getFileParentFolder());
						List<Folder> l = fu.getParentList(folder.getFolderId());
						String pl = new String();
						for (Folder i : l) {
							pl = pl + i.getFolderName() + "/";
						}
						content.append(
								">PATH [" + pl + folder.getFolderName() + "]\r\n>NAME [" + f.getFileName() + "]\r\n");
					}
				}
				content.append("----------------");
				writeToLog("Event", content.toString());
			});
			t.start();
		}

	}

}
