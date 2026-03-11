package com.bjsasc.avidm.mq.framework;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

// 统一处理事件的框架，实现单例模式
public class EventDispatcher {
	private static final EventDispatcher INSTANCE = new EventDispatcher();

	protected final Logger logger = Logger.getLogger(getClass());

	// 一种事件可以关联多个事件处理器，不区分事件处理的循序
	private Map<Class<? extends Event>, List<Handler<? extends Event>>> handlers;

	// 消息体标识和事件的映射
	private Map<String, Class<?>> events;

	final String handlerPath = "com/bjsasc/avidm/mq/handler";

	final String eventPath = "com/bjsasc/avidm/mq/event";

	private EventDispatcher() {
		handlers = new HashMap<Class<? extends Event>, List<Handler<? extends Event>>>();
		events = new HashMap<String, Class<?>>();
		loadEvents();
		loadHandlers();
	}

	public static EventDispatcher getInstance() {
		return INSTANCE;
	}

	// 自动扫描包：com.bjsasc.avidm.test.mq.handler 下所有的类，并注册到事件上
	private void loadHandlers() {
		logger.info("开始加载事件处理器...");

		try {
			ClassLoader cl = this.getClass().getClassLoader();
			Enumeration<URL> urls = cl.getResources(handlerPath);
			while (urls.hasMoreElements()) {

				URL url = urls.nextElement();
				String protocol = url.getProtocol();
				if ("jar".equals(protocol)) {
					JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
					Enumeration<JarEntry> entries = jar.entries();
					while (entries.hasMoreElements()) {
						JarEntry entry = entries.nextElement();

						String name = entry.getName();
						if (name.startsWith(handlerPath) && name.endsWith(".class")) {
							// 完整的类名
							name = name.substring(0, name.length() - 6);
							name = name.replace("/", ".");
							Class<?> clazz = Class.forName(name);
							if (!Handler.class.isAssignableFrom(clazz)) {
								continue;
							}

							@SuppressWarnings("unchecked")
							Handler<? extends Event> handler = (Handler<? extends Event>) clazz.newInstance();
							registerHandler(handler.supportEvent(), handler);
						}
					}
				}
			}
			System.out.println("所有 handlers = " + handlers);
			if(handlers.size()<=0) {
				List<Class<?>> classes = loadClass(handlerPath);
				int size = classes.size();
				for(int i=0;i<size;i++) {
					Class<?> clazz = classes.get(i);
					if(clazz == null) {
						continue;
					}
					if (!Handler.class.isAssignableFrom(clazz)) {
						continue;
					}
					@SuppressWarnings("unchecked")
					Handler<? extends Event> handler = (Handler<? extends Event>) clazz.newInstance();
					registerHandler(handler.supportEvent(), handler);
				}
				System.out.println("读取class 所有 handlers = " + handlers);
			}
			if(handlers.size()<=0) {
				String handlersClass = loadConfig("handlersClass");
				String[] array = handlersClass.split(",");
				for(int i=0;i<array.length;i++) {
					String name = array[i];
					if (name.startsWith(handlerPath) && name.endsWith(".class")) {
						// 完整的类名
						name = name.substring(0, name.length() - 6);
						name = name.replace("/", ".");
						Class<?> clazz = null;
						try {
							clazz = Class.forName(name);
						} catch (Exception e) {
							logger.info("未找到  " + name + " 事件处理器！");
						}
						if(clazz == null) {
							continue;
						}
						if (!Handler.class.isAssignableFrom(clazz)) {
							continue;
						}
						@SuppressWarnings("unchecked")
						Handler<? extends Event> handler = (Handler<? extends Event>) clazz.newInstance();
						registerHandler(handler.supportEvent(), handler);
					}
				}
				System.out.println("读取配置文件 所有 handlers = " + handlers);
			}
			logger.info("成功加载所有事件处理器！");
		} catch (Exception e) {
			logger.error("加载事件处理器发生错误！", e);
		}
	}

