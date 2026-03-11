package com.glaway.mpm.pbom.db.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefaultCacheEngine implements CacheEngine
{
	private Map cache = new HashMap();


	public void add(String key, Object value)
	{
		this.cache.put(key, value);
	}

	public void add(String fqn, String key, Object value)
	{
		Map m = (Map)this.cache.get(fqn);
		if (m == null) {
			m = new HashMap();
		}

		m.put(key, value);
		this.cache.put(fqn, m);
	}


	public Object get(String fqn, String key)
	{
		Map m = (Map)this.cache.get(fqn);
		if (m == null) {
			return null;
		}

		return m.get(key);
	}


	public Object get(String fqn)
	{
		return this.cache.get(fqn);
	}


	public Collection getValues(String fqn)
	{
		Map m = (Map)this.cache.get(fqn);
		if (m == null) {
			return new ArrayList();
		}

		return m.values();
	}


	public void init()
	{
		this.cache = new HashMap();
	}

	public void clear()
	{
		this.cache.clear();
	}

	public void stop() {}


	public void remove(String fqn, String key)
	{
		Map m = (Map)this.cache.get(fqn);
		if (m != null) {
			m.remove(key);
		}
	}


	public void remove(String fqn)
	{
		this.cache.remove(fqn);
	}
}
