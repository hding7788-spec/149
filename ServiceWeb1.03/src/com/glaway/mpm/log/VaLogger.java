package com.glaway.mpm.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import wt.method.MethodContext;
import wt.method.MethodFeedback;
import wt.method.RemoteMethodServer;
import wt.util.Cache;
import wt.util.WTProperties;

import com.glaway.mpm.util.PropertiesUtil;

public class VaLogger {
	private static final Set<String> TRUES = new HashSet<String>(
			Arrays.asList(new String[] { "TRUE", "YES", "ON", "1" }));
	public static String logPath = PropertiesUtil.getLocalCodeBase() + File.separator + "com" + File.separator
			+ "glaway" + File.separator + "mpm" + File.separator + "log" + File.separator + "VaLogger.properties";
	private static boolean threadNameNeeded = false;
	private static boolean invokerNeeded = false;
	private static Level levelDefault = null;
	private static Level levelSystem = null;
	private static PrintWriter filePW = null;
	private static PrintStream stdout = null;

	private static Cache cache = null;
	private static Map<String, Level> loggerRegisters = new HashMap<String, Level>();

	private static final String DEFAULT_CONTEXT = "Default";
	private static final Map<String, SystemInfo> RECORDS = new HashMap<String, SystemInfo>();
	private static final VaMessageFeedback FEEDBACK = new VaMessageFeedback();
	private String name;
	private Level level;

	public static VaLogger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	public static VaLogger getLogger() {
		StackTraceElement ste = new Throwable().getStackTrace()[1];
		return getLogger(ste.getClassName());
	}

	public static VaLogger getLogger(String name) {
		if (cache == null) {
			load();
		}

		VaLogger ret = (VaLogger) cache.get(name);
		if (ret == null) {
			ret = new VaLogger(name, Level.max(levelDefault, levelSystem));
			cache.put(name, ret);

			while (true) {
				Level level = loggerRegisters.get(name);
				if (level != null) {
					ret.level = Level.max(levelSystem, level);
					break;
				}

				int idx = name.lastIndexOf('.');
				if (idx <= 0) {
					break;
				}
				name = name.substring(0, idx);
			}
		}
		return ret;
	}

