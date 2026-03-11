package ext.casc.changephase;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 版本比较 util
 * @author hyuan
 *
 */
public class PhaseComparator implements Comparator<String> {
	
	public static final List<String> PHASE_STATE_LIST = Arrays.asList(ChangePhaseConstants.ASES_PHASE_ARRAY.split("\\" + ChangePhaseConstants.ASES_PHASE_SEP));

	/**
	 * 阶段比较
	 * 当返回 -size(),表明比较出错
	 * 当返回值 > 0, 将要转入的阶段 大于 之前的阶段
	 * 当返回值 < 0，将要转入的阶段 小于 之前的阶段
	 */
	public int compare(String from, String to) {
		if (from == null || to == null || !PHASE_STATE_LIST.contains(from) || !PHASE_STATE_LIST.contains(to)) {
			// -size() is error code
			return 0 - PHASE_STATE_LIST.size();
		}
		
		return PHASE_STATE_LIST.indexOf(to) - PHASE_STATE_LIST.indexOf(from);
	}
	
	public List<String> getAvailablePhaseCodes(String current) {
		if (!PHASE_STATE_LIST.contains(current)) return null;
		else return PHASE_STATE_LIST.subList(PHASE_STATE_LIST.indexOf(current), PHASE_STATE_LIST.size());
	}

}
