package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;

import wt.part.WTPart;

import com.glaway.mpm.pbombuilder.action.CmActionProgressBar;
import com.glaway.mpm.pbombuilder.bom.CmConnectDialog;
import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.dialog.TxcjlbjDialog;
import com.glaway.mpm.pbombuilder.util.CmSearchHelper;
import com.glaway.mpm.pbombuilder.util.CmXmlUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

/**
 * <br>
 * Created on 2012-11-21
 *
 * @author chenyunlong
 */
public class CmMBomCompareEbomMenuItem extends CmMenuItem {

	private static final long serialVersionUID = -8396429397442896329L;
	private CmTreeNode node;
	private Window owner;
	private CmTree tree;

	public CmMBomCompareEbomMenuItem(CmTree tree, CmTreeNode currNode, Window owner) {
		this.node = currNode;
		this.owner = owner;
		this.tree = tree;
		setText("比较EBOM");
		//setIconStr("view2d.png");
		setEnabled(true);
	}


	/**
	 * @param evt
	 */
	@Override
	protected void actionPerformed(ActionEvent evt) {
		final CmActionProgressBar animFrame = new CmActionProgressBar( CmMBomMainFrame.getMainFrame(),
				"比较EBOM", "正在比较EBOM数据", "正在比较EBOM数据，请等待...");
		final CmTree etree = this.tree;
		Thread thread = new Thread() {
				public void run() {
					WTPart ebomPart  = null;
					if(null != etree){
						try {
							ebomPart  = (WTPart) CmSearchHelper.search(WTPart.class,Long.valueOf(CmConnectFrame.partOid));
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						try {
							ebomPart  = (WTPart) CmSearchHelper.search(WTPart.class,Long.valueOf(CmConnectDialog.partOid));
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					CmXmlUtil util = new CmXmlUtil();
					//System.out.println("design oid = "+ String.valueOf(ebomPart.getPersistInfo().getObjectIdentifier().getId()));
					byte[] ebombytes = PBOMEditorToWCIntf.getEBOMXml(String.valueOf(ebomPart.getPersistInfo().getObjectIdentifier().getId()));
				//	CmConnectFrame.startAnimFrame.setHeaderMessage("compareEbomWithXML begin");
					if(null != ebombytes && ebombytes.length > 0){
						util.compareEbomWithXML(ebombytes,etree);
					}
					animFrame.setHeaderMessage("数据加载完成！");
					animFrame.finish();
					animFrame.setVisible(false);
				}
		};
		thread.start();
		animFrame.setVisible(true);

	}

}
