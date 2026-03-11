package com.glaway.mpm.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import wt.doc.WTDocument;

import com.glaway.mpm.swing.KVItem;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import com.glaway.mpm.wcIntf.TemplateIntf;

public class LoadConfig {

	private static final VaLogger log = VaLogger.getLogger(LoadConfig.class);
	private  Properties properties = new  Properties();
	private static LoadConfig instance = null;
	public synchronized static LoadConfig getInstance(){
		if(instance == null){
			instance = new LoadConfig();
			instance.loadConfig();
		}
		return instance;
	}

	public static void main(String[] args){
		String []	method = LoadConfig.getInstance().getTechnicsMethod();
		System.out.println(method.length);
	}

	private LoadConfig(){}
	private  void loadConfig(){
		InputStream in = null;
        try {
        	in = LoadConfig.class.getClassLoader().getResourceAsStream("qm_editor_config.properties");;
			properties.load(in);
			log.info("导入配置文件成功");
		} catch (IOException e) {
			log.error("导入配置文件：qm_editor_config.properties异常",e);
			properties = new Properties();
			e.printStackTrace();
		}finally{
			try {
				if(in!=null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取工艺类型
	 * @return String[][]  0：类型模板     1：描述     2:ProcessPlan 3:pmain 4:pnav 5:doc
	 */
	public String[][] getTechnicsType(){
		String template = this.properties.getProperty("technics_type_template");
		String desc = this.properties.getProperty("technics_type_desc");
		String plant = this.properties.getProperty("technics_type_process_plan");
		String pmain = this.properties.getProperty("technics_type_preview_main_template");
		String pnav = this.properties.getProperty("technics_type_preview_nav_template");
		String doc = this.properties.getProperty("technics_type_doc_type");
		String[] templates = template.split(",");
		String[] descs = desc.split(",");
		String[][] technicsType = new String[6][descs.length];
		technicsType[0] = templates;
		technicsType[1] = descs;
		technicsType[2] = plant.split(",");
		technicsType[3] = pmain.split(",");
		technicsType[4] = pnav.split(",");
		technicsType[5] = doc.split(",");
		return technicsType;

	}

	public String getZylb(String technicsType){
		String specializedType = "";
		String desc = this.properties.getProperty("technics_type_desc");
		String zylb = this.properties.getProperty("technics_zylb");
		String[] descs = desc.split(",");
		String[] zylbs = zylb.split(",");
		for (int i = 0; i < descs.length; i++) {
			if(descs[i].equals(technicsType)){
				specializedType =  zylbs[i];
			}
		}
		return specializedType;
	}

	public Map	getTechnicsTypeShowComponent(){
		Map map = new HashMap();
		String desc = this.properties.getProperty("technics_type_desc");
		String com = this.properties.getProperty("technics_type_show_component");
	    String[] descs = 	desc.split(",");
	    String[] coms = com.split("\\|");
	    for(int i=0;i<descs.length;i++){
	    	map.put(descs[i], coms[i]);
	    }
		return map;
	}

	public String[] getPartOFView(){
		return new String[]{"Design","Manufacturing"};
	}

	public String [] getTechnicsMethod(){
		String desc = this.properties.getProperty("technics_method_desc");
		return desc.split("\\|");
	}


	public String [] getMadeDept(){

		String[] depts = null;
		try {
			depts = TechnicsIntf.queryDept(NewTechnicsPart.getPbomOid());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return depts;
	}


	public String[] getPartOFSecret(){
		return new String[]{"无","内部","秘密","机密"};
	}

	public String[] getPartOFPhase(){
		return new String[]{"M","C","Z"};
	}
	public String getPbomView(){
		return properties.getProperty("pbom_view");
	}

	public String getFrock(){
		return properties.getProperty("GZFrock");
	}

	public String getCommonFrog(){
		return properties.getProperty("GZCommonFrog");
	}
	public String getSpecFrog(){
		return properties.getProperty("GZSpecFrog");
	}
	public String getTool(){
		return properties.getProperty("GZTool");
	}
	public String getMeasure(){
		return properties.getProperty("GZMeasure");
	}

	public Vector<KVItem> getDwgTemplateType(){
//		String desc = this.properties.getProperty("dwg_template_type");
//		String [] descs = desc.split(",");
//		Vector<KVItem> items = new Vector<KVItem>();
//		for(int i=0;i<descs.length;i++){
//			String kv = descs[i];
//			String [] kvs = kv.split("\\|");
//			KVItem item = new KVItem(kvs[0], kvs[1]);
//			items.add(item);
//		}
//		return items;
		Vector<KVItem> items = new Vector<KVItem>();
		List<WTDocument> docList  = TemplateIntf.getDwg2pdfTemplate();
		for(WTDocument doc:docList){
			KVItem item = new KVItem(doc.getNumber(), doc.getName());
			items.add(item);
		}
		return items;
	}
}
