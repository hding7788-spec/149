package com.glaway.mpm.util;

import java.util.Observable;
import java.util.Observer;

public class CommonObservable extends Observable {
	private int type;

	@Override
	public synchronized void addObserver(Observer o) {
		super.addObserver(o);
	}

	@Override
	public synchronized void setChanged() {
		super.setChanged();
	}

	public CommonObservable(int type) {
		super();
		this.type = type;
	}

	public int getType() {
		return type;
	}

}