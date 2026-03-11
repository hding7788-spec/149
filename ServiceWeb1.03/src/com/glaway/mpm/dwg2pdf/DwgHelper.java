package com.glaway.mpm.dwg2pdf;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import wt.services.ServiceFactory;

public class DwgHelper implements Externalizable {

	private static final String CLASSNAME = DwgHelper.class.getName();

	public static final DwgService service = (DwgService) ServiceFactory.getService(DwgService.class);

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub

	}

}
