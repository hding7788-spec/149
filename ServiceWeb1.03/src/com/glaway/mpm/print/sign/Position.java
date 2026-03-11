/**
 * 电子签名定制程序
 * 国睿信维
 */
package com.glaway.mpm.print.sign;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;

/**
 *
 * @author clli
 * @version 0.1
 * @date 2016-03-16
 *
 */
public class Position{

	// The width dimension
	public float height;
	// The height dimension
	public float width;
	// coordinate text
	public String text;
	// coordinate value
	public Image image = null;

	public String activityName;

	public Image fmImage = null;
	public Image seal = null;
	public Image fmSeal = null;
	public Image vakidImage = null;//现行有效章_20170310
	private float fontSize;
	private int fontStyle;
	private Color fontColor;

	public String qrCodeInfoFMText;

	public String qrCodeInfoContentText;

	public Position() {
		this(0.0F, 0.0F,null);
	}

	public Position(float width, float height,String text) {
		this.width = width;
		this.height = height;
		this.text=text;
	}

	public void setSize(float width, float height,String text) {
		this.width = width;
		this.height = height;
		this.text=text;
	}

	public Position(float width, float height,String text, boolean isFm) {
		this.width = width;
		this.height = height;
		if (isFm) {
			this.qrCodeInfoFMText = text;
		} else {
			this.qrCodeInfoContentText = text;
		}
	}

	/**
	 * 设置图片
	 * @param imgPath  图片全路径
	 * @param percent  缩放比例
	 * @param absoluteX  X轴坐标位置
	 * @param absoluteY  Y轴坐标位置
	 */
	public void setImage(String imgPath,float percent,float absoluteX,float absoluteY){
		try {
			image = Image.getInstance(imgPath);
			// 设置图片大小
//			image.scaleAbsolute(60, 80);
			//设置缩放百分比
			image.scalePercent(percent);
			// 设置位置
			image.setAbsolutePosition(absoluteX, absoluteY);
			image.setAlignment(Element.ALIGN_CENTER);
		} catch (BadElementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置图片
	 * @param imgPath  图片全路径
	 * @param percent  缩放比例
	 * @param absoluteX  X轴坐标位置
	 * @param absoluteY  Y轴坐标位置
	 */
	public void setSeal(String imgPath,float percent,float absoluteX,float absoluteY){
		try {
			seal = Image.getInstance(imgPath);
			//设置缩放百分比
			seal.scalePercent(percent);
			// 设置位置
			seal.setAbsolutePosition(absoluteX, absoluteY);
			seal.setAlignment(Element.ALIGN_CENTER);
		} catch (BadElementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置图片
	 * @param imgPath  图片全路径
	 * @param percent  缩放比例
	 * @param absoluteX  X轴坐标位置
	 * @param absoluteY  Y轴坐标位置
	 */
	public void setFmSeal(String imgPath,float percent,float absoluteX,float absoluteY){
		try {
			fmSeal = Image.getInstance(imgPath);
			//设置缩放百分比
			fmSeal.scalePercent(percent);
			// 设置位置
			fmSeal.setAbsolutePosition(absoluteX, absoluteY);
			fmSeal.setAlignment(Element.ALIGN_CENTER);
		} catch (BadElementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Image getFmSeal() {
		return fmSeal;
	}

	/**
	 * 设置图片, 以图片原大小输入
	 * @param imgPath  图片全路径
	 * @param absoluteX  X轴坐标位置
	 * @param absoluteY  Y轴坐标位置
	 */
	public void setImage(String imgPath, float absoluteX, float absoluteY){
		try {
			image = Image.getInstance(imgPath);
			// 设置图片大小
			image.scaleAbsolute(62, 62);
			// 设置位置
			image.setAbsolutePosition(absoluteX, absoluteY);
			image.setAlignment(Element.ALIGN_CENTER);
		} catch (BadElementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置图片, 以图片原大小输入
	 * @param imgPath  图片全路径
	 * @param absoluteX  X轴坐标位置
	 * @param absoluteY  Y轴坐标位置
	 */
	public void setFMImage(String imgPath, float percent, float absoluteX, float absoluteY){
		try {
			fmImage = Image.getInstance(imgPath);
			//设置缩放百分比
			fmImage.scalePercent(percent);
			// 设置位置
			fmImage.setAbsolutePosition(absoluteX, absoluteY - fmImage.getHeight());
			fmImage.setAlignment(Element.ALIGN_CENTER);
		} catch (BadElementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置图片, 以图片原大小输入
	 * @param imgPath  图片全路径
	 * @param absoluteX  X轴坐标位置
	 * @param absoluteY  Y轴坐标位置
	 */
	public void setFMImage(String imgPath, float absoluteX, float absoluteY){
		try {
			fmImage = Image.getInstance(imgPath);
			// 设置图片大小
			fmImage.scaleAbsolute(70, 70);
			// 设置位置
			fmImage.setAbsolutePosition(absoluteX, absoluteY - fmImage.getHeight());
			fmImage.setAlignment(Element.ALIGN_CENTER);
		} catch (BadElementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setImage(InputStream imgis,float percent,float absoluteX,float absoluteY){
		try {
			byte[] imgb = new byte[imgis.available()];
			imgis.read(imgb);

			image = Image.getInstance(imgb);
			// 设置图片大小
//			image.scaleAbsolute(60, 80);
			//设置缩放百分比
			image.scalePercent(percent);
			// 设置位置
			image.setAbsolutePosition(absoluteX, absoluteY);
			image.setAlignment(Element.ALIGN_CENTER);
		} catch (BadElementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置图片, 以图片原大小输入
	 * @param imgPath  图片全路径
	 * @param absoluteX  X轴坐标位置
	 * @param absoluteY  Y轴坐标位置
	 */
	public void setVakidImage(String imgPath, float percent, float absoluteX, float absoluteY) {
		try {
			vakidImage = Image.getInstance(imgPath);
			//设置缩放百分比
			vakidImage.scalePercent(percent);
			// 设置位置
			vakidImage.setAbsolutePosition(absoluteX, absoluteY - vakidImage.getHeight());
			vakidImage.setAlignment(Element.ALIGN_CENTER);
		} catch (BadElementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Image getVakidImage() {
		return vakidImage;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public String getText() {
		return text;
	}

	public Image getImage() {
		return image;
	}

	public Image getFmImage() {
		return fmImage;
	}

	public Image getSeal() {
		return seal;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Position) {
			Position pos = (Position) obj;
			return (width == pos.width) && (height == pos.height);
		}
		return false;
	}

	@Override
	public int hashCode() {
		float sum = width + height;
		return Math.round(sum * (sum + 1) / 2 + width);
	}

	@Override
	public String toString() {
		return getClass().getName() + "[width=" + width + ",height=" + height + "]";
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}

	public int getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}

	public Color getFontColor() {
		return fontColor;
	}

	public void setFontColor(Color fontColor) {
		this.fontColor = fontColor;
	}

	public String getQrCodeInfoFMText() {
		return qrCodeInfoFMText;
	}

	public String getQrCodeInfoContentText() {
		return qrCodeInfoContentText;
	}

}