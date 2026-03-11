package ext.casc.process;

import java.io.Externalizable;
import java.sql.Timestamp;

import wt.access.IdentityAccessControlled;
import wt.content.ContentHolder;
import wt.enterprise.Managed;
import wt.fc.IdentificationObject;
import wt.inf.container.WTContainedIdentified;
import wt.org.OrganizationOwnedImpl;
import wt.org.WTOrganization;
import wt.org.electronicIdentity.ElectronicallySignable;
import wt.recent.RecentlyVisited;
import wt.type.TypeDefinitionInfo;
import wt.type.Typed;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.IconProperties;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.SupportedAPI;
@GenAsPersistable(superClass = Managed.class,
                interfaces = { WTContainedIdentified.class, Typed.class, OrganizationOwnedImpl.class,
                ElectronicallySignable.class, ContentHolder.class,
                IdentityAccessControlled.class, RecentlyVisited.class, Externalizable.class },
                properties = {
				//记录工艺任务的IDA2A2值
    			@GeneratedProperty(name = "ProcessPlanId", type = Long.class, supportedAPI = SupportedAPI.PUBLIC),
                //零部件名称
                @GeneratedProperty(name = "name", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true)),
                //零部件编号
                @GeneratedProperty(name = "number", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true, columnName = "ProcessTaskNumber")),
                //零部件的版本
                @GeneratedProperty(name = "version", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //主制车间
                @GeneratedProperty(name = "zhuzhichejian", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //辅制车间
                @GeneratedProperty(name = "fuzhichejian", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //任务创建时间
                @GeneratedProperty(name = "startDate", type = Timestamp.class, supportedAPI = SupportedAPI.PUBLIC),
                //任务计划完成时间
                @GeneratedProperty(name = "endDate", type = Timestamp.class, supportedAPI = SupportedAPI.PUBLIC),
                //任务实际完成时间
                @GeneratedProperty(name = "wanchengDate", type = Timestamp.class, supportedAPI = SupportedAPI.PUBLIC),
                //任务状态
                @GeneratedProperty(name = "taskState", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //任务类型
                @GeneratedProperty(name = "taskType", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //任务依据
                @GeneratedProperty(name = "renwuyiju", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //任务要求
                @GeneratedProperty(name = "renwuyaoqiu", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(upperLimit = 2000)) },
                iconProperties = @IconProperties(standardIcon = "wtcore/images/projactivity.gif",
                openIcon = "wtcore/images/projactivity.gif"))
public class ProcessTask extends _ProcessTask {
    public static final long serialVersionUID = 1;

    public static ProcessTask newProcessTask() throws WTException {
        ProcessTask instance = new ProcessTask();
        instance.initialize();
        return instance;
    }

    public IdentificationObject getIdentificationObject()
            throws WTException {

        return null;
    }

    public String getFlexTypeIdPath() {

        return null;
    }

    public Object getValue() {

        return null;
    }

    public void setValue(String key, String value) {
    }

    public TypeDefinitionInfo getTypeDefinitionInfo() {
        return null;
    }

    public void setOrganization(WTOrganization a_Organization)
            throws WTPropertyVetoException {
    }

    public WTOrganization getOrganization() {

        return null;
    }
}
