package com.glaway.speciaword.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/***
 * freemarker SVG 杞崲涓篜NG
 * 
 * @author MosesX
 * @2013-4-27
 * 
 * 
 *            璁捐涓哄崟渚�
 */
public class SvgTranscoderToPng {

	// private static final String ROOT_PATH =
	// SvgTranscoderToPng.class.getClass()
	// .getResource("/com/glaway/speciaword/resource/").getPath().substring(1);//
	// System.getProperty("user.dir") +
	// "\\src\\com\\glaway\\speciaword\\resource";

	private static Configuration config = null;

	static {
		try {
			config = new Configuration();
			// config.setDirectoryForTemplateLoading(new File(ROOT_PATH +
			// "\\templates"));
			config.setObjectWrapper(new DefaultObjectWrapper());
			config.setDefaultEncoding("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static SvgTranscoderToPng instance = null;

	private SvgTranscoderToPng() {
	}

	public static SvgTranscoderToPng getInstance() {
		if (instance == null) {
			synchronized (SvgTranscoderToPng.class) {
				if (instance == null) {
					instance = new SvgTranscoderToPng();
				}
			}
		}

		return instance;
	}

	public Template getTemplate(String svgFileName) {
		try {
			// Configuration cfg = new Configuration();
			Template temp = config.getTemplate(svgFileName + ".svg");
			temp.setEncoding("UTF-8");
			return temp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Template getTemplateResource(String type, String name) {
		try {
			// Configuration cfg = new Configuration();
			ClassTemplateLoader ctLoader = new SWClassTemplateLoader(type);
			config.setTemplateLoader(ctLoader);
			// config.setObjectWrapper(new DefaultObjectWrapper());
			// config.setDefaultEncoding("UTF-8");
			Template temp = config.getTemplate(name + ".svg");

			temp.setEncoding("UTF-8");
			return temp;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// @SuppressWarnings("rawtypes")
	// public boolean make(String svgFileName, Map root, String imageName) {

	// try {
	// Template temp = this.getTemplate(svgFileName);
	//
	// Writer out = new StringWriter();
	// temp.process(root, out);
	// byte[] bytes = out.toString().getBytes();
	// ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
	// TranscoderInput input = new TranscoderInput(bis);
	// PNGTranscoder transcoder = new PNGTranscoder();
	// transcoder.addTranscodingHint(PNGTranscoder.KEY_GAMMA, new Float(.8));
	//
	// OutputStream ostream = null;
	// ostream = new FileOutputStream(new File(ROOT_PATH + "\\png\\" + imageName
	// + ".png"));
	// TranscoderOutput output = new TranscoderOutput(ostream);
	//
	// transcoder.transcode(input, output);
	//
	// ostream.flush();
	// ostream.close();
	// return true;
	// } catch (Exception e) {
	// e.printStackTrace();
	// return false;
	// }

	// }

	@SuppressWarnings("rawtypes")
	public BufferedImage makeImage(String type, String name, Map root) {
		BufferedImage image = null;
		try {
			Template temp = this.getTemplateResource(type, name);
			Writer out = new StringWriter();
			temp.process(root, out);

			byte[] b = out.toString().getBytes("utf-8");
			TranscoderInput input = new TranscoderInput(
					new ByteArrayInputStream(b));

			PNGTranscoder transcoder = new PNGTranscoder();
			// transcoder.addTranscodingHint(PNGTranscoder.KEY_GAMMA, new
			// Float(.8));
			transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(
					25));

			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			TranscoderOutput output = new TranscoderOutput(ostream);
			transcoder.transcode(input, output);

			ostream.flush();
			ostream.close();
			ByteArrayInputStream inputStream = new ByteArrayInputStream(
					ostream.toByteArray());
			image = ImageIO.read(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return image;

	}

}
