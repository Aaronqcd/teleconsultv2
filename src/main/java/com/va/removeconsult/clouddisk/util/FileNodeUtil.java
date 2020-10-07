package com.va.removeconsult.clouddisk.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import com.va.removeconsult.clouddisk.model.Folder;
import com.va.removeconsult.clouddisk.model.Node;


public class FileNodeUtil {

	private FileNodeUtil() {
	};

	private static Connection conn;
	private static String url;

	
	public static void initNodeTableToDataBase() {
		try {
			if (conn == null) {
				Class.forName(ConfigureReader.instance().getFileNodePathDriver()).newInstance();
			}
			String newUrl = ConfigureReader.instance().getFileNodePathURL();
			
			if (url == null || !url.equals(newUrl)) {
				conn = DriverManager.getConnection(newUrl, ConfigureReader.instance().getFileNodePathUserName(),
						ConfigureReader.instance().getFileNodePathPassWord());
				url = newUrl;
				final Statement state1 = conn.createStatement();
				state1.execute(
						"CREATE TABLE IF NOT EXISTS FOLDER(folder_id VARCHAR(128) PRIMARY KEY,  folder_name VARCHAR(128) NOT NULL,folder_creation_date VARCHAR(128) NOT NULL,  folder_creator VARCHAR(128) NOT NULL,folder_parent VARCHAR(128) NOT NULL,folder_constraint INT NOT NULL)");
				state1.executeQuery("SELECT count(*) FROM FOLDER WHERE folder_id = 'root'");
				ResultSet rs = state1.getResultSet();
				if (rs.next()) {
					if (rs.getInt(1) == 0) {
						final Statement state11 = conn.createStatement();
						state11.execute("INSERT INTO FOLDER VALUES('root', 'ROOT', '--', '--', 'null', 0)");
					}
				}
				state1.close();
				final Statement state2 = conn.createStatement();
				state2.execute(
						"CREATE TABLE IF NOT EXISTS FILE(file_id VARCHAR(128) PRIMARY KEY,file_name VARCHAR(128) NOT NULL,file_size VARCHAR(128) NOT NULL,file_parent_folder varchar(128) NOT NULL,file_creation_date varchar(128) NOT NULL,file_creator varchar(128) NOT NULL,file_path varchar(128) NOT NULL)");
				state2.close();
				
				if (!ConfigureReader.instance().useMySQL()) {
					final Statement state3 = conn.createStatement();
					state3.execute(
							"ALTER TABLE FOLDER ADD COLUMN IF NOT EXISTS folder_constraint INT NOT NULL DEFAULT 0");
					state3.close();
				}
				
				if (ConfigureReader.instance().useMySQL()) {
					final Statement state4 = conn.createStatement();
					ResultSet indexCount = state4.executeQuery("SHOW INDEX FROM FILE WHERE Key_name = 'file_index'");
					if (!indexCount.next()) {
						final Statement state41 = conn.createStatement();
						state41.execute("CREATE INDEX file_index ON FILE (file_id,file_name)");
						state41.close();
					}
					state4.close();
				} else {
					final Statement state4 = conn.createStatement();
					state4.execute("CREATE INDEX IF NOT EXISTS file_index ON FILE (file_id,file_name)");
					state4.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	
	public static Connection getNodeDBConnection() {
		return conn;
	}

	
	public static String getNewNodeName(String originalName, List<Node> nodes) {
		int i = 0;
		List<String> fileNames = Arrays
				.asList(nodes.stream().parallel().map((t) -> t.getFileName()).toArray(String[]::new));
		String newName = originalName;
		while (fileNames.contains(newName)) {
			i++;
			if (originalName.indexOf(".") >= 0) {
				newName = originalName.substring(0, originalName.lastIndexOf(".")) + " (" + i + ")"
						+ originalName.substring(originalName.lastIndexOf("."));
			} else {
				newName = originalName + " (" + i + ")";
			}
		}
		return newName;
	}

	
	public static String getNewFolderName(String originalName, List<? extends Folder> folders) {
		int i = 0;
		List<String> fileNames = Arrays
				.asList(folders.stream().parallel().map((t) -> t.getFolderName()).toArray(String[]::new));
		String newName = originalName;
		while (fileNames.contains(newName)) {
			i++;
			newName = originalName + " " + i;
		}
		return newName;
	}

	
	public static String getNewFolderName(Folder folder, File parentfolder) {
		int i = 0;
		List<String> fileNames = Arrays.asList(Arrays.stream(parentfolder.listFiles()).parallel()
				.filter((e) -> e.isDirectory()).map((t) -> t.getName()).toArray(String[]::new));
		String newName = folder.getFolderName();
		while (fileNames.contains(newName)) {
			i++;
			newName = folder.getFolderName() + " " + i;
		}
		return newName;
	}

	
	public static String getNewNodeName(Node n, File folder) {
		int i = 0;
		List<String> fileNames = Arrays.asList(Arrays.stream(folder.listFiles()).parallel().filter((e) -> e.isFile())
				.map((t) -> t.getName()).toArray(String[]::new));
		String newName = n.getFileName();
		while (fileNames.contains(newName)) {
			i++;
			if (n.getFileName().indexOf(".") >= 0) {
				newName = n.getFileName().substring(0, n.getFileName().lastIndexOf(".")) + " (" + i + ")"
						+ n.getFileName().substring(n.getFileName().lastIndexOf("."));
			} else {
				newName = n.getFileName() + " (" + i + ")";
			}
		}
		return newName;
	}

}
