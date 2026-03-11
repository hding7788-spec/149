package ext.casc.workflow.util;

import java.util.HashMap;
import wt.inf.container.WTContainerRef;
import wt.util.WTException;

public interface DocService {
    public Object createDoc(String number, String name, String desc, HashMap attributes,
            HashMap<String, Object> softAttr, WTContainerRef containerRef) throws WTException;
}
