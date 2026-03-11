/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.glaway.mpm.qmIntf.technics.entity;

import java.util.List;

import com.glaway.mpm.util.CommonUtil;


/**
 *
 * @author hywang
 */
public class Step {

	//xml中属性
    private String oid;
    private String stepName = "";
    private String stepNumber = "";
    private String workType = "";
    private String workShop = "";
    private String stepHour = "";
    private String isKey = "";
    private String creoView = "";
    private String cortonaID = "";
    
    //自定义增加属性
    private String type="";
    
    private String PrepareWorkHours;
    private String TaktTime;
    private String NumberOfGroup;
    
    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private List subSteps;
    private List parts;
    private List equips;
    private List tools;
    private List materials;
    private List attachs;
    private List images;
    private String procedureContent;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getCortonaID() {
        return cortonaID;
    }

    public void setCortonaID(String cortonaID) {
        this.cortonaID = cortonaID;
    }

    public String getCreoView() {
        return creoView;
    }

    public void setCreoView(String creoView) {
        this.creoView = creoView;
    }

    public String getIsKey() {
        return isKey;
    }

    public void setIsKey(String isKey) {
        this.isKey = isKey;
    }

    public String getStepHour() {
        return stepHour;
    }

    public void setStepHour(String stepHour) {
        this.stepHour = stepHour;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(String stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getWorkShop() {
        return workShop;
    }

    public void setWorkShop(String workShop) {
        this.workShop = workShop;
    }

    public List getEquips() {
        return equips;
    }

    public void setEquips(List equips) {
        this.equips = equips;
    }

    public List getImages() {
        return images;
    }

    public void setImages(List images) {
        this.images = images;
    }

    public List getMaterials() {
        return materials;
    }

    public void setMaterials(List materials) {
        this.materials = materials;
    }

    public List getParts() {
        return parts;
    }

    public void setParts(List parts) {
        this.parts = parts;
    }

    public List getSubSteps() {
        return subSteps;
    }

    public void setSubSteps(List subSteps) {
        this.subSteps = subSteps;
    }

    public List getTools() {
        return tools;
    }

    public void setTools(List tools) {
        this.tools = tools;
    }

    public String getProcedureContent() {
        return procedureContent;
    }

    public void setProcedureContent(String procedureContent) {
        this.procedureContent = procedureContent;
    }

    public String toFormatString() {
        System.out.println("\t工艺步骤：" + this.stepName);
        if (this.subSteps != null && this.subSteps.size() > 0) {
            for (int i = 0; i < this.subSteps.size(); i++) {
                Step cStep = (Step) this.subSteps.get(i);
                cStep.toFormatString();
            }
        }
        return "";
    }

    public String toJSONString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        sb.append(" oid:'" + oid + "',");
        sb.append(" stepName:'" +stepNumber +"_"+ stepName + "',");
        sb.append(" stepNumber:'" + stepNumber + "',");
        sb.append(" workShop:'" + workShop + "',");
        sb.append(" cortonaID:'" + cortonaID + "',");
        sb.append(" procedureContent:'"+CommonUtil.ConvertToJsonFormat(procedureContent)+"',");
        sb.append(" workType:'" + workType + "',");
        sb.append(" isKey:'" + isKey + "',");
        sb.append(" type: '"+ type +"',");
        sb.append(" PrepareWorkHours: '"+ PrepareWorkHours +"',");
        sb.append(" TaktTime: '"+ TaktTime +"',");
        sb.append(" NumberOfGroup: '"+ NumberOfGroup +"',");

//        if (this.parts != null && this.parts.size() > 0) {
//            for (Object object : parts) {
//                if (object instanceof Step) {
//                    sb.append(((Step) object).toFormatString());
//                }
//            }
//        }
        
        sb.append(" parts:[");
        if (this.parts != null && this.parts.size() > 0) {
            for (Object object : parts) {
                if (object instanceof Part) {
                	Part e = (Part)object;
                    sb.append("{partNumber:'" + e.getPartNumber() + "',partName:'" + e.getPartName() + "',material:'" + e.getMaterial() + "',dutu:'" + e.getDutu() + "',remark:'" + e.getRemark() + "',useCount:'" + e.getUseCount() + "'},");
                }
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        sb.append("],");

        sb.append(" equips:[");
        if (this.equips != null && this.equips.size() > 0) {
            for (Object object : equips) {
                if (object instanceof Equipment) {
                	Equipment e = (Equipment)object;
                    sb.append("{eqNum:'" + e.getEqNum() + "',eqName:'" + e.getEqName() + "',eqModel:'" + e.getEqModel() + "',useCount:'" + e.getUseCount() + "'},");
                }
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        sb.append("],");
        sb.append(" tools:[");
        if (this.tools != null && this.tools.size() > 0) {
            for (Object object : tools) {
                if (object instanceof Tools) {
                	Tools t = (Tools) object;
                	sb.append("{toolNum:'" + t.getToolNum() + "',toolName:'" + t.getToolName() + "',toolStdNum:'" + t.getToolStdNum() + "',toolSpec:'" + t.getToolSpec() + "',useCount:'" + t.getUseCount() + "'},");
                }
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        sb.append("],");
        sb.append(" materials:[");
        if (this.materials != null && this.materials.size() > 0) {
            for (Object object : materials) {
                if (object instanceof Material) {
                	Material m = (Material)object;
                	 sb.append("{materialNumber:'" + m.getMaterialNumber() + "',materialName:'" + m.getMaterialName() + "',materialCrision:'" + m.getMaterialCrision() + "',materialCode:'" + m.getMaterialCode() + "',materialState:'" + m.getMaterialState() + "',useCount:'" + m.getUseCount() + "'},");
                }
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        sb.append("],");
        
        if (this.subSteps != null ) {
            
            sb.append(" children:[");
            for (Object object : subSteps) {
                if (object instanceof Step) {
                    sb.append("{ _reference:'" +((Step) object).oid+"'},");
                }
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("]");
        }
        
        if(sb.lastIndexOf(",")  == sb.length()-1)
        {
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        
        sb.append("}");
        
        
        
        return sb.toString();
    }

	public String getPrepareWorkHours() {
		return PrepareWorkHours;
	}

	public void setPrepareWorkHours(String prepareWorkHours) {
		PrepareWorkHours = prepareWorkHours;
	}

	public String getTaktTime() {
		return TaktTime;
	}

	public void setTaktTime(String taktTime) {
		TaktTime = taktTime;
	}

	public String getNumberOfGroup() {
		return NumberOfGroup;
	}

	public void setNumberOfGroup(String numberOfGroup) {
		NumberOfGroup = numberOfGroup;
	}

	public List getAttachs() {
		return attachs;
	}

	public void setAttachs(List attachs) {
		this.attachs = attachs;
	}
    
//    private
}
