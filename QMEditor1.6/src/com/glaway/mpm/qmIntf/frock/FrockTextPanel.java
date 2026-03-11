package com.glaway.mpm.qmIntf.frock;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

public class FrockTextPanel extends JTextPane {
	private static final long serialVersionUID = 1L;

	HTMLEditorKit editorKit = new HTMLEditorKit();
	HTMLDocument document = new HTMLDocument();

	public FrockTextPanel(Map<String, String> map) {
		super();
		try {
			setContentType("text/html");
			setEditorKit(editorKit);
			setDocument(document);
			setEditable(false);
			editorKit
					.insertHTML(document, 0, generateHtml(map), 0, 0, Tag.HTML);
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setLayout(new BorderLayout());
	}

	private String prefix = "<html><body>	<table width='100%' border='2' height='100%'>	";
	private String suffix = "</table></body></html>";
	private String trPrefix = "<tr>";
	private String trSuffix = "</tr>";
	private String[] line1 = { "产品代号", "投产付数", "常用工装", "试模件" };
	private String[] line2 = { "镶件", "评审", "共用工装", "使用单位" };
	private String[] line3 = { "零件图号", "整件图号" };
	private String[] line4 = {};
	private String[] line5 = { "拟制", "审核", "会签", "批准" };

	private String generateHtml(Map<String, String> map) {
		if (map == null) {
			map = new HashMap<String, String>();
		}
		String td11 = getTd(line1[0]);
		String value11 = getTd(map.get("productNumber"));
		String td12 = getTd(line1[1]);
		String value12 = getTd(map.get("productionNumber"));
		String td13 = getTd(line1[2]);
		String value13 = getTd(map.get("isRegularlyTools"));
		String td14 = getTd(line1[3]);
		String value14 = getTd(map.get("isTestPart"));
		String tr1 = trPrefix + td11 + value11 + td12 + value12 + td13
				+ value13 + td14 + value14 + trSuffix;

		td11 = getTd(line2[0]);
		value11 = getTd(map.get("insertPart"));
		td12 = getTd(line2[1]);
		value12 = getTd(map.get("isReview"));
		td13 = getTd(line2[2]);
		value13 = getTd(map.get("isCommonTools"));
		td14 = getTd(line2[3]);
		value14 = getTd(map.get("workShop"));
		String tr2 = trPrefix + td11 + value11 + td12 + value12 + td13
				+ value13 + td14 + value14 + trSuffix;

		td11 = getTd(line3[0], 3);
		value11 = getTd(map.get("partNumber"));
		td12 = getTd(line3[1], 3);
		value12 = getTd(map.get("wholePartNumber"));
		String tr3 = trPrefix + td11 + value11 + td12 + value12 + trSuffix;

		td11 = getTd(line5[0]);
		value11 = getTd(map.get(""));
		td12 = getTd(line5[1]);
		value12 = getTd(map.get(""));
		td13 = getTd(line5[2]);
		value13 = getTd(map.get(""));
		td14 = getTd(line5[3]);
		value14 = getTd(map.get(""));
		String tr4 = trPrefix + td11 + value11 + td12 + value12 + td13
				+ value13 + td14 + value14 + trSuffix;

		return prefix + tr1 + tr2 + tr3 + tr4 + suffix;
	}

	private String getTd(String value) {
		if (value == null) {
			value = "";
		}
		return "<td>" + value + "</td>";
	}

	private String getTd(String value, int number) {
		return "<td colspan='" + number + "'>" + value + "</td>";
	}

	public static void main(String[] args) {

	}
}
