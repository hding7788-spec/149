package ext.casc.process;

import java.io.Externalizable;

import wt.fc.ObjectToObjectLink;
import wt.fc.WTObject;
import wt.util.WTException;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.SupportedAPI;
import com.ptc.windchill.annotations.metadata.TableProperties;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class, interfaces = { Externalizable.class },
        //零部件
        roleA = @GeneratedRole(name = "linkedWTObject", type = WTObject.class, supportedAPI = SupportedAPI.PUBLIC,
                cardinality = Cardinality.ONE_TO_MANY),
        //工艺任务
        roleB = @GeneratedRole(name = "linkedProcessTask", type = ProcessTask.class, supportedAPI = SupportedAPI.PUBLIC,
                cardinality = Cardinality.ONE_TO_MANY, owner = false),
            tableProperties = @TableProperties(tableName = "ProcessTaskLink"))
public class ProcessTaskLink extends _ProcessTaskLink {
    static final long serialVersionUID = 1;

    public static ProcessTaskLink newProcessTaskLink(WTObject linkedWTObject, ProcessTask linkedProcessTask)
            throws WTException {
        ProcessTaskLink instance = new ProcessTaskLink();
        instance.initialize(linkedWTObject, linkedProcessTask);
        return instance;
    }
}
