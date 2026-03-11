package com.glaway.mpm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.glaway.mpm.pbom.table.KVItem;

public class LoadConfig {

	private static final Logger log = Logger.getLogger(LoadConfig.class);
	private  Properties properties = new  Properties();
	private static LoadConfig instance = null;
	public synchronized static LoadConfig getInstance(){
		if(instance == null){
			instance = new LoadConfig();
			instance.loadConfig();
		}
		return instance;
	}

	private LoadConfig(){}
	private  void loadConfig(){
		InputStream in = null;
        try {
//        	in = LoadConfig.class.getClassLoader().getResourceAsStream("erp_config.properties");;
        	File file = new File(PropertiesUtil.getLocalCodeBase(),"erp_config.properties");
        	in = new FileInputStream(file);
			properties.load(in);
			log.info("导入配置文件成功");
		} catch (IOException e) {
			log.error("导入配置文件：erp_config.properties异常",e);
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
	 * erp 物资库
	 * @return
	 */
	public String getErpWzkDBType(){
		return this.properties.getProperty("db_type_erp");
	}

	/**
	 * yxk 物资库
	 * @return
	 */
	public String getYxkWzkDBType(){
		return this.properties.getProperty("db_type_yxk");

	}

	public String[] getDBBeanName(String type){
		String temp =  this.properties.getProperty("db_bean_"+type);
		return temp.split(",");
	}

	/**
	 * 查询零部件类型
	 * @return
	 */
	public String[] getPartType(){
		String type = properties.getProperty("part_type");
		return type.split("\\|");
	}


	public String getCacheType(){
		return properties.getProperty("cache_type");
	}

	public String [] getDbTypes(){
		String  dbtype = properties.getProperty("db_type");
		if(dbtype!=null){
			return dbtype.split(",");
		}
		return null;
	}

	public Vector getDbTypeVector(){
		Vector vector = new Vector();
		String  dbtype = properties.getProperty("db_type");
		String  dbtypeDesc = properties.getProperty("db_type_desc");

		if(dbtype!=null&&dbtypeDesc!=null){
			String []types =  dbtype.split(",");
			String []descs =  dbtypeDesc.split(",");
			for(int i=0;i<types.length;i++){
				vector.addElement(new KVItem(types[i], descs[i]));
			}

		}
		return vector;
	}

	public String [] getWzkTypes(){
		String  type = properties.getProperty("wzk_type");
		if(type!=null){
			return type.split(",");
		}
		return null;
	}

	public Vector getWzTypeVector(){
		Vector vector = new Vector();
		String  type = properties.getProperty("wzk_type");
		String  typeDesc = properties.getProperty("wzk_type_desc");

		if(type!=null&&typeDesc!=null){
			String []types =  type.split(",");
			String []descs =  typeDesc.split(",");
			for(int i=0;i<types.length;i++){
				vector.addElement(new KVItem(types[i], descs[i]));
			}

		}
		return vector;
	}

	public String[]	 getCyfhs(){
		String  cyfh = properties.getProperty("erp_cyfh");
		if(cyfh!=null){
			return cyfh.split(",");
		}
		return null;
	}


	public String getPbomView(){
		return properties.getProperty("pbom_view");
	}

	public String getPbomFloder(){
		return properties.getProperty("gen_pbom_floder");
	}
	public String getPbomDocumentType(){
		return properties.getProperty("gen_pbom_doc_type");
	}

	public List<KVItem> getPlans(){
		List<KVItem> list = new ArrayList<KVItem>();
		String key = properties.getProperty("812_plant");
		String desc = properties.getProperty("812_plant_desc");
		String [] keys = key.split(",");
		String [] descs = desc.split(",");
		KVItem item;
		for(int i=0;i<keys.length;i++){
			item = new KVItem(keys[i], descs[i]);
			list.add(item);
		}
		return list;
	}

	public String[] getMainPlant(){
		String plant = properties.getProperty("812_plant_main");
		return plant.split(",");
	}

	/**
	 * 装配模式
	 * @return
	 */
	public Vector getPartOFPartType (){
		Vector vector = new Vector();
		KVItem item = new KVItem("separable","可分");
		vector.add(item);
		item = new KVItem("inseparable","不可分");
		vector.add(item);
		item = new KVItem("inseparable","组件");
		vector.add(item);
		return vector;
//		return  new String[]{"separable","inseparable","inseparable"};
	}
	/**
	 * 关重件标识
	 * @return
	 */
	public String[] getPartOFKeyComponent (){
		return new String[]{"N","G","Z"};
	}
	/**
	 * 视图
	 * @return
	 */
	public String[] getPartOFView(){
		return new String[]{"Design","Manufacturing"};
	}
	/**
	 *
	 * @return
	 */
	public String[] getPartOFSecret(){
		return new String[]{"无","内部","秘密","机密"};
	}

	public String[] getPartOFPhase(){
		return new String[]{"M","C","Z","S","D","Y"};
	}

	public Vector getPartOFZzdm(){
		Vector vector = new Vector();
		KVItem item = new KVItem("S","批号");
		vector.add(item);
		item = new KVItem("L","批号/序列号");
		vector.add(item);
		item = new KVItem("X","序列号");
		vector.add(item);
		item = new KVItem("0","未追踪");
		vector.add(item);
		return vector;
		//return new String[]{"批号","批号/序列号","序列号","未追踪"};
	}


	public Vector getPartOFUnit(){
		Vector vector = new Vector();
		KVItem item = new KVItem("ea","每个");
		vector.add(item);
		item = new KVItem("as_needed","根据需要");
		vector.add(item);
		item = new KVItem("kg","千克");
		vector.add(item);
		item = new KVItem("m","米");
		vector.add(item);
		item = new KVItem("l","千升");
		vector.add(item);
		item = new KVItem("sq_m","方米");
		vector.add(item);
		item = new KVItem("cu_m","立方米");
		vector.add(item);
		return vector;

//		return new String[]{"ea","kg"};
	}




	/**
	 * 获取工艺类型
	 * @return String[][]  0：类型模板     1：描述     2:ProcessPlan 3:pmain 4:pnav 5:doc
	 */
	public String[][]	getTechnicsType(){
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



	public String [] getTechnicsMethod(){
		String desc = this.properties.getProperty("technics_method_desc");
		return desc.split("\\|");
	}

	public String  getTechnicsDocPrefixPath(){
		return this.properties.getProperty("technics_doc_path_prefix");
	}

	public String getLocalDomainName()	{
		return  this.properties.getProperty("local_domain_name");
	}


	public String getTechnicsPathPrefix(){
		return  this.properties.getProperty("technics_path_prefix");

	}

	public String getDwgFolder(){
		return  this.properties.getProperty("client_dwg_file_path");
	}

	public String getReportTechnicsType(){
		return  this.properties.getProperty("reportTechnics_doc_type");
	}

	public String getReportTechnicsFolderPath(){
		return  this.properties.getProperty("reportTechnics_doc_folderPath");
	}
}
