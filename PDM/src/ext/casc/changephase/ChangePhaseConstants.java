package ext.casc.changephase;

import java.io.File;

import ext.casc.workflow.CSCWorkflowException;
import ext.casc.workflow.util.PropertiesUtil;

public class ChangePhaseConstants {

	/** IBA **/
	public static String ASES_IBA_JIEDUANBIAOSHI = "PHASE_CODE";
	public static String ASES_IBA_SUOSHUCHANPIN= "ENDITEM";
	public static String ASES_IBA_CHANGETYPE = "CHANGETYPE";
	
	/** Version constants **/
	public static String ASES_PHASE_ARRAY = "M|C|S|Z|D";
	public static String ASES_PHASE_ARRAY_PHASE_CHANGETYPE_ECR = "SII|S1II|S2II|S3II|ZII|MIII|M1III|M2III|M3III";
	public static String ASES_PHASE_SEP = "|";
	
	static {
		try {
			PropertiesUtil pu = new PropertiesUtil(File.separator+"codebase"+File.separator+"ext"+File.separator+"ases" + File.separator + "ases.properties");
			ASES_PHASE_ARRAY = pu.getValue("ext.ases.changephase.ASES_PHASE_ARRAY");
			ASES_PHASE_ARRAY_PHASE_CHANGETYPE_ECR = pu.getValue("ext.ases.changephase.ASES_PHASE_ARRAY_PHASE_CHANGETYPE_ECR");
		} catch (CSCWorkflowException e) {
			e.printStackTrace();
		}
	}

}
