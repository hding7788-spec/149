package com.glaway.mpm.pdf;

import gui.ava.html.image.generator.HtmlImageGenerator;

public class HtmlImageGeneratorFactory {
    private static HtmlImageGenerator instance;
    public static HtmlImageGenerator getInstance(){
        if (instance == null) {
            // 双重检查锁定，确保线程安全
            synchronized (HtmlImageGenerator.class) {
                if (instance == null) {
                    instance = new HtmlImageGenerator();
                }
            }
        }
        return instance;
    }
}
