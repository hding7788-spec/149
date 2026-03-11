package com.glaway.mpm.pbombuilder.tree.item;

import com.glaway.mpm.pbombuilder.action.CmActionProgressBar;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.data.PBOMReleasedValidatorBean;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.SetPBOMReleasedDialog;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 添加工艺中间件
 *
 * @author chenyunlong
 */
public class SetPBOMReleasedMenuItem extends CmMenuItem {
    private static final long serialVersionUID = 1L;
    private CmTree tree;
    private CmTreeNode currNode;
    private Window owner;

    public SetPBOMReleasedMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
        this.tree = tree;
        this.currNode = currNode;
        this.owner = owner;
        setText("PBOM发布");
        setIconStr("edit.gif");
        // setEnabled(canEdit?displayValidate(this.currNode):canEdit);
        setEnabled(displayValidate(this.currNode));
    }

    private boolean displayValidate(CmTreeNode node) {
        if (CmCommonStringUtil.isHasFilingOfObj(node)) {
            return false;
        } else if (node.getParent() == node.getRoot()) {
            return true;
        }

        return false;
    }

    @Override
    protected void actionPerformed(ActionEvent evt) {
        final CmActionProgressBar progressBar = new CmActionProgressBar(null, owner, "PBOM发布", "正在执行PBOM发布,请等待...", "PBOM发布中");
        Thread thread = new Thread() {
            public void run() {
                CmTreeNode root = tree.getRoot();
                List<PBOMReleasedValidatorBean> list = new ArrayList<PBOMReleasedValidatorBean>();
                List<PBOMReleasedValidatorBean> returnList = getAllDatas(root, list, progressBar, 0);
                /*StringBuffer sb = new StringBuffer();
                String msg = checkBatch(root, sb);
                if (!"".equals(msg)) {
                	if(msg.length()>50){
                		msg = msg.substring(0, 48);
                    	msg = msg +"...";
                	}
                    JOptionPane.showMessageDialog(tree.getRootPane(), msg + "未设置批次号，请设置批次号后重新发布！");
                    progressBar.finish();
                    progressBar.setVisible(false);
                } else {*/
                    //if(returnList != null && !returnList.isEmpty()) {//有完整性检查未通过的零部件
                    new SetPBOMReleasedDialog(currNode, tree, returnList);

                    //progressBar.setHeaderMessage("有完整性检查未通过的零部件！");
                    progressBar.finish();
                    progressBar.setVisible(false);
//                }
//				} else {//完整性检查通过，则设置PBOM为"已批准"状态
//					int option = JOptionPane.showConfirmDialog(owner, "PBOM发布校验通过，是否需要将PBOM设为已发布状态？", "提示", JOptionPane.OK_CANCEL_OPTION);
//					if(option == 0) {
//						long containerId = ((CmTreeNode)root.children().nextElement()).getPart().getContainerId();
//						setAllChildPartState(root,progressBar,containerId);
//
//						progressBar.setHeaderMessage("PBOM发布完成！");
//					}
//
//					progressBar.finish();
//					progressBar.setVisible(false);
//				}
            }
        };
        thread.start();
        progressBar.setVisible(true);
    }

    /**
     * 校验是否每个部件都设批次号
     *
     * @param root
     * @param msg
     */
    private String checkBatch(CmTreeNode root, StringBuffer sb) {
        String batch;
        Enumeration childs = root.children();
        while (childs.hasMoreElements()) {
            CmTreeNode child = (CmTreeNode) childs.nextElement();
            CmLightPart part = child.getPart();
            if ("自制件".equals(part.getMtype()) || "外配套件".equals(part.getMtype()) || "带料委外件".equals(part.getMtype()) || "不带料委外件".equals(part.getMtype())) {
                batch = part.getBatch();
                if (batch == null || "".equals(batch)) {
                    if ("".equals(sb.toString())) {
                        sb.append(part.getPartNumber());
                    } else {
                        sb.append("," + part.getPartNumber());
                    }
                }
            }
            checkBatch(child, sb);
        }
        return sb.toString();
    }