	// 自动扫描所有的事件（从com.bjsasc.avidm.test.mq.framework.Event衍生）定义类
	private void loadEvents() {
		logger.info("开始加载事件定义类...");
		try {
			ClassLoader cl = this.getClass().getClassLoader();
			Enumeration<URL> urls = cl.getResources(eventPath);
			
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				String protocol = url.getProtocol();
				if ("jar".equals(protocol)) {
					JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
					Enumeration<JarEntry> entries = jar.entries();
					while (entries.hasMoreElements()) {
						JarEntry entry = entries.nextElement();

						String name = entry.getName();
						if (name.startsWith(eventPath) && name.endsWith(".class")) {
							// 完整的类名
							name = name.substring(0, name.length() - 6);
							name = name.replace("/", ".");

							Class<?> clazz = Class.forName(name);
							// 不是抽象类且从事件类衍生
							if (Event.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
								logger.info(name);
								Method method = clazz.getMethod("matchID");
								Object event = clazz.newInstance();
								Object id = method.invoke(event);

								if (id != null) {
									events.put(id.toString(), clazz);
								}

								logger.info(id);
							}
						}
					}
				}
			}
			System.out.println("所有 events = " + events);
			if(events.size()<=0) {
				List<Class<?>> classes = loadClass(eventPath);
				int size = classes.size();
				for(int i=0;i<size;i++) {
					Class<?> clazz = classes.get(i);
					if(clazz == null) {
						continue;
					}
					// 不是抽象类且从事件类衍生
					if (Event.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
						Method method = clazz.getMethod("matchID");
						Object event = clazz.newInstance();
						Object id = method.invoke(event);

						if (id != null) {
							events.put(id.toString(), clazz);
						}

						logger.info(id);
					}
				}
				System.out.println("读取class 所有 events = " + events);
			}
			if(events.size()<=0) {
				String eventsClass = loadConfig("eventsClass");
				System.out.println("eventsClass = "+ eventsClass);
				String[] array = eventsClass.split(",");
				for(int i=0;i<array.length;i++) {
					String name = array[i];
					if (name.startsWith(eventPath) && name.endsWith(".class")) {
						// 完整的类名
						name = name.substring(0, name.length() - 6);
						name = name.replace("/", ".");
						Class<?> clazz = null;
						try {
							clazz = Class.forName(name);
						} catch (Exception e) {
							logger.info("未找到  " + name + " 事件定义类！");
						}
						if(clazz == null) {
							continue;
						}
						// 不是抽象类且从事件类衍生
						if (Event.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
							logger.info(name);
							Method method = clazz.getMethod("matchID");
							Object event = clazz.newInstance();
							Object id = method.invoke(event);

							if (id != null) {
								events.put(id.toString(), clazz);
							}

							logger.info(id);
						}
					}
				}
				System.out.println("读取配置文件 所有 events = " + events);
			}
			logger.info("成功加载所有事件定义类！");
		} catch (Exception e) {
			logger.error("加载事件定义类发生错误！", e);
		}
	}
	private String loadConfig(String key) {
		logger.info("开始读取配置文件：queue.properties...");

		String path = System.getProperty("AVIDM_HOME") + File.separator + "queue.properties";
		File f = new File(path);
		if (!f.exists()) {
			String msg = "AVIDM_HOME目录下queue.properties文件不存在，系统将退出！";
			logger.error(msg);
			throw new RuntimeException(msg);
		}

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		Properties props = new Properties();

		try {
			fis = new FileInputStream(f);
			bis = new BufferedInputStream(fis);
			props.load(bis);
		} catch (Exception e) {
			String msg = "读取配置文件：queue.properties，发生错误，系统将退出！";
			logger.error(msg, e);
			throw new RuntimeException(msg);
		} finally {
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(fis);
		}

		String eventsClass = props.getProperty(key);
		return eventsClass;
	}
	// 注册事件处理器
	private synchronized <E extends Event> void registerHandler(Class<? extends Event> clazz,
			Handler<? extends Event> handler) {
		List<Handler<? extends Event>> l = handlers.get(clazz);
		if (l == null) {
			l = new ArrayList<Handler<? extends Event>>();
			handlers.put(clazz, l);
		}
		l.add(handler);
		System.out.println("所有 handlers = " + l);
	}

	// 根据ID获取事件
	public Event getEvent(String id, JSONObject content) throws Exception {
		if (StringUtils.isEmpty(id)) {
			return null;
		}

		Object event = null;
		Class<?> clazz = events.get(id);

		try {
			event = clazz.newInstance();
			((Event) event).setContent(content);
		} catch (Exception e) {
			logger.error("获取事件失败，事件ID为：" + id, e);
			throw e;
		}

		return (Event) event;
	}

	// 分发事件
	public <E extends Event> void dispatch(E event) {
		List<Handler<? extends Event>> l = (List<Handler<? extends Event>>) handlers.get(event.getClass());
		if (l == null) {
			return;
		}

		for (Handler<? extends Event> handler : l) {
			@SuppressWarnings("unchecked")
			Handler<E> h = (Handler<E>) handler;
			h.setEvent(event);
			h.onEvent(event);
		}
	}
	
	private List<Class<?>> loadClass(String path) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		ClassLoader cl = this.getClass().getClassLoader();
		URL url = cl.getResource(path);
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            // 本地自己可见的代码
            findClassLocal(path,classes);
        }
		return null;
	}
    private void findClassLocal(final String packName,final List<Class<?>> classes){
        URI url = null ;
        final ClassLoader cl = this.getClass().getClassLoader();
        try {
            url = cl.getResource(packName).toURI();
        } catch (URISyntaxException e1) {
            throw new RuntimeException("未找到策略资源");
        }

        File file = new File(url);
        file.listFiles(new FileFilter() {

            public boolean accept(File chiFile) {
                if(chiFile.isDirectory()){
                    findClassLocal(packName+"."+chiFile.getName(),classes);
                }
                if(chiFile.getName().endsWith(".class")){

                    Class<?> clazz = null;
                    try {
                        clazz = cl.loadClass(packName + "." + chiFile.getName().replace(".class", ""));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if(!classes.contains(clazz)) {
                    	classes.add(clazz);
                    }
                    return true;
                }
                return false;
            }
        });

    }
}
