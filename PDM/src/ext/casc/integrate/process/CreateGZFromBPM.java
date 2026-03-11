package ext.casc.integrate.process;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainer;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.mpmresource.AttributeConstants;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.PropertiesConfigs;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.WTContainerUtil;
import com.ptc.windchill.mpml.resource.MPMTooling;

import ext.casc.sop.constants.SopConstants;

/**
 * @ Author     ：CJH.
 * @ Date       ：Created in 2020/4/24
 * @ Description：
 */
public class CreateGZFromBPM {
	private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.MPMRESOURCE_TYPE_TO_FOLDER_CONFIG_PATH);

	public static void createGZ(String gzCode,String gzName,String gzType,String UpdateTime,String SubmitBy){
		try {
			if(!gzCode.startsWith("A%")){
				gzCode = "A%" + gzCode;
	    	}
			String typeName = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.Frock";
			WTContainer container = WTContainerUtil.getLibraryByName(SopConstants.SOP_CONTAINER_GYZYK);
			String folderPath = propertiesUtil.getProperty(typeName);
			if(gzType.equals("A")){
				folderPath = folderPath + "/A";
			}else if(gzType.equals("B")){
				folderPath = folderPath + "/B";
			}else if(gzType.equals("C")){
				folderPath = folderPath + "/C";
			}
			String description = "BPM导入";
			MPMTooling tooling = MPMResourceUtil.createTooling(gzCode, gzName, container, folderPath, typeName,description);
			setIBAValues(tooling,gzType,description,UpdateTime,SubmitBy);
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	private static void setIBAValues(IBAHolder ibaHolder,String gzType,String description,String UpdateTime,String SubmitBy) throws WTException, WTPropertyVetoException, RemoteException{
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(AttributeConstants.remarkKey, description);
        ibaMap.put(AttributeConstants.frocktype, gzType);
        ibaMap.put("BPMCreator", SubmitBy);
        ibaMap.put("BPMCreateTime", UpdateTime);
        IBAHelper helper = new IBAHelper(ibaHolder);
        helper.setIBAValue(ibaHolder, ibaMap);
    }
}
