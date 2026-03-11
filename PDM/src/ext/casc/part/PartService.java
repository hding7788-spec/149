package ext.casc.part;

import java.util.List;

import wt.util.WTException;

public interface PartService {
	public List<String> getEndPartList(String context) throws WTException;
}
