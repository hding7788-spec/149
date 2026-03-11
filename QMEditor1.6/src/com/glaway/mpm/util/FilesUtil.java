package com.glaway.mpm.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.glaway.mpm.visual.view.action.VaActionProgressBar;

import javax.swing.*;

public class FilesUtil {
    private static byte[] buf = new byte[1024 * 4];

    public static void copyFile(File sourceFile, File targetFile) {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            byte[] b = new byte[5120];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }

            outBuff.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inBuff != null)
                try {
                    inBuff.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (outBuff != null)
                try {
                    outBuff.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static void copyDirectiory(String sourceDir, String targetDir) {
        new File(targetDir).mkdirs();

        File[] file = new File(sourceDir).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                File sourceFile = file[i];

                File targetFile = new File(
                        new File(targetDir).getAbsolutePath() + File.separator
                                + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                String dir1 = sourceDir + "/" + file[i].getName();

                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "\\" + tempList[i]);
                delFolder(path + "\\" + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }

    public static void repalce(String sourceFileName, File targetFile,
                               String newName, String postfix) {
        String targetFolder = targetFile.getAbsolutePath().substring(0,
                targetFile.getAbsolutePath().lastIndexOf("\\"));
        targetFile.delete();
        FileInputStream fin = null;
        FileOutputStream fout = null;
        try {
            fin = new FileInputStream(new File(sourceFileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fout = new FileOutputStream(new File(targetFolder + "\\" + newName
                    + "." + postfix));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] b = new byte[5120];
        try {
            int c;
            while ((c = fin.read(b)) != -1) {
                fout.write(b, 0, c);
            }
            fin.close();
            fout.flush();
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getFolderZip(String folderPath) throws Exception {
        if ((folderPath == null) || (folderPath.trim().length() == 0))
            return null;
        File file = new File(folderPath);
        if (!file.exists())
            return null;
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(byteOut);
        out.setEncoding("GBK");
        zip(out, file, "");
        out.close();
        return byteOut.toByteArray();
    }


    public static byte[] getTechnicsByte(String technicsNumber)
            throws Exception {
        File file = new File(WorkSpaceUtil.getTechnicsDirectory(technicsNumber));
        if (!file.exists())
            return null;

        handTechnicsWordFile(technicsNumber);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(byteOut);
        out.setEncoding("GBK");

        zip(out, file, "");

        out.close();
        return byteOut.toByteArray();
    }

    public static byte[] getTechnicsByte(String technicsNumber, VaActionProgressBar progressBar) throws Exception {
        File file = new File(WorkSpaceUtil.getTechnicsDirectory(technicsNumber));
        if (!file.exists())
            return null;
        // ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        File tempFile = new File(System.getenv("TEMP"), System.currentTimeMillis() + ".zip");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFile));
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(bos);
            out.setEncoding("GBK");
            progressBar.setHeaderMessage("开始压缩文件");
            zip(out, file, "");
            progressBar.setHeaderMessage("结束压缩文件");
        } finally {
            out.close();
            bos.close();
        }
        //byte[] techByte = FileUtils.readFileToByteArray(tempFile);
        byte[] techByte = null;
        FileInputStream fis = new FileInputStream(tempFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        techByte = new byte[(int) tempFile.length()];

        bis.read(techByte);

        fis.close();
        bis.close();
        tempFile.delete();
        return techByte;
    }

    public static byte[] getMesTechnicsByte(String technicsNumber) throws Exception {
        String filepath = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
        if (filepath == null) {
            return null;
        }
        File file = new File(WorkSpaceUtil.getTechnicsDirectory(technicsNumber));
        if (!file.exists())
            return null;
        // ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        File tempFile = new File(System.getenv("TEMP"), System.currentTimeMillis() + ".zip");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFile));
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(bos);
            out.setEncoding("GBK");
            zip(out, file, "");
        } finally {
            out.close();
        }
        byte[] techByte = FileUtils.readFileToByteArray(tempFile);
        tempFile.delete();
        return techByte;
    }

    public static void handTechnicsWordFile(String technicsNumber, VaActionProgressBar progressBar) {
        File file = new File(WorkSpaceUtil.getTechnicsDirectory(technicsNumber));
        if (!file.exists())
            return;
//		progressBar.setHeaderMessage("开始转化工艺说明");
//		File technicsDescFile = new File(file,"工艺说明.doc");
//		//转化工艺说明
//		if(technicsDescFile.exists()&&technicsDescFile.isFile()){
//			File htmlFile = Word2HtmlUtil.genTechnicsDescHtml(technicsDescFile);
//			try {
//					FileUtils.copyFileToDirectory(htmlFile, file);
//				} catch (IOException e) {
//						e.printStackTrace();
//				}
//		}
//		progressBar.setHeaderMessage("结束转化工艺说明");
        File additionaltableFolder = new File(file, "additionaltable");
        File[] timeFloders = additionaltableFolder.listFiles();
        if (timeFloders == null) return;
        for (int i = 0; i < timeFloders.length; i++) {
            File timeFile = timeFloders[i];
            File[] wordFiles = timeFile.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".doc")) {
                        return true;
                    }
                    return false;
                }
            });
            for (int j = 0; j < wordFiles.length; j++) {
                try {
                    progressBar.setHeaderMessage("开始转化" + wordFiles[j].getName());
                    File htmlFile = Word2HtmlUtil.genHtml(wordFiles[j]);
                    if (htmlFile == null) {

                        JOptionPane.showMessageDialog(null, "Word转换PDF失败，请尝试通过以下方式解决!\r\n1、请检查确认jacob插件是否安装;\r\n2、若1已安装，请尝试升级本地Word至Word2010;\r\n3、若以上都不行，请联系管理员！");
                        progressBar.finish();
                        progressBar.setVisible(false);
                        break;
                    } else {
                        FileUtils.copyFileToDirectory(htmlFile, timeFile);
                        progressBar.setHeaderMessage("结束转化" + wordFiles[j].getName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void handTechnicsWordFile(String technicsNumber) {
        File file = new File(WorkSpaceUtil.getTechnicsDirectory(technicsNumber));
        if (!file.exists())
            return;
        File technicsDescFile = new File(file, "工艺说明.doc");
        //转化工艺说明
        if (technicsDescFile.exists() && technicsDescFile.isFile()) {
            File htmlFile = Word2HtmlUtil.genTechnicsDescHtml(technicsDescFile);
            try {
                FileUtils.copyFileToDirectory(htmlFile, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File additionaltableFolder = new File(file, "additionaltable");
        File[] timeFloders = additionaltableFolder.listFiles();
        if (timeFloders == null) return;
        for (int i = 0; i < timeFloders.length; i++) {
            File timeFile = timeFloders[i];
            File[] wordFiles = timeFile.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".doc")) {
                        return true;
                    }
                    return false;
                }
            });
            for (int j = 0; j < wordFiles.length; j++) {
                try {
                    File htmlFile = Word2HtmlUtil.genHtml(wordFiles[j]);
                    if (htmlFile == null) {
                        JOptionPane.showMessageDialog(null, "转化失败，请检查本地插件是否安装完整!");
                    } else {
                        FileUtils.copyFileToDirectory(htmlFile, timeFile);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 获取返工工艺字节数组
     *
     * @param technicsNumber
     * @return
     * @throws Exception
     */
    public static byte[] getReworkTechnicsByte(String technicsNumber,
                                               String technicsName) throws Exception {
        File file = new File(WorkSpaceUtil.getReworkTechnicsDirectory(
                technicsNumber, technicsName));
        if (!file.exists())
            return null;
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(byteOut);
        out.setEncoding("GBK");
        zip(out, file, "");
        out.close();
        return byteOut.toByteArray();
    }

    public static byte[] getTempTechnicsByte(String technicsNumber,
                                             String technicsName) throws Exception {
        File file = new File(WorkSpaceUtil.getTempTechnicsDirectory(
                technicsNumber, technicsName));
        if (!file.exists())
            return null;
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(byteOut);
        out.setEncoding("GBK");
        zip(out, file, "");
        out.close();
        return byteOut.toByteArray();
    }

    private static void zip(ZipOutputStream out, File f, String base)
            throws Exception {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            out.putNextEntry(new ZipEntry(base + "/"));
            base = base + "/";
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
        } else {
            if (f.getAbsolutePath().contains("additionaltable") && (f.getName().endsWith(".tmp") || f.getName().endsWith(".mht"))) {

            } else {
                out.putNextEntry(new ZipEntry(base));
                FileInputStream in = new FileInputStream(f);
                int b;
                while ((b = in.read(buf)) >= 0) {
                    out.write(buf, 0, b);
                }
                in.close();
            }

        }
    }

    /**
     * 将指定文件转化成字节数组
     *
     * @param filePath
     * @return
     */
    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fs = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fs.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fs.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        System.out.println("getBytes---end----");
        return buffer;
    }

    /**
     * 将字节数组转化成文件
     *
     * @param buffer
     * @param filePath
     * @param fileName
     */
    public static void getFile(byte[] buffer, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            System.out.println(file.getPath());
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        System.out.println("getFile----ok-----");
    }

    /**
     * 将路径中的文件按时间排序
     */
    public static File[] orderByDate(String filePath) {
        File file = new File(filePath);
        File[] files = file.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0) {
                    return 1;
                } else if (diff == 0) {
                    return 0;
                } else {
                    return -1;
                }
            }

            public boolean equals(Object obj) {
                return true;
            }
        });
        return files;
    }

    /**
     * 将路径中的文件按名称排序
     */
    public static List<File> orderByName(String filePath) {
        List<File> files = Arrays.asList(new File(filePath).listFiles());
        Collections.sort(files, new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                if (f1.isDirectory() && f2.isFile()) {
                    return -1;
                } else if (f1.isFile() && f2.isDirectory()) {
                    return 1;
                } else {
                    return f1.getName().compareTo(f2.getName());
                }
            }
        });
        return files;
    }

    /**
     * 将路径中的文件按大小排序
     */
    public static List<File> orderBySize(String filePath) {
        List<File> files = Arrays.asList(new File(filePath).listFiles());
        Collections.sort(files, new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                long diff = f1.length() - f2.length();
                if (diff > 0) {
                    return 1;
                } else if (diff == 0) {
                    return 0;
                } else {
                    return -1;
                }
            }

            public boolean equals(Object obj) {
                return true;
            }

        });
        return files;
    }
}
