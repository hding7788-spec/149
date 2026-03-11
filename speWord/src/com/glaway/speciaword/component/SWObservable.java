/**
 * 
 */
package com.glaway.speciaword.component;

import java.util.Vector;

import javax.swing.JComponent;

/**
 * @author MosesX
 * 
 */
public class SWObservable {

	private boolean changed = false;
	private Vector<SWObserver> obs;
	private JComponent component;

	public SWObservable() {
		obs = new Vector<SWObserver>();
	}

	public synchronized void setChanged() {
		this.changed = true;
	}

	protected synchronized void clearChanged() {
		changed = false;
	}

	public synchronized void addObserver(SWObserver o) {
		if (o == null)
			throw new NullPointerException();
		if (!obs.contains(o)) {
			obs.addElement(o);
		}
	}

	public synchronized void deleteObserver(SWObserver o) {
		obs.removeElement(o);
	}

	public void notifyObservers(String arg) {
		if (component == null) {
			return;
		}
		Object[] arrLocal;

		synchronized (this) {
			if (!changed)
				return;
			arrLocal = obs.toArray();
			clearChanged();
		}

		for (int i = arrLocal.length - 1; i >= 0; i--)
			((SWObserver) arrLocal[i]).update(component, arg);
	}

	public JComponent getComponent() {
		return component;
	}

	public void setComponent(JComponent component) {
		this.component = component;
	}
}
