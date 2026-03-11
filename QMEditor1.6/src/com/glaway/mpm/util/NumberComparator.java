package com.glaway.mpm.util;

import java.util.Comparator;

public class NumberComparator implements Comparator<Integer> {

	@Override
	public int compare(Integer arg0, Integer arg1) {
		return arg0 - arg1;
	}

}
