package com.va.removeconsult.clouddisk.util;

import com.lowagie.text.FontFactory;

import fr.opensagres.xdocreport.itext.extension.font.AbstractFontRegistry;


public class Docx2PDFFontProvider extends AbstractFontRegistry{
	
	private static Docx2PDFFontProvider instance;
	
	private Docx2PDFFontProvider() {
		FontFactory.setFontImp(new Docx2PDFFontFactory());
	}

	@Override
	protected String resolveFamilyName(String arg0, int arg1) {
		return arg0;
	}
	
	
	public static Docx2PDFFontProvider getInstance() {
		if(instance==null) {
			instance=new Docx2PDFFontProvider();
		}
		return instance;
	}

}
