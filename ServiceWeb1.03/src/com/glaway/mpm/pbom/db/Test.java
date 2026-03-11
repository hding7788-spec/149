package com.glaway.mpm.pbom.db;

import java.util.List;

public class Test {

	public static void main(String[] args) {
		List list = ErpDao.queryWzk("01","01");
		System.out.println(list.size()	);
	}

}
