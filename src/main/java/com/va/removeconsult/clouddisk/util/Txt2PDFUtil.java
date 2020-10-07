package com.va.removeconsult.clouddisk.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsPSMDetector;
import org.springframework.stereotype.Component;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

@Component
public class Txt2PDFUtil {

	public String getFontPath() {
		String os = System.getProperty("os.name");
		if (os.startsWith("win") || os.startsWith("Win")) {
			// Windows 字体路径
			return "fonts/wqy-zenhei.ttc,0";
		} else {
			// Linux 字体路径，需手动将ttc放到/usr/share/fonts目录
			return "/usr/share/fonts/wqy-zenhei.ttc,0";
		}
	}

	public void convertPdf(File in, OutputStream out) throws Exception {
		Rectangle rect = new Rectangle(PageSize.A4);
		Document doc = new Document(rect);
		PdfWriter pw = PdfWriter.getInstance(doc, out);
		doc.open();
		BaseFont songFont = BaseFont.createFont(this.getFontPath(), BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		Font font = new Font(songFont, 12, Font.NORMAL);
		Paragraph paragraph = new Paragraph();
		paragraph.setFont(font);
		String charset = getTxtCharset(new FileInputStream(in));
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(in), charset));
		String line = null;
		while ((line = reader.readLine()) != null) {
			paragraph.add(line + "\n");
		}
		reader.close();
		doc.add(paragraph);
		doc.close();
		pw.flush();
		pw.close();
	}

	private String getTxtCharset(InputStream in) throws Exception {
		int lang = nsPSMDetector.CHINESE;
		nsDetector det = new nsDetector(lang);
		CharsetDetectionObserverImpl cdoi = new CharsetDetectionObserverImpl();
		det.Init(cdoi);
		BufferedInputStream imp = new BufferedInputStream(in);
		byte[] buf = new byte[1024];
		int len;
		boolean isAscii = true;
		while ((len = imp.read(buf, 0, buf.length)) != -1) {
			if (isAscii) {
				isAscii = det.isAscii(buf, len);
			}
			if (!isAscii) {
				if (det.DoIt(buf, len, false)) {
					break;
				}
			}
		}
		imp.close();
		in.close();
		det.DataEnd();
		if (isAscii) {
			return "ASCII";
		} else if (cdoi.getCharset() != null) {
			return cdoi.getCharset();
		} else {
			String[] prob = det.getProbableCharsets();
			if (prob != null && prob.length > 0) {
				return prob[0];
			}
			return "GBK";
		}
	}

}
