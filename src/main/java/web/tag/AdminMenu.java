package web.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class AdminMenu extends SimpleTagSupport implements DynamicAttributes{
	private Map<String,Object> props = new HashMap<String,Object>();
	
	public void setDynamicAttribute(String uri, String localName,Object value) throws JspException {
		// TODO Auto-generated method stub
		props.put(localName, value);
	}
	
	public void doTag() throws JspException, IOException {
		ArrayList<Map<String,String>> rows= new ArrayList<Map<String,String>>();
		Map<String,String> row=new HashMap<String,String>();
		row.put("a", "1");
		row.put("b", "2");
		rows.add(row);
		Map<String,String> row1=new HashMap<String,String>();
		row1.put("a", "3");
		row1.put("b", "4");
		rows.add(row1);
		
		PageContext pc=(PageContext) this.getJspContext();
		for(Map<String,String> r:rows){
			/*
			for(Entry<String, String> entry:r.entrySet()){
				pc.setAttribute(entry.getKey(), entry.getValue());
			}
			*/
			pc.setAttribute("$",r);
			this.getJspBody().invoke(null);
		}
	}
}
