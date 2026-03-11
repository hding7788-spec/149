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
                @GeneratedProperty(name = "ProcessTaskId", type = Long.class, supportedAPI = SupportedAPI.PUBLIC),
                //记录零部件的IDA2A2值
                @GeneratedProperty(name = "partId", type = Long.class, supportedAPI = SupportedAPI.PUBLIC),
                //当前活动的所有者
                @GeneratedProperty(name = "owner", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //零部件名称
                @GeneratedProperty(name = "name", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true)),
                //零部件的编号
                @GeneratedProperty(name = "number", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true, columnName = "ProcessTaskItemNumber")),
                //零部件的版本
                @GeneratedProperty(name = "version", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //记录任务是通过还是驳回
                @GeneratedProperty(name = "routeSelect", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //记录任务完成者
                @GeneratedProperty(name = "completedBy", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //用于记录主任工艺师
                @GeneratedProperty(name = "zhurengongyishi", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //当前任务活动责任人所属的车间
                @GeneratedProperty(name = "chejian", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //用于记录主任工艺师指定的主制车间
                @GeneratedProperty(name = "zhuzhichejian", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //用于记录主任工艺师指定的辅制车间
                @GeneratedProperty(name = "fuzhichejian", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //用于记录工艺组长选择的工艺员
                @GeneratedProperty(name = "gongyiyuan", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //记录当前任务活动的角色
                @GeneratedProperty(name = "executorRole", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //记录当前车间是否主制
                @GeneratedProperty(name = "iszhuzhi", type = Boolean.class, supportedAPI = SupportedAPI.PUBLIC),
                //任务活动名称
                @GeneratedProperty(name = "taskItemName", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //任务活动的状态
                @GeneratedProperty(name = "taskItemState", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //任务活动类型
                @GeneratedProperty(name = "taskType", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //计划完成时间
                @GeneratedProperty(name = "endDate", type = Timestamp.class, supportedAPI = SupportedAPI.PUBLIC),
                //任务要求
                @GeneratedProperty(name = "renwuyaoqiu", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(upperLimit = 2000)),
                //任务依据
                @GeneratedProperty(name = "renwuyiju", type = String.class, supportedAPI = SupportedAPI.PUBLIC),
                //备注，用于执行任务时填写的说明
                @GeneratedProperty(name = "description", type = String.class, supportedAPI = SupportedAPI.PUBLIC, constraints = @PropertyConstraints(upperLimit = 2000)) },
        iconProperties = @IconProperties(standardIcon = "netmarkets/images/open_work.gif",
                openIcon = "netmarkets/images/open_work.gif"))
public class ProcessTaskItem extends _ProcessTaskItem {
    public static final long serialVersionUID = 1;

    public static ProcessTaskItem newProcessTaskItem() throws WTException {
        ProcessTaskItem instance = new ProcessTaskItem();
        instance.initialize();
        return instance;
    }

    public IdentificationObject getIdentificationObject() throws WTException {
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

    public void setOrganization(WTOrganization a_Organization) throws WTPropertyVetoException {
    }

    public WTOrganization getOrganization() {
        return null;
    }
}
