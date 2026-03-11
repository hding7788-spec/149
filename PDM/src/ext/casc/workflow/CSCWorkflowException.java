package ext.casc.workflow;

public class CSCWorkflowException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 160937178872997988L;

	public CSCWorkflowException(String s){
		super(s);
	}
	
	public CSCWorkflowException(String s,Throwable cause){
		super(s,cause);
	}
	
	public String printMessage(){
		StringBuffer sb = new StringBuffer();
		String tempMeesge = this.getMessage();
		sb.append(tempMeesge);
		StackTraceElement ste[] = this.getCause().getStackTrace();
		for(int i = 0 ; i < ste.length ; i++){
			sb.append(",");
			sb.append(ste[i].toString());
		}
		return sb.toString();
	}
}
