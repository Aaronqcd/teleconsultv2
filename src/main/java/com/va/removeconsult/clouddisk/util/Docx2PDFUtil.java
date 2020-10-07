package com.va.removeconsult.clouddisk.util;

import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Component;


@Component
public class Docx2PDFUtil {
	
	
	public void convertPdf(InputStream in, OutputStream out) throws Exception {
		XWPFDocument document = new XWPFDocument(in);
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (XWPFParagraph p : document.getParagraphs()) {
			for (XWPFRun r : p.getRuns()) {
				
				if (Arrays.stream(ge.getAvailableFontFamilyNames()).parallel()
						.anyMatch((e) -> e.equals(r.getFontFamily()))) {
					continue;
				}
				
				r.setFontFamily("WenQuanYi Zen Hei");
			}
		}
		PdfConverter.getInstance().convert(document, out, PdfOptions.create().fontProvider(Docx2PDFFontProvider.getInstance()));
	}

}
