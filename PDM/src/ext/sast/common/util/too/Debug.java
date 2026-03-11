/*
 * Created on 2005-4-9 by liuld
 */
package ext.sast.common.util.too;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

//import wt.util.WTProperties;

/**
 * Debug logic
 * 
 * 调试信息行号
 */
public class Debug {
	static String debugContext = "";
	static int debugCounter = 0;
    static Object debugCounterLock = new Object();
    static boolean VERBOSE = true;
    
    public static boolean enabled() {
        return VERBOSE;
    }
    
    /**
     * 获取当前时间的字符串
     * 
     * @return 当前时间字符??
     */
    private static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return sdf.format(new Date());
    }

    /**
     * 调试输出异常
     * 
     * @param t 异常对象
     */
    public static void E(Throwable t) {
        String s = t.getLocalizedMessage();
        s = s == null ? t.getMessage() : s;
        s = s == null ? "" : s;

        StringBuffer ss = new StringBuffer(t.getClass().getName());
        ss.append(": ").append(s).append('\n');
        StackTraceElement[] ste = t.getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            ss.append("\t@ ").append(ste[i].getClassName());
            ss.append('.').append(ste[i].getMethodName());
            ss.append('(').append(ste[i].getFileName());
            ss.append(':').append(ste[i].getLineNumber());
            if (ste[i].getLineNumber() < 0)
                ss.append(", Native Method");
            ss.append(")\n");
        }

        String s1 = "\n***************************************"
            + "*****************************************";
        String s2 = ste[1].getFileName() + "." 
                + ste[1].getLineNumber() + ": " + getTime();

        println(s1);
        println(s2);
        println(ss.toString());
    }

    /** 输出调试信息,不换??
     * 
     * @param o 调试对象
     */
    public static void P_(Object o) {
        if (VERBOSE)
            print(String.valueOf(o));
    }
    
    public static void P_(long l) {
        if (VERBOSE)
            print(String.valueOf(l));
    }

    public static void P_(double d) {
        if (VERBOSE)
            print(String.valueOf(d));
    }

    public static void P_(Object o1, Object o2) {
        if (VERBOSE)
            print(String.valueOf(o1) + o2);
    }

    public static void P_(Object o1, Object o2, Object o3) {
        if (VERBOSE)
            print(String.valueOf(o1) + o2 + o3);
    }

    public static void P_(Object o1, Object o2, Object o3, Object o4) {
        if (VERBOSE)
            print(String.valueOf(o1) + o2 + o3 + o4);
    }

    public static void P_(Object o1, Object o2, Object o3, Object o4, Object o5) {
        if (VERBOSE)
            print(String.valueOf(o1) + o2 + o3 + o4 + o5);
    }

    public static void P_(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        if (VERBOSE)
            print(String.valueOf(o1) + o2 + o3 + o4 + o5 + o6);
    }

    public static void P_(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6,
            Object o7) {
        if (VERBOSE)
            print(String.valueOf(o1) + o2 + o3 + o4 + o5 + o6 + o7);
    }

    public static void P_(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6,
            Object o7, Object o8) {
        if (VERBOSE)
            print(String.valueOf(o1) + o2 + o3 + o4 + o5 + o6 + o7 + o8);
    }

    public static void P_(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6,
            Object o7, Object o8, Object o9) {
        if (VERBOSE)
            print(String.valueOf(o1) + o2 + o3 + o4 + o5 + o6 + o7 + o8 + o9);
    }

    /**
     * 输出调试信息,并换??
     * @param o
     */
    public static void P(Object o) {
        if (VERBOSE) 
            println2(String.valueOf(o));
    }

    public static void P() {
        if (VERBOSE)
            println2("");
    }
    
    public static void P(long l) {
        if (VERBOSE) 
            println2(Long.toString(l));
    }

    public static void P(double d) {
        if (VERBOSE) 
            println2(Double.toString(d));
    }

    public static void P(Object o1, Object o2) {
        if (VERBOSE) 
            println2(String.valueOf(o1) + o2);
    }

    public static void P(Object o1, Object o2, Object o3) {
        if (VERBOSE) 
            println2(String.valueOf(o1) + o2 + o3);
    }

    public static void P(Object o1, Object o2, Object o3, Object o4) {
        if (VERBOSE) 
            println2(String.valueOf(o1) + o2 + o3 + o4);
    }

    public static void P(Object o1, Object o2, Object o3, Object o4, Object o5) {
        if (VERBOSE) 
            println2(String.valueOf(o1) + o2 + o3 + o4 + o5);
    }

    public static void P(Object o1, Object o2, Object o3, Object o4, Object o5, 
            Object o6) {
        if (VERBOSE) 
            println2(String.valueOf(o1) + o2 + o3 + o4 + o5 + o6);
    }

    public static void P(Object o1, Object o2, Object o3, Object o4, Object o5, 
            Object o6, Object o7) {
        if (VERBOSE) 
            println2(String.valueOf(o1) + o2 + o3 + o4 + o5 + o6 + o7);
    }

    public static void P(Object o1, Object o2, Object o3, Object o4, Object o5, 
            Object o6, Object o7, Object o8) {
        if (VERBOSE) 
            println2(String.valueOf(o1)+ o2 + o3 + o4 + o5 + o6 + o7 + o8);
    }

    public static void P(Object o1, Object o2, Object o3, Object o4, Object o5, 
            Object o6, Object o7, Object o8, Object o9) {
        if (VERBOSE) 
            println2(String.valueOf(o1) + o2 + o3 + o4 + o5 + o6 + o7 + o8 + o9);
    }
    
    private static void println2(String s) {
        StackTraceElement ste = new Throwable().getStackTrace()[2];
        String ss = ste.getFileName() + "." + ste.getLineNumber() + ": ";
        println(ss + s);
    }
    
    /**
     * 向调试器发??调试信息, 不换??
     * 
     * @param s 调试信息
     */
    private static synchronized void print(String s) {
        System.out.print(s);
        System.out.flush();
    }

    /**
     * 向调试器发??调试信息, 并换??
     * 
     * @param s 调试信息
     */
    private static synchronized void println(String s) {
        System.out.println(s);
    }

    /**
     * 获取类文件的File对象
     * 
     * @param klass 类的class变量
     * @return 类文件的File对象
     */
    public static File getClassFile(Class klass) {
        URL url = Debug.class.getResource('/' + klass.getName().replace('.', '/') + ".class");
        return new File(url.getFile());
    }
    
    /**
     * 调试输出当前代码位置信息,包括代码文件时间信息
     *
     */
    public static void LOC() {
        if (VERBOSE) {
            Throwable tt = new Throwable();
            StackTraceElement ste = tt.getStackTrace()[1];
            String fileTime = null;
            try {
                File classFile = getClassFile(Class.forName(ste.getClassName()));
                fileTime = '[' + getTime(new Date(classFile.lastModified())) + ']';
            }
            catch (Throwable t) {
            }
            String ss = debugContext.equals("") ? "" : debugContext + " ";
            println("\n--------------------------------------------------------------------------------"
                    + "\n" + "No." + getCounter() + " " + ss + ste.getFileName() + fileTime
                    + "\n" + "Line " + ste.getLineNumber() + ": " + ste.getClassName() + "." 
                    + ste.getMethodName() + "() " + getTime() + "\n");
        }
    }
    
    /**
     * 取下????LOC调用序列??
     * 
     * @return 序列??
     */
    static int getCounter() {
        synchronized (debugCounterLock) {
            debugCounter++;
            return debugCounter;
        }
    }
    
    /**
     * 获取指定时间的字符串
     * 
     * @param theDate 指定时间
     * @return 指定时间的字符串
     */
    static String getTime(Date theDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        theDate = theDate == null ? new Date() : theDate;
        return sdf.format(theDate);
    }
}
