package ext.casc.zipfile;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.List;

import ext.casc.preview.Preview;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.util.WTException;

public interface ZipFileService {
    public String zipFile(List<String> oidList, String relist, Integer pringsum, String zipFileName)
            throws WTException,
            PropertyVetoException, IOException;

    public String zipFile(List<String> oidList, String zipFileName) throws WTException,
            PropertyVetoException, IOException;

    public String zipFileNew(List<String> oidList, String zipFileName) throws WTException,
            PropertyVetoException, IOException;

    public String userGuidesZipFile(String zipFileName) throws IOException;

    public String downloadPrimaryContent(EPMDocument epmDocument,String number) throws Exception;

    public String downloadPrimaryContent(WTDocument doc,String number) throws Exception;

    public String downloadPrimaryContent(Preview preview,String number) throws Exception;

    public String zipPrimaryFile(List<String> oidList, String zipFileName) throws WTException,
    PropertyVetoException, IOException;
    //add by libo 2017/2/14 begin
    public String zipPackets(String oid) throws Exception;
    //add by libo 2017/2/14 end
}
