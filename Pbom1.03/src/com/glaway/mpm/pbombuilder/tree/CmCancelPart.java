package com.glaway.mpm.pbombuilder.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmCancelPart {

	private int column;
	private String object;
	private CmTreeNode cancelNode;
	private List<CmCancelPart> list;
	private boolean isClear;// 是否清空
	private CmLightPart part;
	private boolean isClearRemark;// 清空备注

	private String oldMType;
	private boolean isChangeMType;
	private String oldZzcj;
	private boolean isChangeZzcj;
	private String oldFzcj;
	private Map<String,Boolean> secondePlant = new HashMap<String,Boolean>();  // 辅助车间
	private boolean isChangeFzcj;

	private String oldKeyComponent;
	private boolean isChangeKeyComponent;

	private String oldPindex;
	private boolean isChangepindex;
	private String oldCindex;
	private boolean isChangecindex;
	private String oldMindex;
	private boolean isChangemindex;
	private String oldPhase_code;
	private boolean isChangephase_code;
	private String oldSetMark;
	private boolean isChangesetMark;
	private String oldAdjustable;

	private boolean isChangeAdjustable;
	private boolean isChangeSecret;

	public boolean isChangeSecret() {
		return isChangeSecret;
	}

	public void setChangeSecret(boolean changeSecret) {
		isChangeSecret = changeSecret;
	}

	private String oldSecret;

	public String getOldSecret() {
		return oldSecret;
	}

	public void setOldSecret(String oldSecret) {
		this.oldSecret = oldSecret;
	}

	public boolean isChangephase_code() {
		return isChangephase_code;
	}

	public void setChangephase_code(boolean isChangephase_code) {
		this.isChangephase_code = isChangephase_code;
	}


	public boolean isChangepindex() {
		return isChangepindex;
	}

	public void setChangepindex(boolean isChangepindex) {
		this.isChangepindex = isChangepindex;
	}

	public boolean isChangecindex() {
		return isChangecindex;
	}

	public void setChangecindex(boolean isChangecindex) {
		this.isChangecindex = isChangecindex;
	}

	public boolean isChangemindex() {
		return isChangemindex;
	}

	public void setChangemindex(boolean isChangemindex) {
		this.isChangemindex = isChangemindex;
	}

	public String getOldPindex() {
		return oldPindex;
	}

	public void setOldPindex(String oldPindex) {
		this.oldPindex = oldPindex;
	}

	public String getOldCindex() {
		return oldCindex;
	}

	public void setOldCindex(String oldCindex) {
		this.oldCindex = oldCindex;
	}

	public String getOldMindex() {
		return oldMindex;
	}

	public void setOldMindex(String oldMindex) {
		this.oldMindex = oldMindex;
	}

	public String getOldPhase_code() {
		return oldPhase_code;
	}

	public void setOldPhase_code(String oldPhase_code) {
		this.oldPhase_code = oldPhase_code;
	}



	@Override
	public String toString() {
		return cancelNode.getPart().getPartNumber() + "  oldMType:" + oldMType+"  isChangeMType:"+isChangeMType
				+ "  oldZzcj:" + oldZzcj +"  isChangeZzcj:"+isChangeZzcj + "  oldFzcj:" + oldFzcj+"  isChangeFzcj:"+isChangeFzcj;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public CmTreeNode getCancelNode() {
		return cancelNode;
	}

	public void setCancelNode(CmTreeNode cancelNode) {
		this.cancelNode = cancelNode;
	}

	public List<CmCancelPart> getList() {
		return list;
	}

	public void setList(List<CmCancelPart> list) {
		this.list = list;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public boolean isClear() {
		return isClear;
	}

	public void setClear(boolean isClear) {
		this.isClear = isClear;
	}

	public CmLightPart getPart() {
		return part;
	}

	public void setPart(CmLightPart part) {
		this.part = part;
	}

	public boolean isClearRemark() {
		return isClearRemark;
	}

	public void setClearRemark(boolean isClearRemark) {
		this.isClearRemark = isClearRemark;
	}

	public String getOldMType() {
		return oldMType;
	}

	public void setOldMType(String oldMType) {
		this.oldMType = oldMType;
	}

	public String getOldZzcj() {
		return oldZzcj;
	}

	public void setOldZzcj(String oldZzcj) {
		this.oldZzcj = oldZzcj;
	}

	public String getOldFzcj() {
		return oldFzcj;
	}

	public void setOldFzcj(String oldFzcj) {
		this.oldFzcj = oldFzcj;
	}

	public boolean isChangeMType() {
		return isChangeMType;
	}

	public void setChangeMType(boolean isChangeMType) {
		this.isChangeMType = isChangeMType;
	}

	public boolean isChangeZzcj() {
		return isChangeZzcj;
	}

	public void setChangeZzcj(boolean isChangeZzcj) {
		this.isChangeZzcj = isChangeZzcj;
	}

	public boolean isChangeFzcj() {
		return isChangeFzcj;
	}

	public void setChangeFzcj(boolean isChangeFzcj) {
		this.isChangeFzcj = isChangeFzcj;
	}

	public Map<String, Boolean> getSecondePlant() {
		return secondePlant;
	}

	public void setSecondePlant(Map<String, Boolean> secondePlant) {
		this.secondePlant = secondePlant;
	}

	public String getOldKeyComponent() {
		return oldKeyComponent;
	}

	public void setOldKeyComponent(String oldKeyComponent) {
		this.oldKeyComponent = oldKeyComponent;
	}

	public boolean isChangeKeyComponent() {
		return isChangeKeyComponent;
	}

	public void setChangeKeyComponent(boolean isChangeKeyComponent) {
		this.isChangeKeyComponent = isChangeKeyComponent;
	}

	public String getOldSetMark() {
		return oldSetMark;
	}

	public void setOldSetMark(String oldSetMark) {
		this.oldSetMark = oldSetMark;
	}

	public boolean isChangesetMark() {
		return isChangesetMark;
	}

	public void setChangesetMark(boolean changesetMark) {
		isChangesetMark = changesetMark;
	}

	public String getOldAdjustable() {
		return oldAdjustable;
	}

	public void setOldAdjustable(String oldAdjustable) {
		this.oldAdjustable = oldAdjustable;
	}

	public boolean isChangeAdjustable() {
		return isChangeAdjustable;
	}

	public void setChangeAdjustable(boolean changeAdjustable) {
		isChangeAdjustable = changeAdjustable;
	}
}
