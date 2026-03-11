package com.glaway.mpm.view;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.glaway.mpm.intf.workproceduce.WorkproceduceUtil;
import com.glaway.mpm.model.PdName;
import com.glaway.mpm.model.PdNameType;
import com.glaway.mpm.model.TechnicsCyy;
import com.glaway.mpm.sop.util.StringUtil;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.ptc.wpcfg.engine2.model.value.ValueChangeListener;
import com.ptc.wpcfg.engine2.model.value.ValueHolder;

public class StepNameComboBox extends JComboBox implements Observer{
//	private static Map<String, String> allStepNameMap = ResourceIntf.getProcessStepName();
	public Map<String, String> stepNameMap;
	private Collection values;
	private Set<String> keys;
	private String workShop = "";

	public StepNameEditor stepNameEditor = new StepNameEditor();

	public StepNameComboBox(String workShop) {
		this.workShop = workShop;
		setEditable(true);
		setEditor(stepNameEditor);

		if (NewTechnicsPart.allStepNameMap != null) {
			this.values = NewTechnicsPart.allStepNameMap.values();
			this.keys = NewTechnicsPart.allStepNameMap.keySet();
			stepNameMap = new HashMap();
			Set<Entry<String, String>>  all = NewTechnicsPart.allStepNameMap.entrySet();
			Iterator<Entry<String, String>> it = all.iterator();
			while(it.hasNext()){
				Entry<String, String> entry = it.next();
				stepNameMap.put(entry.getValue(), entry.getKey());
			}
		}

		getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				StepNameComboBox.this.inputEvent(e);
			}
		});
	}

	public StepNameComboBox() {
		StepNameEditor stepNameEditor1 = new StepNameEditor("");
		setEditable(true);
		setEditor(stepNameEditor1);
		getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				StepNameComboBox.this.inputCyy();
			}
		});

	}


	public void reSetList() {
		String value = (String) getSelectedItem();
		removeAllItems();
		setSelectedItem(value);
		if ((this.workShop == null) || (this.workShop.trim().equals(""))) {
			if (value.trim().equals("")) {
				if (this.values != null) {
					for (Iterator it = this.values.iterator(); it.hasNext();) {
						addItem(it.next());
					}
				}
			} else {
				Vector vector = getShowVector(value);
				if ((vector != null) && (vector.size() > 0)) {
					for (int i = 0; i < vector.size(); i++) {
						addItem(vector.get(i));
					}
				}

			}

		} else if (value.trim().equals("")) {
			if (this.stepNameMap.keySet() != null) {
				for (Iterator it = this.stepNameMap.keySet().iterator(); it
						.hasNext();) {
					addItem(it.next());
				}
			}
		} else {
			Vector vector = getShowVector(value);
			if ((vector != null) && (vector.size() > 0)) {
				for (int i = 0; i < vector.size(); i++) {
					addItem(vector.get(i));
				}
			}
		}
	}

	private Vector reSetVoidList() {
		Vector v = new Vector();
//		if ((this.workShop == null) || (this.workShop.trim().equals(""))) {
//			if (this.values != null) {
//				for (Iterator it = this.values.iterator(); it.hasNext();) {
//					String next = (String) it.next();
//					v.add(next);
//				}
//
//			}
//
//		} else if (this.stepNameMap.keySet() != null) {
			for (Iterator it = this.stepNameMap.keySet().iterator(); it.hasNext();) {
				String next = (String) it.next();
				v.add(next);
			}
//		}

		return v;
	}

	private void inputEvent(KeyEvent e) {
		String s = (String) getEditor().getItem();
		removeAllItems();
		Vector v = null;
		if ((s == null) || (s.equals(""))) {
			setSelectedItem("");
			v = reSetVoidList();
		} else {
			setSelectedItem(s);
			v = getShowVector(s.toLowerCase());
		}
		if ((v == null) || (v.size() == 0)) {
			hidePopup();
			return;
		}
		for (int i = 0; i < v.size(); i++) {
			addItem(v.get(i));
		}
		if (v.size() > 0) {
			hidePopup();
			showPopup();
			repaint();
		}
	}


	private void inputCyy() {
		String s = (String) getEditor().getItem();
		removeAllItems();
		Vector v = null;
		if ((s == null) || (s.equals(""))) {
			setSelectedItem("");
//			v = reSetVoidList();
		} else {
			setSelectedItem(s);
			v = getShowCyyVector(s);
		}
		if ((v == null) || (v.size() == 0)) {
			hidePopup();
			return;
		}
		for (int i = 0; i < v.size(); i++) {
			addItem(v.get(i));

		}
		if (v.size() > 0) {
			hidePopup();
			showPopup();
			repaint();
		}
	}

	private static void parseNode(Vector returnV, List<PdNameType> pdNameType,String s) {
		if (pdNameType != null && pdNameType.size() != 0) {
			for (PdNameType type : pdNameType) {
				List<PdNameType> eptype = type.getPdNameTypeList();
				parseNode(returnV,eptype,s);
				if (type.getPdNameList() != null) {
					for (PdName pdName : type.getPdNameList()) {
						if(pdName.getShortcut().contains(s)) {
							returnV.add(pdName.getName());
						}
					}
				}
			}
		}
	}

	private Vector getShowVector(String s) {
		Vector returnV = new Vector();
		if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			for(Map.Entry<String, String> entry : NewTechnicsPart.allStepNameMap.entrySet()){
				String pdName = entry.getValue();
				String shortCut = StringUtil.convertStr(pdName);
				if(shortCut.contains(s)){
					returnV.add(pdName);
				}
			}
		}else{
			PdNameType pdNameType = ResourceIntf.get812AllPdNameType();
			if (pdNameType.getPdNameList() != null) {
				for (PdName pdName : pdNameType.getPdNameList()) {
					if (pdName.getShortcut().contains(s)) {
						returnV.add(pdName.getName());
					}
				}
			}
			parseNode(returnV, pdNameType.getPdNameTypeList(), s);
		}

