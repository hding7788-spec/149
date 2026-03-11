package com.ptc.extend.ixb.center;

import java.beans.PropertyVetoException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.facade.ixb.IxbElement;
import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.ixb.clientAccess.StandardIXBService;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.util.WTException;
import wt.viewmarkup.ViewMarkUpHelper;
import wt.viewmarkup.Viewable;
import wt.viewmarkup.WTMarkUp;

import com.ptc.extend.ixb.CmExpImpConstraints;
import com.ptc.extend.ixb.CmExporter;
import com.ptc.extend.util.ObjectProperty;
import com.ptc.wvs.server.util.ETB;
import com.ptc.wvs.server.util.PublishUtils;
import com.ptc.wvs.server.util.Util;

public class MQExpWTMarkUp extends MQExpImpPersistable {

    public MQExpWTMarkUp(CmExporter expHdl) throws WTException {
        super(expHdl);
    }

    public String getRootTag() {
        return CmExpImpConstraints.XML_WTMARKUP;
    }

    public void exportObject(Object obj) throws WTException {
        exportMarkupObjects(obj, this.root);

    }

    public Object importObject() throws WTException {
        // TODO Auto-generated method stub
        return null;
    }

    public WTArrayList importObjects() throws WTException {
        // TODO Auto-generated method stub
        return null;
    }

    protected void exportMarkupObjects(Object obj, IxbElement ixbelement)
            throws WTException {
        String number = ObjectProperty.getNumber(obj);
        String name = ObjectProperty.getName(obj);
        String version = ObjectProperty.getVersion(obj);
        String iteration = ObjectProperty.getIteration(obj);
        ixbelement.addValue("number", number);
        ixbelement.addValue("name", name);
        ixbelement.addValue("versionInfo/versionId", version);
        ixbelement.addValue("versionInfo/iterationId", iteration);
        ixbelement.addValue("type", obj.getClass().getName());
        QueryResult qr = RepresentationHelper.service
                .getRepresentations((Representable) obj);
        int count = 0;
        boolean isHasMarkup = false;
        while (qr.hasMoreElements()) {
            Representation rep = (Representation) qr.nextElement();
            IxbElement ixbelement1 = ixbelement.addElement("MarkUp");
            ixbelement1.addValue("repName", emptyIfNull(rep.getName()));
            try {
                File tmpfile = StandardIXBService.getSaveFileOnServer();
                String fullfile = tmpfile.getCanonicalPath();
                if ((saveMarkupsAsZIPFile(getRefFromObject((Persistable) rep),
                        false, fullfile, "UTF-8")) && (tmpfile.exists())) {
                    String s = getSavePathInJar(obj) + "/" + "CONTENTS" + "/"
                            + "Markup-" + String.valueOf(count) + ".jar";
                    FileInputStream fis = new FileInputStream(tmpfile);
                    this.expHdl.reallyStoreContent(fis, s);
                    fis.close();
                    tmpfile.delete();
                    ixbelement1.addValue("markUp/id", emptyIfNull(s));
                    isHasMarkup = true;
                }
            } catch (Exception e) {
                logger("Exception in exportMarkupObjects, ob=<"
                        + ObjectProperty.getObjectDisplay(obj) + ">");
                processException(e);
            }
            count++;
        }
        if (isHasMarkup) {
            reallyStore();
        }
    }

    public static Boolean saveMarkupsAsZIPFile(String s, boolean flag,
            String s1, String s2) throws WTException, PropertyVetoException,
            IOException {
        if (s == null || s1 == null)
            return Boolean.FALSE;
        Viewable viewable = (Viewable) PublishUtils.getObjectFromRef(s);
        if (viewable == null)
            return Boolean.FALSE;
        QueryResult queryresult;
        QueryResult queryresult2 = new QueryResult();
        ObjectVector objectVector = new ObjectVector();
        BufferedOutputStream bufferedoutputstream;
        try {
            queryresult = ViewMarkUpHelper.service.getMarkUps(viewable);
            if (queryresult == null || queryresult.size() == 0)
                return Boolean.TRUE;
            while (queryresult.hasMoreElements()) {
                WTMarkUp markUp = (WTMarkUp) queryresult.nextElement();
                String name = markUp.getName();
                if (name != null && name.startsWith("149")) {
                    objectVector.addElement(markUp);
                }
            }
            queryresult2.appendObjectVector(objectVector);
        } catch (Exception exception) {
            exception.printStackTrace();
            return Boolean.FALSE;
        }
        bufferedoutputstream = new BufferedOutputStream(
                new FileOutputStream(s1));
        saveMarkupsAsZIP(viewable, queryresult2, flag, bufferedoutputstream,
                s2, true);
        bufferedoutputstream.close();
        return Boolean.TRUE;
    }

