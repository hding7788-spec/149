package ext.casc.ixb;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import wt.admin.AdministrativeDomainHelper;
import wt.ixb.clientAccess.StandardIXBService;
import wt.method.MethodContext;
import wt.session.SessionAuthenticator;
import wt.util.WTException;

// This class downloads a file from a URL.

public class ZYKDataReceiveAndImport  {

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
    private String path;

    // Constructor for Download.
    public ZYKDataReceiveAndImport(URL url) {
        this.url = url;
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;
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

            HttpURLConnection connection =

            (HttpURLConnection) url.openConnection();

            // Specify what portion of file to download.

            connection.setRequestProperty("Range",

            "bytes=" + downloaded + "-");

            // Connect to server.

            connection.connect();


            // Make sure response code is in the 200 range.

            if (connection.getResponseCode() / 100 != 2) {

                error();

            }

            File ixbFile = StandardIXBService.getSaveDirectoryOnServer();
            path = ixbFile.getParentFile().getPath() + File.separator + getFileName(url);
            ixbFile.delete();
            file = new RandomAccessFile(path, "rw");
           // file.seek(downloaded);
            stream = connection.getInputStream();

            byte buffer[] = new byte[1024];
            int length = 0;
            while ( (length = stream.read(buffer))!=-1) {
                file.write(buffer, 0, length);
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

    public void dataImportWorker(){
    	DataImportWorker worker = new DataImportWorker(path,"zyk");
    	worker.start();
    }

    public static class DataImportWorker extends Thread {
    	private String context = "Unknown";
	    private String sendFrom;
	    private String path;

	    public DataImportWorker(String path,String sendFrom) {
	    	this.path = path;
	        this.sendFrom = sendFrom;
	    }
        public void run() {
        	MethodContext mc = null;
        	boolean isNew = false;
            try {
                mc = MethodContext.getContext(Thread.currentThread());
                if (mc == null){
                    mc = new MethodContext(null, null);
                    isNew = true;
                }
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
            	 DataImportHandler.processZYKReceivedData(path,sendFrom);
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(isNew&&mc!=null){
					mc.unregister();
				}
			}

        }
    }
    // Download file.

    public void run() {

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
