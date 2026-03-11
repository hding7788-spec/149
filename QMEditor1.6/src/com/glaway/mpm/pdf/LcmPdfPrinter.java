package com.glaway.mpm.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.glaway.mpm.view.NewTechnicsPart;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.AcroFields.FieldPosition;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;

/**
 * 组件名称：PDF打印输出组件<br>
 * 创建时间：2013-8-16 版本:1.0<br>
 * 更新时间：2013-8-23 版本:1.5<br>
 *
 * @author Y.Huang - 黄勇 <h1>功能支持：</h1>
 *         <ul>
 *         <li>在PDF模板指定位置插入文本（包含多行文本显示）</li>
 *         <li>在PDF模板指定位置插入图片，图宽或高超出范围则图片自动按比例缩小到合适大小</li>
 *         <li>在PDF模板指定位置插入html代码，html代码将解析为显示文本及图片---此功能主要为实现 图文混排，暂时不支持复杂样式。</li>
 *         </ul>
 *         <h1>使用方法</h1>
 *         <ul>
 *         本类方法addText（*）、addImage（*）、addHtml（*）、print（*）对外开放。
 *         其中前三个方法用于用户添加打印数据，最后一个方法用于执行PDF文件的打印
 *         </ul>
 *         * <h1>本类一依赖包列表:</h1>
 *         <ul>
 *         <li>itext-asian-5.4.3.jar</li>
 *         <li>itextpdf-5.4.3.jar</li>
 *         <li>xmlworker-5.4.3.jar</li>
 *         </ul>
 */
public class LcmPdfPrinter {
	public final static String PAGECOUNTKEY = "total";
	public final static String PAGEKEY = "page";
	public final static String BarcodeQRCode = "barcode";
	public final static String FP_BG_COLOR = "bgcolor";
	public final static String FP_TEXT_COLOR = "textcolor";
	public final static String FP_TEXT_FONT = "textfont";
	public final static String FP_TEXT_SIZE = "textsize";

	private float offset = 10f;
	public BaseFont defaultFont;
	public float defaultFontSize;

	private Map<String, String> template;
	private Map<String, List<PDFText>> valuesList;
	private Map<String, List<PDFImage>> imageList;
	private Map<String, List<PDFHtml>> htmlList;
	private Map<String,List<PDFFile>> pdfFileList;
	private Map<String,List<PDFFile>> pdfFileImageList;
	private Image barImage ;
	private MyXMLWorkerFontProvider fontProvider;
	public LcmPdfPrinter() {
		try {
			defaultFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			barImage = genBarcodeQRCode();
		} catch (Exception e) {
			System.out.println("默认字体不存在，你需要提供iText的亚洲语言包iTextAsian或者设置你的本地语言");
			e.printStackTrace();
		}
		template = new LinkedHashMap<String, String>();
		valuesList = new HashMap<String, List<PDFText>>();
		imageList = new HashMap<String, List<PDFImage>>();
		htmlList = new HashMap<String, List<PDFHtml>>();
		pdfFileList = new HashMap<String, List<PDFFile>>();
		pdfFileImageList = new HashMap<String, List<PDFFile>>();
	}

