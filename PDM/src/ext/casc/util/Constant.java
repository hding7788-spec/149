package ext.casc.util;

import java.util.HashMap;
import java.util.Map;

public class Constant {
 public static Map<String,String> map=   new HashMap<String,String>();
 public static String zhinengxuanzeyigeString="--请选择一个类型--";
 static{
     map.put("工艺技术协议", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.TECHNOLOGY_AGREEMENT");
     map.put("工艺技术通知单", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_NOTICE");
     map.put("其他类文档", "wt.doc.WTDocument|casc.sast.149.QITALEIWENDANG");
     map.put("软件文档", "wt.doc.WTDocument|casc.sast.149.DESIGN_DOC|casc.sast.149.SOFTWARE_DOC");
     map.put("文档", "wt.doc.WTDocument");
     map.put("文字或表格类设计文件", "wt.doc.WTDocument|casc.sast.149.DESIGN_DOC|casc.sast.149.WRITINGORFORM_DOC");
     map.put("研试文件", "wt.doc.WTDocument|casc.sast.149.RESEARCH_DOC");
     map.put("质量报告", "wt.doc.WTDocument|casc.sast.149.QA_REPORT");
     map.put("国家标准", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.GUOJIABIAOZHUN");
     map.put("典型工艺", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.DX_PROCESS_DOC");
     map.put("通用工艺", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.TY_PROCESS_DOC");
     map.put("DWG简图模板", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.DWG2PDFTemplate");
     map.put("图样", "wt.doc.WTDocument|casc.sast.149.DESIGN_DOC|casc.sast.149.DRAWING_DOC");
     
 }

}
