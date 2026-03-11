package com.glaway.mpm.qmIntf.technics;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.util.*;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;
import org.dom4j.Document;
import org.dom4j.Element;
import wt.method.RemoteMethodServer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class RelatedTypicalTechnicsMainFrame extends JDialog {
    private static final long serialVersionUID = 1L;
    private static byte[] buf = new byte[1024 * 4];
    private static VaLogger logger = VaLogger.getLogger(RelatedTypicalTechnicsMainFrame.class.getName());

    private static String technicsNumber;
    private static String version;

    public RelatedTypicalTechnicsMainFrame() {
        initComponents();
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("关联典型工艺");
        setSize(650, 550);
        CommonUIUtil.setMiddleOnScreenWithDialog(this);
        setVisible(true);
    }

    private void initComponents() {

        List<Technics> technicsList = new ArrayList<Technics>();
        Technics technics;
        Element technicsElement = getTechnicsElement();
        List<Element> borrowTechnicsList = XmlUtility.getBorrowTechnics(technicsElement);
        for (Element borrowElement : borrowTechnicsList) {
            String technicsType = borrowElement.attributeValue("technicsType");
            if ("结构化典型工艺".equals(technicsType)) {
                technics = new Technics();
                technics.setTechnicsNumber(borrowElement.attributeValue("technicsNumber"));
                technics.setDocNumber(borrowElement.attributeValue("docNumber"));
                technics.setVersion(borrowElement.attributeValue("technicsVersion"));
                technicsList.add(technics);
            }
            if("典型工艺".equals(technicsType)){

                String ppNumber = borrowElement.attributeValue("technicsNumber");
                try {
                    TempObject tempObject = TechnicsIntf.getSamePplanNumberDxPlan(ppNumber);
                    if(tempObject != null){
                        technics = new Technics();
                        technics.setTechnicsNumber(tempObject.getNumber());
                        technics.setDocNumber(tempObject.getDocNumber());
                        technics.setVersion(tempObject.getVersion());
                        technicsList.add(technics);
                    }
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        Container container = this.getContentPane();
        RelateTypicalTechnicPanel relateTypicalTechnicPanel = new RelateTypicalTechnicPanel(this, null, technicsList, technicsElement);
        container.add(relateTypicalTechnicPanel);
    }

    private Element getTechnicsElement() {
        Element technicElement = null;
        Vector<Object> vector = MesParameterProcessor.getTechnicDocumentByNumberAndVersion(technicsNumber, version);
        if (vector != null) {
            Document document = MesParameterProcessor.downloadData(vector);
            technicElement = XmlUtility.getTechnicsElement(document);
        }
        return technicElement;
    }

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            args = new String[2];
            args[0] = "1495072366111";
            args[1] = "space";
        }
        if ((args != null) && (args.length > 0)) {
            technicsNumber = args[0];
            version = args[1];
        }
        startMesMainFrame(args);
    }

    public static void startMesMainFrame(final String[] args) {
        if ((args != null) && (args.length > 0)) {
            if ((args != null) && (args.length > 0)) {
                for (int i = 0; i < args.length; i++) {
                    logger.debug("参数 " + i + " ========" + args[i]);
                }
                technicsNumber = args[0];
                version = args[1];
            }
        }
        try {
            System.setProperty("swing.useSystemFontSettings", "0");
            System.setProperty("swing.handleTopLevelPaint", "false");
            System.setProperty("-Dswing.aatext", "true");

            LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(), new ExperienceBlue());
            CommonUIUtil.setGlobalFont("宋体", 0, 14);

            RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
            methodServer.setUserName("wcadmin");
            methodServer.setPassword("Admin@149");

            new RelatedTypicalTechnicsMainFrame();
        } catch (UnsupportedLookAndFeelException e) {
            logger.error(e);
        }
    }

    public static String getTechnicsNumber() {
        return technicsNumber;
    }

    public static String getVersion() {
        return version;
    }

    public static boolean getTechnicsByte() throws Exception {
        File file = new File(WorkSpaceUtil.getMesTechnicsDirectory(technicsNumber));
        String tempFile = System.getenv("TEMP") + File.separator + System.currentTimeMillis() + ".zip";
        ApacheZipUtil.compress(file, tempFile);
        byte[] techByte = FileUtil.readFilePathToByte(tempFile);
        deleteFiles(tempFile);
        return TechnicsIntf.uploadPrimaryOfDocument(techByte, technicsNumber, version);
    }

    /**
     * 删除某一个文件或者文件夹 3:41:50 PM
     *
     * @param inputPath
     * @return
     */
    public static boolean deleteFiles(String inputPath) {
        try {
            File f = new File(inputPath);
            if (f.isDirectory()) {
                File[] flist = f.listFiles();
                for (int i = 0; i < flist.length; i++) {
                    File tmpfile = (File) flist[i];
                    deleteFiles(tmpfile.getAbsolutePath());
                }
                f.delete();
            } else
                f.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
