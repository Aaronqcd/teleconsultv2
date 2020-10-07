package com.va.removeconsult.clouddisk.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;



public class VerificationCodeFactory {

	private char[] alternative;
	private static final Random RANDOM = new Random();
	private int width;
	private int height;
	private int maxLine;
	private int maxOval;
	private int charSize;
	
	
	public VerificationCodeFactory(int charSize,int maxLine,int maxOval,char... alternative) {
		
		if (alternative == null || alternative.length == 0 || charSize <=0 || maxLine <0 || maxOval <0) {
			throw new IllegalArgumentException("��֤�빤�����������������С�������0����������������Բ��������ڵ���0�������ṩһ����ѡ�ַ���");
		} else {
			this.alternative = alternative;
			this.charSize=charSize;
			this.maxLine=maxLine;
			this.maxOval=maxOval;
			this.height=charSize+10;
		}
	}
	
	
	public VerificationCode next(int length) {
		if(length<=0) {
			throw new IllegalArgumentException("验证码工厂：参数有误，字体大小必须大于0，最大行数和最大椭圆数必须大于等于0，至少提供一个候选字符。");
		}
		StringBuffer codeBuffer = new StringBuffer();
		VerificationCode result = new VerificationCode();
		
		width=(length+1)*charSize;
		for (int i = 0; i < length; i++) {
			codeBuffer.append(alternative[RANDOM.nextInt(alternative.length)]);
		}
		result.setCode(codeBuffer.toString());
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, width, height);
		
		graphics.setColor(Color.BLACK);
		graphics.drawRect(0, 0, width-1, height-1);
		
		for(int i=0;i<maxLine;i++) {
			
			graphics.setColor(getRandomColor());
			
			graphics.setStroke(new BasicStroke(RANDOM.nextInt(charSize)/2+1));
			
			int start_x=RANDOM.nextInt(width);
			int start_y=RANDOM.nextInt(height);
			int end_x=RANDOM.nextInt(width);
			int end_y=RANDOM.nextInt(height);
			graphics.drawLine(start_x, start_y, end_x, end_y);
		}
		for(int i=0;i<maxOval;i++) {
			
			graphics.setColor(getRandomColor());
			graphics.setStroke(new BasicStroke(RANDOM.nextInt(charSize)/2+1));
			int start_x=RANDOM.nextInt(width);
			int start_y=RANDOM.nextInt(height);
			int end_x=RANDOM.nextInt(width);
			int end_y=RANDOM.nextInt(height);
			graphics.drawOval(start_x, start_y, end_x, end_y);
		}
		
		
		Font font = new Font("songti", Font.BOLD, charSize);
		
		for(int i=0;i<codeBuffer.length();i++) {
			
			graphics.setColor(getRandomColor());
			
			graphics.setFont(font.deriveFont(AffineTransform.getRotateInstance(Math.toRadians(RANDOM.nextInt(90)),0,-charSize/2)));
			graphics.drawString(String.valueOf(codeBuffer.charAt(i)), (i+1)*charSize, charSize);
		}
		
		result.setImage(image);
		return result;
	}
	
	
	private static Color getRandomColor() {
		int r=RANDOM.nextInt(255);
		int g=RANDOM.nextInt(255);
		int b=RANDOM.nextInt(255);
		
		while(r>200&&g>200&&b>200) {
			r=RANDOM.nextInt(255);
			g=RANDOM.nextInt(255);
			b=RANDOM.nextInt(255);
		}
		return new Color(r, g, b);
	}

}
