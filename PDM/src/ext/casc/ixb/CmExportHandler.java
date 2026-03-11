package ext.casc.ixb;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wt.facade.ixb.IxbElement;
import wt.fc.Persistable;
import wt.inf.container.ExchangeContainer;
import wt.inf.container.WTContainerHelper;
import wt.ixb.clientAccess.StandardIXBService;
import wt.ixb.publicforapps.ApplicationExportHandlerForJar;
import wt.ixb.publicforapps.ApplicationHandlerFactory;
import wt.ixb.publicforapps.IxbHelper;
import wt.util.WTException;

import com.ptc.extend.ixb.CmExporter;
import com.ptc.extend.util.ObjectProperty;

public class CmExportHandler extends ApplicationExportHandlerForJar implements CmExporter{
//	private static final String NAME_IS_TAG = "TAG";
//    private int fileNum;
    private int sequenceNo;
    List exportedList;
    List exportedObjectList;
    HashSet typeDefinitions;
    HashSet ibaDefinitions;

	public CmExportHandler(File file) throws WTException {
		super(file, StandardIXBService.getSaveDirectoryOnServer());
//		fileNum = 0;
		exportedList = new ArrayList();
		exportedObjectList = new ArrayList();
	    sequenceNo = 0;
	    typeDefinitions=new HashSet();
	    ibaDefinitions=new HashSet();
	}

	public List getExportedList() {
        return exportedList;
    }

    public int getNextSequence() {
    	return ++sequenceNo;
    }

    public void addExportedObject(Object obj) {
    	if (!exportedObjectList.contains(obj))
    		exportedObjectList.add(obj);
    }

