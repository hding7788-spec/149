package com.glaway.mpm.util;

import java.util.Comparator;
import java.util.Vector;

public class ParamsComparator implements Comparator<Vector<String>> {

	@Override
	public int compare(Vector<String> v1, Vector<String> v2) {
		int no1 = Integer.valueOf(v1.get(3));
		int no2 = Integer.valueOf(v2.get(3));
		if (no1 > no2) {
			return 1;
		} else if (no1 == no2) {
			return 0;
		} else {
			return -1;
		}
	}

}
