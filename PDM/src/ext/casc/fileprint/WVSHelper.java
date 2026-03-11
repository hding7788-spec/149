package ext.casc.fileprint;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class WVSHelper implements Externalizable {

    private static final String CLASSNAME = WVSHelper.class.getName();

    public static final WVSService service = new WVSServiceFwd();
    static final long serialVersionUID = 1;
    public static final long EXTERNALIZATION_VERSION_UID = 957977401221134810L;
    protected static final long OLD_FORMAT_VERSION_UID = 1262802941457736433L;

    public void writeExternal(ObjectOutput output) throws IOException {
        output.writeLong(EXTERNALIZATION_VERSION_UID);
    }

    public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
        long readSerialVersionUID = input.readLong(); // consume UID
        readVersion(this, input, readSerialVersionUID, false, false); // read fields
    }

    protected boolean readVersion(WVSHelper thisObject, ObjectInput input, long readSerialVersionUID,
            boolean passThrough, boolean superDone)
            throws IOException, ClassNotFoundException {
        boolean success = true;
        if (readSerialVersionUID == EXTERNALIZATION_VERSION_UID) { // if current version UID
        } else {
            success = readOldVersion(input, readSerialVersionUID, passThrough, superDone);
            if (input instanceof wt.pds.PDSObjectInput)
                wt.fc.EvolvableHelper.requestRewriteOfEvolvedBlobbedObject();
        }

        return success;
    }

    private boolean readOldVersion(ObjectInput input, long readSerialVersionUID, boolean passThrough, boolean superDone)
            throws IOException, ClassNotFoundException {
        boolean success = true;
        if (readSerialVersionUID == OLD_FORMAT_VERSION_UID) { // handle previous version
        } else throw new java.io.InvalidClassException(CLASSNAME, "Local class not compatible:"
                           + " stream classdesc externalizationVersionUID=" + readSerialVersionUID
                           + " local class externalizationVersionUID=" + EXTERNALIZATION_VERSION_UID);
        return success;
    }

}
