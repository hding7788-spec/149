package com.glaway.mpm.importdata;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.part.Quantity;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

import com.glaway.mpm.util.WTPartUtil;
import com.ptc.netmarkets.util.beans.NmCommandBean;


public class StandardImportService extends StandardManager implements ImportService {

    private static final long serialVersionUID = 1L;
    private static String tmp_dir = "";
    static final int BUFFER = 2048;

    static {
        try {
            WTProperties pro = WTProperties.getLocalProperties();
            StringBuffer buffer = new StringBuffer();
            tmp_dir = buffer.append(pro.getProperty("wt.codebase.location")).append(pro.getProperty("dir.sep"))
                    .append("ext")
                    .append(pro.getProperty("dir.sep")).append("812").append(pro.getProperty("dir.sep"))
                    .append("temp").append(pro.getProperty("dir.sep")).toString();
            File file = new File(tmp_dir);
            if(!file.exists()){
            	file.mkdirs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static StandardImportService newStandardImportService()
            throws WTException {
        StandardImportService instance = new StandardImportService();
        instance.initialize();
        return instance;
    }

    /**
     * 读取EXCEL表格数据，并且创建文档及其上传主内容。
     *
     * {@inheritDoc}
     *
     * @throws RemoteException
     */
    @Override
    public String importObjects(NmCommandBean cb, File file, String fileName)
            throws WTException, RemoteException {
        // 首先将EXCEL文件保存在服务器端指定目录
    	StringBuffer buffer = new StringBuffer();
        compressZIPData(file, fileName);
        System.out.println("----创建压缩文件完毕");

        // 读取保存在本地的EXCEL文件
        File xlsFile = new File(tmp_dir + fileName);

        // 读取EXCEL表格数据
        List<ProductObjectBean> list = readExcel(xlsFile);

        // 分析EXCEL数据是否存在问题
        String returnValue = analyzeData(list,cb);

        // 数据有问题，返回页面显示
        if (returnValue != null && !"".equals(returnValue)) {
            //JOptionPane.showMessageDialog(null, returnValue,"系统消息",JOptionPane.WARNING_MESSAGE);
        	//System.out.println("-----导入数据存在空值");
            return returnValue;
        } else {
            // 数据没有问题，开始执行创建部件操作
            Transaction tran = new Transaction();

            try {
                tran.start();
                System.out.println("-------开始导入PBOM结构------------");
                int i=0;
                //初始化类型映射
                Map<String, String> typem = initializeTypeMap();
                for (ProductObjectBean bean : list) {
                	++i;
                	String number = bean.getNumber();
                	String name = bean.getName();
                	String productno = bean.getProductNo();
                	String parentno = bean.getParentNumber();
                	String amount = bean.getAmount();
                	String filefolder = bean.getFileFolder();
                	String enditem = bean.getEnditem();
                	String parttype = bean.getParttype();
                	String defaulttracecode = bean.getDefaulttracecode();
                	String defaultunit = bean.getDefaultunit();
                	String hidepartinstructure = bean.getHidepartinstructure();
                	String phantom = bean.getPhantom();
                	String keycomponent = bean.getKeycomponent();
                	String  partkit  = bean.getPartkit();
                	String ctype = bean.getCtype();
                	String view = bean.getView();
                	String secret = bean.getSecret();
                	String phasecode = bean.getPhasecode();
                	WTPart oldpart = WTPartUtil.getLatestPartByPartNumber(number);
                	//收集系统中已经存在的产品
                	if(oldpart!=null) {
                			buffer.append("编号为").append(number).append("的对象在系统中已经存在！没有执行导入，若确定更新，请手动执行").append("<br>");
                	}
                	else{
                    	//创建部件
                       	HashMap<String, String> map = new HashMap<String, String>();
                       	HashMap<String, String> ibaMap = new HashMap<String, String>();
                       	map.put("PRODUCTNO", productno);
                       	map.put("FOLDER", filefolder);
                       	map.put("PARENTNO", parentno);
                       	map.put("AMOUNT", amount);
                       	map.put("ENDITEMIN", enditem);
                       	map.put("PARTTYPE", typem.get(parttype));
                       	map.put("DEFAULTTRACECODE", typem.get(defaulttracecode));
                       	map.put("DEFAULTUNIT", typem.get(defaultunit));
                       	map.put("HIDEPARTINSTRUCTURE",hidepartinstructure);
                       	map.put("PHANTOM", phantom);
                       	ibaMap.put("KEYCOMPONENT", keycomponent);
                       	ibaMap.put("PARTKIT",  partkit);
                       	ibaMap.put("CTYPE", ctype);
                       	map.put("VIEW", view);
                       	ibaMap.put("SCREET", secret);
                       	ibaMap.put("PHASECODE", phasecode);

                        WTContainer conref = cb.getContainer();
                        System.out.println("------容器类:"+conref.getName());
                        System.out.println("-----containner:"+conref.getName());

                        ImportPartUtil.createPart(number, name, map, ibaMap, conref, false);

                    	System.out.println("----产品结构已经导入完成!");
                	}

                }

               // 提交事务
                tran.commit();
                tran = null;
                //创建部件关联关系
                String linkbuffer = createPartUsageLink(list);
                buffer.append(linkbuffer);
                if(buffer.toString()!=null || !(buffer.toString().equals(""))){
                	buffer.insert(0, '1');
                	return buffer.toString();
                }
            } finally {
                // 操作出错，事务回滚
                if (tran != null) {
                    tran.rollback();
                }
            }

        }

        return null;
    }

    public String createPartUsageLink(List<ProductObjectBean> list) throws WTException{
    	StringBuffer buffer = new StringBuffer();
    	Transaction tran = new Transaction();

        try {
            tran.start();
            for (ProductObjectBean bean : list) {
            	String number = bean.getNumber();
            	String parentno = bean.getParentNumber();
                String mount = bean.getAmount();


            	//创建部件关联关系
            	WTPart parentpart = WTPartUtil.getLatestPartByPartNumber(parentno);
            	WTPartMaster childmaster =(WTPartMaster) WTPartUtil.getLatestPartByPartNumber(number).getMaster();
            	if(parentpart==null || parentno.equals("无")||(parentno.equals(""))){
            		buffer.append("导入文件中指定的编号为").append(number).append("的父类对象").append(parentno).append("在系统中不存在！关联关系没有建立成功，可选择添加父类对象后重新导入该条目或手工建立关系").append("<br>");
            		continue;
            	}
            	System.out.println("------文件夹路径:"+parentpart.getFolderPath());
            	String fullPath = parentpart.getFolderPath();
            	int index = fullPath.lastIndexOf("/");
            	String parentPath = fullPath.substring(0, index);
            	System.out.println("文件夹路径："+parentPath);
            	Folder childFolder = FolderHelper.service.getFolder(parentPath,WTContainerRef.newWTContainerRef(parentpart.getContainer()));



            	//获得检出对象的副本
            	WTPart checkoutPart = new WTPart();
            	try {
					 CheckoutLink checkout = WorkInProgressHelper.service.checkout(parentpart, childFolder, "");
					 Object []allObjects = checkout.getAllObjects();
					 for(Object part : allObjects){
						checkoutPart = (WTPart)part;
					 }
				} catch (WTPropertyVetoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	//在检出副本上建立对象间关联关系


			boolean flag = haveRelations(checkoutPart,childmaster);

				//两对象无关联关系时，建立关联关系
			if(!flag){
	            	WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(checkoutPart, childmaster);

	                double quantity =Double.parseDouble(mount);
	                Quantity quantity2 = new Quantity();
	                quantity2.setAmount(quantity);
	            	link.setQuantity(quantity2);
	                PersistenceHelper.manager.save(link);
	            	//将操作对象重新检入
	            	try {
						WorkInProgressHelper.service.checkin(checkoutPart, "");
					} catch (WTPropertyVetoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

	            	System.out.println("-------产品关联建立成功");

				}


            }

            // 提交事务
            tran.commit();
            }
        finally {
            // 操作出错，事务回滚
            if (tran != null) {
                tran.rollback();
            }
        }
        	if(buffer.toString()!=null ||!(buffer.toString().equals("")))
        		return buffer.toString();
        	else
        		return null;
    }

    //判断两对象是否存在关联关系
    private boolean haveRelations(WTPart parentpart, WTPartMaster childmaster) {
//		// TODO Auto-generated method stub
//    	try {
//
//			//判断与父部件是否有关联关系的对象
//			QueryResult usedMember = WTPartHelper.service.getUsesWTPartMasters(parentpart);
//			while(usedMember.hasMoreElements()){
//				WTPartUsageLink memberLink = (WTPartUsageLink)usedMember.nextElement();
//				if(memberLink.getUses().getNumber().equals(childmaster.getNumber())){
//					System.out.println("---------存在关联关系22");
//					try {
//						WorkInProgressHelper.service.checkin(parentpart, "");
//					} catch (WTPropertyVetoException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					return true;
//				}
//
//			}
//
//		try {
//			WorkInProgressHelper.service.checkin(parentpart, "");
//		} catch (WTPropertyVetoException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		} catch (WTException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("------不存在关联关系");
//		return false;
		QuerySpec qs;
		try {
			qs = new QuerySpec(WTPartUsageLink.class);

		long masterId = PersistenceHelper.getObjectIdentifier(childmaster).getId();
		long parentId =  PersistenceHelper.getObjectIdentifier(parentpart).getId();
		SearchCondition sc1;

		sc1 = new SearchCondition(WTPartUsageLink.class,"roleAObjectRef.key.id",SearchCondition.EQUAL,parentId);

		qs.appendWhere(sc1, new int[]{0});
		qs.appendAnd();
		SearchCondition sc2 = new SearchCondition(WTPartUsageLink.class,"roleBObjectRef.key.id",SearchCondition.EQUAL,masterId);
		qs.appendWhere(sc2);

		QueryResult qr = PersistenceHelper.manager.find(qs);
		while(qr.hasMoreElements()){
//若取出关联对象,需重新检入
			WorkInProgressHelper.service.checkin(parentpart, "");

			return true;
		}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	/**
     * 处理上载的文件，将其先保存在服务器端指定路径，然后再对其进行解压。
     *
     */
    public List<String> compressZIPData(File zipFile, String fileName) {
        // 加压压缩包时首先清空该目录下的所有文件
    	System.out.println("---------------test");
        if (fileName.endsWith("zip")) {
            File tmpFile = new File(tmp_dir);
            if (!tmpFile.exists()) {
                tmpFile.mkdirs();
            } else {
                File[] files = tmpFile.listFiles();
                for (File file : files) {
                    deleteFile(file.getPath());
                }
            }
        }
        String test = tmp_dir + fileName;

        File tempFile = new File(tmp_dir + fileName);
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(zipFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(tempFile));

            // 缓冲数组
            byte[] b = new byte[BUFFER * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                if (outBuff != null) {
                    outBuff.close();
                }
                if (inBuff != null) {
                    inBuff.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<String> allFileName = new ArrayList<String>();
        // 解压
        if (tempFile != null && tempFile.length() > 0 && fileName.endsWith(".zip")) {
            try {
                // 指定压缩包里的文件加压的目录
                String dir = tmp_dir + "files" + File.separator;
                allFileName = unzip(tmp_dir + fileName, dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return allFileName;
    }

    /**
     * 解压文件到指定目录
     *
     * @param zipFile
     *            需要解压的压缩包文件
     * @param dest
     *            压缩包存放的目录路径
     * @throws IOException
     */
    private static List<String> unzip(String zipFile, String dest) throws IOException {
        List<String> allFileName = new ArrayList<String>();
        ZipFile zip = new ZipFile(zipFile);
        Enumeration<ZipEntry> en = zip.getEntries();
        ZipEntry entry = null;
        byte[] buffer = new byte[BUFFER];
        int length = -1;
        InputStream input = null;
        BufferedOutputStream bos = null;
        File file = null;
        while (en.hasMoreElements()) {
            entry = (ZipEntry) en.nextElement();
            System.out.println("--------entry:" + entry.getName());
            if (entry.isDirectory()) {
                file = new File(dest, entry.getName());
                if (!file.exists()) {
                    file.mkdir();
                }
                continue;
            }

            String fileName = entry.getName();

            // 将所有文件解压到files目录下
            if (fileName.indexOf("/") > 0) {
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            }
            if (fileName.indexOf("\\") > 0) {
                fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length());
            }
            System.out.println("-------add fileName:" + fileName);
            allFileName.add(fileName);

            input = zip.getInputStream(entry);
            file = new File(dest, fileName);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            bos = new BufferedOutputStream(new FileOutputStream(file));

            while (true) {
                length = input.read(buffer);
                if (length == -1) {
                    break;
                }
                bos.write(buffer, 0, length);
            }
            bos.close();
            input.close();
        }
        zip.close();

        return allFileName;
    }

    private static void deleteFile(String delpath) {
        File file = new File(delpath);
        if (!file.isDirectory()) {
            file.delete();
        } else if (file.isDirectory()) {
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                File delfile = new File(delpath + File.separator + filelist[i]);
                if (!delfile.isDirectory()) {
                    delfile.delete();
                } else if (delfile.isDirectory()) {
                    deleteFile(delpath + File.separator + filelist[i]);
                }
            }
            file.delete();
        }
    }

    /**
     * 读取EXCEL数据
     *
     * @param xlsFile
     *            EXCEL表格文件
     * @return List<DocObjectBean>
     */
    private static List<ProductObjectBean> readExcel(File xlsFile) {
    	System.out.println("开始解析EXCEL:");
        List<ProductObjectBean> list = new ArrayList<ProductObjectBean>();
        try {
            InputStream is = new FileInputStream(xlsFile);
            HSSFWorkbook wb = new HSSFWorkbook(is);
            HSSFSheet sheet = wb.getSheetAt(0);

            int n = sheet.getLastRowNum() - sheet.getFirstRowNum();
            System.out.println("------行数:"+n);
            HSSFRow row = null;

            ProductObjectBean bean = null;
            if (n > 1) {
                for (int i = 1; i<=n; i++) {
                    row = sheet.getRow(i);
                    bean = new ProductObjectBean();
                    // 产品编号
                    String number = "";
                    if (row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                    	bean.setNumber(row.getCell(0).getRichStringCellValue().getString().trim()) ;
                    	number = row.getCell(0).getRichStringCellValue().getString().trim();
                    } else if (row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(0).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(0).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                    	bean.setNumber(temp);
                    	number = temp;
                    }
                    System.out.println("----number:"+number);
                    if ("end".equalsIgnoreCase(number)) {
                        break;
                    } else {
                        bean.setNumber(number);
                    }

                 // 产品名称
                    if(row.getCell(1)==null){
                    	bean.setName("");
                    }else if (row.getCell(1).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setName(row.getCell(1).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(1).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(1).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(1).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setName(temp);
                    }
                    System.out.println("----name:"+bean.getName());
                    // 所属产品编号
                    if(row.getCell(2)==null){
                    	bean.setProductNo("");
                    }else if (row.getCell(2).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setProductNo(row.getCell(2).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(2).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(2).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(2).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setProductNo(temp);
                    }
                   System.out.println("------productno:"+bean.getProductNo());
                    // 上级图号
                   if(row.getCell(3)==null){
                   	bean.setParentNumber("");
                   }else if (row.getCell(3).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setParentNumber(row.getCell(3).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(3).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(3).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(3).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setParentNumber(temp);
                    }
                    System.out.println("-----parentno:"+bean.getParentNumber());
                    //使用数量
                    if(row.getCell(4)==null){
                    	bean.setAmount("");
                    }else if (row.getCell(4).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setAmount(row.getCell(4).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(4).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(4).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(4).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setAmount(temp);
                    }
                    //所属文件夹
                    if(row.getCell(5)==null){
                    	bean.setFileFolder("");
                    }else if (row.getCell(5).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setFileFolder(row.getCell(5).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(5).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setFileFolder(row.getCell(5).getNumericCellValue() + "");
                    }
                    // 是否成品
                    if(row.getCell(6)==null){
                    	bean.setEnditem("");
                    }else if (row.getCell(6).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setEnditem(row.getCell(6).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(6).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setEnditem(row.getCell(6).getNumericCellValue() + "");
                    }
                    // 装配模式
                    if(row.getCell(7)==null){
                    	bean.setParttype("");
                    }else if (row.getCell(7).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setParttype(row.getCell(7).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(7).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setParttype(row.getCell(7).getNumericCellValue() + "");
                    }
                    //默认追踪代码
                    if(row.getCell(8)==null){
                    	bean.setDefaulttracecode("");
                    }else if (row.getCell(8).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setDefaulttracecode(row.getCell(8).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(8).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setDefaulttracecode(row.getCell(8).getNumericCellValue() + "");
                    }
                    //默认单位
                    if(row.getCell(9)==null){
                    	bean.setDefaultunit("");
                    }else if (row.getCell(9).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setDefaultunit(row.getCell(9).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(9).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setDefaultunit(row.getCell(9).getNumericCellValue() + "");
                    }
                    //收集部件
                    if(row.getCell(10)==null){
                    	bean.setHidepartinstructure("");
                    }else if (row.getCell(10).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setHidepartinstructure(row.getCell(10).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(10).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setHidepartinstructure(row.getCell(10).getNumericCellValue() + "");
                    }
                    //虚拟制造部件
                    if(row.getCell(11)==null){
                    	bean.setPhantom("");
                    }else if (row.getCell(11).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setPhantom(row.getCell(11).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(11).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setPhantom(row.getCell(11).getNumericCellValue() + "");
                    }
                    //关重件
                    if(row.getCell(12)==null){
                    	bean.setKeycomponent("");
                    }else if (row.getCell(12).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setKeycomponent(row.getCell(12).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(12).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setKeycomponent(row.getCell(12).getNumericCellValue() + "");
                    }
                    //成套件标识
                    if(row.getCell(13)==null){
                    	bean.setPartkit("");
                    }else if(row.getCell(13).getCellType()==HSSFCell.CELL_TYPE_STRING){
                    	bean.setPartkit(row.getCell(13).getRichStringCellValue().getString().trim());
                    }else if(row.getCell(13).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                    	bean.setPartkit(row.getCell(13).getNumericCellValue()+"");
                    }
                    //零部件分类
                    if(row.getCell(14)==null){
                    	bean.setCtype("");
                    }else if(row.getCell(14).getCellType()==HSSFCell.CELL_TYPE_STRING){
                    	bean.setCtype(row.getCell(14).getRichStringCellValue().getString().trim());
                    }else if(row.getCell(14).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                    	bean.setCtype(row.getCell(14).getNumericCellValue()+"");
                    }
                    //视图
                    if(row.getCell(15)==null){
                    	bean.setView("");
                    }else if(row.getCell(15).getCellType()==HSSFCell.CELL_TYPE_STRING){
                    	bean.setView(row.getCell(15).getRichStringCellValue().getString().trim());
                    }else if(row.getCell(15).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                    	bean.setView(row.getCell(15).getNumericCellValue()+"");
                    }
                    //密级
                    if(row.getCell(16)==null){
                    	bean.setSecret("公开");
                    }else if(row.getCell(16).getCellType()==HSSFCell.CELL_TYPE_STRING){
                    	String secretValue = row.getCell(16).getRichStringCellValue().getString().trim();
                    	if("".equals(secretValue) || "无".equals(secretValue)){
                    		secretValue = "公开";
                    	}
                    	bean.setSecret(secretValue);
                    }else if(row.getCell(16).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                    	bean.setSecret(row.getCell(16).getNumericCellValue()+"");
                    }
                    //当前阶段
                    if(row.getCell(17)==null){
                    	bean.setPhasecode("");
                    }else if(row.getCell(17).getCellType()==HSSFCell.CELL_TYPE_STRING){
                    	bean.setPhasecode(row.getCell(17).getRichStringCellValue().getString().trim());
                    }else if(row.getCell(17).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                    	bean.setPhasecode(row.getCell(17).getNumericCellValue()+"");
                    }

                    list.add(bean);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static String analyzeData(List<ProductObjectBean> list,NmCommandBean cb) throws WTException,
            RemoteException {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            ProductObjectBean bean = list.get(i);
        	String number = bean.getNumber();
        	String name = bean.getName();
        	String productno = bean.getProductNo();
        	String parentno = bean.getParentNumber();
        	String amount = bean.getAmount();
        	String filefolder = bean.getFileFolder();
        	String enditem = bean.getEnditem();
        	String parttype = bean.getParttype();
        	String defaulttracecode = bean.getDefaulttracecode();
        	String defaultunit = bean.getDefaultunit();
        	String hidepartinstructure = bean.getHidepartinstructure();
        	String phantom = bean.getPhantom();
        	String keycomponent = bean.getKeycomponent();
        	String partkit = bean.getPartkit();
        	String ctype = bean.getCtype();
        	String view = bean.getView();
        	if(number==null||number.equals(""))
        		buffer.append("第").append(i+3).append("行“编号”不能为空").append("<br>");
        	if(name==null||name.equals(""))
        		buffer.append("第").append(i+3).append("行“名称”不能为空").append("<br>");
        	if(filefolder==null||filefolder.equals(""))
        		buffer.append("第").append(i+3).append("行“位置”不能为空").append("<br>");


        	if(enditem==null||enditem.equals(""))
        		buffer.append("第").append(i+3).append("行“是否成品”不能为空").append("<br>");
        	if(parttype==null||parttype.equals(""))
        		buffer.append("第").append(i+3).append("行“装配模式”不能为空").append("<br>");
        	if(defaulttracecode==null||defaulttracecode.equals(""))
        		buffer.append("第").append(i+3).append("行“默认追踪代码”不能为空").append("<br>");
        	if(defaultunit==null||defaultunit.equals(""))
        		buffer.append("第").append(i+3).append("行“默认单位”不能为空").append("<br>");
        	if(hidepartinstructure==null||hidepartinstructure.equals(""))
        		buffer.append("第").append(i+3).append("行“收集部件”不能为空").append("<br>");
        	if(phantom==null||phantom.equals(""))
        		buffer.append("第").append(i+3).append("行“虚拟制造部件”不能为空").append("<br>");
        	if(keycomponent==null||keycomponent.equals(""))
        		buffer.append("第").append(i+3).append("行“关重件”不能为空").append("<br>");
        	if(partkit==null||partkit.equals(""))
        		buffer.append("第").append(i+3).append("行“成套件标识”不能为空").append("<br>");
        	if(ctype==null||ctype.equals(""))
        		buffer.append("第").append(i+3).append("行“零部件分类”不能为空").append("<br>");
//判断模板中输入的产品名称与选择的入口产品是否一致
            WTContainer conref = cb.getContainer();
            String confString = conref.getName();
            if(!confString.equals(productno))
            	buffer.append("第").append(i+3).append("行“部件名称”").append(productno).append("与数据导入的入口产品").append(confString).append("不一致！")
            			.append("<br>");
//默认视图选择为Design
//        	if(!view.equals("Design")){
//        		buffer.append("第").append(i+3).append("行“视图”种类默认需为Design").append("<br>");
//        	}
 //判断枚举类型值
 //       	if(!(parttype.equals("可分")||parttype.equals("不可分")||parttype.equals("组件"))){
 //       		buffer.append("第").append(i+3).append("行“装配模式”必须为“可分；不可分；组件”中一类").append("<br>");
 //       	}
        	if(filefolder!=null&&!filefolder.equals("")&&!(filefolder.substring(0, 8).equals("/Default"))){
        		buffer.append("第").append(i+3).append("行“位置”填写不规范，应以“/Default”为起始").append("<br>");
        	}
            if (filefolder != null && !filefolder.equals("") && filefolder.contains("\\")) {
                buffer.append("第").append(i + 3).append("行“位置”填写不规范，应以“/”分隔文件夹目录").append("<br>");
            }
        }
        return buffer.toString();


    }

    /**
     * 通过文件夹路径和容器获取文件对象
     *
     * @param path
     *            String
     * @param con
     *            WTContainer
     * @return Folder
     * @throws WTException
     */
    public static Folder getFolder(String path, WTContainer con) throws WTException {
        Folder folder = null;
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        String subPath = "";
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            subPath = subPath + "/" + token;
            if (subPath != null && !subPath.equalsIgnoreCase("")) {
                try {
                    folder = FolderHelper.service.getFolder(subPath, WTContainerRef.newWTContainerRef(con));
                } catch (FolderNotFoundException e) {
                    boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
                    folder = FolderHelper.service.createSubFolder(subPath, WTContainerRef.newWTContainerRef(con));
                    SessionServerHelper.manager.setAccessEnforced(flag);
                }
            }
        }
        return folder;
    }
//单位映射表
    private static Map<String, String> initializeTypeMap() {
        Map<String, String> map = new HashMap<String, String>();
        //单位映射
        map.put("每个", "ea");
        map.put("根据需要", "as_needed");
        map.put("千克", "kg");
        map.put("米", "m");
        map.put("升", "l");
        map.put("平方米", "sq_m");
        map.put("立方米", "cu_m");
//        map.put("包", "bao");
//        map.put("筒", "tong");
//        map.put("瓶", "ping");
//        map.put("张", "zhang");
//        map.put("只", "zhi");
//        map.put("支", "zhi_2");
        //装配类型映射   可分,不可分,组件
        map.put("可分","separable");
        map.put("不可分","inseparable");
        map.put("组件","component");
        //默认追踪代码映射   批号,批号/序列号,序列号,未追踪
        map.put("批号", "S");
        map.put("批号/序列号", "L");
        map.put("序列号", "X");
        map.put("未追踪", "0");

        return map;
    }

}
