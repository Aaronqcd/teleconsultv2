package com.va.removeconsult.controller.backend;

import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.va.removeconsult.clouddisk.enumeration.SysConfig;
import com.va.removeconsult.clouddisk.service.FolderService;
import com.va.removeconsult.service.AsyncService;
import com.va.removeconsult.service.DbService;
import com.va.removeconsult.util.FileEntity;
import com.va.removeconsult.util.FileUploadTool;
import com.va.removeconsult.util.MD5Utils;
import com.va.removeconsult.util.SendEmail;
import com.va.removeconsult.util.Utils;

@Controller
@RequestMapping("/uploadflv")
public class UploadController {
	
	@Autowired
    DbService dbService;
	@Autowired
	com.va.removeconsult.service.UserService userService;
	
	@Autowired  
	private AsyncService asyncService;
	
	@Resource
	private FolderService folderService;
	/**
	 * 
	 * @param multipartFile
	 * @param type为1是图片文件
	 * @param request
	 * @param map
	 * @return
	 */
    @RequestMapping(value = "/upload", method={RequestMethod.POST})
    @ResponseBody
    public String upload(@RequestParam(value = "file", required = false) MultipartFile multipartFile,
    		@RequestParam(value = "type", required = true) int type,
    		@RequestParam(value = "source", required = false) String source,
            HttpServletRequest request,HttpServletResponse response) {
    	response.setContentType("text/html,charset=UTF-8");
    	response.setCharacterEncoding("utf-8");
    	try {
			request.setCharacterEncoding("utf-8");

		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
    	JSONObject json = new JSONObject();
        FileEntity entity = new FileEntity();
        FileUploadTool fileUploadTool = new FileUploadTool();
        try {
            entity = fileUploadTool.createFile(multipartFile, request,type);
            if (entity != null) {
            	String fileName = multipartFile.getOriginalFilename().toString();
				String fileType = fileName.substring(fileName.lastIndexOf('.') + 1,fileName.length()).toLowerCase();
				/*
				if(!"txt".equals(fileType) && !"doc".equals(fileType) && !"xls".equals(fileType)  && !"ppt".equals(fileType) && !"docx".equals(fileType) && !"xlsx".equals(fileType)
						&& !"dcm".equals(fileType) && !"flv".equals(fileType) && !"rmvb".equals(fileType) && !"mp4".equals(fileType) && !"mvb".equals(fileType)) {
					json.put("code","302");
					return json.toJSONString();
				}*/
				
				if("php".equals(fileType) || "asp".equals(fileType) || "jsp".equals(fileType)) {
					json.put("code","302");
					return json.toJSONString();
				}	
				
            	//service.saveFile(entity);
                json.put("code", "1");
                json.put("data", entity.getTitleAlter()+entity.getType());
                json.put("fileName", fileName);
                System.out.println(entity.getTitleAlter()+entity.getType());
                json.put("path", entity.getPath());
                Map<String, Object> data = new HashMap<String,Object>();
                String fileId = UUID.randomUUID().toString().replaceAll("-", "");
                data.put("id", fileId);
                data.put("source", source);
                data.put("fileName", fileName);
                data.put("sysName", entity.getTitleAlter()+entity.getType());
                data.put("path", entity.getPath());
                data.put("dcmURL", entity.getDcmURL() );
				dbService.addAttr(data);
				json.put("id", fileId);
            } else {
                json.put("code", "-4947");
                json.put("code", "上传失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println(entity);
        String result = json.toJSONString();
		try {
			//result = new String(result.getBytes("iso-8859-1"),"utf-8");
			result=new String(result.getBytes("UTF-8"),"UTF-8");
//			String decode = URLDecoder.decode(result);
//			String encode = URLEncoder.encode(result);
//			System.err.println("decode="+decode);
//			System.err.println("encode="+encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return result;
    }
    
  
    
    
    @RequestMapping(value = "importExcel", method = RequestMethod.POST,produces = "application/json; charset=utf-8")
    @ResponseBody
    public String importExcel(MultipartHttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
    	response.setCharacterEncoding("utf-8");
    	JSONObject result = new JSONObject();
    	//得到上传的文件
    	MultipartFile fileFile = request.getFile("file");
    	String fileName = fileFile.getOriginalFilename();
    	//判断该文件是否为Excel文件
    	if(fileName.matches("^.+\\.(?i)((xls)|(xlsx))$")){
	        try {
	        	int res=0;
	        
	            //转换成输入流
	        	List<Map<String,Object>> excelData =null;
	        	if(POIFSFileSystem.hasPOIFSHeader(new PushbackInputStream(fileFile.getInputStream(), 8))) {
	        	    //System.out.println("2003及以下");
	        		excelData=getExcelData(new HSSFWorkbook(fileFile.getInputStream()));
	        	} else{
	        		//System.out.println("2007及以上");
	        		excelData=getExcelData(new XSSFWorkbook(fileFile.getInputStream()));
	        	}
	        	if(excelData != null && excelData.size()>0 ){
	        		int count =  dbService.getUserIsNotAdminsCount("admins");
	        		int userCount = count+excelData.size();
	        		String userSizeStr = this.folderService.querySysConfValue(SysConfig.USER_SIZE.getKey());
	    			int userSize  = Integer.parseInt(userSizeStr);
	    			if( userCount - userSize > 0 ){
	    				result.put("data","批量导入失败，还可导入"+(userSize-count)+"个用户。请调整Excel表用户数量。");
	    				result.put("code","-4837");
	    				return result.toJSONString();
	    			}
	        	}
	        	
	            int index = 2;
	            boolean isSuccess = true;
	            String errMsg = "";
	            for (Map<String, Object> map : excelData) {
	            	isSuccess = true;
		        	String rowErrMsg = "";	            	
	            	index += 1;
	            	Object userName = map.get("用户名*");
	            	Object name = map.get("姓名");
	            	Object belong = map.get("所属机构");
	            	Object group = map.get("账号类型");
	            	Object email = map.get("邮箱*");
	            	Object sex = map.get("性别");
	            	Object job = map.get("职称");
	            	Object special = map.get("专长");
	            	Object phone = map.get("电话");
	            	if(isNull(name)) {
	            		rowErrMsg = rowErrMsg+"姓名不能为空,";
	            		isSuccess = false;
	            	}
	            	if(isNull(email)) {
	            		rowErrMsg = rowErrMsg+"邮箱不能为空,";
	            		isSuccess = false;
	            	} else if(dbService.isUserUsedEmail(email.toString())){
	            		rowErrMsg = rowErrMsg+"邮箱【"+email+"】已存在,";
	            		isSuccess = false;
                	} else if(email != null && email.toString().indexOf("@")<0) {
                		rowErrMsg = rowErrMsg+"邮箱【"+email+"】格式不正确,";
	            		isSuccess = false;
                	}
	            	if(isNull(userName)) {
	            		rowErrMsg = rowErrMsg+"用户名不能为空";
                    	isSuccess = false;
	            	} else if(dbService.haveUser(userName.toString())){
                    	rowErrMsg = rowErrMsg+"用户名【"+userName+"】已存在或空,";
                    	isSuccess = false;
                    }
                    Map<String, Object> belongMap = null;
                    if(isNull(belong)) {
                    	rowErrMsg = rowErrMsg+"所属机构不能为空,";
                    	isSuccess = false;
                    } else {
                    	belongMap = dbService.getOrganListByName(belong.toString());
                        if(belongMap == null || belongMap.size()==0) {
                        	rowErrMsg = rowErrMsg+"系统找不到所属机构【"+belong.toString()+"】,";
                        	isSuccess = false;
                        }
                        
                        if(isNull(group)) {
                        	rowErrMsg = rowErrMsg+"账号类型不能为空,";
                        	isSuccess = false;
                        } else {
                        	if(!"机构管理员".equals(group.toString().trim()) && !"普通用户".equals(group.toString().trim())) {
                            	rowErrMsg = rowErrMsg+"账号类型【"+group.toString()+"】填写不正确,";
                            	isSuccess = false;
                            }
                			int have=dbService.getOrganAdminUserCount((int)belongMap.get("id"));
                			if(group.toString().equals("机构管理员") && have>0){
                				rowErrMsg = rowErrMsg+"该机构【"+belong.toString()+"】已存在机构管理员,";
                            	isSuccess = false;
                			}
                        }
                    }
                    if(sex == null || (!sex.toString().equals("男") && !sex.toString().equals("女"))) {
                    	rowErrMsg = rowErrMsg+"性别【"+sex+"】格式不正确,";
                    	isSuccess = false;
                    }
                    if(isNull(job)) {
	            		rowErrMsg = rowErrMsg+"职称不能为空,";
	            		isSuccess = false;
	            	}
                    if(isNull(special)) {
	            		rowErrMsg = rowErrMsg+"专长不能为空,";
	            		isSuccess = false;
	            	}
                    if(isNull(phone)) {
	            		rowErrMsg = rowErrMsg+"电话不能为空,";
	            		isSuccess = false;
	            	} else if(!Utils.isMobile(phone.toString())) {
	            		rowErrMsg = rowErrMsg+"电话【"+phone.toString()+"】格式不正确,";
	            		isSuccess = false;
	            	}
                    if(isSuccess) {
                    	Map<String, Object> user = new HashMap<>();
                    	String password = MD5Utils.string2MD5("123456");
                        //赋值实例对象 做插入数据库操作
                        user.put("user", userName);
                        user.put("password", password);
                        user.put("group", group==null?"users":group.toString().equals("机构管理员")?"managers":"users");
                        user.put("name", name==null?"":name);
                        user.put("belong", belongMap!=null?belongMap.get("id"):"0");
                        user.put("email", email);
                        user.put("sex", sex==null?"":sex.toString().equals("男")?"1":"0");
                        user.put("job", job==null?"":job);
                        user.put("special", special==null?"":special);
                        user.put("phone", phone==null?"":phone);
                        //System.out.println(user);
                        try{
                        	dbService.AddUser(user);
                        	res++;
                        	Map<String,String> mailTemplate = dbService.getMailTemplate("mail_user_warn");
        					Map<String, Object> mailParam = new HashMap<String,Object>();
        					mailParam.put("user", user.getOrDefault("user", ""));
        					mailParam.put("password", "123456");
        					mailParam.put("hospitalName", this.folderService.querySysConfValue(SysConfig.EMAIL_HOSPITAL_NAME.getKey()));
        					mailParam.put("sysUrl", this.folderService.querySysConfValue(SysConfig.EMAIL_HOSPITAL_URL.getKey()));
        					String title = mailTemplate.get("template_title");
        					String text = SendEmail.formatTemplate(mailTemplate.get("template_context"), mailParam);
        					asyncService.sendMail(email.toString(), title, text);
        					
        					
        					try {
        				    	if(user!=null && user.containsKey("phone") && user.get("phone")!=null) {
        				    		String toPhones = user.get("phone").toString();
        							String toPhone = toPhones.substring(12);
        							String[] params=new String[]{user.get("user").toString(),String.valueOf("123456")};
        							asyncService.sendSms(toPhone, "SMS_TEMP_4", params);
        						}
        					} catch (Exception e) {
        						e.printStackTrace();
        					}
        					
        					
                        }catch (Exception e){
                        	result.put("e",e);
                        	rowErrMsg = rowErrMsg+"姓名【"+name+"】保存失败【"+e.getMessage()+"】,";
                        	isSuccess = false;
                        }
                    }
                    if(!isSuccess){
                    	errMsg = index+"行:"+rowErrMsg+"<br>"+errMsg;
                    }
                    
				}
	            result.put("data","成功添加" + res + "条记录"+("".equals(errMsg)?"":"<br>"+errMsg));
				result.put("code","1");
	        } catch (Exception e) {
	            e.printStackTrace();
	            result.put("data","添加失败，请检查文件是否符合要求");
				result.put("code","-4837");
	        }
	        dbService.addLog("用户管理>导入用户", userService.getLoginInfo(request));
    	}else{
			result.put("code","-5414");
			result.put("data","添加失败，请检查文件是否为Excel文件");
		}
    	return result.toJSONString();
    }
    
    public boolean isNull(Object obj) {
    	if(obj == null || "".equals(obj.toString().trim()) || "null".equalsIgnoreCase(obj.toString().trim())) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    //POI 提供了对2003版本的Excel的支持 -HSSFWorkbook
    public List<Map<String,Object>> getExcelData(HSSFWorkbook readWb) {
    	List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
    	for (int i = 0; i < readWb.getNumberOfSheets(); i++) {
            HSSFSheet sheet = readWb.getSheetAt(i);
            HSSFRow titleRow = sheet.getRow(1);
            if(titleRow != null) {
	            //查询出标题
	            String[] title = new String[titleRow.getPhysicalNumberOfCells()];
	            for(int cellNum = 0; cellNum < titleRow.getPhysicalNumberOfCells();cellNum++){  
	                title[cellNum] = getCellValue(titleRow.getCell(cellNum));  
	            }
	            //循环第二行开始查询数据
	            for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
		            Map<String,Object> rowData = new HashMap<String,Object>();
	            	HSSFRow dataRow = sheet.getRow(rowNum);
	            	if(dataRow == null) {
	            		break;
	            	}
	            	for(int titleIndex = 0;titleIndex<title.length;titleIndex++) {
	            		Object value = dataRow.getCell(titleIndex);
	            		HSSFCell cell = dataRow.getCell(titleIndex);
	            		try {
	            			if(!StringUtils.isEmpty(value.toString())) {
		            			DecimalFormat df=new DecimalFormat("0"); 
		            			value=df.format(cell.getNumericCellValue());
	            			};
	            		}catch(Exception e) {	            			
	            		}
	            		rowData.put(title[titleIndex], value);
	            	}
	            	data.add(rowData);
	            }
            }
    	}
    	return data;
    }
    
     //POI 提供了对2007版本以及更高版本的支持 ---- XSSFWorkbook  
    public List<Map<String,Object>> getExcelData(XSSFWorkbook readWb) {
    	List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
    	for (int i = 0; i < readWb.getNumberOfSheets(); i++) {
            XSSFSheet sheet = readWb.getSheetAt(i);
            XSSFRow titleRow = sheet.getRow(1);
            if(titleRow != null) {
	            //查询出标题
	            String[] title = new String[titleRow.getPhysicalNumberOfCells()];
	            for(int cellNum = 0; cellNum < titleRow.getPhysicalNumberOfCells();cellNum++){  
	                title[cellNum] = getCellValue(titleRow.getCell(cellNum));  
	            }
	            //循环第二行开始查询数据
	            for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
		            Map<String,Object> rowData = new HashMap<String,Object>();
	            	XSSFRow dataRow = sheet.getRow(rowNum);
	            	if(dataRow == null) {
	            		break;
	            	}
	            	for(int titleIndex = 0;titleIndex<title.length;titleIndex++) {
	            		Object value = dataRow.getCell(titleIndex);
	            		XSSFCell cell = dataRow.getCell(titleIndex);
	            		try {
	            			if(!StringUtils.isEmpty(value.toString())) {
		            			DecimalFormat df=new DecimalFormat("0"); 
		            			value=df.format(cell.getNumericCellValue());
	            			};
	            		}catch(Exception e) {	            			
	            		}
	            		rowData.put(title[titleIndex], value);
	            	}
	            	data.add(rowData);
	            }
            }
    	}
    	return data;
    }
    public static String getCellValue(Cell cell){  
        String cellValue = "";  
        if(cell == null){  
            return cellValue;  
        }  
        //把数字当成String来读，避免出现1读成1.0的情况  
        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){  
            cell.setCellType(Cell.CELL_TYPE_STRING);  
        }  
        //判断数据的类型  
        switch (cell.getCellType()){  
            case Cell.CELL_TYPE_NUMERIC: //数字  
                cellValue = String.valueOf(cell.getNumericCellValue());  
                break;  
            case Cell.CELL_TYPE_STRING: //字符串  
                cellValue = String.valueOf(cell.getStringCellValue());  
                break;  
            case Cell.CELL_TYPE_BOOLEAN: //Boolean  
                cellValue = String.valueOf(cell.getBooleanCellValue());  
                break;  
            case Cell.CELL_TYPE_FORMULA: //公式  
                cellValue = String.valueOf(cell.getCellFormula());  
                break;  
            case Cell.CELL_TYPE_BLANK: //空值   
                cellValue = "";  
                break;  
            case Cell.CELL_TYPE_ERROR: //故障  
                cellValue = "非法字符";  
                break;  
            default:  
                cellValue = "未知类型";  
                break;  
        }  
        return cellValue;  
    }  
    
    
    
    
    
    
    
    /**
     * 会诊资料上传接口
     * @param request
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
    
    @RequestMapping(value = "/uploadfiles", method={RequestMethod.POST})
    @ResponseBody
    public String uploadfiles(@RequestParam(value = "file", required = false) MultipartFile multipartFile,
    		@RequestParam(value = "type", required = true) int type,
    		@RequestParam(value = "source", required = false) String source,
            HttpServletRequest request,HttpServletResponse response) {
    	response.setCharacterEncoding("utf-8");
    	try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
    	JSONObject json = new JSONObject();
        FileEntity entity = new FileEntity();
        FileUploadTool fileUploadTool = new FileUploadTool();
        try {
            entity = fileUploadTool.createFile(multipartFile, request,type);
            if (entity != null) {
            	String fileName = multipartFile.getOriginalFilename().toString();
            	//service.saveFile(entity);
                json.put("code", "1");
                json.put("data", entity.getTitleAlter()+entity.getType());
                json.put("fileName", fileName);
                System.out.println(entity.getTitleAlter()+entity.getType());
                json.put("path", entity.getPath());
                Map<String, Object> data = new HashMap<String,Object>();
                String fileId = UUID.randomUUID().toString().replaceAll("-", "");
                data.put("id", fileId);
                data.put("source", source);
                data.put("fileName", fileName);
                //data.put("sysName", entity.getTitleAlter()+entity.getType());
                data.put("sysName", fileName);
                data.put("path", entity.getPath());
				dbService.addAttr(data);
				json.put("id", fileId);
            } else {
                json.put("code", "-4947");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println(entity);
        String result = json.toJSONString();
		try {
			result = new String(result.getBytes("iso-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return result;
    }
    
}