package com.va.removeconsult.clouddisk.util;

import org.mozilla.intl.chardet.nsICharsetDetectionObserver;


public class CharsetDetectionObserverImpl implements nsICharsetDetectionObserver{
	
	private String charset;

	@Override
	public void Notify(String arg0) {
		
		charset=arg0;
	}

	public String getCharset() {
		return charset;
	}
	
}