//		if ((this.workShop == null) || (this.workShop.trim().equals(""))) {
//			if (allStepNameMap == null)
//				return null;
//			Vector vector1 = new Vector();
//			for (Iterator it = this.values.iterator(); it.hasNext();) {
//				String ss = (String) it.next();
//				if (ss.startsWith(s))
//					vector1.add(ss);
//			}
//			Vector vector2 = new Vector();
//			for (int i = 0; i < vector1.size(); i++) {
//				String ss = (String) vector1.get(i);
//				Set<Entry<String, String>> entries = allStepNameMap.entrySet();
//				for (Map.Entry entry : entries) {
//					if (ss.equals(entry.getValue())) {
//						vector2.add(entry.getKey());
//					}
//				}
//			}
//			return vector2;
//		}
//
//		if (this.stepNameMap == null)
//			return null;
//		Collection values = this.stepNameMap.values();
//		Vector vector1 = new Vector();
//		for (Iterator it = values.iterator(); it.hasNext();) {
//			String ss = (String) it.next();
//			if (ss.startsWith(s))
//				vector1.add(ss);
//		}
//		Vector vector2 = new Vector();
//		for (int i = 0; i < vector1.size(); i++) {
//			String ss = (String) vector1.get(i);
//			Set<Entry<String, String>> entries = allStepNameMap.entrySet();
//			for (Map.Entry entry : entries) {
//				if (ss.equals(entry.getValue())) {
//					vector2.add(entry.getKey());
//				}
//			}
//		}
		return returnV;
	}

	private Vector getShowCyyVector(String s) {
		Vector returnV = new Vector();
		if (TechnicsStepJPanel_XW.cyys != null) {
			for (TechnicsCyy cyy : TechnicsStepJPanel_XW.cyys) {
				if(cyy.getShortName().contains(s) || cyy.getName().contains(s)) {
					returnV.add(cyy.getName());
				}
			}
		}

		return returnV;
	}
	public void setWorkType(String s) {
		this.workShop = s;
		this.stepNameMap = ResourceIntf.getPdNames(this.workShop);
	}

	public class StepNameEditor implements ComboBoxEditor{
		private JTextField editor = new JTextField();
		private EventListenerList listenerList = new EventListenerList();

		private StepNameEditor() {
			editor.addFocusListener(new FocusAdapter() {

				@Override
				public void focusLost(FocusEvent e) {
					if (stepNameMap != null) {
						Set<String> set = stepNameMap.keySet();
						if (set != null) {
							for (String stepName : set) {
								String pName = editor.getText().trim();
								if (stepName.equals(pName)) {
									return;
								}
							}
						}
					}
					editor.setText("");
				}
			});
		}

		private StepNameEditor(String s){

		}

		public void addActionListener(ActionListener l) {
			this.listenerList.add(ActionListener.class, l);
		}

		public Component getEditorComponent() {
			return this.editor;
		}

		public Object getItem() {
			return this.editor.getText();
		}

		public void removeActionListener(ActionListener l) {
			this.listenerList.remove(ActionListener.class, l);
		}

		public void selectAll() {
		}

		public void setItem(Object anObject) {
			if ((anObject instanceof String)) {
				String s = (String) anObject;
				this.editor.setText(s);
			} else {
				this.editor.setText("");
			}
			System.out.println(this.editor.getText());
		}


	}

	public void update(Observable o, Object arg) {
	try {
		if (o != null && o instanceof CommonObservable) {
			CommonObservable co = (CommonObservable) o;
			int type = co.getType();
			if (type == 4)// 表示是工序
			{
				if (arg != null && arg instanceof String) {
					String commonString = (String) arg;
					if(!"检验".equals(this.getSelectedItem().toString())) {//检验工序的名称不能改变
						this.setSelectedItem(commonString);
					}
				}
			}
		}
	} catch (Exception e) {

	}
}

}
