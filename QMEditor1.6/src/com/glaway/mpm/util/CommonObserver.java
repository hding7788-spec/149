package com.glaway.mpm.util;

import java.util.Observable;
import java.util.Observer;

import com.glaway.mpm.visual.log.VaLogger;

public class CommonObserver implements Observer {
	private static VaLogger logger = VaLogger.getLogger(CommonObserver.class);

	@Override
	public void update(Observable o, Object arg) {
		logger.debug(arg);
	}
}
