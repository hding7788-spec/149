package com.glaway.mpm.print.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class GeneralImageTest {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		try{
			String text = "ADK11闭合回路";

			Font font = new Font("宋体", Font.BOLD, 30);
			FontMetrics metrics = new FontMetrics(font) {
			};
			Rectangle2D bounds = metrics.getStringBounds(text, null);
			System.out.println(bounds.getWidth());

			//int width = 22 * aa.length();//每个字符22
			int width = (int) (bounds.getWidth() + 2);
			int height = 50;//每个字符5

			FileImageCreator creator = new FileImageCreator(new SimpleDrawer(), "E:\\img.jpg");
			creator.setWidth(width); //图片宽度
			creator.setHeight(height); //图片高度
			creator.setFontSize(30); //字体大小
			creator.setFontName("宋体");//字体
			creator.setBgColor(Color.WHITE);//背景颜色
			creator.setFontColor(Color.RED);//字体颜色
			creator.setFontBold(Font.BOLD);//是否加粗
			creator.setRectColor(Color.RED);//外框颜色
			creator.setBorderWidth(5.0f);//外框粗细
			creator.generateImage(text);
			System.out.println("ok");

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}