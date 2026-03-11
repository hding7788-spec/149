package com.glaway.mpm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConsCheckTree implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private String gekeyid;

	private List<ConsCheckTree> trees;

	private List<ConsCheckRecord> records;

	public void addTrees(ConsCheckTree tree){
		if(trees == null){
			trees = new ArrayList<ConsCheckTree>();
		}
		trees.add(tree);
	}

	public void addRecord(ConsCheckRecord record){
		if(records == null){
			records = new ArrayList<ConsCheckRecord>();
		}
		records.add(record);
	}

	public ConsCheckTree(String name, String gekeyid) {
		this.name = name;
		this.gekeyid = gekeyid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGekeyid() {
		return gekeyid;
	}

	public void setGekeyid(String gekeyid) {
		this.gekeyid = gekeyid;
	}

	public List<ConsCheckTree> getTrees() {
		return trees;
	}

	public void setTrees(List<ConsCheckTree> trees) {
		this.trees = trees;
	}

	public List<ConsCheckRecord> getRecords() {
		return records;
	}

	public void setRecords(List<ConsCheckRecord> records) {
		this.records = records;
	}
}