	private Image genBarcodeQRCode() throws BadElementException{
        BarcodeQRCode qrcode = new BarcodeQRCode("卫星研究所812".trim(), 1, 1, null);
        Image qrcodeImage = qrcode.getImage();
//        qrcodeImage.setAbsolutePosition(10,600);
        qrcodeImage.scalePercent(200);
        return qrcodeImage;
	}
	public static void mergePdfFiles(String[] files, String savepath) {
		try {
			Document document = new Document(new PdfReader(files[0]).getPageSize(1));
			PdfCopy copy = new PdfCopy(document, new FileOutputStream(savepath));
			document.open();

			for (int i = 0; i < files.length; i++) {
				PdfReader reader = new PdfReader(files[i]);
				int n = reader.getNumberOfPages();
				for (int j = 1; j <= n; j++) {
					document.newPage();
					PdfImportedPage page = copy.getImportedPage(reader, j);
					copy.addPage(page);
				}
			}

			document.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	public Rectangle getRectangleInfo(String templName, String key) {
		Rectangle ret = null;
		try {
			PdfReader reader = new PdfReader(template.get(templName));
			AcroFields af = reader.getAcroFields();
			List<FieldPosition> fpList = af.getFieldPositions(key);
			if (fpList == null || fpList.size() == 0) {
				throw new Exception("在模板中未找到指定的文本域:" + key);
			}
			ret = fpList.get(0).position;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 为指定模板添加一个文本域数据
	 */
	public void addText(String templName, PDFText value) {
		if (!valuesList.containsKey(templName)) {
			valuesList.put(templName, new ArrayList<PDFText>());
		}
		List<PDFText> vals = valuesList.get(templName);
		vals.add(value);
		valuesList.put(templName, vals);
	}

	public PDFText addText(String templName, String key, String value) {
		if (!valuesList.containsKey(templName)) {
			valuesList.put(templName, new ArrayList<PDFText>());
		}

		List<PDFText> vals = valuesList.get(templName);
		PDFText v = new PDFText(key, value);
		vals.add(v);
		return v;
	}

	public PDFImage addImage(String templName, Image image) {
		return addImage(templName, null, image);
	}

	public PDFImage addImage(String templName, String imageFileName) {
		return addImage(templName, null, imageFileName);
	}

	public PDFImage addImage(String templName, String key, Image image) {
		if (!imageList.containsKey(templName)) {
			imageList.put(templName, new ArrayList<PDFImage>());
		}
		List<PDFImage> vals = imageList.get(templName);
		PDFImage img = new PDFImage(key, image);
		vals.add(img);
		return img;
	}

	public PDFImage addImage(String templName, String key, String imageFileName) {
		try {
			Image img = Image.getInstance(imageFileName);
			return addImage(templName, key, img);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	//add by machongqi 2015-6-8
	public PDFHtml addHtml2Blod(String templName, String key, String html,int fontBlodOrNormal) {
		return addHtml2Blod(templName, key, new StringReader(html),fontBlodOrNormal);
	}

	public PDFHtml addHtml2Blod(String templName, String key, Reader reader,int fontBlodOrNormal) {
		if (!htmlList.containsKey(templName)) {
			htmlList.put(templName, new ArrayList<PDFHtml>());
		}
		List<PDFHtml> vals = htmlList.get(templName);
		PDFHtml html = new PDFHtml(key, reader);
		html.setFontBlodOrNormal(fontBlodOrNormal);
		vals.add(html);
		return html;
	}
	//add by machongqi end

	public PDFHtml addHtml(String templName, String key, String html) {
		return addHtml(templName, key, new StringReader(html));
	}

	public PDFHtml addHtml(String templName, String key, File file)
			throws Exception {
		return addHtml(templName, key, new FileReader(file));
	}

	public PDFHtml addHtml(String templName, String key, Reader reader) {
		if (!htmlList.containsKey(templName)) {
			htmlList.put(templName, new ArrayList<PDFHtml>());
		}
		List<PDFHtml> vals = htmlList.get(templName);
		PDFHtml html = new PDFHtml(key, reader);
		vals.add(html);
		return html;
	}

	private PDFFile addPDFPage(String templName,String fieldKey, File file ,int pageNumber) throws FileNotFoundException
		{
		if (!pdfFileList.containsKey(templName)) {
			pdfFileList.put(templName, new ArrayList<PDFFile>());
		}
		List<PDFFile> vals = pdfFileList.get(templName);
		PDFFile pdf = new PDFFile(pageNumber, fieldKey, new FileInputStream(file));
		vals.add(pdf);
		return pdf;
	}

	private PDFFile addPDFPage(String templName, File file ,int pageNumber) throws FileNotFoundException
	{
		if (!pdfFileList.containsKey(templName)) {
			pdfFileList.put(templName, new ArrayList<PDFFile>());
		}
		List<PDFFile> vals = pdfFileList.get(templName);
		PDFFile pdf = new PDFFile(pageNumber, new FileInputStream(file));
		vals.add(pdf);
		return pdf;
	}

	private PDFFile addPDFImagePage(String templName,String key, File file ,int pageNumber,float left,float top) throws FileNotFoundException
	{
		if (!pdfFileImageList.containsKey(templName)) {
			pdfFileImageList.put(templName, new ArrayList<PDFFile>());
		}
		List<PDFFile> vals = pdfFileImageList.get(templName);
		PDFFile pdf = new PDFFile(pageNumber,key, new FileInputStream(file));
		pdf.setLeft(left);
		pdf.setTop(top);
		vals.add(pdf);
		return pdf;
	}

	public List<String> addImage(String templPath, String key, File pdfFileName) {
			List<String> list = new ArrayList<String>();
			PdfReader reader = null;
			InputStream is = null;
			ByteArrayOutputStream baos = null;
			PdfStamper stamp =  null;
			try {
				is = new FileInputStream(pdfFileName);
				baos = new ByteArrayOutputStream();
				reader = new PdfReader(is);
				int cnt = reader.getNumberOfPages();
				stamp = new PdfStamper(reader, baos);
				for(int j = 1; j <= cnt; j++){
					PdfImportedPage  pdfImportedPage = stamp.getImportedPage(reader, j);
					Image  img = Image.getInstance(pdfImportedPage);
					byte[] bytes = img.getRawData();
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
//					BufferedImage  bi = ImageIO.read(bais);
//					int width = bi.getWidth();
//					int height = bi.getHeight();
//
//					//绘制放大后的图片
//					bi.getGraphics().drawImage(bi, 0, 0, width, height, null);
//
//					FileOutputStream out = new FileOutputStream("d:\\kk.jpeg");
//					JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//					encoder.encode(bi);

					String templateName = pdfFileName.getName()+ System.currentTimeMillis()+"I"+UUID.randomUUID()+j;
					this.addTempl(templateName, templPath);
					this.addImage(templateName, key, img);
					list.add(templateName);
				}
			} catch (BadElementException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}finally{
				try {
					stamp.close();
					baos.close();
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return list;
	}


	public List<String> addPDFFile(String templPath,File soureFile) throws IOException{
		FileInputStream fis = new FileInputStream(soureFile);
		PdfReader reader = new PdfReader(fis);
		int cnt = reader.getNumberOfPages();
		List<String> templateNameList = new ArrayList<String>();
		for(int i=1;i<=cnt;i++){
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			String templateName = soureFile.getName()+ System.currentTimeMillis()+"I"+UUID.randomUUID()+i;
			this.addTempl(templateName, templPath);
			this.addPDFPage(templateName, soureFile, i);
			templateNameList.add(templateName);
		}
		return templateNameList;

	}

	public List<String> addPDFFile(String templateName, String templatePath, String fieldKey, File file,int page) throws IOException {
		List<String> templateNameList = new ArrayList<String>();
		InputStream is = null;
		String template = "";
		try {
			is = new FileInputStream(file);
			PdfReader reader = new PdfReader(is);
			int pages = reader.getNumberOfPages();
			for (int i = 1; i <= pages; i++) {
				template = templateName + "_" + (page + i);
				this.addTempl(template, templatePath);
				addPDFPage(template, fieldKey, file, i);
				templateNameList.add(template);
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return templateNameList;
	}

	public List<String> addPDFImage2(String templateName,String templatePath,String key,File soureFile,float left,float top,int gyfbPage) throws IOException{
		FileInputStream fis = new FileInputStream(soureFile);
		PdfReader reader = new PdfReader(fis);
		int cnt = reader.getNumberOfPages();
		List<String> templateNameList = new ArrayList<String>();
		String template = "";
		for(int i=1;i<=cnt;i++){
			template = templateName + "_" + (gyfbPage+i);
			this.addTempl(template, templatePath);
			if (!pdfFileImageList.containsKey(template)) {
				pdfFileImageList.put(template, new ArrayList<PDFFile>());
			}
			List<PDFFile> vals = pdfFileImageList.get(template);
			PDFFile pdf = new PDFFile(i,key, new FileInputStream(soureFile));
			pdf.setLeft(left);
			pdf.setTop(top);
			vals.add(pdf);
			templateNameList.add(template);
		}
		return templateNameList;

	}


	/**
	 * 添加 pdf 到指定的区域  ，如果pdf包含多页  ,每页对应templatePath模板
	 * @param templatePath  模板路径
	 * @param key     文本域名称
	 * @param soureFile  要添加的到 文本域位置的pdf文件
	 * @param left  pdf左边距    单位厘米 //  先用0测试，测试真实距离再改
	 * @param top   pdf上边距   单位厘米//  先用0测试，测试真实距离再改
	 * @return 每个模板的建列表
	 * @throws IOException
	 */
	public List<String> addPDFImage(String templatePath,String key,File soureFile,float left,float top) throws IOException{
		FileInputStream fis = new FileInputStream(soureFile);
		PdfReader reader = new PdfReader(fis);
		int cnt = reader.getNumberOfPages();
		List<String> templateNameList = new ArrayList<String>();
		for(int i=1;i<=cnt;i++){
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			String templateName = soureFile.getName()+ System.currentTimeMillis()+"I"+UUID.randomUUID()+i;
			this.addTempl(templateName, templatePath);
			this.addPDFImagePage(templateName,key, soureFile, i,left,top);
			templateNameList.add(templateName);
		}
		return templateNameList;

	}
	public List<String> addTechnicStatePDFImage(String templatePath,String templateName,String key,File soureFile,float left,float top) throws IOException{
		FileInputStream fis = new FileInputStream(soureFile);
		PdfReader reader = new PdfReader(fis);
		int cnt = reader.getNumberOfPages();
		List<String> templateNameList = new ArrayList<String>();
		for(int i=1;i<=cnt;i++){
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			this.addTempl(templateName, templatePath);
			this.addPDFImagePage(templateName,key, soureFile, i,left,top);
			templateNameList.add(templateName);
		}
		return templateNameList;

	}
	/**
	 * 添加一个模板页
	 */
	public void addTempl(String templName, String templPath) {
		template.put(templName, templPath);
		this.addImage(templName, BarcodeQRCode, barImage);
	}

	public void removeTempl(String templName) {
		template.remove(templName);
	}

	/**
	 * <h1>将输出流写入指定的路径的文件中</h1> 返回 File 生成的新的PDF文件
	 */
	public File print(String targetFileName) throws Exception {
		File ret = new File(targetFileName);
		FileOutputStream fos = new FileOutputStream(ret);
		ByteArrayOutputStream bos = print();
		bos.writeTo(fos);
		fos.close();
		bos.close();
		return ret;
	}

	/**
	 * <h1>合并PDF页面</h1>
	 * <p>
	 * 为了不生成多余的文件，直接将合并后的文件写到一个输出流返回
	 * </p>
	 *
	 * 返回 ByteArrayOutputStream 是一个已合并后的输出流
	 */
	public ByteArrayOutputStream print() throws Exception {
		ByteArrayOutputStream[] pdfStream = write();
		ByteArrayOutputStream targetos = new ByteArrayOutputStream();
		Document doc = new Document();
		PdfCopy pdfCopy = new PdfCopy(doc, targetos);
		doc.open();

		PdfImportedPage impPage = null;
		for (int i = 0; i < pdfStream.length; i++) {
			impPage = pdfCopy.getImportedPage(new PdfReader(pdfStream[i].toByteArray()), 1);
			pdfCopy.addPage(impPage);
		}
		doc.close();
		return targetos;
	}

	private ByteArrayOutputStream[] write() throws Exception {
		int templateCount = template.size();
		ByteArrayOutputStream[] tempStream = new ByteArrayOutputStream[templateCount];

		if (templateCount == 0) {
			return tempStream;
		}

		Iterator<String> it = template.keySet().iterator();
		int index = 0;
		while (it.hasNext()) {
			tempStream[index] = new ByteArrayOutputStream();

			String tmplKey = it.next();
			String fileName = template.get(tmplKey);

			PdfReader reader = null;
			try {
				// 忽略不存在的模板
				reader = new PdfReader(fileName);
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

			PdfStamper stamp = new PdfStamper(reader, tempStream[index]);
			AcroFields fields = stamp.getAcroFields();

			fields.setField(PAGEKEY, String.valueOf(index + 1));
			fields.setField(PAGECOUNTKEY, String.valueOf(templateCount));

			index++;

			List<PDFText> values = valuesList.get(tmplKey);
			if (values != null && values.size() > 0)
				this.writeText(stamp, values);

			List<PDFImage> images = imageList.get(tmplKey);
			if (images != null && images.size() > 0)
				this.writeImg(stamp, images, tmplKey);

			List<PDFHtml> htmls = htmlList.get(tmplKey);
			if (htmls != null && htmls.size() > 0)
				this.writeHtml(stamp, htmls);

			List<PDFFile> files = pdfFileList.get(tmplKey);
			if (files != null && files.size() > 0){
			    if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
                    this.writePdfFile2(stamp, files);
                }else{
                    this.writePdfFile(stamp, files);
                }
			}
			List<PDFFile> imgFiles = pdfFileImageList.get(tmplKey);
			if(imgFiles!=null&& imgFiles.size()>0){
				this.writePdfImageFile(stamp, imgFiles);
			}
			stamp.setFormFlattening(true);
			stamp.close();
			reader.close();
		}

		return tempStream;
	}

	private void writePdfFile(PdfStamper stamp, List<PDFFile> files) throws IOException, DocumentException
			{
		PdfContentByte pdfconbyte = stamp.getOverContent(1);
//		AcroFields fields = stamp.getAcroFields();

		for (PDFFile file : files) {
//			String fieldkey = file.getKey();
			int pageNumber = file.getPageNumber();
			InputStream is = file.getIs();
//			if (fields.getField(fieldkey) == null || is == null)
//				continue;

//			FieldPosition fp = fields.getFieldPositions(fieldkey).get(0);
//			// 定义临时文档的尺寸，以适应输入框的尺寸
//			Rectangle docRectangle = new Rectangle(fp.position);
			PdfReader reader = new PdfReader(is);
			PdfImportedPage  pdfImportedPage = stamp.getImportedPage(reader, pageNumber);
			Image  img = Image.getInstance(pdfImportedPage);
			pdfconbyte.addImage(img,  img.getScaledWidth(), 0f, 0f, img.getScaledHeight(), 0, 0);
			is.close();
		}
	}

    private void writePdfFile2(PdfStamper stamp, List<PDFFile> files) throws IOException, DocumentException
    {
        PdfContentByte pdfconbyte = stamp.getOverContent(1);
        AcroFields fields = stamp.getAcroFields();

        for (PDFFile file : files) {
            String fieldkey = file.getKey();
            FieldPosition fieldPosition = fields.getFieldPositions(fieldkey).get(0);
            int pageNumber = file.getPageNumber();
            InputStream is = file.getIs();
            PdfReader reader = new PdfReader(is);
            PdfImportedPage  pdfImportedPage = stamp.getImportedPage(reader, pageNumber);
            Image  image = Image.getInstance(pdfImportedPage);
            image.scaleAbsolute(460, 324);
            image.setAbsolutePosition(fieldPosition.position.getLeft()-41, fieldPosition.position.getTop()-445);
            pdfconbyte.addImage(image);
            is.close();
        }
    }

	private void writePdfImageFile(PdfStamper stamp, List<PDFFile> files) throws IOException, DocumentException
			{
		if (files == null || files.size() == 0) {
			return;
		}
		PdfContentByte pdfconbyte = stamp.getOverContent(1);
		AcroFields fields = stamp.getAcroFields();
		for (PDFFile file : files) {
			int pageNumber = file.getPageNumber();
			InputStream is = file.getIs();
			String fieldkey = file.getKey();
			PdfReader reader = new PdfReader(is);
			PdfImportedPage  pdfImportedPage = stamp.getImportedPage(reader, pageNumber);
			Image  img = Image.getInstance(pdfImportedPage);
			float imgX = 0f;
			float imgY = 0f;
			float imgWidth = 0f;
			float imgHeight = 0f;
		    if (fields.getField(fieldkey) != null) {
				FieldPosition fp = fields.getFieldPositions(fieldkey).get(0);
//				float[] wh = zoomImg(fp.position.getWidth(),
//						fp.position.getHeight(), img);
//				imgWidth = wh[0];
//				imgHeight = wh[1];
//
//				imgX = fp.position.getLeft();
//				imgY = fp.position.getTop() - imgHeight;

				imgWidth = img.getWidth();
				imgHeight = img.getHeight();
				imgX = fp.position.getLeft() - (file.getLeft()*28.3464f);
				imgY = fp.position.getTop()- imgHeight + ((file.getTop()*28.3464f));
				img.scaleAbsolute(imgWidth, imgHeight);
				img.setAbsolutePosition(imgX, imgY);
				pdfconbyte.addImage(img);
			}

			is.close();
		}
	}

	private void writeHtml(PdfStamper stamp, List<PDFHtml> htmls) throws Exception {
		PdfContentByte pdfconbyte = stamp.getOverContent(1);
		AcroFields fields = stamp.getAcroFields();

		Document document = null;
		ByteArrayOutputStream tempStream = null;
		for (PDFHtml pdfHtml : htmls) {
			String fieldkey = pdfHtml.getKey();
			Reader reader = pdfHtml.getHtmlsource();
			//add by machongqi 2015-6-8
			int fontBlodOrNormal=pdfHtml.getFontBlodOrNormal();
			//add by machongqi end
			if (fields.getField(fieldkey) == null || reader == null){
				continue;
			}

			tempStream = new ByteArrayOutputStream();
			FieldPosition fp = fields.getFieldPositions(fieldkey).get(0);

			// 定义临时文档的尺寸，以适应输入框的尺寸
			Rectangle docRectangle = new Rectangle(fp.position);
			document = new Document(docRectangle, 0, 0, 0, 0);

			BaseFont textFont = pdfHtml.getTextfont();// 设置字体
			float fontSize = pdfHtml.getFontSize();

			if (fontSize <= 0) {
				fontSize = 12f;
			}
			//modify by machongqi 2015-6-8
			boolean	 flag =simpleParseHtml(document, pdfHtml.getHtmlsource(), tempStream, textFont, fontSize,fontBlodOrNormal);
			//modify by machongqi end
			if(flag){
				PdfImportedPage pdfImportedPage = stamp.getImportedPage(new PdfReader(tempStream.toByteArray()), 1);
				float x = 0f;
				float y = 0f;
				if (document != null) {
					x = fp.position.getLeft();
					y = fp.position.getTop() - document.getPageSize().getHeight();
				}
				// *，宽度缩放，旋转，右倒,,x,y
				pdfconbyte.addTemplate(pdfImportedPage, 1f, 0f, 0f, 1f, x, y);
			}

		}
	}

	/**
	 * @deprecated 还无法使用的方法
	 */
	private void xmlWorkerParse(Document document, Reader reader,
			ByteArrayOutputStream os, BaseFont textFont, float fontSize)
			throws Exception {
		PdfWriter writer = PdfWriter.getInstance(document, os);
		document.open();
		InputStream iss = null;
		XMLWorkerHelper.getInstance().parseXHtml(writer, document, iss, null,
				new MyXMLWorkerFontProvider(textFont, fontSize,Font.NORMAL));
		document.close();
	}

	//modify by machongqi 2015-6-8
	private boolean simpleParseHtml(Document document, Reader reader,
			ByteArrayOutputStream os, BaseFont textFont, float fontSize,int font)
			throws Exception {
		boolean flag = false;
		PdfWriter.getInstance(document, os);
		StyleSheet st = new StyleSheet();
		// st.loadTagStyle("body", "leading", "16,0");
		HashMap<String, Object> mm = new HashMap<String, Object>();
		//modify by machongqi 2015-6-8
		if(fontProvider == null){
			fontProvider = new MyXMLWorkerFontProvider(textFont, fontSize,font); //根据字体设置的不同，可以增加相应数量的全局变量，当该类型字体已实例化后，不需要重复实例化。
		}
		fontProvider.setFontBlodOrNormal(font);
		//mm.put("font_factory", new MyXMLWorkerFontProvider(textFont, fontSize,font));
		mm.put("font_factory", fontProvider);
		List<Element> elems = HTMLWorker.parseToList(reader, st, mm);
		if(!elems.isEmpty()){
			flag = true;
			document.open();
			for (Element elem : elems) {
				document.add(reSetElement(document, elem));
			}
			document.close();
		}

		os.close();
		return flag;
	}

	private Element reSetElement(Document doc, Element elem) {

		/*f (elem instanceof Paragraph) {
			Paragraph par = (Paragraph) elem;
			List<Chunk> chunklist = par.getChunks();
			for (Chunk ck : chunklist) {
				Image img = ck.getImage();
				if (img != null) {

					float[] wh = zoomImg(doc.getPageSize().getWidth(), doc.getPageSize().getHeight(), img);
					float imgNewWidth = wh[0];
					float imgNewHeight = wh[1];
					img.scaleAbsolute(imgNewWidth, imgNewHeight);
					continue;
				}
			}
		}*/

		return elem;
	}

	private void writeText(PdfStamper stamp, List<PDFText> values)
			throws Exception {
		if (values == null || values.size() == 0) {
			return;
		}
		AcroFields fields = stamp.getAcroFields();
		Iterator<PDFText> vit = values.iterator();
		while (vit.hasNext()) {
			PDFText v = vit.next();

			if (v.getBgcolor() != null) {
				fields.setFieldProperty(v.getKey(), FP_BG_COLOR, v.getBgcolor(), new int[] { 0 });
			}

			if (v.getTextcolor() != null) {
				fields.setFieldProperty(v.getKey(), FP_TEXT_COLOR, v.getTextcolor(), new int[] { 0 });
			}

			/*
			 * 数据字体和默认字体都为空时： 如果为多行显示则必须使用一种已知字体以方便计算换行位置
			 * 如果为单行显示不需要计算换行位置，则不使用内置字体，而直接使用模板设置的字体 下面字体大小的逻辑与此处相同
			 */
			BaseFont textFont = v.getTextfont();// 设置字体
			if (textFont == null) {
				textFont = defaultFont;
			}
			if (textFont == null && v.isMultiLine()) {
				textFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			}
			if (textFont != null)
				fields.setFieldProperty(v.getKey(), FP_TEXT_FONT, textFont, new int[] { 0 });

			float textsize = v.getTextsize();// 设置字体大小
			if (textsize <= 0) {
				textsize = defaultFontSize;
			}
			if (textsize <= 0 && v.isMultiLine()) {
				textsize = 12f;
			}
			if (textsize > 0)
				fields.setFieldProperty(v.getKey(), FP_TEXT_SIZE, textsize, new int[] { 0 });

			List<FieldPosition> fps = fields.getFieldPositions(v.getKey());

			String content = v.getContent();

			if (v.isMultiLine()) {
				insertBr2Content(content, textFont, textsize, fps);
			}

			fields.setField(v.getKey(), content);
		}
	}



	private void writeImg(PdfStamper stamp, List<PDFImage> images,
			String tmplKey) throws Exception {
		if (images == null || images.size() == 0) {
			return;
		}
		PdfContentByte pdfconbyte = stamp.getOverContent(1);
		AcroFields fields = stamp.getAcroFields();
		Iterator<PDFImage> img = images.iterator();
		while (img.hasNext()) {
			PDFImage pdfimg = img.next();
			String fieldkey = pdfimg.getKey();
			Image image = pdfimg.getImg();

			float imgWidth = pdfimg.getWidth() > 0 ? pdfimg.getWidth() : image.getWidth();
			float imgHeight = pdfimg.getHeight() > 0 ? pdfimg.getHeight() : image.getHeight();

			float imgX = 0f;
			float imgY = 0f;
			if (fieldkey == null) {
				imgX = pdfimg.getX();
				imgY = pdfimg.getY();
			} else if (fields.getField(fieldkey) != null) {
				FieldPosition fp = fields.getFieldPositions(fieldkey).get(0);
				float[] wh = zoomImg(fp.position.getWidth(), fp.position.getHeight(), image);
				imgWidth = wh[0];
				imgHeight = wh[1];

				imgX = fp.position.getLeft();
				imgY = fp.position.getTop() - imgHeight;
			} else {
				// 如果不指定图片输入框则按图片指定的位置显示（默认位置为0,0）
				// 如果指定图片输入框存在则按贴到输入框所在位置---如图片宽或高大小输入框则按比例缩小图片
				// 如果指定也输入框，而此输入框在模板中不存在，则不处理此图片
				continue;
			}
			image.scaleAbsolute(imgWidth, imgHeight);
			image.setAbsolutePosition(imgX, imgY);
			pdfconbyte.addImage(image);
		}
	}

	/**
	 * 支持图片尺寸缩放
	 */
	private float[] zoomImg(float maxWidth, float maxHeight, Image img) {
		float wRatio = maxWidth / img.getWidth();
		float hRatio = maxHeight / img.getHeight();
		float ratio = wRatio <= hRatio ? wRatio : hRatio;
		float width = img.getWidth();
		float height = img.getHeight();
		if (ratio < 1) {
			width = width * ratio;
			height = height * ratio;
		}
		return new float[] { width, height };
	}

	/**
	 * <h1>计算换行位置，在需要换行的字符后面插入一个换行符</h1>
	 */
	protected String insertBr2Content(String content, BaseFont font, float size, List<FieldPosition> fps) {
		StringBuilder ret = new StringBuilder(content);
		FieldPosition fp = fps.get(0);
		float rwidth = fp.position.getWidth();
		int startindex = 0;
		for (int i = 1; i < ret.length(); i++) {
			String ss = ret.substring(startindex, i);

			float tempWidth = font.getWidthPoint(ss, size);
			if (tempWidth + offset >= rwidth) {
				ret.insert(i, "\n");
				startindex = i;
			}
		}
		return ret.toString();
	}

	public class PDFText {
		/**
		 * 模板中的文本域名称
		 */
		private String key;

		/**
		 * 需要填入的数据
		 */
		private String content;

		/**
		 * 填入数据字体，默认按模板设置
		 */
		private BaseFont textfont;

		/**
		 * 字体大小，默认按模板设置
		 */
		private float textsize;

		/**
		 * 字体颜色，默认按模板设置
		 */
		private BaseColor textcolor;

		/**
		 * 背景颜色，默认按模板设置
		 */
		private BaseColor bgcolor;

		/**
		 * 是否多行显示，默认为单行
		 */
		private boolean multiLine = false;

		public PDFText(String key, String value) {
			this.key = key;
			this.content = value;
		}

		public boolean isMultiLine() {
			return multiLine;
		}

		public void setMultiLine(boolean multiLine) {
			this.multiLine = multiLine;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public BaseFont getTextfont() {
			return textfont;
		}

		public void setTextfont(BaseFont textfont) {
			this.textfont = textfont;
		}

		public void setTextfont(String name, String encoding, boolean embedded)
				throws Exception {
			this.textfont = BaseFont.createFont(name, encoding, embedded);
		}

		public float getTextsize() {
			return textsize;
		}

		public void setTextsize(float textsize) {
			this.textsize = textsize;
		}

		public BaseColor getTextcolor() {
			return textcolor;
		}

		public void setTextcolor(BaseColor textcolor) {
			this.textcolor = textcolor;
		}

		public void setTextcolor(int r, int g, int b) {
			this.textcolor = createBaseColor(r, g, b);
			;
		}

		public BaseColor getBgcolor() {
			return bgcolor;
		}

		public void setBgcolor(BaseColor bgcolor) {
			this.bgcolor = bgcolor;
		}

		public void setBgcolor(int r, int g, int b) {
			this.bgcolor = createBaseColor(r, g, b);
		}

		private BaseColor createBaseColor(int r, int g, int b) {
			return new BaseColor(val(r), val(g), val(b));
		}

		private int val(int i) {
			return i < 0 ? 0 : i > 255 ? 255 : i;
		}
	}
	public class PDFStrImg {
		private String key;
		private List strImgList;

		public PDFStrImg(String key, List strImgList) {
			super();
			this.key = key;
			this.strImgList = strImgList;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public List getStrImgList() {
			return strImgList;
		}

		public void setStrImgList(List strImgList) {
			this.strImgList = strImgList;
		}

	}
	public class PDFImage {
		private String key;
		private Image img;
		private float width;
		private float height;
		private float x;
		private float y;

		public PDFImage(String key, Image img) {
			this.key = key;
			this.img = img;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Image getImg() {
			return img;
		}

		public void setImg(Image img) {
			this.img = img;
		}

		public float getWidth() {
			return width;
		}

		public void setWidth(float width) {
			this.width = width;
		}

		public float getHeight() {
			return height;
		}

		public void setHeight(float height) {
			this.height = height;
		}

		public float getX() {
			return x;
		}

		public void setX(float x) {
			this.x = x;
		}

		public float getY() {
			return y;
		}

		public void setY(float y) {
			this.y = y;
		}

	}

	public class PDFFile {
		private String key;
//		private OutputStream os;
		private InputStream is;
		private int pageNumber;

		private float left;
		private float top;

		public PDFFile(int  pageNumber, InputStream is) {
//			this.key = key;
			this.is = is;
			this.pageNumber = pageNumber;
		}
		public PDFFile(int  pageNumber,String key, InputStream is) {
			this(pageNumber,is);
			this.key = key;
		}
		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public InputStream getIs() {
			return is;
		}

		public void setIs(InputStream is) {
			this.is = is;
		}

		public int getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}
		public float getLeft() {
			return left;
		}
		public void setLeft(float left) {
			this.left = left;
		}
		public float getTop() {
			return top;
		}
		public void setTop(float top) {
			this.top = top;
		}
	}

	public class PDFHtml {
		private String key;
		private Reader htmlsource;
		private BaseFont font;
		private float fontSize;
		//add by machongqi 2015-6-8
		private int fontBlodOrNormal;

		public int getFontBlodOrNormal() {
			return fontBlodOrNormal;
		}

		public void setFontBlodOrNormal(int fontBlodOrNormal) {
			this.fontBlodOrNormal = fontBlodOrNormal;
		}
		//add by machongqi end

		public PDFHtml(String key, Reader htmlsource) {
			this.key = key;
			this.htmlsource = htmlsource;
		}

		public float getFontSize() {
			return fontSize;
		}

		public void setFontSize(float fontSize) {
			this.fontSize = fontSize;
		}

		public BaseFont getTextfont() {
			return font;
		}

		public void setTextfont(BaseFont f) {
			this.font = f;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Reader getHtmlsource() {
			return htmlsource;
		}

		public void setHtmlsource(Reader htmlsource) {
			this.htmlsource = htmlsource;
		}
	}

	class MyXMLWorkerFontProvider extends XMLWorkerFontProvider {
		private BaseFont textFont;
		private float fontSize;
		private int fontBlodOrNormal;

		/**
		 * @param textFont
		 */
		//modify by machongqi 2015-6-8
		public MyXMLWorkerFontProvider(BaseFont textFont, float fontSize,int fontBlodOrNormal) {
			this.textFont = textFont;
			this.fontSize = fontSize;
			//modify by machongqi 2015-6-8
			this.fontBlodOrNormal=fontBlodOrNormal;
			//modify by machongqi end
		}

		@Override
		public Font getFont(String fontname, String encoding, boolean embedded,
				float size, int style, BaseColor color) {
			try {
				if (textFont == null) {
					textFont = BaseFont.createFont("STSong-Light",
							"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
				}
				if (fontSize > 0) {
					size = fontSize;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//modify by machongqi 2015-6-8
			Font font = new Font(textFont, size, this.fontBlodOrNormal, color);
			//modify by machongqi end
			return font;
		}

		public int getFontBlodOrNormal() {
			return fontBlodOrNormal;
		}

		public void setFontBlodOrNormal(int fontBlodOrNormal) {
			this.fontBlodOrNormal = fontBlodOrNormal;
		}
	}

	public Map<String, String> getTemplate() {
		return template;
	}

	public void setTemplate(Map<String, String> template) {
		this.template = template;
	}

	/**
	 * 获取指定模板中指定表单域的宽度
	 * @param templateFilePath  PDF模板文件路径
	 * @param key               表单域名称
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	public float getFieldWidth(String templateFilePath, String key) throws DocumentException, IOException {
		PdfReader reader = null;
		try {
			reader = new PdfReader(templateFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PdfStamper stamp = new PdfStamper(reader, os);
		AcroFields fields = stamp.getAcroFields();
		List<FieldPosition> list = fields.getFieldPositions(key);
		FieldPosition fp = list.get(0);
		return fp.position.getWidth();
	}

}
