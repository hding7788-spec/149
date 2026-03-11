/**
 * @(#)DownloadEnvelopeContent.java
 *
 *
 * @author Leon Zhang
 * @version 1.00 2010/1/19
 */
package ext.ases.envelope;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import wt.content.*;
import wt.doc.*;
import wt.enterprise.RevisionControlled;
import wt.epm.*;
import wt.fc.*;
import wt.log4j.LogR;
import wt.part.*;
import wt.representation.*;
import wt.util.*;
import wt.vc.*;
import wt.vc.config.*;

import ext.ases.envelope.*;

public class DownloadEnvelopeContent {
	
	public static String downloadFileName = "";
	protected HttpServletRequest request;
	protected PageContext pageContext;
	public Locale locale = WTContext.getContext().getLocale();
	private static final Logger logger = LogR.getLogger("ext.ases.envelope.DownloadEnvelopeContent");
	String oid = null;
	String type = null;

    public DownloadEnvelopeContent() {
    }
    
    public DownloadEnvelopeContent(PageContext pc) throws Exception {

		pageContext = pc;
		request = (HttpServletRequest) pc.getRequest();
		request.setCharacterEncoding("gbk");
		getParams();
	}

	private void getParams() throws Exception {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		oid = (String)request.getParameter("oid");
		type = (String)request.getParameter("type");
	}
    
    public String generateFile() throws Exception {
		Vector objList = new Vector();
		ReferenceFactory rf = new ReferenceFactory();
		Object obj = rf.getReference(oid).getObject();
		ProcessEnvelope processEnvelope = (ProcessEnvelope)obj;
		ArrayList arraylist = EnvelopeHelper.service.getAllMembers(processEnvelope);
		for(int i=0;i<arraylist.size();i++){
			RevisionControlled rc = (RevisionControlled)arraylist.get(i);
			if(rc instanceof WTPart){
				WTPart wtpart = (WTPart)rc;
				for(QueryResult qr = WTPartHelper.service.getDescribedByDocuments(wtpart,true);qr.hasMoreElements();){
					WTObject wtobj = (WTObject)qr.nextElement();
					objList.add(wtobj);
				}
				for(QueryResult qr = WTPartHelper.service.getReferencesWTDocumentMasters(wtpart);qr.hasMoreElements();){
					WTDocumentMaster master = (WTDocumentMaster)qr.nextElement();
					WTDocument wtdoc = getLatestWTDocument(master);
					objList.add((WTObject)wtdoc);
				}
			}else if(rc instanceof EPMDocument){
				objList.add((WTObject)rc);
				
			}else if(rc instanceof WTDocument){
				objList.add((WTObject)rc);
			}
		}
		String s = processEnvelope.getNumber();
		downloadFileName = File.separator+ s + File.separator + DownloadHelper.service.getContents(objList,s,type);
		return downloadFileName;
	}
	
	public static WTDocument getLatestWTDocument(WTDocumentMaster doc_master)
		throws WTException
	{
		Iterated iter=null; 
		boolean flag=false;
		LatestConfigSpec latestconfigspec = new LatestConfigSpec();
		QueryResult queryresult = ConfigHelper.service.filteredIterationsOf(doc_master, latestconfigspec);
		if(queryresult.size()<=0)
		{
			ConfigSpec configspec = ConfigHelper.service.getDefaultConfigSpecFor(WTDocumentMaster.class);
			queryresult = ConfigHelper.service.filteredIterationsOf(doc_master, configspec);
		}
		while( queryresult.hasMoreElements() && (!flag) )
		{ 
			iter=(Iterated)(queryresult.nextElement());
			flag= iter.isLatestIteration();
		}
		return (WTDocument)iter;
	}
}