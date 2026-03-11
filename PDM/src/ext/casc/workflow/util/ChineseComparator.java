package ext.casc.workflow.util;

import java.io.Serializable;
import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import wt.project.Role;

public class ChineseComparator implements Comparator ,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 84981065495624559L;
	Collator collator = Collator.getInstance();
	
	public int compare(Object o1, Object o2) {
		CollationKey key1 = collator.getCollationKey(((Role)o1).getDisplay(Locale.CHINA).toString());
		CollationKey key2 = collator.getCollationKey(((Role)o2).getDisplay(Locale.CHINA).toString());
		return key1.compareTo(key2);
	}
}
