package com.glaway.mpm.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.dom4j.Element;



public class ProcedureFlowJDialog extends JDialog {
	private final int A4_WIDTH = 842;
	private final int A4_HEIGHT = 595;
	private final int A3_WIDTH = 1190;
	private final int A3_HEIGHT = 842;

	// 水平边距
	private final int HORIZONTAL_DISTANCE = 40;
	// 垂直边距
	private final int VERTICAL_DISTANCE = 30;
	// 矩形框长度
	private final int RECTANGLE_WIDTH = 90;
	// 矩形框宽度
	private final int RECTANGLE_HEIGHT = 55;
	// 折线宽度
	private final int LINE_DISTANCE = 15;
	// 矩形框水平方向之间距离
	private final int RECTANGLE_HORIZONTAL_DISTANCE = 30;
	// 矩形框垂直方向之间距离
	private final int RECTANGLE_VERTICAL_DISTANCE = 45;

	// 每行矩形框个数
	private int countPerRow;
	// 窗口宽度
	private int windowWidth = A4_WIDTH;

	// 流程图画板
	private ProcedureFlowPanel panel;
	// 父窗口
	private NewTechnicsPart parent;
	private List procedureList;
	private Vector rectangleUnitVector;
	private Vector lineUnitVector;

	private JPopupMenu popMenu = new JPopupMenu();
	private JMenuItem saveAs = new JMenuItem("另存为...");

