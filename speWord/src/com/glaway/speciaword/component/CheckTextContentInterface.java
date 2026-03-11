/**
 * 
 */
package com.glaway.speciaword.component;

/**
 * @author MosesX 用户自定义检查文本长度、存储位置接口
 * 
 */
public interface CheckTextContentInterface {

	public boolean checkContentLength(String html);

	public String localImagePath();

}
