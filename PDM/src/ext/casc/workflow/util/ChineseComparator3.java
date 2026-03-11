package ext.casc.workflow.util;

import java.io.Serializable;
import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;
import java.util.Map;

public class ChineseComparator3 implements Comparator ,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3818820407987166048L;
	Collator collator = Collator.getInstance();
	public int compare(Object o1, Object o2) {
		CollationKey key1 = collator.getCollationKey(((Map<String, String>)o1).get("单位简称").toString());
		CollationKey key2 = collator.getCollationKey(((Map<String, String>)o2).get("单位简称").toString());
		return key1.compareTo(key2);
	}
}
