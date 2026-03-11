/**
 * <br>Created on 2011-3-18
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree;

import java.util.Comparator;

/**
 * <br>
 * Created on 2011-3-18
 * 
 * @author Alex.Huang - ����
 */
public class VaTreeNodeComparator implements Comparator<VaTreeNode> {
	public static final int	LESS		= -1;
	public static final int	EQUAL		= 0;
	public static final int	GREATER		= 1;
	public static final int	UNDECIDABLE	= 999;

	public int compare(VaTreeNode o1, VaTreeNode o2) {
		if (o1.getPart() != null && o2.getPart() != null) {
			String o1Num = o1.getPart().getNumber();
			String o2Num = o2.getPart().getNumber();

			if (o1Num != null && o2Num != null && o1Num.equals(o2Num)) {
				if (o1.getOccId().equals(o2.getOccId())) {
					return EQUAL;
				}
			}
		}
		return UNDECIDABLE;
	}
}