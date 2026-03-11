/**
 *
 */
package ext.casc.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

/**
 * @author cfire
 *
 */
public class Main {

    public static final String SRC = "D:/PDFPreview_14.pdf";
    public static final String DEST = "D:/PDFPreview_14_1.pdf";
    public static void main(String[] args) throws IOException, DocumentException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new PdfConversion().manipulatePdf(SRC, DEST,"space.4");
    }


}
