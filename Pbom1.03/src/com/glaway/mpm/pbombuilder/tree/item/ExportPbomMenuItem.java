package com.glaway.mpm.pbombuilder.tree.item;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FileUtils;

import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.ImportPbomUtil;
import com.glaway.mpm.pbombuilder.wcInterface.ErpToWCIntf;

public class ExportPbomMenuItem extends CmMenuItem{
	private CmTree tree;
	private CmTreeNode node;
	private Window owner;
	private String title = "导出PBOM数据";
	private static final String filePath = File.separator+"pbom";
	public ExportPbomMenuItem(CmTree tree, CmTreeNode node, Window owner) {
		this.setText(title);
		this.setIconStr("view2d.png");
		this.node = node;
		this.tree = tree;
		this.owner = owner;

		setEnabled(displayValidate(this.node));
	}

	private boolean displayValidate(CmTreeNode node) {
//		if(CmCommonStringUtil.isPackageOfParent(node)||CmCommonStringUtil.isPackage(node)){
//			//父节点是打包结构时
//			return false;
//		}
		return true;
	}


	@Override
	protected void actionPerformed(ActionEvent evt) {

		CmCommonPackageAction common = new CmCommonPackageAction();
		common.packageAllNode(tree);
		ImportPbomUtil util = new ImportPbomUtil(tree);
		List<Wzk> dataList =  util.genImportWzkListNew();
		File f = null;
		JFileChooser  jfc = new JFileChooser ();
		jfc.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "*.xls文件";
			}

			@Override
			public boolean accept(File f) {
				if( f.isDirectory() || f.getName().endsWith( ".xls" ) ) {
					return true;
				} else {
					return false;
				}
			}
		});

        int state=jfc.showDialog(null,"保存PBOM");
	        if(JFileChooser.APPROVE_OPTION!=state){
	            return;
	        }
	        else{
	            f = jfc.getSelectedFile();//f为选择到的目录
	        }
	    String fileName = jfc.getName(f);
	    if(!fileName.endsWith(".xls"))	{
	    	fileName = fileName+ ".xls";
	    }
	    String fpath = jfc.getCurrentDirectory().getAbsolutePath() +File.separator+ fileName;
	    File file =  new File(fpath);
	    if(file.exists()){
	    	int flag = JOptionPane.showConfirmDialog(owner, "文件已经存在,是否覆盖", "确定",JOptionPane.YES_NO_OPTION);
	    	if(flag == 1){
	    		fpath = jfc.getCurrentDirectory().getAbsolutePath() +File.separator+ fileName.substring(0,fileName.length()-4) +"副本"+".xls";
	    		file =  new File(fpath);
	    	}
	    }



		byte[] b = ErpToWCIntf.savePbomToExcel(fileName,dataList);
		try {
				FileUtils.writeByteArrayToFile(file, b);
			} catch (Exception e) {

				JOptionPane.showMessageDialog(owner, "保存PBOM 结构失败");
			}
	}

}

