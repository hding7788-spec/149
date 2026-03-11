package ext.casc.ixb;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

import wt.admin.AdministrativeDomainHelper;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.ixb.clientAccess.StandardIXBService;
import wt.method.MethodContext;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.session.SessionAuthenticator;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

// This class downloads a file from a URL.

public class DataReceiveAndImport{

    // Max size of download buffer.
    private static final int MAX_BUFFER_SIZE = 1024;

    // These are the status names.
    public static final String STATUSES[] = { "Downloading", "Paused",
            "Complete", "Cancelled", "Error" };

    // These are the status codes.
    public static final int DOWNLOADING = 0;

    public static final int PAUSED = 1;

    public static final int COMPLETE = 2;

    public static final int CANCELLED = 3;
    public static final int ERROR = 4;
    private URL url; // download URL
    private int size; // size of download in bytes
    private int downloaded; // number of bytes downloaded
    private int status; // current status of download
    private String wfProcessOid;
    private String activityTemplateID;
    private String activityName;
    private String activityOid;
    private String reviewType;
    private String workflowType;
    private String sendFrom;
    private String orderIID;
    private String previewUser;
    private String path;
    private String pboname;
    private String pbonumber;
    private String dataImportStateID;


    // Constructor for Download.
    public DataReceiveAndImport(URL url, String wfProcessOid, String activityTemplateID,String activityName,String activityOid,String reviewType,String workflowType,String sendFrom,String orderIID,String previewUser) {
        this.url = url;
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;
        this.wfProcessOid = wfProcessOid;
        this.activityTemplateID = activityTemplateID;
        this.activityName = activityName;
        this.activityOid = activityOid;
        this.reviewType = reviewType;
        this.workflowType = workflowType;
        this.sendFrom = sendFrom;
        this.orderIID = orderIID;
        this.previewUser = previewUser;
    }

    public DataReceiveAndImport(URL url, String wfProcessOid, String activityTemplateID,String activityName,String activityOid,String reviewType,String workflowType,String sendFrom,String orderIID,String previewUser,String pbonumber,String pboname,String dataImportStateID) {
        this.url = url;
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;
        this.wfProcessOid = wfProcessOid;
        this.activityTemplateID = activityTemplateID;
        this.activityName = activityName;
        this.activityOid = activityOid;
        this.reviewType = reviewType;
        this.workflowType = workflowType;
        this.sendFrom = sendFrom;
        this.orderIID = orderIID;
        this.previewUser = previewUser;
        this.pbonumber = pbonumber;
        this.pboname = pboname;
        this.dataImportStateID=dataImportStateID;
    }
    public DataReceiveAndImport(URL url, String wfProcessOid, String activityTemplateID,String activityName,String activityOid,String reviewType,String workflowType,String sendFrom,String orderIID) {
        this.url = url;
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;
        this.wfProcessOid = wfProcessOid;
        this.activityTemplateID = activityTemplateID;
        this.activityName = activityName;
        this.activityOid = activityOid;
        this.reviewType = reviewType;
        this.workflowType = workflowType;
        this.sendFrom = sendFrom;
        this.orderIID = orderIID;
    }

  //为匹配805所老流程
    public DataReceiveAndImport(URL url, String activityOid805, String activityOid149,String approvedType,String isFormal) {
        this.url = url;
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;
        this.wfProcessOid = activityOid805;
        this.activityOid = activityOid149;
        this.reviewType = approvedType;
        this.workflowType = isFormal;
        this.sendFrom = "805";

    }

    public DataReceiveAndImport(URL url, String activityOid805, String activityOid149,String approvedType,String isFormal,String previewUser) {
        this.url = url;
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;
        this.wfProcessOid = activityOid805;
        this.activityOid = activityOid149;
        this.reviewType = approvedType;
        this.workflowType = isFormal;
        this.sendFrom = "805";
        this.previewUser = previewUser;
    }



    // Get this download's URL.
    public String getUrl() {

        return url.toString();

    }

    // Get this download's size.

    public int getSize() {

        return size;

    }


    // Get this download's progress.

    public float getProgress() {

        return ((float) downloaded / size) * 100;

    }

    // Get this download's status.

    public int getStatus() {

        return status;

    }

    // Pause this download.

    public void pause() {

        status = PAUSED;

        stateChanged();

    }

