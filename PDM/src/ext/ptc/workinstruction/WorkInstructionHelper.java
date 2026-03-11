package ext.ptc.workinstruction;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.config.LatestConfigSpec;

// This class downloads a file from a URL.
public class WorkInstructionHelper {
    // Max size of download buffer.
    private static final int MAX_BUFFER_SIZE = 1024;

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

    // Constructor for Download.
    public WorkInstructionHelper(String strUrl) {
        try {
            this.url = new URL(strUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;

        // Begin the download.
        // download();
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
    }

    // Resume this download.
    public void resume() {
        status = DOWNLOADING;
        download();
    }

    // Cancel this download.
    public void cancel() {
        status = CANCELLED;
    }

    // Mark this download as having an error.
    private void error() {
        status = ERROR;
    }

    // Get file name portion of URL.
    private String getFileName(URL url) {
        String fileName = url.getFile();
        String ary[] = fileName.split("[&]");
        for (int i = 0; i < ary.length; i++) {
            if (ary[i].contains("originalFileName")) {
                fileName = ary[i].split("[=]")[1];
            }
        }
        // System.out.println("url.getFile():::"+url.getFile());
        return fileName;
    }

    public static String getContentData2(String docName) {
        if (docName == null || "".equals(docName)) {
            return "";
        }
        String wthomepath = "";
        String fileName = "";
        try {
            WTDocument document = getDocumentByName(docName);
            WTProperties props = WTProperties.getLocalProperties();
            wthomepath = props.getProperty("wt.home");
            ApplicationData applicationData = null;
            ContentHolder contentholder = null;
            contentholder = ContentHelper.service.getContents(document);
            ContentItem primary = ContentHelper.getPrimary((FormatContentHolder) contentholder);
            applicationData = (ApplicationData) primary;
            if (applicationData != null) {
                fileName = applicationData.getFileName();
                String path = (new StringBuilder(String.valueOf(wthomepath))).append(File.separator).append("codebase")
                        .append(File.separator).append("netmarkets").append(File.separator).append("jsp")
                        .append(File.separator).append("flv").append(File.separator).append(fileName).toString();
                ContentServerHelper.service.writeContentStream(applicationData, path);
            }
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public static WTDocument getDocumentByName(String name) throws WTException {
        QuerySpec qSpec = new QuerySpec(WTDocument.class);
        int index[] = new int[1];
        SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NAME, "=", name);
        qSpec.appendWhere(sc, index);
        QueryResult qResult = PersistenceHelper.manager.find(qSpec);
        LatestConfigSpec lcs = new LatestConfigSpec();
        qResult = lcs.process(qResult);
        WTDocument document;
        for (document = null; qResult.hasMoreElements(); document = (WTDocument) qResult.nextElement())
            ;
        return document;
    }

    // Download file.
    public String download() {
        RandomAccessFile file = null;
        InputStream stream = null;
        String fileName = null;
        try {
            // Open connection to URL.
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();

            // Specify what portion of file to download.
            connection.setRequestProperty("Range", "bytes=" + downloaded + "-");

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

            /*
             * Set the size for this download if it hasn't been already set.
             */
            if (size == -1) {
                size = contentLength;
            }

            // Open file and seek to the end of it.
            fileName = getFileName(url);
            // System.out.println("File Name:"+fileName);
            file = new RandomAccessFile(fileName, "rw");
            file.seek(downloaded);

            stream = connection.getInputStream();
            while (status == DOWNLOADING) {
                /*
                 * Size buffer according to how much of the file is left to
                 * download.
                 */
                byte buffer[];
                if (size - downloaded > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[size - downloaded];
                }

                // Read from server into buffer.
                int read = stream.read(buffer);
                if (read == -1)
                    break;

                // Write buffer to file.
                file.write(buffer, 0, read);
                downloaded += read;
            }

            /*
             * Change status to complete if this point was reached because
             * downloading has finished.
             */
            if (status == DOWNLOADING) {
                status = COMPLETE;
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
        // System.out.println("fsdjlfjfldsjf:"+file.toString());
        return fileName;
    }

    public void convert() throws Exception {
        String command = "";
        Process process = Runtime.getRuntime().exec(command);
        final InputStream is1 = process.getInputStream();
        new Thread(new Runnable() {
            public void run() {
                BufferedReader br = new BufferedReader(new InputStreamReader(is1));
                try {
                    while (br.readLine() != null)
                        ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        InputStream is2 = process.getErrorStream();
        BufferedReader br2 = new BufferedReader(new InputStreamReader(is2));
        StringBuilder buf = new StringBuilder();
        String line = null;
        while ((line = br2.readLine()) != null)
            buf.append(line);
    }
}
