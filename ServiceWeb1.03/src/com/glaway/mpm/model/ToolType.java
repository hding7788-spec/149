package com.glaway.mpm.model;

import java.io.Serializable;
import java.text.Collator;
import java.util.List;
import java.util.Locale;

public class ToolType implements Serializable, Comparable<ToolType> {

	private static final long serialVersionUID = 1L;
	/**
	 * @Fields name :设备类型的名称
	 */
	private String name;
	/**
	 * @Fields commonStrings : 设备集合
	 */
	private List<Tool> tools;

	private List<ToolType> toolTypes;

	private String typePath;

	public ToolType(String name) {
		super();
		this.name = name;
	}

	public ToolType(String name, List<Tool> tools, List<ToolType> toolTypes) {
		super();
		this.name = name;
		this.tools = tools;
		this.toolTypes = toolTypes;
	}

	public String getTypePath() {
		return typePath;
	}

	public void setTypePath(String typePath) {
		this.typePath = typePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Tool> getTools() {
		return tools;
	}

	public void setTools(List<Tool> tools) {
		this.tools = tools;
	}

	public List<ToolType> getToolTypes() {
		return toolTypes;
	}

	public void setToolTypes(List<ToolType> toolTypes) {
		this.toolTypes = toolTypes;
	}

	public int compareTo(ToolType o) {
		return Collator.getInstance(Locale.CHINA).compare(this.getName(),
				o.getName());
	}

}
