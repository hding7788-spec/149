/**
 *
 */
package ext.casc.pdf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

/**
 * @author cfire
 *
 */
public class PdfConversion {
	// 定义关键字
    private static String KEY_WORD = "D阶段";
    // 定义返回值
    // 定义返回页码
    public void manipulatePdf(String src, String dest,String version) throws IOException, DocumentException {
    	List<float[]> loc =getKeyWords("D:/PDFPreview.pdf");

        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        int pageNum = reader.getNumberOfPages();
        for (int i = 1; i <= pageNum; i++)
        {
            PdfContentByte canvas = stamper.getOverContent(i);
            float x,y;
            x= 30;//2
            y = 504;//3
            canvas.saveState();
            canvas.setColorFill(BaseColor.WHITE);
            canvas.rectangle(x, y, 55, 34);//设置覆盖面的大小

            canvas.fill();
            canvas.restoreState();
            //开始写入文本
            canvas.beginText();
            //BaseFont bf = BaseFont.createFont(URLDecoder.decode(CutAndPaste.class.getResource("/AdobeSongStd-Light.otf").getFile()), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
            Font font = new Font(bf,10,Font.BOLD);
            //设置字体和大小
            canvas.setFontAndSize(font.getBaseFont(), 10);
            canvas.setTextMatrix(x+14, y+14);
            //要输出的text
            canvas.showText(version );
            //设置字体的输出位置
            canvas.setTextMatrix(x, y-1);
            //要输出的text
            canvas.showText("小批量试生产" );

            canvas.endText();

        }
        stamper.close();
        reader.close();

    }

    private  List<float[]> resu = new ArrayList<float[]>();
    // 定义返回页码
    private  int i = 0;

    /*
     * 返回关键字所在的坐标和页数 float[0] >> X; float[1] >> Y; float[2] >> page;
     */

    public  List<float[]> getKeyWords(String filePath)
    {
        try
        {
            PdfReader pdfReader = new PdfReader(filePath);
            int pageNum = pdfReader.getNumberOfPages();
            System.out.println(pageNum);
            PdfReaderContentParser pdfReaderContentParser = new PdfReaderContentParser(
                    pdfReader);

            // 下标从1开始
            for (i = 1; i <= pageNum; i++)
            {
                pdfReaderContentParser.processContent(i, new RenderListener()
                {

                    @Override
                    public void renderText(TextRenderInfo textRenderInfo)
                    {
                        String text = textRenderInfo.getText();
                        if (null != text && text.contains(KEY_WORD))
                        {
                            com.itextpdf.awt.geom.Rectangle2D.Float boundingRectange = textRenderInfo
                                    .getBaseline().getBoundingRectange();
                           float[] f = new float[3];
                            f[0] = boundingRectange.x;
                            f[1] = boundingRectange.y;
                            f[2] = i;
                            System.out.println("======="+f[0]+":"+f[1]);

                            resu.add(f);
                        }
                    }

                    @Override
                    public void renderImage(ImageRenderInfo arg0)
                    {
                    }

                    @Override
                    public void endTextBlock()
                    {

                    }

                    @Override
                    public void beginTextBlock()
                    {
                    }
                });
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return resu;
    }




}
