package com.glaway.mpm.pbom.db.cache;

import java.util.Collection;

public interface CacheEngine
{
	public static final String DUMMY_FQN = "";
	public static final String NOTIFICATION = "notification";


	public void init();
	public void clear();

	public void stop();


	public void add(String key, Object value);


	public void add(String fqn, String key, Object value);


	public Object get(String fqn, String key);


	public Object get(String fqn);


	public Collection getValues(String fqn);


	public void remove(String fqn, String key);


	public void remove(String fqn);
}