    // Resume this download.

    public void resume() {

        status = DOWNLOADING;

        stateChanged();


    }

    // Cancel this download.

    public void cancel() {

        status = CANCELLED;

        stateChanged();

    }

    // Mark this download as having an error.

    private void error() {

        status = ERROR;

        stateChanged();

    }


    // Get file name portion of URL.

    private String getFileName(URL url) {

        String fileName = url.getFile();

        return fileName.substring(fileName.lastIndexOf('/') + 1);

    }

    public String transData(){
    	RandomAccessFile file = null;

        InputStream stream = null;

        try {

            // Open connection to URL.
        	if(dataImportStateID!=null &&!"".equals(dataImportStateID)){
        		String receiveState = "数据包接收中";
        		String columns = "GWKEY,ORDERIID,TYPE,RECEIVESTATE,WORKFLOWTYPE";
         		String values = "'"+dataImportStateID+"','"+orderIID+"','"+reviewType+"','"+receiveState+"','"+workflowType+"'";
        		DataImportHandler.updateDataReceiveState(dataImportStateID,orderIID, reviewType, workflowType, columns, values);
        		DataImportHandler.sendImportStateToNo8(dataImportStateID,orderIID,receiveState,"");
        	}

            HttpURLConnection connection =

            (HttpURLConnection) url.openConnection();

            // Specify what portion of file to download.

            connection.setRequestProperty("Range",

            "bytes=" + downloaded + "-");

            // Connect to server.

            connection.connect();


            // Make sure response code is in the 200 range.

            if (connection.getResponseCode() / 100 != 2) {
            	//未接收到
            	 if(dataImportStateID!=null &&!"".equals(dataImportStateID)){
             		String receiveState = "未收到数据包";
             		String columns = "GWKEY,ORDERIID,TYPE,RECEIVESTATE,WORKFLOWTYPE";
             		String values = "'"+dataImportStateID+"','"+orderIID+"','"+reviewType+"','"+receiveState+"','"+workflowType+"'";
             		DataImportHandler.updateDataReceiveState(dataImportStateID,orderIID, reviewType, workflowType, columns, values);
            		DataImportHandler.sendImportStateToNo8(dataImportStateID,orderIID,receiveState,"");
            	}
                error();

            }


            File ixbFile = StandardIXBService.getSaveDirectoryOnServer();
            path = ixbFile.getParentFile().getPath() + File.separator + getFileName(url);
            ixbFile.delete();
            file = new RandomAccessFile(path, "rw");
            file.seek(downloaded);
            stream = connection.getInputStream();

            byte buffer[] = new byte[1024];
            int length = 0;
            while ( (length = stream.read(buffer)) >= 0) {
                file.write(buffer, 0, length);
            }
            if( file.length()>0){
            	String receiveState = "数据包接收成功";
            	 if(dataImportStateID!=null &&!"".equals(dataImportStateID)){
              		String columns = "GWKEY,ORDERIID,TYPE,RECEIVESTATE,WORKFLOWTYPE";
             		String values = "'"+dataImportStateID+"','"+orderIID+"','"+reviewType+"','"+receiveState+"','"+workflowType+"'";
              		DataImportHandler.updateDataReceiveState(dataImportStateID,orderIID, reviewType, workflowType, columns, values);
              		DataImportHandler.sendImportStateToNo8(dataImportStateID,orderIID,receiveState,"");
            	 }
            }

            if (status == DOWNLOADING) {
                status = COMPLETE;
            }

        } catch (Exception e) {

            error();
            e.printStackTrace();

        } finally {

            // Close file.

            if (file != null) {

                try {

                    file.close();

                } catch (Exception e) {
                }

            }

            // Close connection to server.

            if (stream != null) {

                try {

                    stream.close();

                } catch (Exception e) {
                }

            }

        }
    	return status+"";
    }

