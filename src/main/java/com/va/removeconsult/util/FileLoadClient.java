package com.va.removeconsult.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class FileLoadClient {
	
	 private static Logger logger = Logger.getLogger(FileLoadClient.class);
	 
	 
	 	/**
	     * 文件上传
	     * @param url
	     * @param file
	     * @return
	     */
	    public static String fileload(String url, File file) {
	        String body = "{}";
	        
	        if (url == null || url.equals("")) {
	            return "参数不合法";
	        }
	        if (!file.exists()) {
	            return "要上传的文件名不存在";
	        }
	        
	        PostMethod postMethod = new PostMethod(url);
	        
	        try {            
	            // FilePart：用来上传文件的类,file即要上传的文件
	            FilePart fp = new FilePart("file", file);
	            Part[] parts = { fp };

	            // 对于MIME类型的请求，httpclient建议全用MulitPartRequestEntity进行包装
	            MultipartRequestEntity mre = new MultipartRequestEntity(parts, postMethod.getParams());
	            postMethod.setRequestEntity(mre);
	            
	            HttpClient client = new HttpClient();
	            // 由于要上传的文件可能比较大 , 因此在此设置最大的连接超时时间
	            client.getHttpConnectionManager().getParams() .setConnectionTimeout(50000);
	            
	            int status = client.executeMethod(postMethod);
	            if (status == HttpStatus.SC_OK) {
	                InputStream inputStream = postMethod.getResponseBodyAsStream();
	                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
	                
	                StringBuffer stringBuffer = new StringBuffer();
	                String str = "";
	                while ((str = br.readLine()) != null) {
	                    stringBuffer.append(str);
	                }                
	                body = stringBuffer.toString();                
	            } else {
	                body = "fail";
	            }
	        } catch (Exception e) {
	            logger.warn("上传文件异常", e);
	        } finally {
	            // 释放连接
	            postMethod.releaseConnection();
	        }        
	        return body;
	    }    
	    
	    /**
	     * 字节流读写复制文件
	     * @param src 源文件
	     * @param out 目标文件
	     */
	    public static void InputStreamOutputStream(String src, String out) {
	        FileOutputStream outputStream = null;
	        FileInputStream inputStream = null;
	        try {
	            outputStream = new FileOutputStream(out);
	            inputStream = new FileInputStream(src);
	            byte[] bytes = new byte[1024];
	            int num = 0;
	            while ((num = inputStream.read(bytes)) != -1) {
	                outputStream.write(bytes, 0, num);
	                outputStream.flush();
	            }

	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                outputStream.close();
	                inputStream.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }

	    }
	    
	     /** *//**文件重命名 
	     * @param path 文件目录 
	     * @param oldname  原来的文件名 
	     * @param newname 新文件名 
	     */ 
	     public static void renameFile(String path,String oldname,String newname){ 
	         if(!oldname.equals(newname)){//新的文件名和以前文件名不同时,才有必要进行重命名 
	             File oldfile=new File(path+"/"+oldname); 
	             File newfile=new File(path+"/"+newname); 
	             if(!oldfile.exists()){
	                 return;//重命名文件不存在
	             }
	             if(newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
	                 System.out.println(newname+"已经存在！"); 
	             else{ 
	                 oldfile.renameTo(newfile); 
	             } 
	         }else{
	             System.out.println("新文件名和旧文件名相同...");
	         }
	     }
	     
	     public static MultipartFile getMultipartFileByFile(File file) {
	    	try {
				FileInputStream fileInputStream = new FileInputStream(file);
				return new MockMultipartFile(file.getName(), file.getName(), "text/plain", IOUtils.toByteArray(fileInputStream));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
	     }
	     
	     /** 
	      *  根据路径删除指定的目录或文件，无论存在与否 
	      *@param sPath  要删除的目录或文件 
	      *@return 删除成功返回 true，否则返回 false。 
	      */  
	     public static boolean deleteFolder(String sPath) {  
	         boolean flag = false;  
	         File file = new File(sPath);  
	         // 判断目录或文件是否存在  
	         if (!file.exists()) {  // 不存在返回 false  
	             return flag;  
	         } else {  
	             // 判断是否为文件  
	             if (file.isFile()) {  // 为文件时调用删除文件方法  
	                 return deleteFile(sPath);  
	             } else {  // 为目录时调用删除目录方法  
	                 return deleteDirectory(sPath);  
	             }  
	         }  
	     }
	     
	     /** 
	      * 删除单个文件 
	      * @param   sPath    被删除文件的文件名 
	      * @return 单个文件删除成功返回true，否则返回false 
	      */  
	     public static  boolean deleteFile(String sPath) {  
	         boolean flag = false;  
	         File file = new File(sPath);  
	         // 路径为文件且不为空则进行删除  
	         if (file.isFile() && file.exists()) {  
	             file.delete();  
	             flag = true;  
	         }  
	         return flag;  
	     }
	     
	     /** 
	      * 删除目录（文件夹）以及目录下的文件 
	      * @param   sPath 被删除目录的文件路径 
	      * @return  目录删除成功返回true，否则返回false 
	      */  
	     public static boolean deleteDirectory(String sPath) {  
	         //如果sPath不以文件分隔符结尾，自动添加文件分隔符  
	         if (!sPath.endsWith(File.separator)) {  
	             sPath = sPath + File.separator;  
	         }  
	         File dirFile = new File(sPath);  
	         //如果dir对应的文件不存在，或者不是一个目录，则退出  
	         if (!dirFile.exists() || !dirFile.isDirectory()) {  
	             return false;  
	         }  
	         boolean flag = true;  
	         //删除文件夹下的所有文件(包括子目录)  
	         File[] files = dirFile.listFiles();  
	         for (int i = 0; i < files.length; i++) {  
	             //删除子文件  
	             if (files[i].isFile()) {  
	                 flag = deleteFile(files[i].getAbsolutePath());  
	                 if (!flag) break;  
	             } //删除子目录  
	             else {  
	                 flag = deleteDirectory(files[i].getAbsolutePath());  
	                 if (!flag) break;  
	             }  
	         }  
	         if (!flag) return false;  
	         //删除当前目录  
	         if (dirFile.delete()) {  
	             return true;  
	         } else {  
	             return false;  
	         }  
	     }
	     
	     public static void main(String[] args) {
			String path = "D:\\apache-tomcat-8.5.35\\webapps\\teleconsult\\M20191009190027";
			boolean deleteDirectory = deleteDirectory(path);
			System.out.println(deleteDirectory);
		}
	 
}
