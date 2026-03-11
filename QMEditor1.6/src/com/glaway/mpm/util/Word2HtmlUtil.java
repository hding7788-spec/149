package com.glaway.mpm.util;

import java.io.File;

import com.glaway.mpm.visual.log.VaLogger;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class Word2HtmlUtil {
	private static VaLogger logger = VaLogger.getLogger(Word2HtmlUtil.class);
	static final int wdDoNotSaveChanges = 0;// 不保存待定的更改。
	public static ActiveXComponent app = null;

	public static void main(String[] args) {
		String path = args[0];
		genPdf(new File(path));
	}

	public static File genTechnicsDescHtml(File wordFile){
		return genHtml(wordFile,"technics_desc");
	}

	public static File genHtml(File wordFile){
		String fileName = wordFile.getName();
		fileName = fileName.substring(0,fileName.lastIndexOf("."));
		String htmlFilename = System.getProperty("java.io.tmpdir") + fileName+".mht";
		String wordFilePth = wordFile.getAbsolutePath();

		File htmlFile = new File(htmlFilename);
		if (htmlFile.exists()) {
			htmlFile.delete();
		}
		long start = System.currentTimeMillis();
		ActiveXComponent app = null;
		try {
			app = new ActiveXComponent("Word.Application");
			app.setProperty("Visible", new Variant(false));
			Dispatch docs = app.getProperty("Documents").toDispatch();
			Dispatch doc = Dispatch.call(docs, "Open", wordFilePth, false, true).toDispatch();
			Dispatch.call(doc, "SaveAs", htmlFilename, 9);
			Dispatch.call(doc, "Close", false);
			long end = System.currentTimeMillis();
			logger.info("word： "+fileName + " 文件转化  " +htmlFilename+"用时： " + (end- start)+"ms");
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			if (app != null){
				app.invoke("Quit", new Variant[] { new Variant(wdDoNotSaveChanges) });
			} else {
				return null;
			}
		}

		return htmlFile;
	}

	public static File genHtml(File wordFile,String fileName){
		String htmlFilename = System.getProperty("java.io.tmpdir") + fileName+".mht";
		String wordFilePth = wordFile.getAbsolutePath();

		File htmlFile = new File(htmlFilename);
		if (htmlFile.exists()) {
			htmlFile.delete();
		}
		long start = System.currentTimeMillis();
		ActiveXComponent app = null;
		try {
			app = new ActiveXComponent("Word.Application");
			app.setProperty("Visible", new Variant(false));
			Dispatch docs = app.getProperty("Documents").toDispatch();
			Dispatch doc = Dispatch.call(docs, "Open", wordFilePth, false, true).toDispatch();
			Dispatch.call(doc, "SaveAs", htmlFilename, 9);
			Dispatch.call(doc, "Close", false);
			long end = System.currentTimeMillis();
			logger.info("word： "+fileName + " 文件转化  " +htmlFilename+"用时： " + (end- start)+"ms");
		}finally{
			if (app != null){
				app.invoke("Quit", new Variant[] { new Variant(wdDoNotSaveChanges) });
			}
		}

		return htmlFile;
	}

	public static File genPdf(File wordFile){
		String fileName = wordFile.getName();
		fileName = fileName.substring(0,fileName.lastIndexOf("."));
		String wordFilePth = wordFile.getAbsolutePath();
		String pdfFilename = wordFile.getParent()+File.separator + fileName+".pdf";

		File htmlFile = new File(pdfFilename);
		if (htmlFile.exists()) {
			htmlFile.delete();
		}
		long start = System.currentTimeMillis();
		ActiveXComponent app = null;
		try {
			app = new ActiveXComponent("Word.Application");
			app.setProperty("Visible", new Variant(false));
			Dispatch docs = app.getProperty("Documents").toDispatch();
			Dispatch doc = Dispatch.call(docs, "Open", wordFilePth, false, true).toDispatch();

			Dispatch.call(doc, "SaveAs", pdfFilename, 17);
			Dispatch.call(doc, "Close", false);
			long end = System.currentTimeMillis();
			logger.info("word： "+fileName + " 文件转化  " +pdfFilename+"用时： " + (end- start)+"ms");
		}finally{
			if (app != null){
				app.invoke("Quit", new Variant[] { new Variant(wdDoNotSaveChanges) });
			}
		}

		return htmlFile;
	}

    public static File genWord2Pdf(File wordFile) throws Exception {
        String fileName = wordFile.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        //String pdfFilename = System.getProperty("java.io.tmpdir") + fileName + ".pdf";
        String wordFilePth = wordFile.getAbsolutePath();
		String pdfFilename = wordFile.getParent()+File.separator + fileName+".pdf";

        File htmlFile = new File(pdfFilename);
        if (htmlFile.exists()) {
            htmlFile.delete();
        }
        long start = System.currentTimeMillis();
//        ActiveXComponent app = null;
        try {
            // app = new ActiveXComponent("Word.Application");
            app.setProperty("Visible", new Variant(false));
            Dispatch docs = app.getProperty("Documents").toDispatch();
            Dispatch doc = Dispatch.call(docs, "Open", wordFilePth, false, true).toDispatch();

            Dispatch.call(doc, "SaveAs", pdfFilename, 17);
            Dispatch.call(doc, "Close", false);
            long end = System.currentTimeMillis();
            logger.info("word： " + fileName + " 文件转化  " + pdfFilename + "用时： " + (end - start) + "ms");
        } catch (Exception e) {
            throw new Exception("WORD转PDF时出错，请检查本地JRE环境bin路径下是否有jacob-*-x86.dll文件。\r\nWORD文件：" + wordFile.getPath() + "\r\n错误信息：" + e.getLocalizedMessage());
        } catch (Throwable t) {
            throw new Exception("WORD转PDF时出错，请检查本地JRE环境bin路径下是否有jacob-*-x86.dll文件。\r\nWORD文件：" + wordFile.getPath() + "\r\n错误信息：" + t.getLocalizedMessage());
        }
        // finally{
        // if (app != null){
        // app.invoke("Quit", new Variant[] { new Variant(wdDoNotSaveChanges) });
        // }
        // }

        return htmlFile;
    }

	public static void openActiveXComponent() throws ComFailException {
        app = new ActiveXComponent("Word.Application");
    }

    public static void colseActiveXComponent() throws ComFailException {
        if (app != null) {
            app.invoke("Quit", new Variant[] { new Variant(0) });// 不保存待定的更改。
        }
    }
}
