package com.glaway.mpm.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import com.faw_qm.speChar.speChar.SpeClassUtil;
import com.faw_qm.speChar.speChar.SpeIcon;
import com.faw_qm.speChar.view.MyUtilities;

public class SpecialCharUtil {
	private static JButton btn = new JButton();

	public static void generateImage(String s, int width, String filename) {
		if ((s == null) || (s.trim().length() == 0))
			return;
		if (width < 1)
			return;
		if ((filename == null) || (filename.trim().length() == 0)) {
			return;
		}
		Font useFont = new Font("Dialog", 0, 14);
		BufferedImage img = null;
		FontMetrics metrics = btn.getFontMetrics(useFont);
		ArrayList textList = new ArrayList();
		ArrayList LineList = SpeClassUtil.splitContent(s, useFont);

		ArrayList lineEnds = MyUtilities.splitString(LineList, width, metrics);

		ArrayList result = new ArrayList();
		if ((lineEnds == null) || (lineEnds.size() == 0)) {
			textList.add("");
		}

		ArrayList list = MyUtilities.getLines(LineList, lineEnds);

		if ((list != null) && (list.size() > 0)) {
			int height = (metrics.getHeight() + 5) * list.size() + 5;

			img = new BufferedImage(width, height, 5);
			Graphics2D g = img.createGraphics();
			g.setFont(useFont);
			FontMetrics fm = g.getFontMetrics();
			g.setBackground(Color.white);
			g.clearRect(0, 0, width + 10, height + 10);
			int x = 0;
			int y = 0;
			for (int m = 0; m < list.size(); m++) {
				g.setColor(Color.black);
				Font temp = new Font("Dialog", 0, 14);
				g.setFont(temp);
				ArrayList sub = (ArrayList) list.get(m);
				x = 3;
				if (m != 0) {
					y += fm.getHeight() + 2;
				}

				for (int j = 0; j < sub.size(); j++) {
					Object o = sub.get(j);
					if (o != null) {
						if ((o instanceof String)) {
							int ty = y + fm.getHeight();
							g.drawString(o.toString(), x, ty);
							x += fm.stringWidth(o.toString());
						}

						if ((o instanceof SpeIcon)) {
							int ty = y + 5;
							SpeIcon icon = (SpeIcon) o;
							icon.getShape(g, x, ty);
							x = icon.getIconWidth() + x;
						}
					}
				}
			}
		}

		try {
			if (img != null) {
				File f = new File(filename);
				ImageIO.write(img, "jpg", f);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		double a1 = System.currentTimeMillis();
		String s = "的广泛的开个房ЩЯЭ2TX007ζSHж4жЮζSHж5/□6жЮζЩЯЭ现场说法ЩЯЭ3TC010ж5жЩЯЭ\n双方开始的5ЩЯЭ5HXж+4жж-5жЩЯЭ\n仨ЩЯЭ1TX006ζFAЮSHж3жЮTBжMжЮTBжLжЮζFAЮSHж4жЮTBжLжЮTBжSжЮζSHж5жЮTBжLжЮζSHж6жЮTBжMжЮζSHж7жЮTBжPжЮζЩЯЭ\n所得税减肥\n的放得开更符合司法局的挥洒离开国家的离开国际法放得开给甲方可怜的环境法律\n宋德福\n宋德福sdgsg\n神鼎飞丹砂";

		String text = "sadfnsa\ndmfnЩЯЭ2TX003ζSHж5жЮζSHж6/□7жЮζЩЯЭasdfЩЯЭ1TX001ζFAЮSHж34жЮTBжMжЮζFAЮSHж34жЮζЩЯЭ";
		for (int i = 0; i < 100; i++) {
			generateImage(s, 500, "c:/image/" + i + ".jpg");
		}
		double a2 = System.currentTimeMillis();
		System.out.println("时间==========" + (a2 - a1) / 1000.0D);
	}
}
