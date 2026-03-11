package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

/**
 * @author ylshao
 * @ClassName: TpType
 * @Description: 模板对象
 * @date 2012-11-20
 * 
 */
public class TpType implements Serializable, Comparable<TpType> {
	private static final long serialVersionUID = 1L;

	/**
	 * @Fields name : 模板类型的名称
	 */
	private String name;

	/**
	 * @Fields typePath :模板类型的文件夹路径
	 */
	private String typePath;
	/**
	 * @Fields processTemplates : 模板的集合
	 */
	private List<ProcessTemplate> processTemplates;

	/**
	 * @Fields tpTypes : 子模板类型
	 */
	private List<TpType> tpTypes;

	public TpType(String name) {
		super();
		this.name = name;
	}

	public TpType(String name, List<ProcessTemplate> processTemplates, List<TpType> tpTypes) {
		super();
		this.name = name;
		this.processTemplates = processTemplates;
		this.tpTypes = tpTypes;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ProcessTemplate> getProcessTemplates() {
		return processTemplates;
	}

	public void setProcessTemplates(List<ProcessTemplate> processTemplates) {
		this.processTemplates = processTemplates;
	}

	public List<TpType> getTpTypes() {
		return tpTypes;
	}

	public void setTpTypes(List<TpType> tpTypes) {
		this.tpTypes = tpTypes;
	}

	public String getTypePath() {
		return typePath;
	}

	public void setTypePath(String typePath) {
		this.typePath = typePath;
	}

	public int compareTo(TpType o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getName(), o.getName());
	}

}
