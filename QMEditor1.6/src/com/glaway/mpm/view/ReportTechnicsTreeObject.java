package com.glaway.mpm.view;

import java.awt.Image;
import java.util.Vector;

import javax.swing.ImageIcon;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;

public class ReportTechnicsTreeObject implements XWTreeObject {
	private String partNumber;
	private String pplanNumber;
	private String technicsNumber;
	private String version;
	private String type;
	private String batch;
	private String technicsCategory;
	private String pplanName;
	private String name;

	public ReportTechnicsTreeObject(String partNumber, String number, String docNumber,
			String version, String type, String batch, String technicsCategory, String pplanName,String name) {
		this.partNumber = partNumber;
		this.pplanNumber = number;
		this.technicsNumber = docNumber;
		this.version = version;
		this.type = type;
		this.batch = batch;
		this.technicsCategory = technicsCategory;
		this.pplanName = pplanName;
		this.name = name;
	}

	@Override
	public int compareTo(Object arg0) {
		if (!(arg0 instanceof ReportTechnicsTreeObject)) {
			throw new RuntimeException("树节点类型不匹配！");
		}
		ReportTechnicsTreeObject temp = (ReportTechnicsTreeObject) arg0;
		String me =technicsNumber;
//		String me = getDisplayName();
//		String number = temp.getDisplayName();
		String number = temp.getTechnicsNumber();
		return number.compareTo(me);
	}

	@Override
	public void setTreeCellData(Element paramElement) {
		// TODO Auto-generated method stub

	}

	public Document getTechnicsDocument() {
		return WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(this.technicsNumber);
	}

	@Override
	public Element getTreeCellData() {
		Document document = getTechnicsDocument();
		return XmlUtility.getTechnicsElement(document);
	}

	@Override
	public Vector expand() throws Exception {
		Vector vec = new Vector();
		return vec;
	}

	@Override
	public String getDisplayName() {
	    if (pplanName!=null &&name!=null && pplanName.equals(name)) {
            return this.name +"("+pplanNumber+")"+"_" + this.version;
        }else{
            return this.name +"_" + this.version;
        }
	}

	@Override
	public String getTipNoteText() {
	    if (pplanName!=null &&name!=null&& pplanName.equals(name)) {
	        return this.name +"("+pplanNumber+")"+"_" + this.version;
        }else{
            return this.name +"_" + this.version;
        }
	}

	@Override
	public Image getOpenImage() {
		String iconName = "/images/reports_24.png";
		ImageIcon icon = new ImageIcon(getClass().getResource(iconName));
		return icon.getImage();
	}

	@Override
	public Image getCloseImage() {
		return getOpenImage();
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getTechnicsNumber() {
		return technicsNumber;
	}

	public String getPplanNumber() {
		return pplanNumber;
	}

	public String getVersion() {
		return version;
	}

	public String getType() {
		return type;
	}

	public String getBatch() {
		return batch;
	}

	public String getTechnicsCategory() {
		return technicsCategory;
	}



	@Override
	public String getEqualsName() {
		// TODO Auto-generated method stub
		return this.getDisplayName();
	}

    public String getPplanName() {
        return pplanName;
    }

}
