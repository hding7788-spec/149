package com.glaway.mpm.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.glaway.mpm.log.VaLogger;

/**
 * 提供UI界面通用方法。
 *
 * @author 龙秀川
 *
 */
public class CommonUIUtil {
	private static VaLogger logger = VaLogger.getLogger(CommonUIUtil.class);
	public static final Dimension DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int SCREEN_WIDTH = DIMENSION.width;
	public static final int SCREEN_HEIGHT = DIMENSION.height;

	/**
	 * 设置对话框大小并居中
	 * @param dialog
	 * @param width		屏幕宽的几分之一的分母：2,3,4...
	 * @param height	屏幕高的几分之一的分母：2,3,4...
	 */
	public static void setSize(JDialog dialog, int width, int height) {
		if (dialog != null) {
			dialog.setSize(DIMENSION.width / width, DIMENSION.height / height);
		}
	}

	/**
	 * 
	 * @param dialog	
	 * @param widthRate		宽的百分比
	 * @param heightRate	高的百分比
	 */
	public static void setSize(JDialog dialog, double widthRate, double heightRate) {
		if (dialog != null) {
			dialog.setSize(getWidth(widthRate), getHeight(heightRate));
		}
	}

	/**
	 * 获取屏幕的宽的百分比
	 * @param widthRate
	 * @return
	 */
	public static int getWidth(double widthRate) {
		return Double.valueOf(DIMENSION.width * widthRate).intValue();
	}

	/**
	 * 获取屏幕的高的百分比
	 * @param heightRate
	 * @return
	 */
	public static int getHeight(double heightRate) {
		return Double.valueOf(DIMENSION.height * heightRate).intValue();
	}
	public static void addOneRow(DefaultTableModel tableModel) {
		Vector<Object> vec = new Vector<Object>();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vec.add("");
		}
		tableModel.addRow(vec);
	}

	public static void clearTable(DefaultTableModel tableModel) {
		tableModel.setRowCount(0);
	}

	/**
	 * 创建具有指定红色、绿色和蓝色值的不透明的 sRGB 颜色，这三个颜色值都在 (0.0 - 1.0) 的范围内。
	 *
	 * @param r 红色
	 * @param g 绿色
	 * @param b 蓝色
	 * @return Color
	 */
	public static Color getColor(float r, float g, float b) {
		return new Color(r, g, b);
	}

	/**
	 * 创建具有指定红色、绿色和蓝色值的不透明的 sRGB 颜色，这三个颜色值都在 (0.0 - 1.0) 的范围内。
	 *
	 * @param r 红色
	 * @param g 绿色
	 * @param b 蓝色
	 * @return Color
	 */
	public static Color getColor(String sr, String sg, String sb) {
		float r = Float.valueOf(sr);
		float g = Float.valueOf(sg);
		float b = Float.valueOf(sb);
		return getColor(r, g, b);
	}

	public static Component getFocusComponent(Container container) {
		Component component = null;
		Component[] components = container.getComponents();
		for (Component child : components) {
			if (child.hasFocus()) {
				component = child;
				break;
			}
		}
		return component;
	}

	/**
	 * 获取字体对象
	 *
	 * @param name 字体名称
	 * @param style 字体样式.0:PLAIN,1:BOLD,2:ITALIC.
	 * @param size 字体大小
	 * @return Font 字体对象
	 */
	public static Font getFont(String name, int style, int size) {
		return new Font(name, style, size);
	}

	public static int getTableRowByOid(JTable table, long oid) {
		int row = -1;
		for (int i = 0; i < table.getRowCount(); i++) {
			String rowOid = table.getModel().getValueAt(i, 0).toString();
			if (rowOid.equals(String.valueOf(oid))) {
				row = i;
				break;
			}
		}
		return row;
	}

	/**
	 * 隐藏指定的列
	 *
	 * @param table 列表
	 * @param column 需要隐藏列的索引
	 */
	public static void hiddenCell(JTable table, int column) {
		TableColumn tableColumn = table.getTableHeader().getColumnModel().getColumn(column);
		tableColumn.setMaxWidth(0);
		tableColumn.setPreferredWidth(0);
		tableColumn.setWidth(0);
		tableColumn.setMinWidth(0);
		table.getTableHeader().getColumnModel().getColumn(column).setMaxWidth(0);
		table.getTableHeader().getColumnModel().getColumn(column).setMinWidth(0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void removeSelectedRows(DefaultTableModel tableModel, int[] selectRows) {
		Vector vector = tableModel.getDataVector();
		Vector<Object> selObjects = new Vector<Object>();
		for (int row : selectRows) {
			selObjects.add(vector.elementAt(row));
		}
		vector.removeAll(selObjects);
	}

	public static void setColumnWidth(JTable table, int col, int width) {
		table.getColumnModel().getColumn(col).setMinWidth(width);
		table.getColumnModel().getColumn(col).setPreferredWidth(width);
		table.getColumnModel().getColumn(col).setMaxWidth(width);
	}

	/**
	 * 统一设置字体，父界面设置之后，所有由父界面进入的子界面都不需要再次设置字体
	 *
	 * @param font 字体对象
	 */
	public static void setGlobalFont(Font font) {
		FontUIResource fontRes = new FontUIResource(font);
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, fontRes);
			}
		}
	}

	/**
	 * 统一设置字体，父界面设置之后，所有由父界面进入的子界面都不需要再次设置字体
	 *
	 * @param name 字体名称
	 * @param style 字体样式.0:PLAIN,1:BOLD,2:ITALIC.
	 * @param size 字体大小
	 */
	public static void setGlobalFont(String name, int style, int size) {
		Font font = getFont(name, style, size);
		setGlobalFont(font);
	}

	public static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

