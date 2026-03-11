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
public class PdfConversion2 {
    // 定义返回页码
    public void manipulatePdf(String src, String dest,String version) throws IOException, DocumentException {
    	//List<float[]> loc =getKeyWords("D:/PDFPreview.pdf");

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
            canvas.rectangle(x, y-3, 59, 35);//设置覆盖面的大小

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
            canvas.setTextMatrix(x+14, y-1);
            //要输出的text
            canvas.showText("D阶段");

            //设置字体的输出位置
            canvas.setTextMatrix(x, y-16);
            //要输出的text
            canvas.showText("小批量试生产");

            canvas.endText();

        }
        stamper.close();
        reader.close();

    }





}
