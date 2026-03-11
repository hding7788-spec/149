package ext.casc.ixb;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Observable;

import wt.ixb.clientAccess.StandardIXBService;
import wt.util.WTException;

// This class downloads a file from a URL.

public class PreviewDataReceiveAndImport extends Observable implements Runnable {

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

	private HashMap paramMap = new HashMap();
//  String designer = (String)getParam("Designer");
//    String previewReason = (String)getParam("PreviewReason");
//    String productName = (String)getParam("ProductName");
//    String previewUser = (String)getParam("PreviewUser");
//    String workItemOid = (String)getParam("WorkItemOid");

	// Constructor for Download.
	public PreviewDataReceiveAndImport(URL url, HashMap paramMap) {
		this.url = url;
		this.paramMap = paramMap;
		size = -1;
		downloaded = 0;
		status = DOWNLOADING;
		// Begin the download.
//		download();
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

		download();

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

	// Start or resume downloading.

	private void download() {

		Thread thread = new Thread(this);

		thread.start();

	}

	// Get file name portion of URL.

	private String getFileName(URL url) {

		String fileName = url.getFile();

		return fileName.substring(fileName.lastIndexOf('/') + 1);

	}

	// Download file.

	public void run() {


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

			// Check for valid content length.

			int contentLength = connection.getContentLength();

			if (contentLength < 1) {

				error();

			}

			// Set the size for this download if it hasn't been already set.

			if (size == -1) {

				size = contentLength;

				stateChanged();

			}

			// Open file and seek to the end of it.
			File ixbFile = StandardIXBService.getSaveDirectoryOnServer();
			String path = ixbFile.getParentFile().getPath()+File.separator+getFileName(url);
			ixbFile.delete();
//			file = new RandomAccessFile(getFileName(url), "rw");
			file = new RandomAccessFile(path, "rw");
			file.seek(downloaded);

			stream = connection.getInputStream();

			byte buffer[] = new byte[1024];
            int length = 0;
            while ( (length = stream.read(buffer)) >= 0) {
                file.write(buffer, 0, length);
            }

			// Change status to complete if this point was reached because
			// downloading has finished.

			if (status == DOWNLOADING) {
				status = COMPLETE;
			}
			try {
			    DataPreviewProcess.createPreviewForm(path, paramMap);
            } catch (WTException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

		} catch (Exception e) {

			error();

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

	}

	// Notify observers that this download's status has changed.

	private void stateChanged() {

		setChanged();

		notifyObservers();

	}

	public static void main(String args[]) {

		try {
		    HashMap paramMap = new HashMap();
		    paramMap.put(IXBConstants.DESIGNER, "stefanie.z");
		    paramMap.put(IXBConstants.PREVIEW_REASON, "No reason");
		    paramMap.put(IXBConstants.PRODUCT_NAME, "863-707");
		    paramMap.put(IXBConstants.PREVIEWUSER, "user1");
		    paramMap.put(IXBConstants.OBJECT_NAME, "xxx");
		    paramMap.put(IXBConstants.OBJECT_NUMBER, "xxxx");
		    paramMap.put(IXBConstants.WFACTIVITY_OID, "xxx");
		    System.out.println(">>>>>>>>>>>>>>>.we are here");
		    PreviewDataReceiveAndImport d = new PreviewDataReceiveAndImport(new URL(
					"http://pds.805.sast.casc/hai计方案预审单.zip"), paramMap);

			d.run();

		} catch (MalformedURLException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

	}

}
