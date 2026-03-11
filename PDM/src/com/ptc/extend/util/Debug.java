package com.ptc.extend.util;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import wt.util.WTProperties;

public class Debug
{

    static int debugCounter = 0;
    static Object debugCounterLock = new Object();
    static String debugContext = "";
    static final String VERBOSE_KEY = "com.netflux.debug.verbose";
    public static boolean VERBOSE;
    static int verboseCount = 0;

    public Debug()
    {
    }

    public static boolean enabled()
    {
        return VERBOSE;
    }

    public static synchronized boolean enter()
    {
        verboseCount++;
        VERBOSE = verboseCount > 0;
        return VERBOSE;
    }

    public static synchronized boolean leave()
    {
        verboseCount--;
        VERBOSE = verboseCount > 0;
        return VERBOSE;
    }

    private static String getTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return sdf.format(new Date());
    }

    public static void E(Throwable t)
    {
        String s = t.getLocalizedMessage();
        s = s != null ? s : t.getMessage();
        s = s != null ? s : "";
        StringBuffer ss = new StringBuffer(t.getClass().getName());
        ss.append(": ").append(s).append('\n');
        StackTraceElement ste[] = t.getStackTrace();
        for(int i = 0; i < ste.length; i++)
        {
            ss.append("\t@ ").append(ste[i].getClassName());
            ss.append('.').append(ste[i].getMethodName());
            ss.append('(').append(ste[i].getFileName());
            ss.append(':').append(ste[i].getLineNumber());
            if(ste[i].getLineNumber() < 0)
            {
                ss.append(", Native Method");
            }
            ss.append(")\n");
        }

        String s1 = "\n******************************************************************************" +
"**"
;
        String s2 = (new StringBuilder(String.valueOf(ste[1].getFileName()))).append(".").append(ste[1].getLineNumber()).append(": ").append(getTime()).toString();
        println(s1);
        println(s2);
        println(ss.toString());
    }

    public static void P_(Object o)
    {
        if(VERBOSE)
        {
            print(String.valueOf(o));
        }
    }

    public static void P_(long l)
    {
        if(VERBOSE)
        {
            print(String.valueOf(l));
        }
    }

    public static void P_(double d)
    {
        if(VERBOSE)
        {
            print(String.valueOf(d));
        }
    }

    public static void P_(Object o1, Object o2)
    {
        if(VERBOSE)
        {
            print((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).toString());
        }
    }

    public static void P_(Object o1, Object o2, Object o3)
    {
        if(VERBOSE)
        {
            print((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).append(o3).toString());
        }
    }

    public static void P_(Object o1, Object o2, Object o3, Object o4)
    {
        if(VERBOSE)
        {
            print((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).append(o3).append(o4).toString());
        }
    }

    public static void P_(Object o1, Object o2, Object o3, Object o4, Object o5)
    {
        if(VERBOSE)
        {
            print((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).append(o3).append(o4).append(o5).toString());
        }
    }

    public static void P_(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6)
    {
        if(VERBOSE)
        {
            print((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).append(o3).append(o4).append(o5).append(o6).toString());
        }
    }

    public static void P_(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7)
    {
        if(VERBOSE)
        {
            print((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).append(o3).append(o4).append(o5).append(o6).append(o7).toString());
        }
    }

    public static void P_(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8)
    {
        if(VERBOSE)
        {
            print((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).append(o3).append(o4).append(o5).append(o6).append(o7).append(o8).toString());
        }
    }

    public static void P_(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, 
            Object o9)
    {
        if(VERBOSE)
        {
            print((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).append(o3).append(o4).append(o5).append(o6).append(o7).append(o8).append(o9).toString());
        }
    }

    public static void P(Object o)
    {
        if(VERBOSE)
        {
            println2(String.valueOf(o));
        }
    }

    public static void P()
    {
        if(VERBOSE)
        {
            println2("");
        }
    }

    public static void P(long l)
    {
        if(VERBOSE)
        {
            println2(Long.toString(l));
        }
    }

    public static void P(double d)
    {
        if(VERBOSE)
        {
            println2(Double.toString(d));
        }
    }

    public static void P(Object o1, Object o2)
    {
        if(VERBOSE)
        {
            println2((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).toString());
        }
    }

    public static void P(Object o1, Object o2, Object o3)
    {
        if(VERBOSE)
        {
            println2((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).append(o3).toString());
        }
    }

    public static void P(Object o1, Object o2, Object o3, Object o4)
    {
        if(VERBOSE)
        {
            println2((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).append(o3).append(o4).toString());
        }
    }

    public static void P(Object o1, Object o2, Object o3, Object o4, Object o5)
    {
        if(VERBOSE)
        {
            println2((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).append(o3).append(o4).append(o5).toString());
        }
    }

    public static void P(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6)
    {
        if(VERBOSE)
        {
            println2((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).append(o3).append(o4).append(o5).append(o6).toString());
        }
    }

    public static void P(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7)
    {
        if(VERBOSE)
        {
            println2((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).append(o3).append(o4).append(o5).append(o6).append(o7).toString());
        }
    }

    public static void P(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8)
    {
        if(VERBOSE)
        {
            println2((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).append(o3).append(o4).append(o5).append(o6).append(o7).append(o8).toString());
        }
    }

    public static void P(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, Object o8, 
            Object o9)
    {
        if(VERBOSE)
        {
            println2((new StringBuilder(String.valueOf(String.valueOf(o1)))).append(o2).append(o3).append(o4).append(o5).append(o6).append(o7).append(o8).append(o9).toString());
        }
    }

    public static void info(Object o)
    {
        println2(String.valueOf(o));
    }

    public static void info(Object o1, Object o2)
    {
        println2((new StringBuilder()).append(o1).append(o2).toString());
    }

    public static void info(Object o1, Object o2, Object o3)
    {
        println2((new StringBuilder()).append(o1).append(o2).append(o3).toString());
    }

    public static void info(Object o1, Object o2, Object o3, Object o4)
    {
        println2((new StringBuilder()).append(o1).append(o2).append(o3).append(o4).toString());
    }

    public static void info(Object o1, Object o2, Object o3, Object o4, Object o5)
    {
        println2((new StringBuilder()).append(o1).append(o2).append(o3).append(o4).append(o5).toString());
    }

    public static void info(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6)
    {
        println2((new StringBuilder()).append(o1).append(o2).append(o3).append(o4).append(o5).append(o6).toString());
    }

    public static void info(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7)
    {
        println2((new StringBuilder()).append(o1).append(o2).append(o3).append(o4).append(o5).append(o6).append(o7).toString());
    }

    private static void println2(String s)
    {
        StackTraceElement ste = (new Throwable()).getStackTrace()[2];
        String ss = (new StringBuilder(String.valueOf(ste.getFileName()))).append(".").append(ste.getLineNumber()).append(": ").toString();
        println((new StringBuilder(String.valueOf(ss))).append(s).toString());
    }

    private static synchronized void print(String s)
    {
        System.out.print(s);
        System.out.flush();
    }

    private static synchronized void println(String s)
    {
        System.out.println(s);
    }

    public static File getClassFile(Class klass)
    {
        URL url = com.ptc.extend.util.Debug.class.getResource((new StringBuilder(String.valueOf('/'))).append(klass.getName().replace('.', '/')).append(".class").toString());
        return new File(url.getFile());
    }

    static 
    {
        VERBOSE = true;
        try
        {
            WTProperties wtp = WTProperties.getLocalProperties();
            VERBOSE = wtp.getProperty("com.netflux.debug.verbose", true);
            verboseCount = VERBOSE ? 1 : 0;
        }
        catch(Exception e)
        {
            throw new ExceptionInInitializerError(e);
        }
    }
}
