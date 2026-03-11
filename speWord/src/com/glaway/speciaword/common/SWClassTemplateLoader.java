package com.glaway.speciaword.common;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import freemarker.cache.ClassTemplateLoader;

public class SWClassTemplateLoader extends ClassTemplateLoader {
	private ClassLoader classloader;

	private String type;

	public SWClassTemplateLoader(String type) {
		this.type = type;
		classloader = this.getClass().getClassLoader();
	}

	@Override
	protected URL getURL(String name) {
		return classloader
				.getResource("com/glaway/speciaword/resource/templates/" + type
						+ "/" + name);
	}

	public BufferedImage loadImage(String name) {
		BufferedImage image = null;

		try {
			InputStream is = classloader
					.getResourceAsStream("com/glaway/speciaword/resource/templates/"
							+ type + "/" + name);

			image = ImageIO.read(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}
}
