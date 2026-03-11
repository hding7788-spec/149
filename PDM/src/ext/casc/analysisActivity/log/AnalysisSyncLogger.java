package ext.casc.analysisActivity.log;

import wt.util.WTProperties;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AnalysisSyncLogger {
    static AnalysisSyncLogger log = null;
    SimpleDateFormat ddf;
    String logHome, logName;
    PrintWriter logfile = null;

    private AnalysisSyncLogger() {
        ddf = new SimpleDateFormat("yyyyMM");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        ddf.setTimeZone(tz);
        logName = ddf.format(new Date());
        try {
            WTProperties wtp = WTProperties.getLocalProperties();
            logHome = wtp.getProperty("wt.logs.dir", "");
            File logFile = new File(logHome + "/AnalysisSync_" + logName + ".log");
            logfile = new PrintWriter(new FileWriter(logFile, true), false);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void checkLogger() {
        String now = ddf.format(new Date());
        if(!now.equals(logName)) {
            if(logfile != null) {
                logfile.close();
                logfile = null;
            }
            logName = now;
            try {
                File logFile = new File(logHome + "/AnalysisSync_" + logName + ".log");
                logfile = new PrintWriter(new FileWriter(logFile, true), false);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized static AnalysisSyncLogger getInstance() {
        if(log != null)
            return log;
        return new AnalysisSyncLogger();
    }

    public synchronized void log(Object o) {
        if(o instanceof Throwable) {
            Throwable t = (Throwable) o;
            while(t.getCause() != null)
                t = t.getCause();
            CharArrayWriter caw = new CharArrayWriter();
            PrintWriter pw = new PrintWriter(caw);
            t.printStackTrace(pw);
            pw.flush();
            log(caw.toString());
        } else {
            SimpleDateFormat ddf2 = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
            ddf2.setTimeZone(tz);
            String[] lines = String.valueOf(o).split("\n");
            for(int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if(line.endsWith("\r"))
                    line = line.substring(0, line.length() - 1);
                if(logfile != null) {
                    String time = ddf2.format(new Date());
                    logfile.println(time + ":  " + line);
                }
            }
            if(logfile != null)
                logfile.flush();
        }
    }
}
