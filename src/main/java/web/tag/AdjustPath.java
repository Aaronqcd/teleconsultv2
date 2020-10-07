package web.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class AdjustPath extends SimpleTagSupport implements DynamicAttributes{
	private Map<String,Object> props = new HashMap<String,Object>();
	
	public void doTag() throws JspException, IOException {
        /*
		for (Entry<String, Object> entry : props.entrySet()) {
        	String key=entry.getKey();
        	String value=(String) entry.getValue();
        	getJspContext().getOut().write("key:"+key+" value:"+value+"<br/>\r\n");
        }
        */
		//JspFragment jspFragment = this.getJspBody();
		//jspFragment.invoke(null);
		PageContext pc=(PageContext) this.getJspContext();
		ServletContext application = pc.getServletContext();
		HttpServletRequest request=(HttpServletRequest)pc.getRequest();
		
		String c=request.getRequestURI();
		String path=application.getContextPath();
		
		//String dir=new java.io.File(path).getParent();
		getJspContext().getOut().write(c);
    }

	public void setDynamicAttribute(String uri, String localName,Object value) throws JspException {
		// TODO Auto-generated method stub
		props.put(localName, value);
	}
}