	private synchronized static void load() {
		cache = new Cache(64);

		try {
			RECORDS.put(DEFAULT_CONTEXT, new SystemInfo());
			String filePath = null;
			Properties properties = new Properties();
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(new File(logPath));
				properties.load(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			Enumeration e = properties.keys();
			while(e.hasMoreElements()){
				String key = e.nextElement()+"";
				String val = properties.getProperty(key);
				if ("logger.level.system".equalsIgnoreCase(key)) {
					levelSystem = Level.getLevel(val);
				} else if ("logger.level.default".equalsIgnoreCase(key)) {
					levelDefault = Level.getLevel(val);
				} else if ("logger.threadName".equalsIgnoreCase(key)) {
					threadNameNeeded = TRUES.contains(val.toUpperCase());
				} else if ("logger.invoker".equalsIgnoreCase(key)) {
					invokerNeeded = TRUES.contains(val.toUpperCase());
				} else if ("logger.stdout".equalsIgnoreCase(key)) {
					stdout = TRUES.contains(val.toUpperCase()) ? System.out
							: null;
				} else if ("logger.loc.file".equalsIgnoreCase(key)) {
					if (!RemoteMethodServer.ServerFlag) {
						filePath = new WTProperties(System.getProperties())
								.substitute(val);
					}
				} else if ("logger.svr.file".equalsIgnoreCase(key)) {
					if (RemoteMethodServer.ServerFlag) {
						filePath = WTProperties.getLocalProperties()
								.substitute(val);
					}
				} else {
					loggerRegisters.put(key, Level.getLevel(val));
				}
			}

			if (filePath != null) {
				File dir = new File(filePath).getParentFile();
				if (!dir.exists()) {
					dir.mkdirs();
				}
				filePW = new PrintWriter(new BufferedWriter(new FileWriter(
						filePath, true)), true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*private synchronized static void load() {
		cache = new Cache(64);
		String configFile = VaLogger.class.getName().replace('.', '/')
				.concat(".ini");
		InputStream is = VaLogger.class.getResourceAsStream("/" + configFile);

		try {
			RECORDS.put(DEFAULT_CONTEXT, new SystemInfo());
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "gbk"));
			String line;
			String filePath = null;
			while ((line = reader.readLine()) != null) {
				if (line.length() == 0 || line.startsWith("#")
						|| line.startsWith(";") || line.startsWith("[")) {
					continue;
				}

				int deliPos = line.indexOf("=");
				if (deliPos < 0) {
					continue;
				}
				String key = line.substring(0, deliPos).trim();
				String val = line.substring(deliPos + 1, line.length()).trim();
				if (key.startsWith("\"") && key.endsWith("\"")) {
					key = key.substring(1, key.length() - 1);
				}
				if (val.startsWith("\"") && val.endsWith("\"")) {
					val = val.substring(1, val.length() - 1);
				}

				if ("logger.level.system".equalsIgnoreCase(key)) {
					levelSystem = Level.getLevel(val);
				} else if ("logger.level.default".equalsIgnoreCase(key)) {
					levelDefault = Level.getLevel(val);
				} else if ("logger.threadName".equalsIgnoreCase(key)) {
					threadNameNeeded = TRUES.contains(val.toUpperCase());
				} else if ("logger.invoker".equalsIgnoreCase(key)) {
					invokerNeeded = TRUES.contains(val.toUpperCase());
				} else if ("logger.stdout".equalsIgnoreCase(key)) {
					stdout = TRUES.contains(val.toUpperCase()) ? System.out
							: null;
				} else if ("logger.loc.file".equalsIgnoreCase(key)) {
					if (!RemoteMethodServer.ServerFlag) {
						filePath = new WTProperties(System.getProperties())
								.substitute(val);
					}
				} else if ("logger.svr.file".equalsIgnoreCase(key)) {
					if (RemoteMethodServer.ServerFlag) {
						filePath = WTProperties.getLocalProperties()
								.substitute(val);
					}
				} else {
					loggerRegisters.put(key, Level.getLevel(val));
				}
			}
			if (filePath != null) {
				File dir = new File(filePath).getParentFile();
				if (!dir.exists()) {
					dir.mkdirs();
				}
				filePW = new PrintWriter(new BufferedWriter(new FileWriter(
						filePath, true)), true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	private VaLogger() {
	}

	private VaLogger(String name, Level level) {
		this.name = name;
		this.level = level;
	}

	public boolean isEnabled() {
		return this.level.lessThanOrEqual(Level.ALL);
	}

	public boolean isDisabled() {
		return this.level.greaterThanOrEqual(Level.OFF);
	}

	public boolean isTraceEnabled() {
		return this.level.lessThanOrEqual(Level.TRACE);
	}

	public boolean isDebugEnabled() {
		return this.level.lessThanOrEqual(Level.DEBUG);
	}

	public boolean isInfoEnabled() {
		return this.level.lessThanOrEqual(Level.INFO);
	}

	public boolean isWarnEnabled() {
		return this.level.lessThanOrEqual(Level.WARN);
	}

	public boolean isErrorEnabled() {
		return this.level.lessThanOrEqual(Level.ERROR);
	}

	public void trace(Object... o) {
		log(Level.TRACE, o);
	}

	public void debug(Object... o) {
		log(Level.DEBUG, o);
	}

	public void info(Object... o) {
		log(Level.INFO, o);
	}

	public void warn(Object... o) {
		log(Level.WARN, o);
	}

	public void error(Object... o) {
		log(Level.ERROR, o);
	}

	public void trace(String msg) {
		log(Level.TRACE, msg);
	}

	public void debug(String msg) {
		log(Level.DEBUG, msg);
	}

	public void info(String msg) {
		log(Level.INFO, msg);
	}

	public void warn(String msg) {
		log(Level.WARN, msg);
	}

	public void error(String msg) {
		log(Level.ERROR, msg);
	}

	public void trace(Throwable t) {
		log(Level.TRACE, t);
	}

	public void debug(Throwable t) {
		log(Level.DEBUG, t);
	}

	public void info(Throwable t) {
		log(Level.INFO, t);
	}

	public void warn(Throwable t) {
		log(Level.WARN, t);
	}

	public void error(Throwable t) {
		log(Level.ERROR, t);
	}

	public void traceContext(String context) {
		if (this.isTraceEnabled()) {
			if (context == null) {
				context = DEFAULT_CONTEXT;
			}
			SystemInfo info = RECORDS.get(context);
			if (info == null) {
				RECORDS.put(context, new SystemInfo());
			} else {
				info.update();
			}

			log(Level.TRACE, new StringBuffer().append(context).append('=')
					.append(info).toString());
		}
	}

	public void traceContext(String context, Object extraInfo) {
		if (this.isTraceEnabled()) {
			if (context == null) {
				context = DEFAULT_CONTEXT;
			}
			SystemInfo info = RECORDS.get(context);
			if (info == null) {
				info = new SystemInfo();
				RECORDS.put(context, info);
			}

			log(Level.TRACE, new StringBuffer().append(context).append('(')
					.append(extraInfo).append(")=").append(info).toString());
		}
	}

	private void log(Level level, Object... o) {
		if (this.level.lessThanOrEqual(level)) {
			StringBuffer buf = getLayoutPrefix(level);
			for (Object obj : o)
				buf.append(obj);
			println(buf);
			feedback(buf);
		}
	}

	private void log(Level level, String msg) {
		if (this.level.lessThanOrEqual(level)) {
			StringBuffer buf = getLayoutPrefix(level);
			buf.append(msg);
			println(buf);
			feedback(buf);
		}
	}

	private void log(Level level, Throwable msg) {
		if (this.level.lessThanOrEqual(level)) {
			StringBuffer buf = getLayoutPrefix(level);

			String s = msg.getLocalizedMessage() == null ? "(null)" : msg
					.getLocalizedMessage();
			buf.append(msg.getClass().getName()).append('\n').append(s)
					.append('\n');

			StackTraceElement[] te = msg.getStackTrace();
			for (int i = 0; i < te.length; i++) {
				buf.append("\t@ ").append(te[i].getClassName());
				buf.append('.').append(te[i].getMethodName());
				buf.append('(').append(te[i].getFileName());
				buf.append(':').append(te[i].getLineNumber());
				if (te[i].getLineNumber() < 0) {
					buf.append(", Native Method");
				}
				buf.append(")\n");
			}

			Throwable cause = msg.getCause();
			while (cause != null) {
				buf.append("\n");

				te = cause.getStackTrace();
				for (int i = 0; i < te.length; i++) {
					buf.append("\t@ ").append(te[i].getClassName());
					buf.append('.').append(te[i].getMethodName());
					buf.append('(').append(te[i].getFileName());
					buf.append(':').append(te[i].getLineNumber());
					if (te[i].getLineNumber() < 0) {
						buf.append(", Native Method");
					}
					buf.append(")\n");
				}

				cause = cause.getCause();
			}

			println(buf);
			feedback(buf);
		}
	}

	private StringBuffer getLayoutPrefix(Level level) {
		StackTraceElement ste = new Throwable().getStackTrace()[3];

		StringBuffer ret = new StringBuffer(512).append(getTime()).append(" ").append(level);
		if (threadNameNeeded) {
			ret.append(" [").append(Thread.currentThread().getName()).append("]");
		}
		if(ste.getFileName()!=null){
			ret.append(' ').append(ste.getFileName().substring(0, ste.getFileName().length() - 5));
		}
		ret.append('.').append(ste.getMethodName()).append("().").append(ste.getLineNumber()).append(": ");
		return ret;
	}

	private static synchronized void println(StringBuffer buf) {
		if (stdout != null) {
			System.out.println(buf);
		}
		if (filePW != null) {
			filePW.println(buf);
			filePW.flush();
		}
	}

	private synchronized void feedback(StringBuffer buf) {
		if (invokerNeeded && RemoteMethodServer.ServerFlag) {
			FEEDBACK.setMessage(buf.toString());
			try {
				MethodContext.getContext().sendFeedback(FEEDBACK);
			} catch (Throwable tt) {
				tt.printStackTrace();
			}
		}
	}

	private static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		return sdf.format(new Date());
	}

	@Override
	public String toString() {
		return new StringBuffer("VaLogger[").append(this.name).append(", ")
				.append(this.level).append(']').toString();
	}
}

class VaMessageFeedback implements MethodFeedback, Serializable {
	private static final long serialVersionUID = 8624074334803904715L;

	String message = "";

	public VaMessageFeedback() {
	}

	@Override
	public void execute() {
		System.out.println(message);
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

class SystemInfo {
	private long currentTimeMillis;

	public SystemInfo() {
		this.currentTimeMillis = System.currentTimeMillis();
	}

	public void update() {
		this.currentTimeMillis = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		long currentTimeMillis = this.currentTimeMillis;
		this.currentTimeMillis = System.currentTimeMillis();

		return new StringBuffer().append("[time spent=")
				.append(this.currentTimeMillis - currentTimeMillis).append("]")
				.toString();
	}
}

class Level {
	public static final Level ALL = new Level(100, "ALL");
	public static final Level TRACE = new Level(200, "TRACE");
	public static final Level DEBUG = new Level(300, "DEBUG");
	public static final Level INFO = new Level(400, "INFO");
	public static final Level WARN = new Level(500, "WARN");
	public static final Level ERROR = new Level(600, "ERROR");
	public static final Level OFF = new Level(700, "OFF");

	private static final Level[] LEVELS = { ALL, TRACE, DEBUG, INFO, WARN,
			ERROR, OFF };

	private int value;
	private String display;

	public static Level getLevel(String display) {
		Level ret = ALL;
		int i = 0;
		int len = LEVELS.length - 1;
		display = display.toUpperCase();
		while (!LEVELS[i].display.equals(display) && i < len) {
			ret = LEVELS[++i];
		}
		return ret;
	}

	private Level() {
	}

	private Level(int value, String display) {
		this.value = value;
		this.display = display == null ? "" : display.toUpperCase();
	}

	public boolean lessThanOrEqual(Level level) {
		return value <= level.value;
	}

	public boolean greaterThanOrEqual(Level level) {
		return value >= level.value;
	}

	public static Level max(Level one, Level other) {
		if (one == null) {
			return other;
		} else if (other == null) {
			return one;
		}
		return one.value > other.value ? one : other;
	}

	@Override
	public String toString() {
		String ret = this.display + "     ";
		return ret.substring(0, 5);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((this.display == null) ? 0 : this.display.hashCode());
		result = PRIME * result + this.value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Level other = (Level) obj;
		if (this.display == null) {
			if (other.display != null) {
				return false;
			}
		} else if (!this.display.equals(other.display)) {
			return false;
		}
		if (this.value != other.value) {
			return false;
		}
		return true;
	}
}
