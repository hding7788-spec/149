package com.glaway.mpm.erp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public abstract class AbstractERPDialog extends JDialog {

	private volatile boolean executorActive;
	public Window owner;

	public AbstractERPDialog() {
		super();
		this.owner = owner;
		initDialog();
	}

	public AbstractERPDialog(Window owner) {
		super(owner);
		this.owner = owner;
		initDialog();
	}

	public AbstractERPDialog(Window owner, String title) {
		super(owner, title);
		this.owner = owner;
		setTitle(title);
		initDialog();
	}

	public AbstractERPDialog(Window owner, String title, boolean modal) {
		super(owner, title);
		this.setModal(modal);
		initDialog();
	}

	private ERPTitle titleComponent;
	private JPanel contentPanel = new JPanel();
	private Container content = null;

	public void setContentPane(Container container) {
		if (container == contentPanel) {
			super.setContentPane(container);
		} else {
			if (content != null) {
				contentPanel.remove(content);
			}
			content = container;
			contentPanel.add(content, BorderLayout.CENTER);
		}

	}

	public Container getContentPane() {
		return content;
	}

	public void setTitle(String title) {
		super.setTitle(title);
		if (titleComponent != null) {
			titleComponent.setTitle(title);
		}
	}

	public void setIconImage(Image image) {
		super.setIconImage(image);
		if (titleComponent != null) {
			titleComponent.setIcon(image);
		}
	}

	public void setUndecorated(boolean undecorated) {
		super.setUndecorated(true);
	}

	private void initDialog() {
		titleComponent = new ERPTitle(this);
		contentPanel.setBorder(new ERPTitleBorder());
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(titleComponent, BorderLayout.NORTH);
		setContentPane(contentPanel);
		setTitle(getTitle());
		this.setUndecorated(true);
		this.setResizable(true);
		initResizeListener();
	}

	public void setResizable(boolean resizable) {
		super.setResizable(false);
		contentPanel.setCursor(Cursor.getDefaultCursor());
		titleComponent.setResizable(resizable);
	}

	private boolean isInLeft(Point p) {
		Rectangle rect = contentPanel.getBounds();
		if (p.getX() > rect.getX() && p.getX() < rect.getX() + 5) {
			return true;
		}
		return false;
	}

	private boolean isInRight(Point p) {
		Rectangle rect = contentPanel.getBounds();
		if (p.getX() < rect.getX() + rect.getWidth()
				&& p.getX() > rect.getX() + rect.getWidth() - 5) {
			return true;
		}
		return false;
	}

	private boolean isInTop(Point p) {
		Rectangle rect = contentPanel.getBounds();
		if (p.getY() > rect.getY() && p.getY() < rect.getY() + 5) {
			return true;
		}
		return false;
	}

	private boolean isInBottom(Point p) {
		Rectangle rect = contentPanel.getBounds();
		if (p.getY() < rect.getY() + rect.getHeight()
				&& p.getY() > rect.getY() + rect.getHeight() - 5) {
			return true;
		}
		return false;
	}

	private Cursor getResizeCursor(Point p) {
		boolean l = isInLeft(p);
		boolean r = isInRight(p);
		boolean t = isInTop(p);
		boolean b = isInBottom(p);
		if (l) {
			if (t) {
				return Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
			} else if (b) {
				return Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
			}
			return Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
		} else if (r) {
			if (t) {
				return Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
			} else if (b) {
				return Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
			}
			return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
		} else if (b) {
			return Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
		} else if (t) {
			return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		}

		return Cursor.getDefaultCursor();
	}

	private Point pressPoint;

	private void resizeFrame(Point dragPoint) {
		double minWidth = 100;
		int minHeight = titleComponent.getHeight() + 10;
		if (contentPanel.getCursor() == Cursor
				.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)) {
			int offset = dragPoint.y - pressPoint.y;
			int nW = this.getWidth();
			int nH = this.getHeight() - offset;
			if (nH <= minHeight || nW < minWidth) {
				return;
			}
			this.setLocation(this.getX(), this.getY() + offset);
			this.setSize(nW, nH);
		} else if (contentPanel.getCursor() == Cursor
				.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) {
			int offset = dragPoint.x - pressPoint.x;
			int nW = this.getWidth() + offset;
			int nH = this.getHeight();
			if (nH <= minHeight || nW < minWidth) {
				return;
			}
			pressPoint.x += offset;
			this.setSize(nW, nH);
		} else if (contentPanel.getCursor() == Cursor
				.getPredefinedCursor(Cursor.S_RESIZE_CURSOR)) {
			int offset = dragPoint.y - pressPoint.y;
			int nW = this.getWidth();
			int nH = this.getHeight() + offset;
			if (nH <= minHeight || nW < minWidth) {
				return;
			}
			pressPoint.y += offset;
			this.setSize(nW, nH);
		} else if (contentPanel.getCursor() == Cursor
				.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)) {
			int offset = dragPoint.x - pressPoint.x;
			// pressPoint.x += offset;
			int nW = this.getWidth() - offset;
			int nH = this.getHeight();
			if (nH <= minHeight || nW < minWidth) {
				return;
			}
			this.setBounds(this.getX() + offset, this.getY(), nW, nH);
		} else if (contentPanel.getCursor() == Cursor
				.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR)) {
			int xoffset = dragPoint.x - pressPoint.x;
			int yoffset = dragPoint.y - pressPoint.y;
			int nW = this.getWidth() + xoffset;
			int nH = this.getHeight() - yoffset;
			if (nH <= minHeight || nW < minWidth) {
				return;
			}
			pressPoint.x += xoffset;
			this.setLocation(this.getX(), this.getY() + yoffset);
			this.setSize(nW, nH);
		} else if (contentPanel.getCursor() == Cursor
				.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR)) {
			int xoffset = dragPoint.x - pressPoint.x;
			int yoffset = dragPoint.y - pressPoint.y;

			int nW = this.getWidth() + xoffset;
			int nH = this.getHeight() + yoffset;
			if (nH <= minHeight || nW < minWidth) {
				return;
			}
			pressPoint.x += xoffset;
			pressPoint.y += yoffset;
			this.setLocation(this.getX(), this.getY());
			this.setSize(nW, nH);
		} else if (contentPanel.getCursor() == Cursor
				.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR)) {
			int xoffset = dragPoint.x - pressPoint.x;
			int yoffset = dragPoint.y - pressPoint.y;

			int nW = this.getWidth() - xoffset;
			int nH = this.getHeight() + yoffset;
			if (nH <= minHeight || nW < minWidth) {
				return;
			}
			pressPoint.y += yoffset;
			this.setLocation(this.getX() + xoffset, this.getY());
			this.setSize(nW, nH);
		} else if (contentPanel.getCursor() == Cursor
				.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR)) {
			int xoffset = dragPoint.x - pressPoint.x;
			int yoffset = dragPoint.y - pressPoint.y;
			int nW = this.getWidth() - xoffset;
			int nH = this.getHeight() - yoffset;
			if (nH <= minHeight || nW < minWidth) {
				return;
			}
			this.setLocation(this.getX() + xoffset, this.getY() + yoffset);
			this.setSize(nW, nH);
		}
		this.validate();
	}

	private void initResizeListener() {
		contentPanel.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				if (titleComponent.isMaxed() || isResizable()) {
					return;
				}
				Point point = e.getPoint();
				Cursor cursor = getCursor();
				Cursor resizeCursor = getResizeCursor(point);
				if (cursor != resizeCursor) {
					contentPanel.setCursor(resizeCursor);
				}
			}

			public void mouseDragged(MouseEvent e) {
				if (titleComponent.isMaxed() || isResizable()) {
					return;
				}
				resizeFrame(e.getPoint());
			};
		});
		contentPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				pressPoint = e.getPoint();
			}

			public void mouseExited(MouseEvent e) {
				content.setCursor(Cursor.getDefaultCursor());
			}
		});
	}

	public ERPTitle getTitleComponent() {
		return titleComponent;
	}

	public boolean isExecutorActive() {
		return this.executorActive;
	}

	public void setExecutorActive(boolean executorActive) {
		this.executorActive = executorActive;
	}

	protected void initDimension() {

		// Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		//
		// this.setBounds(owner.getX(), owner.getY(), owner.getWidth(),
		// owner.getHeight());
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screen.width > 1100 ? 1000 : 800;
		int height = 700;
		String maximized = "";

		if (maximized.equals("MAIN_MAXIMIZED")) {
			setBounds(0, 0, screen.width, screen.height);
		} else {
			int ancleft = (screen.width - width) / 2;
			int anctop = (screen.height - height) / 2;

			setBounds(ancleft, anctop, width, height);
		}
	}

	protected abstract void initActions();

	protected abstract void initComponents();

	protected abstract void initLayout();

	protected abstract void loadInitDatas();

}