    public static void saveMarkupsAsZIP(Viewable viewable,
            QueryResult queryresult, boolean flag, OutputStream outputstream,
            String s, boolean flag1) throws WTException, PropertyVetoException,
            IOException {
        if (viewable == null || outputstream == null || queryresult == null
                || queryresult.size() == 0)
            return;
        Object obj;
        if (flag)
            obj = new JarOutputStream(outputstream);
        else obj = new ZipOutputStream(outputstream);
        addMarkupsToZip(viewable, queryresult, ((ZipOutputStream) (obj)),
                "etb.etb", s, flag1);
        ((ZipOutputStream) (obj)).close();
    }

    private static void addMarkupsToZip(Viewable viewable,
            QueryResult queryresult, ZipOutputStream zipoutputstream, String s,
            String s1, boolean flag) throws WTException, PropertyVetoException,
            IOException {
        byte abyte0[] = new byte[1024];
        StringBuffer stringbuffer = new StringBuffer(300);
        StringBuffer stringbuffer1 = new StringBuffer(300);
        while (queryresult.hasMoreElements()) {
            WTMarkUp wtmarkup = (WTMarkUp) queryresult.nextElement();
            String s3 = wtmarkup.getAdditionalInfo();
            ETB etb = new ETB(s3);
            String s4 = etb.getWcFile();
            String s5 = s4;
            int j = s4.indexOf("!>");
            if (j >= 0 && j < s4.length() - 3) {
                s5 = s4.substring(j + 2);
                s3 = Util.SandR(s3, s4, s5);
            }
            String s6 = etb.getTargetWcFile();
            j = s6.indexOf("!>");
            if (j >= 0 && j < s6.length() - 3)
                s3 = Util.SandR(s3, s6, s6.substring(j + 2));
            stringbuffer.append(s3).append("\n");
            if (flag && wtmarkup.getDescription() != null
                    && wtmarkup.getDescription().length() > 0)
                stringbuffer1.append(
                        (new StringBuilder()).append(etb.getName())
                                .append("@@").append(etb.getTag()).append("=")
                                .append(wtmarkup.getDescription()).toString())
                        .append("\n");
            wtmarkup = (WTMarkUp) ContentHelper.service.getContents(wtmarkup);
            Vector vector = ContentHelper.getContentListAll(wtmarkup);
            int k = 0;
            while (k < vector.size()) {
                ContentItem contentitem = (ContentItem) vector.elementAt(k);
                if (contentitem instanceof ApplicationData) {
                    ApplicationData applicationdata = (ApplicationData) contentitem;
                    String s2;
                    if (applicationdata.getRole() == ContentRoleType.THUMBNAIL)
                        s2 = Util.setExtension(s5, "gif");
                    else s2 = s5;
                    InputStream inputstream = ContentServerHelper.service
                            .findContentStream(applicationdata);
                    try {
                        zipoutputstream.putNextEntry(new ZipEntry(s2));
                        int i;
                        while ((i = inputstream.read(abyte0, 0, 1024)) > 0)
                            zipoutputstream.write(abyte0, 0, i);
                        zipoutputstream.closeEntry();
                    } catch (ZipException zipexception) {
                    }
                }
                k++;
            }
        }
        if (stringbuffer != null) {
            zipoutputstream.putNextEntry(new ZipEntry(s));
            if (s1 != null && s1.length() > 0)
                zipoutputstream.write(stringbuffer.toString().getBytes(s1));
            else zipoutputstream.write(stringbuffer.toString().getBytes());
            zipoutputstream.closeEntry();
        }
        if (stringbuffer1 != null && stringbuffer1.length() > 0) {
            zipoutputstream.putNextEntry(new ZipEntry("mkdescr.dcr"));
            if (s1 != null && s1.length() > 0)
                zipoutputstream.write(stringbuffer1.toString().getBytes(s1));
            else zipoutputstream.write(stringbuffer1.toString().getBytes());
            zipoutputstream.closeEntry();
        }
    }

}