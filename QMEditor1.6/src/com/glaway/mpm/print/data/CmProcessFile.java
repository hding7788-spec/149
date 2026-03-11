package com.glaway.mpm.print.data;

import java.io.Serializable;
import java.util.List;

public class CmProcessFile  implements Serializable{

	private static final long serialVersionUID = -4682440818456858784L;

	private List<CmPrintInfoBean> fileList;

	private boolean repeat;

	public List<CmPrintInfoBean> getFileList() {
		return fileList;
	}

	public void setFileList(List<CmPrintInfoBean> fileList) {
		this.fileList = fileList;
	}

	public boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}
}
