package com.glaway.mpm.visual.view.tree;

import com.glaway.mpm.visual.bean.VaInstanceIdentifier;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.bean.VaPartUsesOcc;
import com.glaway.mpm.visual.biz.VaBizObjUtil;
import com.glaway.mpm.visual.control.VaMathUtil;
import com.glaway.mpm.visual.util.VaSearchHelper;
import com.glaway.mpm.visual.util.VaXML;
import com.glaway.mpm.visual.util.VaXML.VaXmlProperty;
import com.glaway.mpm.visual.view.pview.VaEPViewStructureGenerator;
import com.ptc.pview.pvkapp.ComponentInstance;
import com.ptc.pview.pvkapp.ComponentNode;
import com.ptc.pview.pvkapp.ShapeInstance;
import wt.part.WTPart;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.vecmath.Matrix4d;
import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class VaTreeNode extends DefaultMutableTreeNode implements Serializable, Comparable<VaTreeNode> {
	private static final long serialVersionUID = 610285217272571232L;

	private VaLightPart part;

	private Matrix4d matrix;
	private Matrix4d relativeMatrix;
	private String occId;
	private String occpath;

	public String getOccpath() {
		return occpath;
	}

	public void setOccpath(String occpath) {
		this.occpath = occpath;
	}
	private VaInstanceIdentifier instanceIdentifier;

	private boolean isSelected;
	private boolean marked;

	// ProductView objects -> start
	private ComponentNode _pviewComponentNode;
	private ComponentInstance _pviewComponentInstance;
	private ShapeInstance _pviewShapeInstance;
	private Collection<String[]> _pviewInstanceProperties;

	private Object cadObject;
	private boolean isInWorkspace;
	private boolean isLockedForCad;
	private String fktGrp;
	private String[] infoTxt = { "", "" };
	private Matrix4d relative_matrix = null;
	private boolean locationChange;
	private Vector bboxes = null;
	private Matrix4d basematrix = null;
	private String diode = "";
	private int _index;
	// ProductView objects -> end

	//可替代件信息
	private String replaceableParts;

	private boolean isAnno = false;

	public boolean isAnno() {
		return isAnno;
	}

	public void setAnno(boolean isAnno) {
		this.isAnno = isAnno;
	}
	//是否已经装配
	private boolean isUsed = false;

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}
	protected String nodeType;

	public static final String AO = "AO";
	public static final String OPS = "OPS";
	public static final String CPS = "CPS";
	public static final String OP = "OP";
	public static final String CP = "CP";
	public static final String PART = "PART";
	public static final String GY = "GY";
	public static final String CONSULT = "CONSULT";

	//设计资源库，工艺定额属性信息
    private String flag ;
    //单位
    private String dw2 ;
    //主计量单位
    private String dw ;
    //型号牌号
    private String xhph;
    //规格
    private String gg;
    //生产厂家
    private String sccj;
    //质量等级
    private String zldj;
    //封装形式
    private String fzxs;
    //电参数特选要求
    private String dcstxyq ;
    //备注
    private String comment;
    //技术条件
    private String jstj;

    //设计资源库工艺定额添加
    //零组件生产类型
    private String dataType;
    //标准号
    private String bzh;
    //机械性能等级
    private String jxxndjhyd;
    //表面处理
    private String bmcl;
    //热处理
    private String rcl;
    //产品形式
    private String cpxs;
    //产品等级
    private String cpdj;
    //扳令形式
    private String bnxs;
    //是否进口
    private String sfjk;

    private String cl;

	private String wh;

	private List<VaLightPart> pathFromCI;
	//可以装的总数量
	private double zCount;
	//可以拆的总数量
	private double cCount;

//	private boolean hasEpmDoc;

	private boolean isMore;

	public boolean isMore() {
		return isMore;
	}

	public void setMore(boolean more) {
		isMore = more;
	}
	
//	public boolean isHasEpmDoc() {
//		return hasEpmDoc;
//	}

