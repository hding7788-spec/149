package ext.casc.report.technics;

import java.util.HashSet;
import java.util.Set;

public class TreeNode {
	private Set<TreeNode> parents = new HashSet<TreeNode>();
	private String number;
	private Set<TreeNode> childrens = new HashSet<TreeNode>();;

	public TreeNode(TreeNode parent, String number, TreeNode children) {
		this.number = number;
		if(parent!=null){
			this.parents.add(parent);
		}
		if(children!=null){
			this.childrens.add(children);
		}
	}

	public Set<TreeNode> getParents() {
		return parents;
	}

	public void setParents(Set<TreeNode> parents) {
		this.parents = parents;
	}

	public Set<TreeNode> getChildrens() {
		return childrens;
	}

	public void setChildrens(Set<TreeNode> childrens) {
		this.childrens = childrens;
	}

	public void addP(TreeNode p){
		this.parents.add(p);
	}
	public void addC(TreeNode p){
		this.parents.add(p);
	}
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}




}
