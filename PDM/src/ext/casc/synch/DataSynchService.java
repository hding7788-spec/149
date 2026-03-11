package ext.casc.synch;

import wt.doc.WTDocument;
import wt.util.WTException;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changerequest.ChangeRequest;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.preview.Preview;

public interface DataSynchService {
	public abstract String exportChangePackagedTargets(ChangePackaged packaged)
			throws WTException;

	public abstract String exportProcessEnvelopeTargets(ProcessEnvelope pe)
			throws WTException;

	public abstract String exportChangeRequestTargets(ChangeRequest cr)
            throws WTException;

	public abstract String exportProcessNotice(WTDocument doc)
			throws WTException;

	public abstract String exportCommonProcess(WTDocument doc)
			throws WTException;

	public abstract String exportPreviewTargets(Preview preview)
			throws WTException;
}
