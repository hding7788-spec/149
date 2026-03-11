package com.glaway.mpm.dwg2pdf;

import wt.doc.WTDocument;
import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface DwgService {

	public boolean sendToDwgWorkerQueue(WTDocument doc) throws WTException;

}
