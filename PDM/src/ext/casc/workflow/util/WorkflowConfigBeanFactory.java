package ext.casc.workflow.util;

import java.io.File;

import ext.casc.workflow.CSCWorkflowException;

public class WorkflowConfigBeanFactory {
	public static final String WORKFLOWCONFIGBEAN_CLASS_NAME = "ClassName";

	public static WorkflowConfigBean getWorkflowConfigBeanInstance(){
		WorkflowConfigBean wcb = null;
		try {
			PropertiesUtil pu = new PropertiesUtil(File.separator+"codebase"+File.separator+"ext"+File.separator+"ases"+File.separator+"workflow"+File.separator+"util"+File.separator+"workflowconfigbean.properties");
			String class_name = pu.getValue(WORKFLOWCONFIGBEAN_CLASS_NAME);
			Class c = Class.forName(class_name);
			wcb = (WorkflowConfigBean)c.newInstance();
		}catch (CSCWorkflowException e) {
			e.printMessage();
		}catch(ClassNotFoundException e){
			CSCWorkflowException e1 =  new CSCWorkflowException("反射指定类失败，指定的类未找到!",e);
			e1.printMessage();
		}catch (InstantiationException e) {
			e.printStackTrace();
		}catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return wcb;
	}
}