//	public void setHasEpmDoc(boolean hasEpmDoc) {
//		this.hasEpmDoc = hasEpmDoc;
//	}

	public double getzCount() {
		return zCount;
	}

	public void setzCount(double zCount) {
		this.zCount = zCount;
	}

	public double getcCount() {
		return cCount;
	}

	public void setcCount(double cCount) {
		this.cCount = cCount;
	}

	public VaTreeNode(Object useObject) {
		super(useObject);
//		this.rootPath = (String) useObject;
		 if (useObject instanceof VaNode) {
		 this.part = ((VaNode) useObject).getPart();
		 }

		 this.instanceIdentifier = new VaInstanceIdentifier();
		 this.matrix = VaPartUsesOcc.STD_MATRIX4D;
		 this.relativeMatrix = VaPartUsesOcc.STD_MATRIX4D;
		 this.occId = String.valueOf(System.nanoTime());
		this.isSelected = false;
		 this.marked = false;
		 this.nodeType = PART;

	}

	public List<VaLightPart> getPathFromCI() {
		return pathFromCI;
	}

	public void setPathFromCI(List<VaLightPart> pathFromCI) {
		this.pathFromCI = pathFromCI;
	}

	public VaLightPart getPart() {
		if (getUserObject() instanceof VaNode) {
			VaLightPart lightPart = ((VaNode) getUserObject()).getPart();
			return lightPart == null ? VaLightPart.EMPTY_PART : lightPart;
		}
		return part == null ? VaLightPart.EMPTY_PART : part;
	}

	public void setPart(VaLightPart part) {
       this.part=part;

    }

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		setSelectedWithoutPropagate(isSelected);

		Enumeration allChilds = breadthFirstEnumeration();
		while (allChilds.hasMoreElements()) {
			VaTreeNode child = (VaTreeNode) allChilds.nextElement();
			child.setSelectedWithoutPropagate(isSelected);
		}

		if (isSelected) {
			VaTreeNode father = (VaTreeNode) getParent();
			while (father != null && !father.isSelected()) {
				father.setSelectedWithoutPropagate(isSelected);
				father = (VaTreeNode) father.getParent();
			}
		} else {
			// ��������ֵܽڵ�δѡ�У�����Ҫȡ��ѡ�и��ڵ��
			unselectFatherIfNoSelectedSiblings(this);
		}

	}

	private void unselectFatherIfNoSelectedSiblings(VaTreeNode node) {
		VaTreeNode siblNode = (VaTreeNode) node.getPreviousSibling();
		while (siblNode != null && !siblNode.isSelected()) {
			siblNode = (VaTreeNode) siblNode.getPreviousSibling();
		}
		if (siblNode == null) {// no selected sibl node found!
			siblNode = (VaTreeNode) node.getNextSibling();
			while (siblNode != null && !siblNode.isSelected()) {
				siblNode = (VaTreeNode) siblNode.getNextSibling();
			}
		}
		if (siblNode == null) {// no selected sibl node found!
			VaTreeNode father = (VaTreeNode) node.getParent();
			if (father != null) {
				father.setSelectedWithoutPropagate(false);
				unselectFatherIfNoSelectedSiblings(father);
			}
		}
	}

	private void setSelectedWithoutPropagate(boolean isSelected) {
		this.isSelected = isSelected;

		try {
			if (!isSelected) {
				VaEPViewStructureGenerator.desactivate(get_pviewShapeInstance());
			} else {
				VaEPViewStructureGenerator.activate(get_pviewShapeInstance());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	public VaInstanceIdentifier getInstanceIdentifier() {
		return instanceIdentifier;
	}

	public void setInstanceIdentifier(VaInstanceIdentifier instanceIdentifier) {
		this.instanceIdentifier = instanceIdentifier;
	}

	public Matrix4d getMatrix() {
		return matrix;
	}

	public void setMatrix(Matrix4d matrix) {
		this.matrix = matrix;
	}

	public Matrix4d getRelativeMatrix() {
		return relativeMatrix;
	}

	public void setRelativeMatrix(Matrix4d relativeMatrix) {
		this.relativeMatrix = relativeMatrix;
	}

	public String getOccId() {
		return occId;
	}

	public void setOccId(String occId) {
		this.occId = occId;
	}

	public ComponentInstance get_pviewComponentInstance() {
		return _pviewComponentInstance;
	}

	public void set_pviewComponentInstance(ComponentInstance componentInstance) {
		_pviewComponentInstance = componentInstance;
	}

	public ComponentNode get_pviewComponentNode() {
		return _pviewComponentNode;
	}

	public void set_pviewComponentNode(ComponentNode componentNode) {
		_pviewComponentNode = componentNode;
	}

	public ShapeInstance get_pviewShapeInstance() {
		return _pviewShapeInstance;
	}

	public void set_pviewShapeInstance(ShapeInstance shapeInstance) {
		_pviewShapeInstance = shapeInstance;
	}

	public Collection<String[]> get_pviewInstanceProperties() {
		return _pviewInstanceProperties;
	}

	public void set_pviewInstanceProperties(Collection<String[]> _pviewInstanceProperties) {
		this._pviewInstanceProperties = _pviewInstanceProperties;
	}

	/***
	 * obj->xml & xml->obj
	 *
	 * @param xml
	 * @return
	 */
	public VaXML write2XML() {
		VaXML xml = new VaXML("Node");
		Vector<VaXmlProperty> propertys = new Vector<VaXmlProperty>();
		propertys.add(new VaXmlProperty("occId", getOccId()));
		propertys.add(new VaXmlProperty("nodeType", getNodeType()));

		if (getPart() == null)
			part = VaLightPart.newLightPart("", "", "", 0l);
		long oid = getPart().getOid();
		String oidStr = String.valueOf(oid);
		propertys.add(new VaXmlProperty("oid", oidStr));
		propertys.add(new VaXmlProperty("name", getPart().getName()));
		propertys.add(new VaXmlProperty("number", getPart().getNumber()));
		propertys.add(new VaXmlProperty("version", getPart().getVersion()));
		long masterOid = getPart().getMasterOid();
		String masterOidStr = String.valueOf(masterOid);
		propertys.add(new VaXmlProperty("masterOid", masterOidStr));

//		if (getUserObject() instanceof CmMPartMaster) {
//			CmMPartMaster master = (CmMPartMaster) getUserObject();
//			propertys.add(new CmXmlProperty("quantity", String.valueOf(master.getQuantity())));
//		}
		// ��node������EBOM ci�е�·��
		xml.append(writePathFromCI());

		if (getInstanceIdentifier() != null)
			xml.append(getInstanceIdentifier().toXML());
		xml.setAttrs(propertys);
		if (matrix != null)
			xml.set("matrix", VaMathUtil.getLocationAsString(matrix));
		if (relativeMatrix != null)
			xml.set("relativeMatrix", VaMathUtil.getLocationAsString(relativeMatrix));

		writeChildNode2XML(xml);

		return xml;
	}

	protected VaXML writePathFromCI() {
		VaXML xml = new VaXML("PathFromCI");
		if (pathFromCI != null) {
			for (VaLightPart lightPart : pathFromCI) {
				if (lightPart != null) {
					VaXML partXml = new VaXML("PathPart");
					partXml.set("oid", lightPart.getOid());
					partXml.set("name", lightPart.getName());
					partXml.set("number", lightPart.getNumber());
					partXml.set("oversion", lightPart.getVersion());
					xml.append(partXml);
				}
			}
		}
		return xml;
	}

	public static List<VaLightPart> loadPathFromCI4XML(VaXML xml) {
		List<VaLightPart> pathPart = new Vector<VaLightPart>();
		VaXML pathXML = xml.loc(".PathFromCI");
		if (pathXML != null)
			loadPathPart4XML(pathXML.firstElem(), pathPart);
		return pathPart;
	}

	public static void loadPathPart4XML(VaXML xml, List<VaLightPart> pathPart) {
		if (xml != null && "PathPart".equals(xml.getName())) {
			VaLightPart lightPart = loadPart4XML(xml);
			pathPart.add(lightPart);
			loadPathPart4XML(xml.nextElem(), pathPart);
		}
	}

	private void writeChildNode2XML(VaXML nodeXml) {
		Enumeration<VaTreeNode> children = children();
		if (children.hasMoreElements()) {
			VaXML childrenXML = new VaXML("children");
			nodeXml.append(childrenXML);
			while (children.hasMoreElements()) {
				VaTreeNode child = children.nextElement();
				childrenXML.append(child.write2XML());
			}
		}
	}

	/***
	 * obj->xml xml->obj
	 *
	 * @param xml
	 * @return
	 */
	public static VaTreeNode load4XML(VaXML xml) {
		VaXML nodeXML = xml == null ? null : xml.loc(".Node");
		if (nodeXML == null)
			return null;

		return loadNode4XML(nodeXML);
	}

	private static VaTreeNode loadNode4XML(VaXML xml) {
		// ������CmMPartInstance��Ҳ������CmMPartMaster
		VaTreeNode node = null;

		VaLightPart lightPart = loadPart4XML(xml);
//		if (xml.attrvalnum("quantity") > 0) {
//			node = new CmTreeNode(new CmMPartMaster(lightPart, xml.attrvalnum("quantity")));
//		} else {
//			node = new CmTreeNode(new CmMPartInstance(lightPart));
//		}

		node.setPathFromCI(loadPathFromCI4XML(xml));
		node.instanceIdentifier.loadFromXML(xml.loc(".VaInstanceIdentifier"));

		String matrix = xml.attrval("matrix");
		if (matrix != null)
			node.matrix = VaMathUtil.getMatrix4dFromString(matrix);

		String relativeMatrix = xml.attrval("relativeMatrix");
		if (relativeMatrix != null)
			node.relativeMatrix = VaMathUtil.getMatrix4dFromString(relativeMatrix);

		String occId = xml.attrval("occId");
		node.setOccId(occId);

		String nodeType = xml.attrval("nodeType");
		node.setNodeType(nodeType);

		VaXML childrenXML = xml.loc(".children");
		if (childrenXML != null)
			loadChildNode4XML(childrenXML, node);

		return node;
	}

	private static void loadChildNode4XML(VaXML xml, VaTreeNode parentNode) {
		if (xml == null)
			return;

		VaXML nextXML = xml.firstElem();
		while (nextXML != null) {
			VaTreeNode node = loadNode4XML(nextXML);
			if (node != null)
				parentNode.add(node);
			nextXML = nextXML.nextElem();
		}
	}

	private static VaLightPart loadPart4XML(VaXML nodeXML) {
		if (nodeXML == null)
			return null;

		VaLightPart lightPart = null;
		try {
			String oid = nodeXML.attrval("oid");
			long oidl = Long.valueOf(oid);
			lightPart = null;
			if (oidl > 0) {
				WTPart part = (WTPart) VaSearchHelper.search(WTPart.class, oidl);
				lightPart = VaBizObjUtil.buildVaLightPartFromWTPart(part);
			}
			if (lightPart == null) {
				String name = nodeXML.attrval("name");
				String number = nodeXML.attrval("number");
				String version = nodeXML.attrval("version");
				String xhph = nodeXML.attrval("xhph");
				String gg = nodeXML.attrval("gg");

				lightPart = VaLightPart.newLightPart(number, name, version, 0l);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lightPart;
	}

	public void set_cadObject(Object object) {
		cadObject = object;
	}

	public Object get_cadObject() {
		return cadObject;
	}

	public boolean get_wsInfo() {
		return isInWorkspace;
	}

	public void set_wsInfo(boolean isInWS) {
		isInWorkspace = isInWS;
	}

	public void set_LockedForCAD(boolean locked) {
		isLockedForCad = locked;
	}

	public boolean get_LockedForCAD() {
		return isLockedForCad;
	}

	public String getFktGrp() {
		return fktGrp;
	}

	public void setFktGrp(String _fktGrp) {
		this.fktGrp = _fktGrp;
	}

	public String[] getInfoTxt() {
		return infoTxt;
	}

	public void setInfoTxt(String[] infoTxt) {
		this.infoTxt = infoTxt;
	}

	public Matrix4d getRelative_matrix() {
		return relative_matrix;
	}

	public void setRelative_matrix(Matrix4d a_Relative_matrix) {
		relative_matrix = a_Relative_matrix;
	}

	public boolean getLocChange() {
		return locationChange;
	}

	public void setLocChange(boolean locChange) {
		locationChange = locChange;
	}

	public Vector getBboxes() {
		return bboxes;

	}

	public void setBboxes(Vector a_Bboxes) {
		bboxes = a_Bboxes;

	}

	public void setBaseMatrix(Matrix4d a_Matrix) {
		basematrix = a_Matrix;
	}

	public Matrix4d getBaseMatrix() {
		return basematrix;
	}

	public String getDiode() {
		return diode;
	}

	public void setDiode(String a_Diode) {
		diode = a_Diode;
		try {
			if (this.get_pviewShapeInstance() != null) {
//				CmAOTechnicsMarkMPViewGenerator.applyColorOnPView(this, a_Diode);
			}
			if (this.get_cadObject() != null) {
//				CadAdapter.applyColor(this, a_Diode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int get_index() {
		return _index;
	}

	public void set_index(int _index) {
		this._index = _index;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getDw2() {
        return dw2;
    }

    public void setDw2(String dw2) {
        this.dw2 = dw2;
    }

    public String getDw() {
        return dw;
    }

    public void setDw(String dw) {
        this.dw = dw;
    }

    public String getXhph() {
        return xhph;
    }

    public void setXhph(String xhph) {
        this.xhph = xhph;
    }

    public String getGg() {
        return gg;
    }

    public void setGg(String gg) {
        this.gg = gg;
    }

    public String getSccj() {
        return sccj;
    }

    public void setSccj(String sccj) {
        this.sccj = sccj;
    }

    public String getZldj() {
        return zldj;
    }

    public void setZldj(String zldj) {
        this.zldj = zldj;
    }

    public String getFzxs() {
        return fzxs;
    }

    public void setFzxs(String fzxs) {
        this.fzxs = fzxs;
    }

    public String getDcstxyq() {
        return dcstxyq;
    }

    public void setDcstxyq(String dcstxyq) {
        this.dcstxyq = dcstxyq;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getBzh() {
        return bzh;
    }

    public void setBzh(String bzh) {
        this.bzh = bzh;
    }

    public String getJxxndjhyd() {
        return jxxndjhyd;
    }

    public void setJxxndjhyd(String jxxndjhyd) {
        this.jxxndjhyd = jxxndjhyd;
    }

    public String getBmcl() {
        return bmcl;
    }

    public void setBmcl(String bmcl) {
        this.bmcl = bmcl;
    }

    public String getRcl() {
        return rcl;
    }

    public void setRcl(String rcl) {
        this.rcl = rcl;
    }

    public String getCpxs() {
        return cpxs;
    }

    public void setCpxs(String cpxs) {
        this.cpxs = cpxs;
    }

    public String getCpdj() {
        return cpdj;
    }

    public void setCpdj(String cpdj) {
        this.cpdj = cpdj;
    }

    public String getBnxs() {
        return bnxs;
    }

    public void setBnxs(String bnxs) {
        this.bnxs = bnxs;
    }

    public String getSfjk() {
        return sfjk;
    }

    public void setSfjk(String sfjk) {
        this.sfjk = sfjk;
    }

    public String getJstj() {
    	return jstj;
    }

	public void setJstj(String jstj) {
		this.jstj = jstj;
	}

	public String getCl() {
		return cl;
	}

	public void setCl(String cl) {
		this.cl = cl;
	}

	public String getWh() { return wh;}

	public void setWh(String wh) {this.wh = wh;}

	public String getReplaceableParts() {
		return replaceableParts;
	}

	public void setReplaceableParts(String replaceableParts) {
		this.replaceableParts = replaceableParts;
	}

	@Override
	public int compareTo(VaTreeNode o) {
		VaTreeNode vatn = (VaTreeNode) o;
        return this.xhph.compareTo(vatn.getXhph());
	}

}
