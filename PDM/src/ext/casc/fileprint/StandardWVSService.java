package ext.casc.fileprint;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import com.ptc.wvs.common.util.WVSProperties;
import com.ptc.wvs.server.util.Util;

public class StandardWVSService extends StandardManager implements WVSService, Serializable {

    private static final String CLASSNAME = StandardWVSService.class.getName();

    static {
        try {
            WTProperties wtproperties = WTProperties.getLocalProperties();
        } catch (Throwable throwable) {
            System.err.println("WVSService: Error reading ext.ases.fileprint.* properties");
            throwable.printStackTrace(System.err);
            throw new ExceptionInInitializerError(throwable);
        }
    }

    public String getConceptualClassname() {
        return CLASSNAME;
    }

    public static StandardWVSService newStandardWVSService() throws WTException {
        StandardWVSService instance = new StandardWVSService();
        instance.initialize();
        return instance;
    }

    public Representable repToAttachment(Representable representable, boolean flag) throws WTException,
            PropertyVetoException, IOException {
        Representation representation = RepresentationHelper.service.getDefaultRepresentation(representable);
        // 如果主内容是AutoCAD，则不处理它的可视化文件
        if (representable instanceof WTDocument) {
            ContentItem item = (ContentItem) ContentHelper.service.getPrimary((WTDocument) representable);
            if (item != null && item instanceof ApplicationData) {
                if (((ApplicationData) item).getFileName().toUpperCase().endsWith(".DWG")){
                    return representable;
                }
            } else {
                System.out.println("item is null");
            }
        }

        boolean hasPdf = false;
        if (representation != null) {
            representation = (Representation) ContentHelper.service.getContents(representation);
            Vector vector1 = ContentHelper.getContentList(representation);
            for (int l = 0; l < vector1.size(); l++) {
                ContentItem contentitem = (ContentItem) vector1.elementAt(l);
                if (contentitem instanceof ApplicationData) {
                    ApplicationData applicationdata = (ApplicationData) contentitem;
                    String filename = applicationdata.getFileName();
                    String extention = Util.getExtension(filename);
                    String removeExtention = Util.removeExtension(filename);

                    String downloadDir = WVSProperties.getPropertyValue("productview.downloadlocation");
                    if (extention.equalsIgnoreCase("PDF")) {
                        hasPdf = true;
                        wt.content.ContentItem docContentItem = ContentHelper.service.getPrimary((FormatContentHolder) representable);
                        if (docContentItem == null){
                            continue;
                        }
                        ApplicationData applicationdataPrimary = (ApplicationData) docContentItem;
                        String prifileName = applicationdataPrimary.getFileName();
                        // 如果附件的文件名是drw0002.drw.pdf,那么获取附件的文件名称是{$CAD_NAME}.pdf,不会得到具体的文件名.drw0002.drw是祝内容文件的名称.
                        // 这里取到具体的文件名,作为drw0002作为附件的文件名,这样附件的文件名为drw0002.pdf,在FilePrintUtil.java中获取的时候就是drw0002.pdf
                        if (prifileName.equals("{$CAD_NAME}")) {
                            EPMDocument epm = (EPMDocument) representable;
                            prifileName = epm.getCADName();
                        }

                        String newfilename = PrintUtil.getQualityFileName((FormatContentHolder) representation, prifileName, "SOURCE");

                        removeExtention = Util.removeExtension(newfilename);
                        newfilename = removeExtention + ".pdf";
                        System.out.println("newfilename:" + newfilename + ",flag=" + flag);
                        // 如果flag是true,则替换掉同名的附件
                        if (flag) {
                            removeAttachment(representable, newfilename);
                        }

                        File file = new File(downloadDir + File.separator + newfilename);
                        int i = 1;
                        String m_filename = removeExtention;
                        while (file.exists()) {
                            m_filename = removeExtention + "(" + i + ")";
                            newfilename = m_filename + "." + extention;
                            file = new File(downloadDir + File.separator + newfilename);
                            i = i + 1;
                        }

                        ContentServerHelper.service.writeContentStream(applicationdata, downloadDir + File.separator + newfilename);
                        ApplicationData appData = ApplicationData.newApplicationData(representable);
                        appData.setRole(ContentRoleType.SECONDARY);
                        ContentServerHelper.service.updateContent(representable, appData, downloadDir + File.separator + newfilename);
                        File tFile = new File(downloadDir + File.separator + newfilename);
                        tFile.delete();
                    } else {
                        if (!hasPdf){
                            System.out.println(CLASSNAME + "没有可视化的pdf文件,请检查work机配置是否可视化结果是pdf!");
                        }
                    }
                } else {
                    System.out.println(CLASSNAME + "contentitem instanceof ApplicationData IS false!");
                }
            }
        } else {
            System.out.println("StandardWVSService--representation == null");
        }
        return representable;
    }

    public static Representable removeAttachment(Representable representable, String attachName) throws WTException,
            PropertyVetoException {
        try {
            wt.content.ContentHolder doc = ContentHelper.service.getContents(representable);
            Vector apps = ContentHelper.getApplicationData(doc);
            for (Enumeration e = apps.elements(); e.hasMoreElements();) {
                ApplicationData contentItem = (ApplicationData) e.nextElement();

                if (contentItem.getFileName().equalsIgnoreCase(attachName)) {
                    ContentServerHelper.service.deleteContent(doc, contentItem);
                }

            }
        } catch (WTPropertyVetoException wtpve) {
            wtpve.printStackTrace();

        }
        return representable;
    }

    public static WTProperties wtProperties;

    static {
        try {
            wtProperties = WTProperties.getLocalProperties();

        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
