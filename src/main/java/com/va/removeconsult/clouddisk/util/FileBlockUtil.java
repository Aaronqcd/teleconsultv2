package com.va.removeconsult.clouddisk.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.zeroturnaround.zip.FileSource;
import org.zeroturnaround.zip.ZipEntrySource;
import org.zeroturnaround.zip.ZipUtil;

import com.va.removeconsult.clouddisk.model.Folder;
import com.va.removeconsult.clouddisk.model.Node;
import com.va.removeconsult.dao.FolderDao;
import com.va.removeconsult.dao.NodeDao;

@Component
public class FileBlockUtil {
	@Resource
	private NodeDao fm;
	@Resource
	private FolderDao flm;

	private final String fileBlocks = ConfigureReader.instance().getFileBlockPath();

	public String saveToFileBlocks(final MultipartFile f) {
		final String fileBlocks = ConfigureReader.instance().getFileBlockPath();
		final String id = UUID.randomUUID().toString().replace("-", "");
		final String path = "file_" + id + ".block";
		final File file = new File(fileBlocks, path);
		try {
			f.transferTo(file);
			return path;
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}

	public String getFileSize(final MultipartFile f) {
		final long size = f.getSize();
		return "" + size;
	}

	public boolean deleteFromFileBlocks(Node f) {
//		final File file = new File(ConfigureReader.instance().getFileBlockPath(), f.getFilePath());
//		return file.exists() && file.isFile() && file.delete();
		boolean result = false, temp = false;
		File file = new File(ConfigureReader.instance().getFileBlockPath(), f.getFilePath());
		temp = file.exists() && file.isFile() && file.delete();
		result |= temp;

		try {
			file = Paths.get(ConfigureReader.instance().getFileBlockPath() + "..", f.getFileName()).toFile()
					.getCanonicalFile();
			temp = file.exists() && file.isFile() && file.delete();
			result |= temp;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public File getFileFromBlocks(Node f) {
		final File file = new File(ConfigureReader.instance().getFileBlockPath(), f.getFilePath());
		if (file.exists() && file.isFile()) {
			return file;
		}
		return null;
	}

	public void checkFileBlocks() {
		String fileblocks = ConfigureReader.instance().getFileBlockPath();
		Thread checkThread = new Thread(() -> {
			List<Node> nodes = fm.queryAll();
			for (Node node : nodes) {
				File block = new File(fileblocks, node.getFilePath());
				if (!block.exists()) {
					fm.deleteById(node.getFileId());
				}
			}
			File blocks = new File(fileblocks);
			String[] bn = blocks.list();
			for (String n : bn) {
				Node node = fm.queryByPath(n);
				if (node == null) {
					File f = new File(fileblocks, n);
					f.delete();
				}
			}
		});
		checkThread.start();
	}

	public String createZip(final List<String> idList, final List<String> fidList, String account) {
		final String zipname = "tf_" + UUID.randomUUID().toString() + ".zip";
		final String tempPath = ConfigureReader.instance().getTemporaryfilePath();
		final File f = new File(tempPath, zipname);
		try {
			final List<ZipEntrySource> zs = new ArrayList<>();

			final List<Folder> folders = new ArrayList<>();
			for (String fid : fidList) {
				Folder fo = flm.queryById(fid);
				if (fo != null) {
					folders.add(fo);
				}
			}
			final List<Node> nodes = new ArrayList<>();
			for (String id : idList) {
				Node n = fm.queryById(id);
				if (n != null) {
					nodes.add(n);
				}
			}
			for (Folder fo : folders) {
				int i = 1;
				String flname = fo.getFolderName();
				while (true) {
					if (folders.parallelStream().filter((e) -> e.getFolderName().equals(fo.getFolderName()))
							.count() > 1) {
						fo.setFolderName(flname + " " + i);
						i++;
					} else {
						break;
					}
				}
				addFoldersToZipEntrySourceArray(fo, zs, account, "");
			}
			for (Node node : nodes) {
				int i = 1;
				String fname = node.getFileName();
				while (true) {
					if (nodes.parallelStream().filter((e) -> e.getFileName().equals(node.getFileName())).count() > 1
							|| folders.parallelStream().filter((e) -> e.getFolderName().equals(node.getFileName()))
									.count() > 0) {
						if (fname.indexOf(".") >= 0) {
							node.setFileName(fname.substring(0, fname.lastIndexOf(".")) + " (" + i + ")"
									+ fname.substring(fname.lastIndexOf(".")));
						} else {
							node.setFileName(fname + " (" + i + ")");
						}
						i++;
					} else {
						break;
					}
				}
				zs.add((ZipEntrySource) new FileSource(node.getFileName(), new File(fileBlocks, node.getFilePath())));
			}
			for (ZipEntrySource zes : zs) {
				System.out.println(zes.getPath());
			}
			ZipUtil.pack(zs.toArray(new ZipEntrySource[0]), f);
			return zipname;
		} catch (Exception e) {
			return null;
		}
	}

	private void addFoldersToZipEntrySourceArray(Folder f, List<ZipEntrySource> zs, String account, String parentPath) {
		if (f != null && ConfigureReader.instance().accessFolder(f, account)) {
			String folderName = f.getFolderName();
			String thisPath = parentPath + folderName + "/";
			zs.add(new ZipEntrySource() {

				@Override
				public String getPath() {
					return thisPath;
				}

				@Override
				public InputStream getInputStream() throws IOException {
					return null;
				}

				@Override
				public ZipEntry getEntry() {
					return new ZipEntry(thisPath);
				}
			});
			List<Folder> folders = flm.queryByParentId(f.getFolderId());
			for (Folder fo : folders) {
				int i = 1;
				String flname = fo.getFolderName();
				while (true) {
					if (folders.parallelStream().filter((e) -> e.getFolderName().equals(fo.getFolderName()))
							.count() > 1) {
						fo.setFolderName(flname + " " + i);
						i++;
					} else {
						break;
					}
				}
				addFoldersToZipEntrySourceArray(fo, zs, account, thisPath);
			}
			List<Node> nodes = fm.queryByParentFolderId(f.getFolderId());
			for (Node node : nodes) {
				int i = 1;
				String fname = node.getFileName();
				while (true) {
					if (nodes.parallelStream().filter((e) -> e.getFileName().equals(node.getFileName())).count() > 1
							|| folders.parallelStream().filter((e) -> e.getFolderName().equals(node.getFileName()))
									.count() > 0) {
						if (fname.indexOf(".") >= 0) {
							node.setFileName(fname.substring(0, fname.lastIndexOf(".")) + " (" + i + ")"
									+ fname.substring(fname.lastIndexOf(".")));
						} else {
							node.setFileName(fname + " (" + i + ")");
						}
						i++;
					} else {
						break;
					}
				}
				zs.add(new FileSource(thisPath + node.getFileName(), new File(fileBlocks, node.getFilePath())));
			}
		}
	}
}
