package com.va.removeconsult.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;


/**
 * 
 * @author andong
 *
 */
public class CutPicUtil {
	

	/**
	 * 屏幕截图
	 * @param fileName
	 * @param filePath
	 * @param imgType
	 * @param x
	 * @param y
	 * @throws Exception
	 */
	public static File captureScreen( String fileName,String filePath,String imgType,String imgStr,double ratio) throws Exception {
        try {
        	  // 截图保存的路径
            File screenFile = new File(filePath+File.separator+fileName+"."+imgType);
            // 如果文件夹路径不存在，则创建
            if (!screenFile.getParentFile().exists()) {
                screenFile.getParentFile().mkdirs();
            }
            /*File path = new File(ResourceUtils.getURL("classpath:static").getPath());
            if(!path.exists()) path = new File("");
            File upload = new File(path.getAbsolutePath()+File.separator+fileName+"."+imgType,filePath+File.separator+fileName+"."+imgType);
            if(!upload.exists()) upload.getParentFile().mkdirs();*/
            
            if(null != imgStr) {
            	//Base64解码
                byte[] b = Base64.getDecoder().decode(imgStr.replace("\r\n", ""));
                for(int i=0;i<b.length;++i)
                {
                    if(b[i]<0)
                    {//调整异常数据
                        b[i]+=256;
                    }
                }
                //生成图片
                OutputStream out = new FileOutputStream(screenFile);
                out.write(b);
                out.flush();
                out.close();
            }
            
            ratio = 1.0;  
        	 /*int newWidth = (int) (image.getWidth() * ratio);  
             int newHeight = (int) (image.getHeight() * ratio);  
             Image newimage = image.getScaledInstance(newWidth, newHeight,  
             Image.SCALE_SMOOTH);  
             BufferedImage tag = new BufferedImage(newWidth, newHeight,  
                     BufferedImage.TYPE_INT_RGB);  
             Graphics g = tag.getGraphics();  
             g.drawImage(newimage, 0, 0, null);  
             g.dispose();  
             ImageIO.write(tag, imgType, screenFile);*/
             return screenFile;
        	 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
    
    public static boolean isWindows() {
		return System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1;

    }
    public static void main(String[] args) {
    	try {
			//captureScreen("test","D://test//","jpg",565,50,699,980);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    /**
     * base64字符串转化成图片
     * @param imgStr base64字符串
     * @param imageName 图片名称
     * @return
     * @throws FileNotFoundException
     */
    public static boolean GenerateImage(String imgStr,String imageName,String uploadPath) throws FileNotFoundException {
        File path = new File(ResourceUtils.getURL("classpath:static").getPath());
        if(!path.exists()) path = new File("");
        File upload = new File(path.getAbsolutePath(),uploadPath);
        if(!upload.exists()) upload.mkdirs();
        //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        try
        {
            //Base64解码
            byte[] b = Base64.getDecoder().decode(imgStr.replace("\r\n", ""));
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            OutputStream out = new FileOutputStream(upload+File.separator+imageName);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

}
