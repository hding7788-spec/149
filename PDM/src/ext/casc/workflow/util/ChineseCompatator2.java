package ext.casc.workflow.util;

import java.io.Serializable;
import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;

import wt.org.WTUser;

public class ChineseCompatator2 implements Comparator ,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3818820407987166048L;
	Collator collator = Collator.getInstance();
	public int compare(Object o1, Object o2) {
		CollationKey key1 = collator.getCollationKey(((WTUser)o1).getFullName().toString());
		CollationKey key2 = collator.getCollationKey(((WTUser)o2).getFullName().toString());
		return key1.compareTo(key2);
	}
}
