package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

/**
 * @author ylshao
 * @Description: 临时对象，用来保存搜索出的对象的部份属性
 * @date 2012-11-9
 *
 */
public class TempObject implements Serializable, Comparable<TempObject> {
	private static final long serialVersionUID = 1L;
	private String oid;
	private String number;
	private String occId;

	private String name;
	private String type;
	private String lifecycle;
	private String version;
	private String docNumber;

	public TempObject() {
		super();
	}

	public TempObject(String oid, String number, String occId) {
		super();
		this.oid = oid;
		this.number = number;
		this.occId = occId;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getOccId() {
		return occId;
	}

	public void setOccId(String occId) {
		this.occId = occId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getLifecycle() {
		return lifecycle;
	}

	public void setLifecycle(String lifecycle) {
		this.lifecycle = lifecycle;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDocNumber() {
		return docNumber;
	}

	public void setDocNumber(String docNumber) {
		this.docNumber = docNumber;
	}

	public int compareTo(TempObject o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getName(),
				o.getName());
	}
}
