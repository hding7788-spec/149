package com.glaway.mpm.mainTest;

import java.util.Vector;

class Technics {
	private String technicsNumber;
	private String technicsName;
	private String technicsCategory;

	public String getTechnicsNumber() {
		return technicsNumber;
	}

	public void setTechnicsNumber(String technicsNumber) {
		this.technicsNumber = technicsNumber;
	}

	public String getTechnicsName() {
		return technicsName;
	}

	public void setTechnicsName(String technicsName) {
		this.technicsName = technicsName;
	}

	public String getTechnicsCategory() {
		return technicsCategory;
	}

	public void setTechnicsCategory(String technicsCategory) {
		this.technicsCategory = technicsCategory;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Technics)) {
			return false;
		}
		Technics technics = (Technics) obj;
		return technics.getTechnicsNumber().equals(this.getTechnicsNumber())
				&& technics.getTechnicsName().equals(this.getTechnicsName());
	}
}

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Vector<Technics> technicss = new Vector<Technics>();
		for (int i = 0; i < 3; i++) {
			Technics t = new Technics();
			t.setTechnicsNumber(i + "");
			t.setTechnicsName(i + "");
			t.setTechnicsCategory(i + "");
			technicss.add(t);
		}
		Technics t = new Technics();
		t.setTechnicsNumber("1");
		t.setTechnicsName("1");
		t.setTechnicsCategory("2");
		System.out.println(technicss.contains(t));
	}

}
