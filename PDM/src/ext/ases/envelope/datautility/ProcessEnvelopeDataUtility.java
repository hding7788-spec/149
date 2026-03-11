package ext.ases.envelope.datautility;

import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;

import ext.ases.envelope.ProcessEnvelope;

public class ProcessEnvelopeDataUtility extends AbstractDataUtility {

    @Override
    public Object getDataValue(String columnName, Object obj, ModelContext mc) throws WTException {
        if (obj instanceof ProcessEnvelope) {
            ProcessEnvelope processEnvelope = (ProcessEnvelope)obj;
            if ("LifeCycleState".equals(columnName)) {
                return processEnvelope.getState().getState().getDisplay();
            }
        }
        return null;
    }

}
