package ext.casc.report;

public class AllWorkFlow {
	private String processName;
	private String template;
	private String containerName;
	private String starter;
	private String startTime;
	private String runningActivity;
	private String state;
	private String processOid;
	public String getProcessOid() {
		return processOid;
	}
	public void setProcessOid(String processOid) {
		this.processOid = processOid;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getContainerName() {
		return containerName;
	}
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	public String getStarter() {
		return starter;
	}
	public void setStarter(String starter) {
		this.starter = starter;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getRunningActivity() {
		return runningActivity;
	}
	public void setRunningActivity(String runningActivity) {
		this.runningActivity = runningActivity;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
}