//            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//            	logger.debug("InstalledLookAndFeel:"+info.getName());
//                if ("Nimbus".equals(info.getName())) {
//                    UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
        } catch (ClassNotFoundException ex) {
        	logger.error(ex);
        } catch (InstantiationException ex) {
        	logger.error(ex);
        } catch (IllegalAccessException ex) {
        	logger.error(ex);
        } catch (UnsupportedLookAndFeelException ex) {
        	logger.error(ex);
        }
	}

	/**
	 * 使弹出的对话框显示在屏幕中间
	 *
	 * @param dialog 弹出的对话框对象
	 */
	public static void setMiddleOnScreenWithDialog(JDialog dialog){
		int windowWidth = dialog.getWidth();
	    int windowHeight = dialog.getHeight();
	    Toolkit kit = Toolkit.getDefaultToolkit();
	    Dimension screenSize = kit.getScreenSize();
	    int screenWidth = screenSize.width;
	    int screenHeight = screenSize.height;
	    dialog.setLocation(screenWidth/2-windowWidth/2, screenHeight/2-windowHeight/2);
	}

	public static void setMiddleOnScreenWithDialog(JFrame frame){
		int windowWidth = frame.getWidth();
	    int windowHeight = frame.getHeight();
	    Toolkit kit = Toolkit.getDefaultToolkit();
	    Dimension screenSize = kit.getScreenSize();
	    int screenWidth = screenSize.width;
	    int screenHeight = screenSize.height;
	    frame.setLocation(screenWidth/2-windowWidth/2, screenHeight/2-windowHeight/2);
	}

	public static void setTreeNodeSelected(JTree tree, DefaultMutableTreeNode treeNode) {
		if (tree != null && treeNode != null) {
			tree.setSelectionPath(new TreePath(treeNode.getPath()));
		}
	}

	public static void stopTableCellEditing(JTable table) {
		if (table.getCellEditor() != null) {
			table.getCellEditor().stopCellEditing();
		}
	}

	public static JComboBox getWorkCenterComboBox() {
		JComboBox workCenterCombox = null;
		/*List<CmWorkCenter> workCenters = MainFrameHelper.getWorkCenters();
		if (workCenters != null && !workCenters.isEmpty()) {
			String[] workCenterComboValue = new String[workCenters.size()+1];
			workCenterComboValue[0] = "";
			CmWorkCenter cmWorkCenter = null;
			for (int i = 0; i < workCenters.size(); i++) {
				cmWorkCenter = workCenters.get(i);
				workCenterComboValue[i+1] = cmWorkCenter.getName();
			}

			workCenterCombox = new JComboBox(workCenterComboValue);
		} else {
			workCenterCombox = new JComboBox();
		}

		 */
		return workCenterCombox;
	}

	/*public static JComboBox getRiskTypeComboBox() {
		String[] riskTypeComboValue = LoadConfigurations.getInstance(0).getRiskTypeValue();
		JComboBox riskTyperCombox = new JComboBox(riskTypeComboValue);
		return riskTyperCombox;
	}

	public static JComboBox getHandlerComboBox() {
		String[] handlerComboValue = LoadConfigurations.getInstance(0).getHandlerValue();
		JComboBox handlerCombox = new JComboBox(handlerComboValue);
		return handlerCombox;
	}

	public static JComboBox getTechnicsTypeComboBox() {
		Vector<String> technicsTypeValue = new Vector<String>();
		technicsTypeValue.add("");
		technicsTypeValue.addAll(TechnicsTreeProcessor.getAllMPMSkill());
		JComboBox technicsTypeCombox = new JComboBox(technicsTypeValue);
		return technicsTypeCombox;
	}

	public static void clearSearch(JTree tree) {
		XWTreeNode root = (XWTreeNode) tree.getModel().getRoot();
		Enumeration<?> enums = root.preorderEnumeration();
		while (enums.hasMoreElements()) {
			XWTreeNode node = (XWTreeNode) enums.nextElement();
			node.setSearched(false);
		}
	}

	//add by liangbo
	public static String[] getResourceAttribute(String resourceType){
		String[] resourceValue = LoadConfigurations.getInstance(3).getResourceAttribute(resourceType);
		return resourceValue;
	}*/

	public static int showConfirmDialog(Component component, String message)  {
		return showConfirmDialog(component, message, UIManager.getString("OptionPane.titleText"));
	}

	public static int showConfirmDialog(Component component, String message, String title)  {
		return showConfirmDialog(component, message, title, 1);
	}

	public static int showConfirmDialog(Component component, String message, String title, int type)  {
		return showOptionDialog(component, message, title, type, 3, null, null, null);
	}

	public static int showOptionDialog(Component component, String message, String title, int type, int paramInt, Icon paramIcon, Object[] paramArrayOfObject, Object paramObject) {
	    JOptionPane localJOptionPane = new JOptionPane(message, paramInt, type, paramIcon, paramArrayOfObject, paramObject);
	    localJOptionPane.setInitialValue(paramObject);

	    Class[] jOptionPaneClass = new Class[] { Component.class, String.class, int.class };
		try {
			Method createDialog = JOptionPane.class.getDeclaredMethod("createDialog", jOptionPaneClass);
			createDialog.setAccessible(true);
			JDialog localJDialog = (JDialog) createDialog.invoke(localJOptionPane, new Object[] {component, title, -1});
			localJOptionPane.selectInitialValue();
			localJDialog.setAlwaysOnTop(true);
			localJDialog.show();
			localJDialog.dispose();
			Object localObject = localJOptionPane.getValue();
			if (localObject == null)
				return -1;
			if (paramArrayOfObject == null) {
				if (localObject instanceof Integer)
					return ((Integer)localObject).intValue();
				return -1;
			}
			int j = 0;
			int k = paramArrayOfObject.length;
			while (j < k) {
				if (paramArrayOfObject[j].equals(localObject))
					return j;
				++j;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return -1;
	}
	public static void setColumnPrefferredWidth(JTable table, int col, int width) {
//		table.getColumnModel().getColumn(col).setMinWidth(width);
		table.getColumnModel().getColumn(col).setPreferredWidth(width);
	}
	public static void showMessageDialog(Component component, String message) {
		showMessageDialog(component, message, "", 1);
	}

	public static void showMessageDialog(Component component, String message, String title) {
		showMessageDialog(component, message, title, 1);
	}

	public static void showMessageDialog(Component component, String message, String title, int type) {
		JOptionPane optionPane = new JOptionPane(message, type);
		JDialog dialog = null;
		if (component == null) {
			dialog = optionPane.createDialog(title);
		} else {
			dialog = optionPane.createDialog(component, title);
		}
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
	}
}
