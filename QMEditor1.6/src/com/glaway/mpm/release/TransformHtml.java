/**
 * 
 */
package com.glaway.mpm.release;

import java.io.IOException;

import org.dom4j.DocumentException;

public class TransformHtml {

	public static void main(String[] args) throws Exception {

		// ====================Test 客户端预览=========================

		// ProcessInfoReleaseController
		// .processInfoPview("C:\\Users\\MosesX\\Desktop\\AL2_907_1459`单刀双掷开关`装配工艺`AL2_907_1459`多基地面雷达");

		ProcessInfoReleaseController
				.processInfoPview("C:\\Users\\GHOST\\Desktop\\1389169714996");
		// ProcessInfoReleaseController
		// .processInfoPview("C:\\Users\\MosesX\\Desktop\\AL2_907_1459`单刀双掷开关`装配工艺`AL2_907_1459`多基地面雷达");

		// 测试注释集
		// ProcessInfoReleaseController
		// .processInfoPview("C:\\Users\\MosesX\\Desktop\\annotest\\AL2.2001.2`al2_2001_2`装配工艺`AL2.2001.2`多基地面雷达");

		// ProcessInfoReleaseController
		// .processInfoPview("C:\\Users\\MosesX\\Desktop\\AL1.000001.270`发动机整机层含底板_`装配工艺`AL1.000001.270`多基地面雷达");

		// ====================Test 服务器断发布=========================
		// ApacheZipUtil.compress("C:\\Users\\MosesX\\Desktop\\AL2_907_1459`单刀双掷开关`装配工艺`AL2_907_1459`多基地面雷达",
		// "C:\\Users\\MosesX\\Desktop\\AL2_907_1459`单刀双掷开关`装配工艺`AL2_907_1459`多基地面雷达.zip");
		// String targetPath = System.getProperty("user.home") + "\\3D_PView";
		// String path = ProcessInfoReleaseController.processInfoRelease(
		// "C:\\Users\\MosesX\\Desktop\\AL2_907_1459`单刀双掷开关`装配工艺`AL2_907_1459`多基地面雷达.zip", targetPath);
		// System.out.println(path);
	}
}