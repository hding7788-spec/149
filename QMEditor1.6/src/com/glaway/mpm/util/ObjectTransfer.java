package com.glaway.mpm.util;

import org.dom4j.Element;

import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.view.TechnicsMessageTreeObject;

public class ObjectTransfer {
	public static UploadTechnics technicsElementToUploadTechnics(Element element) {
		UploadTechnics technics = null;
		if (element != null) {
			technics = new UploadTechnics();
			technics.setTechnicsNumber(element.attributeValue("technicsNumber"));
			technics.setTechnicsName(element.attributeValue("technicsName"));
			technics.setTechnicsCategory(element.attributeValue("technicsCategory"));
		}
		return technics;
	}

	public static UploadTechnics technicsToUploadTechnics(TechnicsMessageTreeObject tmto) {
		UploadTechnics technics = null;
		if (tmto != null) {
			String technicsNumber = tmto.getTechnicsNumber();
			String technicsName = tmto.getTechName();
			String technicsCategory = tmto.getTechnicsCategory();
			technics = new UploadTechnics();
			technics.setTechnicsNumber(technicsNumber);
			technics.setTechnicsName(technicsName);
			technics.setTechnicsCategory(technicsCategory);
		}
		return technics;
	}
}
