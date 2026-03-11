package com.glaway.mpm.mpmresource.gznumber.rule;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.glaway.mpm.mpmresource.gznumber.bean.PropertiesBean;
import com.glaway.mpm.mpmresource.gznumber.bean.RequestInfoBean;


public class GenerationRuleHelper {
	
	/**
	 * Get Fix Label Definitions; 
	 * EX: FixLabelDefinition=CompanyName,PartType
	 * Result: <ArrayList> CompanyName and PartType
	 * @param pb
	 * @return
	 */
	public static ArrayList getFixLabelDefinitions(PropertiesBean pb) {
		ArrayList fixLabelDefinitions = new ArrayList();
		String fixLabelDefinition = (String)pb.getValue(FixLabelDefinition.FIXLABEL_DEFINITION);

		StringTokenizer st = new StringTokenizer(fixLabelDefinition,",");
		while(st.hasMoreElements()){
			String fixLabelDefinitionType = st.nextToken();	//PartType
			fixLabelDefinitions.add(fixLabelDefinitionType);
		}
			
		return fixLabelDefinitions;
	}

	/**
	 * Get Fix Label Classes
	 * Ex: FixLabelDefinition.PartType=StandardPart,GenericPart
	 * Argument: "PartType"
	 * Result: <ArrayList> StandardPart and GenericPart
	 * @param pb
	 * @param fixLabelDefinition Like
	 * @return
	 */
	public static ArrayList getFixLabelClasses(PropertiesBean pb, String fixLabelDefinition) {
		ArrayList fixLabelDefinitions = new ArrayList();
		String fixLabelDefinitionClasses = (String)pb.getValue(FixLabelDefinition.FIXLABEL_DEFINITION + "." + fixLabelDefinition); 

		StringTokenizer st = new StringTokenizer(fixLabelDefinitionClasses,",");
		while(st.hasMoreElements()){
			String fixLabelDefinitionType = st.nextToken();	//GenericPart
			fixLabelDefinitions.add(fixLabelDefinitionType);
		}
			
		return fixLabelDefinitions;
	}
	
	/**
	 * Get FixLabelClassValues
	 * Ex: FixLabelDefinition.PartType.GenericPart.Name=通用件
			FixLabelDefinition.PartType.GenericPart.Value=.T.
	 * Argument: "PartType", "GenericPart"
	 * Result: <HashMap> <key>Name<value>通用件  <key>Value<value>.T.
	 * @param pb
	 * @param fixLabelDefinition
	 * @param fixLabelClass
	 * @return
	 */
	public static FixLabelDefinition getFixLabelClassesValue(PropertiesBean pb, String fixLabelDefinition, String fixLabelClass){
		FixLabelDefinition fixLabel = new FixLabelDefinition();
		String fixLabelDefinitionName = (String)pb.getValue(FixLabelDefinition.FIXLABEL_DEFINITION + "." + fixLabelDefinition + "." + fixLabelClass + "." + FixLabelDefinition.FIXLABEL_CLASSESNAME); 
		String fixLabelDefinitionValue = (String)pb.getValue(FixLabelDefinition.FIXLABEL_DEFINITION + "." + fixLabelDefinition + "." + fixLabelClass + "." + FixLabelDefinition.FIXLABEL_CLASSESVALUE); 

		fixLabel.setFixLabelDefinitionName(fixLabelDefinitionName);
		fixLabel.setFixLabelDefinition(fixLabelDefinition);
		fixLabel.setFixLabelValue(fixLabelDefinitionValue);
		
		return fixLabel; 
	}
	
	/**
	 * 
	 * @param requestBean
	 * @return
	 */
	public static RuleInfoContained getRule(RequestInfoBean rb,PropertiesBean pb){
		GenerationRule rule = new GenerationRule();

		ArrayList selFixLabelValue = rb.getSelectedFixedLabels();
		String numberFormat = pb.getNumberFormat();
		
		FixLabelDefinition fixLabel;
		for (int i = 0; i < selFixLabelValue.size(); i++) {
			fixLabel = (FixLabelDefinition) selFixLabelValue.get(i);
			numberFormat = numberFormat.replaceAll("<FixLabel-" + fixLabel.getFixLabelDefinition() + ">", fixLabel.getFixLabelValue());
		}

		
		rule.setRuleformat(numberFormat);
		rule.setFixlabel(selFixLabelValue);
	
		return rule;
	}
}
