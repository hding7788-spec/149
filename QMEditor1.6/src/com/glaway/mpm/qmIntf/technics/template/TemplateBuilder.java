package com.glaway.mpm.qmIntf.technics.template;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.glaway.mpm.qmIntf.technics.TechnicsPreview;
import com.glaway.mpm.qmIntf.technics.entity.Attach;
import com.glaway.mpm.qmIntf.technics.entity.Image;
import com.glaway.mpm.qmIntf.technics.entity.Material;
import com.glaway.mpm.qmIntf.technics.entity.Part;
import com.glaway.mpm.qmIntf.technics.entity.Step;
import com.glaway.mpm.qmIntf.technics.entity.Technics;
import com.glaway.mpm.resource.Constants;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class TemplateBuilder {

	private String techFolder;
	private String wrlNameType = ""; // 装配动画标记
	private String wrlName = "";

	public Template getTemplate(String tempName) throws IOException {
		Configuration cfg = new Configuration();
		// 从目录加载模板
		// cfg.setDirectoryForTemplateLoading(new
		// File(System.getProperty("user.dir") + Constants.FM_TEMPLATES_PATH););

		// 从类路径加载模板
		cfg.setClassForTemplateLoading(getClass(), Constants.FM_TEMPLATES_PATH);

		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setDefaultEncoding("UTF-8");
		Template temp = cfg.getTemplate(tempName);
		temp.setEncoding("UTF-8");
		return temp;
	}

	public boolean makejs(String jstemplate, String data, String buildPath) {
		Writer out = null;
		try {
			int type = buildPath.indexOf(TechnicsPreview.PREVIEW) != -1 ? 0 : 1;
			int technicsType = Constants.FM_TEMPLATE_CREO_JS.equals(jstemplate) ? 0
					: 1;

			Template temp = this.getTemplate(Constants.FM_TEMPLATE_JS);
			Map root = new HashMap();
			root.put("data", data);
			root.put("type", type);
			root.put("technicsType", technicsType);
			File file = new File(buildPath + File.separator
					+ Constants.FM_TEMPLATE_JS);
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			temp.process(root, out);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close(out);
		}
	}

	public boolean makeHTML(String tempName, Technics tech, String buildPath) {
		Writer out = null;
		try {
			Template temp = this.getTemplate(tempName);
			Map root = new HashMap();
			root.put("tech", tech);
			root.put("folderPath", this.techFolder);
			root.put("tables", this.makeTables(tech.getSteps()));

			root.put("fujian", this.generateFj(tech.getSteps()));
			wrlName = tech.getWrlFile();
			wrlName += wrlName == null || wrlName.equals("") ? "" : ".wrl";

			// 如果没有wrl文件，取第一个image文件
			if ("".equals(wrlName)) {
				wrlNameType = "wrl";
				List<Image> lstImage = ((Step) tech.getSteps().get(0))
						.getImages();
				if (lstImage != null && lstImage.size() > 0) {
					for (int i = 0; i < lstImage.size(); i++) {
						wrlName = lstImage.get(0).getDrawingName();
					}
				}
			}

			root.put("wrlName", wrlName);
			root.put("modelList", this.makeModelList(tech.getSteps(), 1));

			File file = new File(buildPath + File.separator
					+ Constants.FM_BUILD_CORTONA_HTML);
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			temp.process(root, out);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close(out);
		}
	}

	public boolean makeHTMLTable(String tempName, Technics tech,
			String buildPath) {
		Writer out = null;
		try {
			Template temp = this.getTemplate(tempName);
			Map root = new HashMap();
			root.put("tech", tech);
			root.put("tables", this.makeTables(tech.getSteps()));
			root.put("dataTable", this.makeDataTable(tech.getSteps()));
			File file = new File(buildPath + File.separator
					+ Constants.FM_BUILD_CORTONA_HTML);
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			temp.process(root, out);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close(out);
		}
	}

	public boolean makeHTMLCreo(String tempName, Technics tech, String buildPath) {
		Writer out = null;
		try {
			Template temp = this.getTemplate(tempName);
			Map root = new HashMap();
			root.put("tech", tech);
			root.put("folderPath", this.techFolder);

			root.put("modelList", this.makeModelList(tech.getSteps(), 0));
			root.put("fujian", this.generateFj(tech.getSteps()));

			String olName = "";
			try {
				List<Image> lstImage = ((Step) tech.getSteps().get(0))
						.getImages();
				if (lstImage != null && lstImage.size() > 0) {
					for (int i = 0; i < lstImage.size(); i++) {
						olName = lstImage.get(0).getDrawingName() + "."
								+ lstImage.get(0).getDrawingType();
					}
				}
			} catch (Exception e) {
				olName = "";
			}
			root.put("olName", olName);

			List mList = tech.getMaterials();

			Material m = null;
			if (mList != null && mList.size() > 0) {
				m = (Material) mList.get(0);
			} else {
				m = new Material();
			}
			root.put("material", m);

			// Step firstStep = (Step) tech.getSteps().get(0);
			// root.put("defaultModel", firstStep.getCreoView().split(";")[0]);

			root.put("defaultModel", "");

			File file = new File(buildPath + File.separator
					+ Constants.FM_BUILD_CREO_HTML);
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			temp.process(root, out);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close(out);
		}
	}

	private String makeDataTable(List steps) {
		StringBuilder data = new StringBuilder();
		data.append("<table  id='stepsTab' width='100%'  cellpadding='0' cellspacing='0' >");
		data.append("<thead> <th>车间</th><th>工序</th><th>工步</th> <th>内容描述</th>  <th>工种</th><th>关键工序标识</th> <th>设备，工装及工具</th> </thead>");
		for (Object object : steps) {
			if (object instanceof Step) {
				Step step = (Step) object;
				String tStep = "<tr><td rowspan=" + step.getSubSteps().size()
						+ ">" + step.getWorkShop() + "</td><td rowspan="
						+ step.getSubSteps().size() + ">"
						+ step.getStepNumber() + step.getStepName() + "</td>";

				if (step.getSubSteps() != null && step.getSubSteps().size() > 0) {
					for (Object obj : step.getSubSteps()) {
						if (obj instanceof Step) {
							Step sub = (Step) obj;
							String tSub = "<td>"
									+ sub.getStepNumber()
									+ sub.getStepName()
									+ "</td><td><applet name='spechar' code='com.faw_qm.speChar.view.TextApplet.class' width='380px' height='100px' ><param name=''  value='' default=''/></applet></td><td>"
									+ sub.getWorkType() + "</td><td>"
									+ sub.getIsKey() + "</td><td></td><tr>";
							if (sub == step.getSubSteps().get(0)) {
								data.append(tStep).append(tSub);
							} else {
								data.append(tSub);
							}

						}
					}
				}
			}
		}
		data.append("</table>");

		return data.toString();
	}

	private String makeTables(List steps) {
		StringBuilder tables = new StringBuilder();
		for (Object object : steps) {
			if (object instanceof Step) {
				StringBuilder table = new StringBuilder();
				Step step = (Step) object;
				if (step.getParts() != null && step.getParts().size() > 0) {
					table.append("<table  id='part"
							+ step.getStepNumber()
							+ "' width='100%' class='tb_part_info' cellpadding='0' cellspacing='0' style='display:none'>");
					table.append("<thead> <th>代号</th> <th>名称</th> <th>材料</th> <th>镀涂</th> <th>数量</th> <th>备注</th> </thead>");
					for (Object obj : step.getParts()) {
						if (obj instanceof Part) {
							Part part = (Part) obj;
							String tbody = "<tr><td>" + part.getPartNumber()
									+ "</td><td>" + part.getPartName()
									+ "</td><td>" + part.getMaterial()
									+ "</td><td>" + part.getDutu()
									+ "</td><td>" + part.getUseCount()
									+ "</td><td>" + part.getRemark()
									+ "</td></tr>";
							table.append(tbody);
						}
					}
					table.append("</table>");
				}
				tables.append(table);
			}
		}
		return tables.toString();
	}

	private int index = 0;

	private String makeModelList(List steps, int imgType) {
		StringBuilder strLists = new StringBuilder();

		for (Object object : steps) {
			if (object instanceof Step) {
				Step step = (Step) object;
				generateUL(step, strLists, imgType);
				List<Step> list = step.getSubSteps();
				if (list != null)
					for (Step s : list) {
						generateUL(s, strLists, imgType);
					}
			}
		}

		return strLists.toString();
	}

	private void generateUL(Step step, StringBuilder strLists, int imgType) {
		StringBuilder strList = new StringBuilder();
		String modelNames = step.getCreoView();
		List<Image> imageList = step.getImages();
		String model[] = modelNames.split(";");

		if (model.length > 0 || (imageList != null && imageList.size() > 0)) {

			// strList.append("<ul class='part_nav' " +
			// (steps.get(0).equals(step) ? "" : " style='display:none'") +
			// "  id='part_model"
			// + step.getStepNumber() + "'>");

			strList.append("<span class='part_nav' "
					+ (index++ == 0 ? "" : "style='display:none'")
					+ " id='part_model" + step.getOid() + "'>");
			if (imgType == 1) {
				if (!"".equals(wrlName)) {
					if ("".equals(wrlNameType)) {
						strList.append("<a  class='select'  onclick=\"changeModel(this,'cortona','cor')\" style='cursor: pointer; color: blue;'>装配动画</a>，");
					}
				} else {
					strList.append("<a  class='select'  onclick=\"changeModel(this,'cortona','cor')\" style='cursor: pointer; color: blue;'></a>");
				}
			}
			int i;
			for (i = 0; i < model.length; i++) {
				String m = model[i];
				if ("".equals(m)) {
					continue;
				}
				strList.append("<a "
						+ (imgType == 0 && i == 0 ? " class='select' " : "")
						+ " onclick=\"changeModel(this,'"
						+ m
						+ "','pview') \" style='cursor: pointer; color: blue;'>"
						+ model[i].substring(model[i].indexOf("/") + 1)
						+ "</a>，");
			}

			for (int j = i; j < imageList.size() + i; j++) {
				String type = imageList.get(j - i).getDrawingType();
				String t = "pview";
				if ("png".equalsIgnoreCase(type)
						|| "jpg".equalsIgnoreCase(type)
						|| "gif".equalsIgnoreCase(type))
					t = "image";

				String name = imageList.get(j - i).getDrawingName() + "."
						+ imageList.get(j - i).getDrawingType();
				if ("".equals(name)) {
					continue;
				}
				strList.append("<a " + (j == 0 ? " class='select' " : "")
						+ " onclick=\"changeModel(this,'"
						+ imageList.get(j - i).getDrawingLink() + "','" + t
						+ "')\" style='cursor: pointer; color: blue;'>" + name
						+ "</a>，");
			}

			if (strList.charAt(strList.length() - 1) == '，')
				strList.deleteCharAt(strList.length() - 1);

			strList.append("</span>");
		}
		strLists.append(strList);
	}

	private String generateFj(List<Step> steps) {
		StringBuilder sb = new StringBuilder();
		if (steps != null) {
			boolean isFirst = true;
			for (Step step : steps) {
				List<Attach> attachs = step.getAttachs();
				if (attachs != null) {
					String style = "";
					if (isFirst) {
						isFirst = false;
					} else {
						style += "style='display: none;'";
					}
					sb.append("<div id='" + step.getOid() + "_fj' " + style
							+ ">");
					int i = 0;
					for (Attach a : attachs) {
						sb.append("<font style='color: blue;cursor: pointer;' onclick='window.open(\""
								+ a.getPath()
								+ "\")'>"
								+ a.getName()
								+ "."
								+ a.getType() + "</font>，");
					}

					if (sb.charAt(sb.length() - 1) == '，')
						sb.deleteCharAt(sb.length() - 1);

					sb.append("</div>");
				}
			}
		}
		return sb.toString();
	}

	private String makeCortonaList(Technics tech) {
		StringBuilder strLists = new StringBuilder();
		strLists.append("<ul class='part_nav'  id='cortona_list'>");

		strLists.append("<li  class='select'  onclick=\"changeModel(this,'cortona','cor')\">装配动画</li>");

		List imageList = tech.getImages();

		if (imageList != null && imageList.size() > 0) {
			for (Object object : imageList) {
				if (object instanceof Image) {
					Image image = (Image) object;
					strLists.append("<li  onclick=\"changeModel(this,'"
							+ image.getDrawingLink() + "','"
							+ image.getDrawingType() + "')\">"
							+ image.getDrawingName() + "</li>");
				}

			}
		}
		strLists.append("</ul>");
		return strLists.toString();
	}

	/**
	 * 根据模板生成html和js
	 */
	public boolean makeTemplate(Technics t, String buildPath, String tFolder) {
		this.techFolder = tFolder;
		if ("装配工艺".equals(t.getTechnicsType())) {
			makeHTML(Constants.FM_TEMPLATE_CORTONA_HTML, t, buildPath);
			// makeHTMLTable("templateCortona_fixedTable.html", t, buildPath);
			makejs(Constants.FM_TEMPLATE_CORTONA_JS, t.toJSONString(),
					buildPath);
			Constants.PUBLISH_HTML = Constants.FM_BUILD_CORTONA_HTML;
		} else if ("零件工艺".equals(t.getTechnicsType())) {
			makeHTMLCreo(Constants.FM_TEMPLATE_CREO_HTML, t, buildPath);
			makejs(Constants.FM_TEMPLATE_CREO_JS, t.toJSONString(), buildPath);
			Constants.PUBLISH_HTML = Constants.FM_BUILD_CREO_HTML;
		}
		return false;
	}

	/**
	 * 关闭流
	 */
	private void close(Writer out) {
		try {
			if (out != null) {
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 简图panel
	 * 
	 * @param tempName
	 * @param path
	 * @param root
	 * @return
	 */
	public boolean makeCreoPanel(String tempName, String path, Map root,String htmlPath) {
		try {
			Template temp = this.getTemplate(tempName);

			File file = new File(path + File.separator
					+ htmlPath);
			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));

			temp.process(root, out);
			out.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
