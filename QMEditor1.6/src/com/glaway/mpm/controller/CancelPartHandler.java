package com.glaway.mpm.controller;

import com.glaway.mpm.view.NewTechnicsPart;

import java.util.Vector;

public class CancelPartHandler {
	public static NewTechnicsPart frame = null;

	public static boolean clearFittings(Vector fList, boolean isClearAll)
			throws Exception {
		if (frame == null)
			return false;
		return frame.clearFittings(fList, isClearAll);
	}
}
