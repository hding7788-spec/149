package ext.casc.fileprint;

import java.beans.PropertyVetoException;
import java.io.IOException;
import wt.representation.Representable;
import wt.util.WTException;

public interface WVSService {

   public Representable repToAttachment( Representable representable, boolean flag )
            throws WTException, PropertyVetoException, IOException;
}