    public void writeManifest(String exportsiteurl) throws WTException {
    	try {
    		String manifest=(new StringBuilder()).append(getSaveDir().getAbsolutePath()).append("/").append(ApplicationHandlerFactory.MANIFEST_FILE_NAME).toString();
    		File file = new File(manifest);
    		FileOutputStream fileoutputstream = new FileOutputStream(file);
    		BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(fileoutputstream, "UTF-8"));
   			bufferedwriter.write((new StringBuilder()).append("exportsite=").append(exportsiteurl).append("\n").toString());

    		bufferedwriter.close();
    		fileoutputstream.close();
    		getJarWriter().addEntry(file);
    		file.delete();
    	} catch (Exception exception) {
    		exception.printStackTrace();
    	}
    }

    private void writeExportObjectList() {
    	try {
    		File file = new File(getSaveDir(), "exportObjectList.txt");
    		FileOutputStream fileoutputstream = new FileOutputStream(file);
    		PrintStream printstream = new PrintStream(fileoutputstream);
    		Iterator it=exportedObjectList.iterator();
    		while (it.hasNext()) {
    			Object o=it.next();
    			printstream.println((new StringBuilder()).append("Class=<").append(o.getClass().getName()).append("> ").append(ObjectProperty.getObjectDisplay(o)).toString());
            }

    		printstream.flush();
    		printstream.close();
    		fileoutputstream.close();
    		getJarWriter().addEntry(file);
    		file.delete();
        } catch(Exception exception) {
        	exception.printStackTrace();
        }
    }

    public void finalizeJar() throws WTException {
	    try {
	    	writeExportObjectList();
	        super.getJarWriter().finalizeJar();
	        cleanTempDir();
	    } catch (IOException ioexception) {
			throw new WTException(ioexception);
	    }
	}

	public String storeDocumentAsContent(IxbElement ixbelement) throws WTException {
	    return storeDocumentAsRole(ixbelement, IxbHelper.STANDARD_DTD, "CONTENTS");
	}

	public String storeDocumentAsRole(IxbElement ixbelement, String s, String s1) throws WTException {
	    String s4;
//	    String s2 = ixbelement.getTag();
//	    String s3 = (new StringBuilder()).append("TAG-").append(fileNum++).append(".xml").toString();
	    try {
		    File file1 = StandardIXBService.getSaveFileOnServer();
		    FileOutputStream fileoutputstream = new FileOutputStream(file1);
		    ixbelement.store(fileoutputstream, s);
		    fileoutputstream.close();
		    s4 = super.storeContent(file1);
		    file1.delete();
		    return s4;
	    } catch (IOException ioexception) {
	    	throw new WTException(ioexception);
	    }
	}

	public String storeDocumentAsNameInDir(IxbElement ixbelement, String name, String dir) throws WTException {
		String s4;
		try {
		    File file1 = StandardIXBService.getSaveFileOnServer();
		    FileOutputStream fileoutputstream = new FileOutputStream(file1);
		    ixbelement.store(fileoutputstream, IxbHelper.STANDARD_DTD);
		    fileoutputstream.close();
		    FileInputStream fileinputstream = new FileInputStream(file1);
		    String savedir = (new StringBuilder()).append(dir).append("/").append(name).toString();
		    getJarWriter().addEntry(fileinputstream, savedir);
		    fileinputstream.close();
		    file1.delete();
		    s4=savedir;
		    return s4;
	    } catch (IOException ioexception) {
	    	throw new WTException(ioexception);
	    }
	}

	public String storeDocumentInDir(IxbElement ixbelement, String dir) throws WTException {
	    String s4;
	    String s2 = ixbelement.getTag();
	    String s3 = (new StringBuilder()).append("TAG-").append(s2).append(".xml").toString();
	    try {
		    File file1=StandardIXBService.getSaveFileOnServer();
		    FileOutputStream fileoutputstream = new FileOutputStream(file1);
		    ixbelement.store(fileoutputstream, IxbHelper.STANDARD_DTD);
		    fileoutputstream.close();
		    FileInputStream fileinputstream = new FileInputStream(file1);
		    String savedir = (new StringBuilder()).append(dir).append("/").append(s3).toString();
		    getJarWriter().addEntry(fileinputstream, savedir);
		    fileinputstream.close();
		    file1.delete();
		    //s4 = super.storeContent(file1);
		    s4=savedir;
		    return s4;
	    } catch (IOException ioexception) {
	    	throw new WTException(ioexception);
	    }
	}

	public String storeDocument(IxbElement ixbelement, String s) throws WTException {
		try {
		    String s1;
		    s1 = super.storeDocument(ixbelement, s);
		    exportedList.add(s1);
		    return s1;
		} catch (Exception exception) {
			throw new WTException(exception);
		}
	}

    public void reallyStoreContent(InputStream inputstream, String s) throws WTException {
	    try {
	        if (inputstream != null)
	            getJarWriter().addEntry(inputstream, s);
	    } catch (IOException ioexception) {
	    	ioexception.printStackTrace();
	        //throw new WTException(ioexception);
	    }
	}

    public void logger(Object s) {
//    	CmExpImpHelper.logger(s);
    }

    public void processException(Exception e) throws WTException {
    	CharArrayWriter caw = new CharArrayWriter();
        PrintWriter pw = new PrintWriter(caw);
        e.printStackTrace(pw);
        pw.flush();
		logger((new StringBuilder()).append("****Export Exception as below****\n").append(caw.toString()).toString());
		if (e instanceof WTException)
			throw (WTException)e;
		else
			throw new WTException(e);
	}

    public String getSavePathInJar(Object obj) {
		return getUniqueSavePathInJar(obj);
	}

    public void storeTypeDefinition(String s) {
    	if (!typeDefinitions.contains(s))
    		typeDefinitions.add(s);
    }

    public void storeIBADefinition(String s) {
    	if (!ibaDefinitions.contains(s))
    		ibaDefinitions.add(s);
    }

    public HashSet getTypeDefinitionSet() {
    	return typeDefinitions;
    }

    public HashSet getIBADefinitionSet() {
    	return ibaDefinitions;
    }

    //
	public String getUniqueSavePathInJar(Object obj) {
		StringBuilder sb=new StringBuilder();
		/*String number=ObjectProperty.getNumber(obj);
		sb.append(getObjectClassname(obj)).append("@");
		Pattern pattern=Pattern.compile("^[0-9a-zA-Z_-]{1,20}$");
		Matcher matcher = pattern.matcher(number);
		if (matcher.matches()) {
			sb.append(escapeNumberString(ObjectProperty.getNumber(obj)));
		} else {
			sb.append("LOCALID").append(String.valueOf(((Persistable)obj).getPersistInfo().getObjectIdentifier().getId()));
		}*/
		sb.append("LOCALID").append(String.valueOf(((Persistable)obj).getPersistInfo().getObjectIdentifier().getId()));

		return sb.toString();
	}

	private String escapeNumberString(String str) {
		return str.replace(' ', '_').replace('/', '_').replace('\\', '_');
	}

	public static String getObjectClassname(Object obj) {
		String cn=obj.getClass().getName();
		int i = cn.lastIndexOf(".");
		if (i == -1)
			return cn;
		else
			return cn.substring(i + 1);
	}

	public static String getLocalExchangeContainerURL() {
		try {
			ExchangeContainer ec=(ExchangeContainer)WTContainerHelper.getExchangeRef().getReferencedContainer();
			return ec.getInternetDomain();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "unknow";
	}
}
