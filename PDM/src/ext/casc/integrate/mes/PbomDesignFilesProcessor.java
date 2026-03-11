package ext.casc.integrate.mes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;

import com.glaway.mpm.util.ApacheZipUtil;

import ext.casc.util.IBAUtility;

public class PbomDesignFilesProcessor {
	private static String wt_temp;

	static {
		try {
			WTProperties pro = WTProperties.getLocalProperties();
			wt_temp = pro.getProperty("wt.temp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据零部件的图号信息获取该零部件所有相关的设计文件
	 * 包括：
	 * 1、设计模型（PVS文件）
	 * 2、DRW二维图（PDF文件）
	 * 3、DWG二维图（PDF文件）
	 *
	 * @param productNumber 工艺文件编号
	 * @param productVersionType 工艺文件批次
	 * @param productVersion 工艺文件版本
	 * @param guid 用户ID
	 * @throws Exception
	 */
	public String getPbomDesignFiles(String productNumber,String productVersionType,String productVersion,String guid) throws Exception {
		StringBuffer result = new StringBuffer();
		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		WTPart part = getDesignPartByNumberAndVersion(productNumber,productVersionType,productVersion,"Design");
		if(part != null) {
			result.append("<designFiles>");
			result.append("<partNumber>");
			result.append(part.getNumber());
			result.append("</partNumber>");
			result.append("<partVersion>");
			result.append(part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue());
			result.append("</partVersion>");

			String number = part.getNumber();
			if(number.contains(".")) {
				number = number.substring(0, number.lastIndexOf('.'));
			}

			//获取该零部件的PVS文件
			String partPvsFileName = MesUtil.getPvsFile(part, number);
			String partVersion = part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue();
			builderXml(result,number,part.getName(),partVersion,partPvsFileName);

			//获取该零部件关联的二维图PDF文件
			MesFileBean bean = MesUtil.getEpmDrwDocumentByPart2(part,number);
			if(bean != null) {
				builderXml(result,bean);
			}

			//获取该零部件关联的文档对象
			List<WTDocument> list = MesUtil.getRelateDocByPart(part);
			if(list != null && !list.isEmpty()) {
				for (WTDocument wtDocument : list) {
					bean = MesUtil.getPdfFileForDoc(wtDocument, number);
					if(bean != null) {
						builderXml(result,bean);
					}
				}
			}


			String fileDir = wt_temp + File.separator + "IXBExpImp" + File.separator + number;
			ApacheZipUtil.compress(fileDir, fileDir+".zip");

			File file = new File(fileDir+".zip");
			if(file.exists()) {
				result.append("<fileName>");
				result.append(file.getName());
				result.append("</fileName>");
			} else {
				result.append("<fileName>");
				result.append("");
				result.append("</fileName>");
			}

			File dirFile = new File(fileDir);
			if(dirFile.exists()) {
				dirFile.deleteOnExit();
			}
			result.append("</designFiles>");
		} else {
			result.append("<error>");
			result.append("part not exist");
			result.append("</error>");
		}
		return result.toString();
	}

	private static void builderXml(StringBuffer result,String number,String name,String version,String fileName) {
		result.append("<file>");
		result.append("<number>");
		result.append(number);
		result.append("</number>");

		result.append("<name>");
		result.append(name);
		result.append("</name>");

		result.append("<version>");
		result.append(version);
		result.append("</version>");
		result.append("</file>");

		result.append("<type>");
		result.append(fileName);
		result.append("</type>");
		result.append("</file>");
	}

	private static void builderXml(StringBuffer result,MesFileBean bean) {
		result.append("<file>");
		result.append("<number>");
		result.append(bean.getNumber());
		result.append("</number>");

		result.append("<name>");
		result.append(bean.getName());
		result.append("</name>");

		result.append("<version>");
		result.append(bean.getVersion());
		result.append("</version>");
		result.append("</file>");

		result.append("<type>");
		result.append(bean.getFilePath());
		result.append("</type>");
	}

	public WTPart getDesignPartByNumberAndVersion(String number,String batch,String version,String viewName) throws WTException {
		View view = MesUtil.getViewByName(viewName);
		long viewOid = view.getPersistInfo().getObjectIdentifier().getId();
		QuerySpec qSpec = new QuerySpec(WTPart.class);
		qSpec.setAdvancedQueryEnabled(true);
        int[] index = { 0 };

        SearchCondition sCondition = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number);
        qSpec.appendWhere(sCondition, index);

        qSpec.appendAnd();
        qSpec.appendWhere(new SearchCondition(WTPart.class, "view.key.id", SearchCondition.EQUAL, viewOid),
				new int[] { 0 });

        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        LatestConfigSpec lcs = new LatestConfigSpec();
        qResult = lcs.process(qResult);
        WTPart part = null;
        IBAUtility ibaUtility = null;
        while(qResult.hasMoreElements()) {
        	part = (WTPart)qResult.nextElement();
        	ibaUtility = new IBAUtility(part);
        	String bc = ibaUtility.getIBAValue("BATCH");
        	String ver = part.getVersionIdentifier().getValue();

        	if(batch != null && !"".equals(batch)) {
        		if(!batch.equals(bc)) {
        			continue;
        		}
        	}

        	if(version != null && !"".equals(version)) {
        		if(!ver.equals(version)) {
        			continue;
        		}
        	}

        	return part;
        }

        return null;
	}
}
