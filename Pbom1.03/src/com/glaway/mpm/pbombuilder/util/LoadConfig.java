package com.glaway.mpm.pbombuilder.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.glaway.mpm.pbom.table.KVItem;
import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;



public class LoadConfig {

	private static final Logger log = Logger.getLogger(LoadConfig.class);
	private  Properties properties = new  Properties();
	private static LoadConfig instance = null;
	private  String[] depts = null;
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
        	in = LoadConfig.class.getClassLoader().getResourceAsStream("erp_config.properties");
//        	File file = new File(PropertiesUtil.getLocalCodeBase(),"erp_config.properties");
//        	in = new FileInputStream(file);
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	public String getDbTypes(){
		String  dbtype = properties.getProperty("db_type");
		if(dbtype!=null){
			return dbtype;
		}
		return "02";
	}
//
//	public String [] getDbTypeDesc(){
//		String  dbtypeDesc = properties.getProperty("db_type_desc");
//		if(dbtypeDesc!=null){
//			return dbtypeDesc.split(",");
//		}
//		return null;
//	}

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

	public String[] getWzkTypes(){
		String  type = properties.getProperty("wzk_type");
		if(type!=null){
			return type.split(",");
		}
		return null;
	}

	public Vector getWzTypeVector(){
		Vector vector = new Vector();
		String  type = properties.getProperty("pbom_wzk_type");
		String  typeDesc = properties.getProperty("pbom_wzk_type_desc");
		if(type!=null&&typeDesc!=null){
			String []types =  type.split(",");
			String []descs =  typeDesc.split(",");
			for(int i=0;i<types.length;i++){
				vector.addElement(new KVItem(types[i], descs[i]));
			}
		}
		return vector;
	}

	public Vector<KVItem> getWzTypeVector2(String flag){
		Vector<KVItem> vector = new Vector<KVItem>();
		String  type = properties.getProperty("wzk_type_"+flag);
		String  typeDesc = properties.getProperty("wzk_type_desc_"+flag);
		if(type!=null&&typeDesc!=null){
			String []types =  type.split(",");
			String []descs =  typeDesc.split(",");
			for(int i=0;i<types.length;i++){
				vector.addElement(new KVItem(types[i], descs[i]));
			}
		}
		return vector;
	}

	public String[] getCyfhs(){
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
//		List<KVItem> list = new ArrayList<KVItem>();
//		String key = properties.getProperty("812_plant");
//		String desc = properties.getProperty("812_plant_desc");
//		String [] keys = key.split(",");
//		String [] descs = desc.split(",");
//		KVItem item;
//		for(int i=0;i<keys.length;i++){
//			item = new KVItem(keys[i], descs[i]);
//			list.add(item);
//		}
//		return list;
		if(this.depts==null){
			this.depts = PBOMEditorToWCIntf.queryDept(CmConnectFrame.partOid);
		}
		String [] depts = this.depts;
		List<KVItem> list = new ArrayList<KVItem>(depts.length);
		KVItem item;
		for(int i=0;i<depts.length;i++){
			item = new KVItem(depts[i], depts[i]);
			list.add(item);
		}
		return list;
	}

	public String[] getMainPlant(){
		if(this.depts==null){
			this.depts = PBOMEditorToWCIntf.queryDept(CmConnectFrame.partOid);
		}
		String [] depts = this.depts;
		String plant[] = new String[depts.length+1];
		plant[0] = "";
		for(int i=0;i<depts.length;i++){
			plant[i+1] = depts[i];
		}
		return plant;
//		String plant = properties.getProperty("812_plant_main");
//		return plant.split(",");
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
		return new String[]{"公开","内部","秘密","机密"};
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


	public Vector getWztypeVectorByOpType(int opType){
		if(ErpUtil.ERP_OP_TYPE_CLBM==opType){
			return getClWztypeVector();
		}else if(ErpUtil.ERP_OP_TYPE_WLBM==opType){
			return getWlWztypeVector();
		} else if(ErpUtil.ERP_OP_TYPE_CJLBJ==opType){
			return getCjlbjWztypeVector();
		} else if(ErpUtil.ERP_OP_TYPE_GENBOM==opType){
			return this.getWzTypeVector();
		}
		return null;
	}

	public Vector getWztypeVectorByOpType2(int opType,String flag){
		if(ErpUtil.ERP_OP_TYPE_CLBM==opType){
			return getClWztypeVector();
		}else if(ErpUtil.ERP_OP_TYPE_WLBM==opType){
			return getWlWztypeVector2(flag);
		} else if(ErpUtil.ERP_OP_TYPE_CJLBJ==opType){
			return getCjlbjWztypeVector();
		} else if(ErpUtil.ERP_OP_TYPE_GENBOM==opType){
			return this.getWzTypeVector();
		}
		return null;
	}

	public Vector<KVItem> getWztypeVectorByOpType3(String flag){
		return getWlWztypeVector2(flag);
	}

	public String getWztypeByOpType(String opt,String sel) {
		String type = this.properties.getProperty("wzk_type_"+opt);
		String type_desc = this.properties.getProperty("wzk_type_desc_"+opt);
		String[] types = type.split(",");
		String[] type_descs = type_desc.split(",");
		for (int i=0;i<type_descs.length;i++) {
			String str = type_descs[i];
			if(str.equals(sel)) {
				return types[i];
			}
		}
		return "";
	}

	public Vector getWlWztypeVector(){
		Vector v = new Vector();
		Vector vector = this.getWzTypeVector();

		return vector;
	}

	public Vector<KVItem> getWlWztypeVector2(String flag){
		Vector<KVItem> vector = this.getWzTypeVector2(flag);

		return vector;
	}

	public Vector getClWztypeVector(){
		Vector v = new Vector();
		Vector vector = this.getWzTypeVector();
		String type = this.properties.getProperty("cl_wzk_type");
		String[] types =type.split(",");
		KVItem item ;
		for(int i=0;i<vector.size();i++){
			 item = (KVItem)vector.get(i);
			 for(int j=0;j<types.length;j++){
				 if(types[j].equals(item.getKey())){
					 v.add(item);
					 break;
				 }
			 }
		}
		return v;
	}

	public Vector getCjlbjWztypeVector(){
		Vector v = new Vector();
		Vector vector = this.getWzTypeVector();
		String type = this.properties.getProperty("cjlbj_wzk_type");
		String[] types =type.split(",");
		KVItem item ;
		for(int i=0;i<vector.size();i++){
			 item = (KVItem)vector.get(i);
			 for(int j=0;j<types.length;j++){
				 if(types[j].equals(item.getKey())){
					 v.add(item);
					 break;
				 }
			 }
		}
		return v;
	}

	public String getQueryWzkDdtype(){
		return this.properties.getProperty("query_wzk_db_type");
	}
}