	public ProcedureFlowJDialog(NewTechnicsPart parent, List procedureList,
			String techNumber) {
		super(parent, true);
		this.parent = parent;
		this.procedureList = procedureList;
		setTitle(new StringBuffer("工艺").append(techNumber).append("的工序流程图")
				.toString());
		// setModal(true);

		panel = new ProcedureFlowPanel(this);
		Container container = getContentPane();
		container.setBackground(Color.white);
		container.add(new JScrollPane(panel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));

		addComponentListener(new WindowResizedListener(this));

		panel.add(popMenu);
		popMenu.add(saveAs);
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					createComponentImage();
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			}
		});

		panel.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension.getWidth() - A4_WIDTH) / 2,
				(int) (dimension.getHeight() - A4_HEIGHT) / 2, A4_WIDTH,
				A4_HEIGHT);
		setMinimumSize(new Dimension(350, 0));
		setVisible(true);
	}

	private void createComponentImage() throws Exception {
		String filePath = null;
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (file == null)
				return;
			filePath = file.getAbsolutePath();
			if (filePath == null || filePath.trim().equals(""))
				return;
			if (!filePath.toLowerCase().endsWith(".jpg"))
				filePath = filePath + ".jpg";
		}
		int panelHeight = panel.setComponentPreferredSize();
		BufferedImage comImage = (BufferedImage) panel.createImage(
				panel.getWidth(), panelHeight);
		Graphics og = comImage.getGraphics();
		og.setClip(0, 0, panel.getWidth(), panelHeight);
		panel.paint(og);
		FileOutputStream output = new FileOutputStream(filePath);
		javax.imageio.ImageIO.write(comImage, "jpg", output);
		output.close();
		JOptionPane.showMessageDialog(this, "生成图片成功！", "提示",
				JOptionPane.INFORMATION_MESSAGE);
	}

	class ProcedureFlowPanel extends JPanel {
		private ProcedureFlowJDialog dialog;

		public ProcedureFlowPanel(ProcedureFlowJDialog dialog) {
			this.dialog = dialog;
			setBackground(Color.white);
			setLayout(null);
			windowAdjusting();
		}

		private void windowAdjusting() {
			windowWidth = dialog.getWidth();
			if (windowWidth == 0)
				windowWidth = A4_WIDTH;
			countPerRow = (windowWidth - HORIZONTAL_DISTANCE * 2)
					/ (RECTANGLE_WIDTH + RECTANGLE_HORIZONTAL_DISTANCE);
			rectangleUnitVector = getProcedureRectangles(procedureList);
			lineUnitVector = getLines();
			setComponentPreferredSize();
			repaint();
			updateUI();
		}

		private int setComponentPreferredSize() {
			int row = (int) Math.ceil((double) rectangleUnitVector.size()
					/ (double) countPerRow);
			int drawHeight = VERTICAL_DISTANCE * 2 + row * RECTANGLE_HEIGHT
					+ (row - 1) * RECTANGLE_VERTICAL_DISTANCE;
			setPreferredSize(new Dimension(windowWidth, drawHeight));
			return drawHeight;
		}

		// private int getVeticalPoint(List procedureList)
		// {
		// int row =
		// (int)Math.ceil((double)procedureList.size()/(double)COUNT_PER_ROW);
		// return
		// (A4_HEIGHT-RECTANGLE_HEIGHT*row-RECTANGLE_VERTICAL_DISTANCE*(row-1))/2;
		// }

		private Vector getProcedureRectangles(List procedureList) {
			Vector vector = new Vector();
			int i = 0;
			// int verticalPoint = getVeticalPoint(procedureList);
			for (Iterator<Element> it = procedureList.iterator(); it.hasNext();) {
				Element element = it.next();
				int leftDistance = (int) ((windowWidth - countPerRow
						* RECTANGLE_WIDTH - (countPerRow - 1)
						* RECTANGLE_HORIZONTAL_DISTANCE) / 2);
				int x = leftDistance + i % countPerRow
						* (RECTANGLE_WIDTH + RECTANGLE_HORIZONTAL_DISTANCE);
				int y = VERTICAL_DISTANCE + i / countPerRow
						* (RECTANGLE_HEIGHT + RECTANGLE_VERTICAL_DISTANCE);
				String assemblageDept = element.attributeValue("workShop");
				String number = element.attributeValue("stepNumber");
				String name = element.attributeValue("stepName");
				ProcedureRectangleUnit unit = new ProcedureRectangleUnit(x, y,
						RECTANGLE_WIDTH, RECTANGLE_HEIGHT, assemblageDept,
						number, name);
				vector.add(unit);
				i++;
			}
			return vector;
		}

		private Vector getLines() {
			Vector vector = new Vector();
			if (rectangleUnitVector != null) {
				if (rectangleUnitVector.size() > 1) {
					for (int i = 0; i < rectangleUnitVector.size() - 1; i++) {
						ProcedureRectangleUnit unit1 = (ProcedureRectangleUnit) rectangleUnitVector
								.get(i);
						ProcedureRectangleUnit unit2 = (ProcedureRectangleUnit) rectangleUnitVector
								.get(i + 1);
						// 每行最后一个元素
						if (i != 0 && (i + 1) % countPerRow == 0) {
							int[] xPoints = {
									unit1.getX() + RECTANGLE_WIDTH,
									unit1.getX() + RECTANGLE_WIDTH
											+ LINE_DISTANCE,
									unit1.getX() + RECTANGLE_WIDTH
											+ LINE_DISTANCE,
									unit2.getX() - LINE_DISTANCE,
									unit2.getX() - LINE_DISTANCE, unit2.getX() };
							int[] yPoints = {
									unit1.getY() + RECTANGLE_HEIGHT / 2,
									unit1.getY() + RECTANGLE_HEIGHT / 2,
									(unit1.getY() + RECTANGLE_HEIGHT + unit2
											.getY()) / 2,
									(unit1.getY() + RECTANGLE_HEIGHT + unit2
											.getY()) / 2,
									unit2.getY() + RECTANGLE_HEIGHT / 2,
									unit2.getY() + RECTANGLE_HEIGHT / 2 };
							LineUnit unit = new LineUnit(xPoints, yPoints);
							vector.add(unit);
						}
						// 每行非最后一个元素
						else {
							int[] xPoints = { unit1.getX() + RECTANGLE_WIDTH,
									unit2.getX() };
							int[] yPoints = {
									unit1.getY() + RECTANGLE_HEIGHT / 2,
									unit2.getY() + RECTANGLE_HEIGHT / 2 };
							LineUnit unit = new LineUnit(xPoints, yPoints);
							vector.add(unit);
						}
					}
				}
			}
			return vector;
		}

		public void paint(Graphics g) {
			super.paint(g);
			drawRectangles(g);
			drawLines(g);
		}

		private void drawRectangles(Graphics g) {
			if (rectangleUnitVector != null) {
				for (Iterator<ProcedureRectangleUnit> it = rectangleUnitVector
						.iterator(); it.hasNext();) {
					ProcedureRectangleUnit unit = it.next();
					unit.drawSelf(g);
				}
			}
		}

		private void drawLines(Graphics g) {
			if (lineUnitVector != null) {
				for (Iterator<LineUnit> it = lineUnitVector.iterator(); it
						.hasNext();) {
					LineUnit unit = it.next();
					unit.drawSelf(g);
				}
			}
		}
	}

	class WindowResizedListener extends ComponentAdapter {
		private ProcedureFlowJDialog dialog;

		public WindowResizedListener(ProcedureFlowJDialog dialog) {
			this.dialog = dialog;
		}

		public void componentResized(ComponentEvent e) {
			dialog.panel.windowAdjusting();
		}
	}
}