//    private void setAllChildPartState(CmTreeNode root, CmActionProgressBar progressBar, long containerId) {
//        Enumeration childs = root.children();
//        while (childs.hasMoreElements()) {
//            CmTreeNode child = (CmTreeNode) childs.nextElement();
//            CmLightPart part = child.getPart();
//            if (containerId != part.getContainerId()) {
//                progressBar.setHeaderMessage("借用件：" + part.getPartNumber() + " 未发布！");
//                System.out.println("借用件：" + part.getPartNumber() + " 未发布！");
//            } else {
//                long oid = part.getOid();
//                progressBar.setHeaderMessage("PBOM发布，零组件编号：" + part.getPartNumber());
//                String msg = PBOMEditorToWCIntf.setPartStateForPbom(oid, "APPROVED");
//                if (msg != null && "".equals(msg)) {
//                    part.setLifecycle("已批准");
//                    setAllChildPartState(child, progressBar, containerId);
//                } else {
//                    JOptionPane.showMessageDialog(tree.getRootPane(), "PBOM发布失败！" + msg);
//                    return;
//                }
//            }
//        }
//    }

    private List<PBOMReleasedValidatorBean> getAllDatas(CmTreeNode root, List<PBOMReleasedValidatorBean> list, CmActionProgressBar progressBar, long containerId) {
        Enumeration childs = root.children();
        while (childs.hasMoreElements()) {

            CmTreeNode child = (CmTreeNode) childs.nextElement();
            if (0 == containerId) {
                containerId = child.getPart().getContainerId();
            }
            CmLightPart part = child.getPart();
            System.out.println("-------mtype----------" + part.getMtype());
            progressBar.setHeaderMessage("完整性检查，零组件编号：" + part.getPartNumber());
            PBOMReleasedValidatorBean bean = new PBOMReleasedValidatorBean();
            bean.setNumber(part.getPartNumber());
            bean.setName(part.getPartName());
            bean.setMtype(part.getMtype());
            bean.setPhaseCode(part.getPhase_code());
            bean.setKeycomponent(part.getKeycomponent());

            String zzcj = part.getZzcj();
            String fzcj = part.getFzcj();
            String routing = "";
            if (zzcj != null && !"".equals(zzcj)) {
                routing = zzcj;
            }
            if (fzcj != null && !"".equals(fzcj)) {
                if (!"".equals(routing)) {
                    routing = routing + "-" + fzcj;
                } else {
                    routing = fzcj;
                }
            }
            bean.setGylx(routing);
            part.setRouting(routing);

            bean.setChbm(part.getChbm());

            Map<String, String> map = PBOMEditorToWCIntf.getReleasedInfo(part.getOid());
            if (map != null) {
                part.setZgyzt(map.get("state"));
                part.setZldezt(map.get("CLDEZT"));
                part.setZgyNumber(map.get("technicsNumber"));
                bean.setZgyzt(map.get("state"));
                bean.setZldezt(map.get("CLDEZT"));
                bean.setZgyNumber(map.get("technicsNumber"));
            }

            list.add(bean);
            getAllDatas(child, list, progressBar, containerId);

            boolean b1 = true;
            //1、所有Part属性中“零组件生产类型”不为空
            if (part.getMtype() == null || "".equals(part.getMtype())) {
                b1 = false;
            }

            boolean b2 = true;
            //2、“标准件、元器件”的物资编码属性不为空
            if ("标准件".equals(part.getMtype()) || "元器件".equals(part.getMtype())) {
                if (part.getWzk().getInvcode() == null || "".equals(part.getWzk().getInvcode())) {
                    //b2 = false;
                }
            }

            boolean b3 = true;
            //3、“自制件、外配套件、带料委外件、不带料委外件”的主制车间不能为空
            if ("自制件".equals(part.getMtype()) || "外配套件".equals(part.getMtype())
                    || "带料委外件".equals(part.getMtype()) || "不带料委外件".equals(part.getMtype())) {
                if (part.getZzcj() == null || "".equals(part.getZzcj())) {
                    b3 = false;
                }
            }

            boolean b4 = true;
            //4、“自制件、外配套件、带料委外件、不带料委外件”节点必须有主制工艺文件，且主制工艺文件的材料定额状态为“已批准”
            //如果主工艺文件已经提交签审批准了，则就不用检查材料定额状态了。
            //2016.07.13外配套件可以不编工艺
            if ("自制件".equals(part.getMtype()) //|| "外配套件".equals(part.getMtype())
                    || "带料委外件".equals(part.getMtype()) /*|| "不带料委外件".equals(part.getMtype())*/) {
                if (map != null) {
                    if (!"已批准".equals(map.get("state"))) {
                        if (!"已批准".equals(map.get("CLDEZT"))) {
                            b4 = false;
                        }
                    }
                } else {
                    b4 = false;
                }
            }
            //2016.07.13外配套件可以不编工艺，如果编了工艺，必须是已批准或者材料定额已批准
            if ("外配套件".equals(part.getMtype())) {
                if (map != null) {
                    if (!"已批准".equals(map.get("state"))) {
                        if (!"已批准".equals(map.get("CLDEZT"))) {
                            b4 = false;
                        }
                    }
                }
            }

            /*boolean b5 = true;
            if (containerId == part.getContainerId()) {
                if ("自制件".equals(part.getMtype()) || "外配套件".equals(part.getMtype())
                        || "带料委外件".equals(part.getMtype()) || "不带料委外件".equals(part.getMtype())) {
                    String bath = part.getBatch();
                    if (bath == null || "".equals(bath)) {
                        b5 = false;
                    }
                }
            }*/
            System.out.println("-----b1=" + b1 + " b2=" + b2 + " b3=" + b3 + " b4=" + b4);
            if (b1 && b2 && b3 && b4) {
                part.setIsOk("是");
                bean.setIsOk("是");
            } else {
                part.setIsOk("否");
                bean.setIsOk("否");
            }

        }
        return list;
    }

}