    public void dataImportProcess(){
		 try {
			DataSynchQueueHelper.createProcessingQueue(path, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID,previewUser,dataImportStateID);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
             //DataImportHandler.processReceivedData(path, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID,previewUser);
    }

    public void dataImportWorker(){
    	DataImportWorker worker = new DataImportWorker(path, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID,previewUser, pbonumber, pboname,dataImportStateID);
    	worker.start();
    }
    public static class DataImportWorker extends Thread {
    	private String context = "Unknown";
    	private String wfProcessOid;
	    private String activityTemplateID;
	    private String activityName;
	    private String activityOid;
	    private String reviewType;
	    private String workflowType;
	    private String sendFrom;
	    private String orderIID;
	    private String previewUser;
	    private String path;
	    private String pboname;
	    private String pbonumber;
	    private String dataImportStateID;
	    public DataImportWorker(String path,String wfProcessOid, String activityTemplateID,String activityName,String activityOid,String reviewType,String workflowType,String sendFrom,String orderIID,String previewUser,String pbonumber,String pboname,String dataImportStateID) {
	    	this.path = path;
	    	this.wfProcessOid = wfProcessOid;
	        this.activityTemplateID = activityTemplateID;
	        this.activityName = activityName;
	        this.activityOid = activityOid;
	        this.reviewType = reviewType;
	        this.workflowType = workflowType;
	        this.sendFrom = sendFrom;
	        this.orderIID = orderIID;
	        this.previewUser = previewUser;
	        this.pbonumber = pbonumber;
	        this.pboname = pboname;
	        this.dataImportStateID=dataImportStateID;
	    }
        public void run() {
            try {

            	System.out.println("####DataImportWorker");
                MethodContext mc = MethodContext.getContext(Thread.currentThread());
                if (mc == null)
                    mc = new MethodContext(null, null);
                if (mc.getAuthentication() == null) {
                    SessionAuthenticator sa = new SessionAuthenticator();
                    mc.setAuthentication(sa.setUserName(AdministrativeDomainHelper.ADMINISTRATOR_NAME));
                }
                context = mc.getId().toString();
            } catch (Throwable t) {
                t.printStackTrace();
                System.err.println("Error create service session context.");
            }
             try {
				DataImportHandler.processReceivedData(path, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID,previewUser,dataImportStateID);
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
    }
    public String startDataImportProcess(){
		try {
			WTPrincipal currentUser = SessionHelper.manager.getPrincipal();
			WTOrganization  userOrg = currentUser.getOrganization();
			WTContainerRef  userConRef = null;
			if(userOrg==null){
				userConRef = WTContainerHelper.service.getExchangeRef();
			}else{
				userConRef = userOrg.getContainerReference();
			}
			WfProcessDefinition wfprocessdefinition = WfDefinerHelper.service
					.getProcessDefinition("149数据包导入流程");
			WfProcess wfprocess = WfEngineHelper.service.createProcess(
					wfprocessdefinition, null,userConRef);
			wfprocess.setName("149数据包导入流程_"+pbonumber+"_"+pboname);
			ProcessData processdata = wfprocess.getContext();
			processdata.setValue("wfProcessOid",wfProcessOid);
			processdata.setValue("activityOid",activityOid);
			processdata.setValue("activityName", activityName);
			processdata.setValue("reviewType",reviewType);
			processdata.setValue("workflowType",workflowType);
			processdata.setValue("activityTemplateID", activityTemplateID);
			processdata.setValue("sendFrom", sendFrom);
	        processdata.setValue("orderIID", orderIID);
	        processdata.setValue("filepath", path);
	        processdata.setValue("previewUser", previewUser);
			WfEngineHelper.service.startProcess(wfprocess,processdata, 1);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return "";
    }

    // Download file.

    public void run() {
    	 try {
             DataImportHandler.processReceivedData(path, wfProcessOid, activityTemplateID,activityName,activityOid, reviewType,workflowType,sendFrom,orderIID,previewUser,dataImportStateID);
         } catch (WTException e1) {
             // TODO Auto-generated catch block
             e1.printStackTrace();
         }

    }

    // Notify observers that this download's status has changed.

    private void stateChanged() {



    }

    public static void main(String args[]) {/*
                                             *
                                             * try {
                                             * DataReceiveAndImport d = new DataReceiveAndImport(new URL(
                                             * "http://pds.805.sast.casc/jmxcoreWeb.jar"));
                                             *
                                             * d.run();
                                             *
                                             * } catch (MalformedURLException e) {
                                             *
                                             * // TODO Auto-generated catch block
                                             *
                                             * e.printStackTrace();
                                             *
                                             * }
                                             */
    }

}
