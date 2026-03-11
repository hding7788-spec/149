package ext.casc.importdata;

import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.access.AccessAdminUtil;
import ext.casc.constants.Constants;
import ext.casc.listener.ExtStandardListenerService;
import ext.casc.part.CSCPart;
import ext.casc.util.CSCUtil;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.part.*;
import wt.pdmlink.PDMLinkProduct;
import wt.pom.PersistenceException;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.series.HarvardSeries;
import wt.series.IntegerSeries;
import wt.services.StandardManager;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.*;
import wt.vc.wip.WorkInProgressHelper;

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;
import java.util.Map.Entry;

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
                    .append(pro.getProperty("dir.sep")).append("casc").append(pro.getProperty("dir.sep"))
                    .append("temp").append(pro.getProperty("dir.sep")).toString();
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
    	File tempDir = new File(tmp_dir);

    	tempDir.mkdirs();

        compressZIPData(file, fileName);
        System.out.println("----创建压缩文件完毕");

        // 读取保存在本地的EXCEL文件
        File xlsFile = new File(tmp_dir + fileName);

        // 读取EXCEL表格数据
        List<ProductObjectBean> list = readExcel(xlsFile);

        // 分析EXCEL数据是否存在问题
        if(list.size()>300){
        	return "Excel数据超过300行，为了提高导入效率，请分为多个Excel文件进行导入！";
        }
        String returnValue = analyzeData(list,cb,fileName);

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
                System.out.println("-------开始导入产品结构");
                int i=0;
                //初始化类型映射
                List<WTPart> parts = new ArrayList<WTPart>();
                for (ProductObjectBean bean : list) {
                	++i;
                	String number = bean.getNumber();
                	number = CSCUtil.trim(number);

                	String name = bean.getName();
                	name = CSCUtil.trim(name);

                	String productno = bean.getProductNo();
                	String parentno = bean.getParentNumber();
                	parentno = CSCUtil.trim(parentno);

                	String amount = bean.getAmount();
                	String filefolder = bean.getFileFolder();
                	String enditem = bean.getEnditem();
                	String parttype = bean.getParttype();
                	String defaulttracecode = bean.getDefaulttracecode();
                	String defaultunit = bean.getDefaultunit();
                	String hidepartinstructure = bean.getHidepartinstructure();
                	String phantom = bean.getPhantom();
                	String keycomponent = bean.getKeycomponent();
                	String setmark = bean.getSetmark();
                	String ctype = bean.getCtype();
                	String view = bean.getView();
                	String secret = bean.getSecret();
                	String phasecode = bean.getPhasecode();
                	String csize = bean.getCsize();
                	String cindex = bean.getCindex();
                    String material = bean.getMaterial();
                	String material_name = bean.getMaterial_name();
                	String enditem_1 = bean.getEnditem_1();
                	String commonname = bean.getCommnon_name();
                	String product_index = bean.getProduct_index();
                	String company = bean.getCompany();
                	String remark = bean.getRemark();
                	String routing = bean.getRouting();
                	String mat_up = bean.getMat_up();
                	String mat_down = bean.getMat_down();
                	String CHBM = bean.getCHBM();
                	String BATCH = bean.getBATCH();
                	String  XHPH = bean.getXHPH();
                	String JSTJ = bean.getJSTJ();
                	String MTYPE = bean.getMTYPE();

                	String material_paihao = bean.getMaterial_paihao();// 材料牌号
                    String pzggbzh = bean.getPzggbzh();// 品种规格标准号
                    String jstjbzh = bean.getJstjbzh();// 技术条件标准号
                    String jddj = bean.getJddj();// 精度等级
                    String zldj = bean.getZldj();// 质量等级
                    String clzt = bean.getClzt();// 材料状态
                    String cldw = bean.getCldw();// 材料单位
                    String zqclmc = bean.getZqclmc();// 增强材料名称
                    String jbclmc = bean.getJbclmc();// 基体材料名称
                    String zqclbzh = bean.getZqclbzh();// 增强材料标准号

                    String pindex = bean.getPindex();// 产品代号
                    String mindex = bean.getMindex();// 所属型号
                    String designer = bean.getDesigner();// 设计者
                    String size = bean.getSize();// 二维工程图图幅

                	WTPart oldpart = CSCPart.getPart(number);
                	HashMap<String, String> IBAmap = new HashMap<String, String>();
                	//收集系统中已经存在的产品
                	if(oldpart!=null) {
                		String version = oldpart.getVersionIdentifier().getValue()+"."+oldpart.getIterationIdentifier().getValue();
                		if("space.0".equals(version)){
                			buffer.append("编号为").append(number).append("的对象在系统中已经存在！更新成功！").append("<br>");
                           	IBAmap.put("KEYCOMPONENT", keycomponent);
                           	IBAmap.put("SETMARK", setmark);
                           	IBAmap.put("CTYPE", ctype);
                           	IBAmap.put("SECRET", secret);
                           	IBAmap.put("PHASE_CODE", phasecode);
                           	IBAmap.put("CSIZE", csize);
                           	IBAmap.put("CINDEX", cindex);
                           	IBAmap.put("CMAT", material);
                           	IBAmap.put("PTC_MATERIAL_NAME", material_name);
                           	IBAmap.put("ENDITEMIN",enditem_1);
                           	IBAmap.put("PTC_COMMON_NAME", commonname);
                           	IBAmap.put("PRODUCT_INDEX", product_index);
                           	IBAmap.put("COMPANY", company);
                           	IBAmap.put("REMARK", remark);
                           	IBAmap.put("ROUTING", routing);
                           	IBAmap.put("CMAT_UP", mat_up);
                           	IBAmap.put("CMAT_DOWN", mat_down);

                           	IBAmap.put("MATERIAL", material_paihao);
                            IBAmap.put("PZGGBZH", pzggbzh);
                            IBAmap.put("JSTJBZH",jstjbzh);
                            IBAmap.put("JDDJ", jddj);
                            IBAmap.put("ZLDJ", zldj);
                            IBAmap.put("CLZT", clzt);
                            IBAmap.put("CLDW", cldw);
                            IBAmap.put("ZQCLMC", zqclmc);
                            IBAmap.put("JBCLMC", jbclmc);
                            IBAmap.put("ZQCLBZH", zqclbzh);

                            IBAmap.put("PINDEX", pindex);
                            IBAmap.put("MINDEX", mindex);
                            IBAmap.put("DESIGNER", designer);
                            IBAmap.put("SIZE", size);
                            IBAmap.put("CHBM", CHBM);
                            IBAmap.put("BATCH", BATCH);
                            IBAmap.put("XHPH",  XHPH);
                            IBAmap.put("JSTJ", JSTJ);
                            IBAmap.put("MTYPE", MTYPE);
                			IBAUtility ibaUtility = new IBAUtility(oldpart);
                            Set<Entry<String, String>> set = IBAmap.entrySet();
                            for(Entry<String, String> entry :set){

                            	try {
    								ibaUtility.setIBAValue(entry.getKey(),entry.getValue());
    								oldpart =(WTPart)ibaUtility.updateAttributeContainer(oldpart);
    								ibaUtility.updateIBAHolder(oldpart);
									ExtStandardListenerService.setSecurityLabels(oldpart, "MIJI", Constants.miji.get(secret));
								} catch (WTPropertyVetoException e) {
    								// TODO Auto-generated catch block
    								e.printStackTrace();
    							} catch (ClassNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
                            }
                		}else{
                			buffer.append("编号为").append(number).append("的对象在系统中已经存在！版本不是space.0，不允许更新！").append("<br>");
                		}

                	}else{
                    	//创建部件
                       	HashMap<String, String> map = new HashMap<String, String>();
                       	map.put("PRODUCTNO", productno);
                       	map.put("FOLDER", filefolder);
                       	map.put("PARENTNO", parentno);
                       	map.put("AMOUNT", amount);
                       	map.put("ENDITEMIN", enditem);
                       	map.put("PARTTYPE", Constants.useUnitMap.get(parttype));
                       	map.put("DEFAULTTRACECODE", Constants.useUnitMap.get(defaulttracecode));
                       	map.put("DEFAULTUNIT", Constants.useUnitMap.get(defaultunit));
                       	map.put("HIDEPARTINSTRUCTURE",hidepartinstructure);
                       	map.put("PHANTOM", phantom);
                       	IBAmap.put("KEYCOMPONENT", keycomponent);
                       	IBAmap.put("SETMARK", setmark);
                       	IBAmap.put("CTYPE", ctype);
                       	map.put("VIEW", view);
                       	IBAmap.put("SECRET", secret);
                       	IBAmap.put("PHASE_CODE", phasecode);
                       	IBAmap.put("CSIZE", csize);
                       	IBAmap.put("CINDEX", cindex);
                       	IBAmap.put("CMAT", material);
                       	IBAmap.put("PTC_MATERIAL_NAME", material_name);
                       	IBAmap.put("ENDITEMIN",enditem_1);
                       	IBAmap.put("PTC_COMMON_NAME", commonname);
                       	IBAmap.put("PRODUCT_INDEX", product_index);
                       	IBAmap.put("COMPANY", company);
                       	IBAmap.put("REMARK", remark);
                       	IBAmap.put("ROUTING", routing);
                       	IBAmap.put("CMAT_UP", mat_up);
                       	IBAmap.put("CMAT_DOWN", mat_down);

                       	IBAmap.put("MATERIAL", material_paihao);
                        IBAmap.put("PZGGBZH", pzggbzh);
                        IBAmap.put("JSTJBZH",jstjbzh);
                        IBAmap.put("JDDJ", jddj);
                        IBAmap.put("ZLDJ", zldj);
                        IBAmap.put("CLZT", clzt);
                        IBAmap.put("CLDW", cldw);
                        IBAmap.put("ZQCLMC", zqclmc);
                        IBAmap.put("JBCLMC", jbclmc);
                        IBAmap.put("ZQCLBZH", zqclbzh);

                        IBAmap.put("PINDEX", pindex);
                        IBAmap.put("MINDEX", mindex);
                        IBAmap.put("DESIGNER", designer);
                        IBAmap.put("SIZE", size);
                        IBAmap.put("CHBM", CHBM);
                        IBAmap.put("BATCH", BATCH);
                        IBAmap.put("XHPH",  XHPH);
                        IBAmap.put("JSTJ", JSTJ);
                        IBAmap.put("MTYPE", MTYPE);

                        WTContainer conref = cb.getContainer();
                        System.out.println("------容器类:"+conref.getName());
                        System.out.println("-----containner:"+conref.getName());
                    	WTPart part = CSCPart.createPart(number, name, map,IBAmap, conref, false);
                        try {
 							//setVersionId(part, "space");
 							setIterationId(part, "0");
 							PersistenceServerHelper.manager.update(part);
							ExtStandardListenerService.setSecurityLabels(part, "MIJI", Constants.miji.get(secret));
							parts.add(part);
 						} catch (Exception e1) {
 							e1.printStackTrace();
 							throw new WTException(e1);
 						}
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
                String defaultunit = bean.getDefaultunit();

                if(number.equals(parentno)) {
                	buffer.append("子件编号为 ").append(number).append(" 与父件编号的 ").append(parentno).append(" 相同，不执行父子关系的建立！").append("<br>");
            		continue;
                }

                WTPart childPart = CSCPart.getPartByNumberAndViewName(number,"Design");
                if(childPart == null) {
                	buffer.append("导入文件中编号为 ").append(number).append(" 的Design视图零部件在系统中不存在！").append("<br>");
            		continue;
                }
            	WTPartMaster childmaster = (WTPartMaster)childPart.getMaster();

            	//判断该零部件是否已经于parentno存在父子关系
            	QueryResult qr = WTPartHelper.service.getUsedByWTParts(childmaster);
            	WTPart pPart = null;
            	//标记是否已经存在父子关系
            	boolean isLinked = false;
            	while(qr.hasMoreElements()) {
            		pPart = (WTPart)qr.nextElement();
            		if(pPart.getNumber().equals(parentno)) {
            			isLinked = true;
            			break;
            		}
            	}

            	if(isLinked) {
            		buffer.append("子件编号为 ").append(number).append(" 的零部件与父件编号为 ").append(parentno).append(" 的零部件在系统中已经存在父子关系了！").append("<br>");
            		continue;
            	}

            	//创建部件关联关系
            	WTPart parentpart = CSCPart.getPartByNumberAndViewName(parentno,"Design");
            	if(parentpart==null || parentno.equals("无")||(parentno.equals(""))){
            		buffer.append("导入文件中指定的编号为").append(number).append("的部件为顶级部件，请检查确认！").append("<br>");
            		continue;
            	}

            	//判断是否存在循环使用关系,及是该父件的上级或是N上级有每有被该子件使用过
            	boolean isUsed = checkUsedLinked(parentpart,number);
            	if(isUsed) {
            		buffer.append("子件编号为 ").append(number).append(" 的零部件与父件编号为 ").append(parentno).append(" 的零部件在系统中出现循环使用关系，不执行父子关系建立！").append("<br>");
            		continue;
            	}



            	//获得检出对象的副本
            	/*WTPart checkoutPart = new WTPart();
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

*/
			boolean flag = haveRelations(parentpart,childmaster);
			boolean flag2 = haveRelations2(parentpart,childmaster);

				//两对象无关联关系时，建立关联关系
			if(!flag&&!flag2){
	            	WTPartUsageLink link = WTPartUsageLink.newWTPartUsageLink(parentpart, childmaster);

	                double quantity =Double.parseDouble(mount);
	                Quantity quantity2 = new Quantity();
	                quantity2.setAmount(quantity);

	            	//设置单位
	            	String unitKey = Constants.useUnitMap.get(defaultunit);
	            	if(unitKey != null && !"".equals(unitKey)) {
	            		QuantityUnit unit = QuantityUnit.toQuantityUnit(unitKey);
	            		quantity2.setUnit(unit);
	            	}

	            	link.setQuantity(quantity2);

	               // PersistenceHelper.manager.save(link);
	            	PersistenceServerHelper.manager.insert(link);
	            	//将操作对象重新检入
	            	/*try {
						WorkInProgressHelper.service.checkin(parentpart, "");
					} catch (WTPropertyVetoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/

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

    private boolean haveRelations2(WTPart parentpart, WTPartMaster childmaster) throws PersistenceException, WTException {
    	QueryResult allVersionsQs = VersionControlHelper.service.allVersionsOf(childmaster);
		if (allVersionsQs.hasMoreElements()) {
			WTPart childLatestPart = (WTPart) allVersionsQs.getEnumeration().nextElement();
			return checkUsesLinked(childLatestPart, parentpart.getNumber());
		}
    	return false;
	}

	private static boolean checkUsedLinked(WTPart parentPart,String childNumber) throws WTException {
    	QueryResult qr = WTPartHelper.service.getUsedByWTParts((WTPartMaster)parentPart.getMaster());
    	WTPart pPart = null;
    	while(qr.hasMoreElements()) {
    		pPart = (WTPart)qr.nextElement();
    		if(pPart.getNumber().equals(childNumber)) {
    			return true;
    		}
    		return checkUsedLinked(pPart,childNumber);
    	}
    	return false;
    }

    private static boolean checkUsesLinked(WTPart childPart,String parentNumber) throws WTException {
    	QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(childPart);
    	WTPart pPart = null;
    	while (qr.hasMoreElements()) {
			WTPartMaster childPartMaster = ((WTPartMaster) ((WTPartUsageLink) qr.nextElement())
					.getRoleBObjectRef().getObject());
			// 遍历子part
			QueryResult allVersionsQs = VersionControlHelper.service.allVersionsOf(childPartMaster);
			if (allVersionsQs.hasMoreElements()) {
				WTPart childLatestPart = (WTPart) allVersionsQs.getEnumeration().nextElement();
				if(childLatestPart.getNumber().equals(parentNumber)) {
	    			return true;
	    		}
				return checkUsesLinked(childLatestPart,childPart.getNumber());
			}
		}
    	return false;

    }

    /**
   	 * 设定新版本 Note:the method can't be overriden.
   	 *
   	 * @param v
   	 * @param versionId
   	 * @throws Exception
   	 */
   	public static void setVersionId(Versioned v, String versionId)
   			throws Exception {
   		VersionIdentifier vi = newVersionId(versionId);
   		if (v.getVersionInfo() == null)
   			v.setVersionInfo(VersionInfo.newVersionInfo());
   		if (v.getIterationInfo() == null)
   			v.setIterationInfo(IterationInfo.newIterationInfo());
   		VersionControlHelper.setVersionIdentifier(v, vi);
   	}

   	/**
   	 * 产生新版本ID Note:the method can't be overriden.
   	 *
   	 * @param versionId
   	 * @return
   	 * @throws Exception
   	 */
   	public static VersionIdentifier newVersionId(String versionId)
   			throws Exception {
   		HarvardSeries hs = HarvardSeries.newHarvardSeries();
   		hs.setValue(versionId);
   		return VersionIdentifier.newVersionIdentifier(hs);
   	}

   	/**
   	 * 产生小版本ID Note:the method can't be overriden.
   	 *
   	 * @param iterationId
   	 * @return
   	 * @throws Exception
   	 */
   	public static IterationIdentifier newIterationId(String iterationId)
   			throws Exception {
   		IntegerSeries is = IntegerSeries.newIntegerSeries();
   		is.setValueWithoutValidating(iterationId);
   		return IterationIdentifier.newIterationIdentifier(is);
   	}

   	/**
   	 * 设定小版本 Note:the method can't be overriden.
   	 *
   	 * @param i
   	 * @param iterationId
   	 * @throws Exception
   	 */
   	public static void setIterationId(Iterated i, String iterationId)
   			throws Exception {
   		IterationIdentifier ii = newIterationId(iterationId);
   		if (i.getIterationInfo() == null)
   			i.setIterationInfo(IterationInfo.newIterationInfo());
   		VersionControlHelper.setIterationIdentifier(i, ii);
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
            if (n > 0) {
                for (int i = 1; i<=n; i++) {
                    row = sheet.getRow(i);

                    bean = new ProductObjectBean();
                    // 产品编号
                    String number = "";

                    if (row == null || row.getCell(0)==null||row.getCell(0).getStringCellValue()==null
                            ||"".equals(row.getCell(0).getStringCellValue().trim())) {
                        continue;
                    }

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
                    bean.setEnditem("否");
                    /*if(row.getCell(6)==null){
                    	bean.setEnditem("");
                    }else if (row.getCell(6).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setEnditem(row.getCell(6).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(6).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setEnditem(row.getCell(6).getNumericCellValue() + "");
                    }*/
                    // 装配模式
                    bean.setParttype("不可分");
                   /* if(row.getCell(7)==null){
                    	bean.setParttype("");
                    }else if (row.getCell(7).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setParttype(row.getCell(7).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(7).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setParttype(row.getCell(7).getNumericCellValue() + "");
                    }*/
                    //默认追踪代码
                    bean.setDefaulttracecode("未追踪");
                   /* if(row.getCell(8)==null){
                    	bean.setDefaulttracecode("");
                    }else if (row.getCell(8).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setDefaulttracecode(row.getCell(8).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(8).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setDefaulttracecode(row.getCell(8).getNumericCellValue() + "");
                    }*/
                    //默认单位
                    if(row.getCell(6)==null){
                    	bean.setDefaultunit("");
                    }else if (row.getCell(6).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setDefaultunit(row.getCell(6).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(6).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setDefaultunit(row.getCell(6).getNumericCellValue() + "");
                    }
                    //收集部件
                    bean.setHidepartinstructure("否");
                    /*if(row.getCell(10)==null){
                    	bean.setHidepartinstructure("");
                    }else if (row.getCell(10).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setHidepartinstructure(row.getCell(10).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(10).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setHidepartinstructure(row.getCell(10).getNumericCellValue() + "");
                    }*/
                    //虚拟制造部件
                    bean.setPhantom("否");
                    /*if(row.getCell(11)==null){
                    	bean.setPhantom("");
                    }else if (row.getCell(11).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setPhantom(row.getCell(11).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(11).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setPhantom(row.getCell(11).getNumericCellValue() + "");
                    }*/
                    //关重件
                    if(row.getCell(7)==null){
                    	bean.setKeycomponent("");
                    }else if (row.getCell(7).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setKeycomponent(row.getCell(7).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(7).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setKeycomponent(row.getCell(7).getNumericCellValue() + "");
                    }
                    //成套件标识
                    bean.setSetmark("否");
                    /*if(row.getCell(13)==null){
                    	bean.setSetmark("");
                    }else if(row.getCell(13).getCellType()==HSSFCell.CELL_TYPE_STRING){
                    	bean.setSetmark(row.getCell(13).getRichStringCellValue().getString().trim());
                    }else if(row.getCell(13).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                    	bean.setSetmark(row.getCell(13).getNumericCellValue()+"");
                    }*/
                    //零部件分类
                    if(row.getCell(8)==null){
                    	bean.setCtype("");
                    }else if(row.getCell(8).getCellType()==HSSFCell.CELL_TYPE_STRING){
                    	bean.setCtype(row.getCell(8).getRichStringCellValue().getString().trim());
                    }else if(row.getCell(8).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                    	bean.setCtype(row.getCell(8).getNumericCellValue()+"");
                    }
                    //视图
                    bean.setView("Design");
                    /*
                    if(row.getCell(15)==null){
                    	bean.setView("");
                    }else if(row.getCell(15).getCellType()==HSSFCell.CELL_TYPE_STRING){
                    	bean.setView(row.getCell(15).getRichStringCellValue().getString().trim());
                    }else if(row.getCell(15).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                    	bean.setView(row.getCell(15).getNumericCellValue()+"");
                    }*/
                    //密级
                    if(row.getCell(9)==null){
                    	bean.setSecret("公开");
                    }else if(row.getCell(9).getCellType()==HSSFCell.CELL_TYPE_STRING){
                    	String secretValue = row.getCell(9).getRichStringCellValue().getString().trim();
                    	if("".equals(secretValue) || "无".equals(secretValue)){
                    		secretValue = "公开";
                    	}
                    	bean.setSecret(secretValue);
                    }else if(row.getCell(9).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                    	bean.setSecret(row.getCell(9).getNumericCellValue()+"公开");
                    }
                    //当前阶段
                    if(row.getCell(10)==null){
                    	bean.setPhasecode("");
                    }else if(row.getCell(10).getCellType()==HSSFCell.CELL_TYPE_STRING){
                    	bean.setPhasecode(row.getCell(10).getRichStringCellValue().getString().trim());
                    }else if(row.getCell(10).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                    	bean.setPhasecode(row.getCell(10).getNumericCellValue()+"");
                    }
                    //规格
                    if(row.getCell(11)==null){
                    	bean.setCsize("");
                    }else if(row.getCell(11).getCellType()==HSSFCell.CELL_TYPE_STRING){
                    	bean.setCsize(row.getCell(11).getRichStringCellValue().getString().trim());
                    }else if(row.getCell(11).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                        row.getCell(11).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(11).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                    	bean.setCsize(temp);
                    }
                    //图号
                    if(row.getCell(12)==null){
                    	bean.setCindex("");
                    }else if(row.getCell(12).getCellType()==HSSFCell.CELL_TYPE_STRING){
                    	bean.setCindex(row.getCell(12).getRichStringCellValue().getString().trim());
                    }else if(row.getCell(12).getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
                        row.getCell(12).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(12).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                    	bean.setCindex(temp);
                    }
                    //材料
                    if(row.getCell(13)==null){
                    	bean.setMaterial("");
                    }else if (row.getCell(13).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setMaterial(row.getCell(13).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(13).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(13).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(13).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setMaterial(temp);
                    }
                    //材料名称
                    if(row.getCell(14)==null){
                    	bean.setMaterial_name("");
                    }else if (row.getCell(14).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setMaterial_name(row.getCell(14).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(14).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(14).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(14).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setMaterial_name(temp);
                    }
                    //所属成品
                    if(row.getCell(15)==null){
                    	bean.setEnditem_1("");
                    }else if (row.getCell(15).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setEnditem_1(row.getCell(15).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(15).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(15).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(15).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setEnditem_1(temp);
                    }
                    //中文名称
                    if(row.getCell(16)==null){
                    	bean.setCommnon_name("");
                    }else if (row.getCell(16).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setCommnon_name(row.getCell(16).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(16).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(16).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(16).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setCommnon_name(temp);
                    }
                    //工装代号
                    if(row.getCell(17)==null){
                    	bean.setProduct_index("");
                    }else if (row.getCell(17).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setProduct_index(row.getCell(17).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(17).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(17).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(17).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setProduct_index(temp);
                    }
                    //设计单位
                    if(row.getCell(18)==null){
                    	bean.setCompany("");
                    }else if (row.getCell(18).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setCompany(row.getCell(18).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(18).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(18).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(18).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setCompany(temp);

                    }
                    //备注
                    if(row.getCell(19)==null){
                    	bean.setRemark("");
                    }else if (row.getCell(19).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setRemark(row.getCell(19).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(19).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(19).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(19).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setRemark(temp);
                    }
                    //工艺路线
                    if(row.getCell(20)==null){
                    	bean.setRouting("");
                    }else if (row.getCell(20).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setRouting(row.getCell(20).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(20).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setRouting(row.getCell(20).getNumericCellValue() + "");
                    }
                    //材料上标
                    if(row.getCell(21)==null){
                    	bean.setMat_up("");
                    }else if (row.getCell(21).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setMat_up(row.getCell(21).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(21).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(21).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(21).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setMat_up(temp);
                    }
                    //材料下标
                    if(row.getCell(22)==null){
                    	bean.setMat_down("");
                    }else if (row.getCell(22).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setMat_down(row.getCell(22).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(22).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(22).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(22).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setMat_down(temp);
                    }

                  //材料牌号
                    if(row.getCell(23)==null){
                        bean.setMaterial_paihao("");
                    }else if (row.getCell(23).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setMaterial_paihao(row.getCell(23).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(23).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(23).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(23).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setMaterial_paihao(temp);
                    }

                  //品种规格标准号
                    if(row.getCell(24)==null){
                        bean.setPzggbzh("");
                    }else if (row.getCell(24).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setPzggbzh(row.getCell(24).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(24).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(24).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(24).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setPzggbzh(temp);
                    }

                  //技术条件标准号
                    if(row.getCell(25)==null){
                        bean.setJstjbzh("");
                    }else if (row.getCell(25).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setJstjbzh(row.getCell(25).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(25).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(25).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(25).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setJstjbzh(temp);
                    }

                  //精度等级
                    if(row.getCell(26)==null){
                        bean.setJddj("");
                    }else if (row.getCell(26).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setJddj(row.getCell(26).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(26).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(26).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(26).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setJddj(temp);
                    }

                  //质量等级
                    if(row.getCell(27)==null){
                        bean.setZldj("");
                    }else if (row.getCell(27).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setZldj(row.getCell(27).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(27).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(27).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(27).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setZldj(temp);
                    }

                  //材料状态
                    if(row.getCell(28)==null){
                        bean.setClzt("");
                    }else if (row.getCell(28).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setClzt(row.getCell(28).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(28).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(28).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(28).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setClzt(temp);
                    }

                  //材料单位
                    if(row.getCell(29)==null){
                        bean.setCldw("");
                    }else if (row.getCell(29).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setCldw(row.getCell(29).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(29).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setCldw(row.getCell(29).getNumericCellValue() + "");
                    }

                  //增强材料名称
                    if(row.getCell(30)==null){
                        bean.setZqclmc("");
                    }else if (row.getCell(30).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setZqclmc(row.getCell(30).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(30).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(30).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(30).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setZqclmc(temp);
                    }

                  //基体材料名称
                    if(row.getCell(31)==null){
                        bean.setJbclmc("");
                    }else if (row.getCell(31).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setJbclmc(row.getCell(31).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(31).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(31).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(31).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setJbclmc(temp);
                    }

                  //增强材料标准号
                    if(row.getCell(32)==null){
                        bean.setZqclbzh("");
                    }else if (row.getCell(32).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setZqclbzh(row.getCell(32).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(32).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(32).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(32).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setZqclbzh(temp);
                    }

                  //产品代号
                    if(row.getCell(33)==null){
                        bean.setPindex("");
                    }else if (row.getCell(33).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setPindex(row.getCell(33).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(33).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(33).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(33).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setPindex(temp);
                    }

                  //所属型号
					//2023年9月5日  不读所属型号列  所属型号从产品库获取非密型号代号
					String mindex = "";
                    if (row.getCell(34).getCellType() == HSSFCell.CELL_TYPE_STRING) {
						mindex = row.getCell(34).getRichStringCellValue().getString().trim();
                    } else if (row.getCell(34).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(34).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(34).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
						mindex = temp;
                    }
					String productNo = bean.getProductNo();
					if(StringUtils.isNotEmpty(productNo)){
						PDMLinkProduct product = WCUtil.getProductByName(productNo);
						if(product != null) {
							String unmindex = IBAHelper.getIBAStringValue(product, "UNMINDEX");
							bean.setMindex(unmindex == null || "".equals(unmindex) ? mindex : unmindex);
						}else {
							bean.setMindex(mindex);
						}
					}

					//设计者
                    if(row.getCell(35)==null){
                        bean.setDesigner("");
                    }else if (row.getCell(35).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setDesigner(row.getCell(35).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(35).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setDesigner(row.getCell(35).getNumericCellValue() + "");
                    }

                  //二维工程图图幅
                    if(row.getCell(36)==null){
                        bean.setSize("");
                    }else if (row.getCell(36).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setSize(row.getCell(36).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(36).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(36).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(36).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setSize(temp);
                    }

                    //零组件生产类
                    /*
                    if(row.getCell(3)==null){
                        bean.setMTYPE("");
                    }else if (row.getCell(44).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setMTYPE(row.getCell(44).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(44).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(44).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(44).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setMTYPE(temp);
                    }*/


                    //技术条件
                    if(row.getCell(37)==null){
                        bean.setJSTJ("");
                    }else if (row.getCell(37).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setJSTJ(row.getCell(37).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(37).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(37).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(37).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setJSTJ(temp);
                    }

                  //型号牌号
                    if(row.getCell(38)==null){
                        bean.setXHPH("");
                    }else if (row.getCell(38).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setXHPH(row.getCell(38).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(38).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(38).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(38).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setXHPH(temp);
                    }


                    //批次
                    if(row.getCell(39)==null){
                        bean.setBATCH("");
                    }else if (row.getCell(39).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setBATCH(row.getCell(39).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(39).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(39).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(39).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setBATCH(temp);
                    }

                  //存货编码
                    if(row.getCell(40)==null){
                        bean.setCHBM("");
                    }else if (row.getCell(40).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setCHBM(row.getCell(40).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(40).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(40).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(40).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setCHBM(temp);
                    }
                    list.add(bean);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch(WTException e) {
			throw new RuntimeException(e);
		}

		return list;
    }

    private static String analyzeData(List<ProductObjectBean> list, NmCommandBean cb, String fileName) throws WTException,
            RemoteException {
		StringBuffer buffer = new StringBuffer();
		String productSecret = "";

		//保密校验
		WTContainer container = cb.getContainer();
		if(container instanceof PDMLinkProduct){
			PDMLinkProduct product = (PDMLinkProduct) container;
			productSecret = IBAHelper.getIBAStringValue(product, "SECRET");
			if(productSecret != null){
				if(productSecret.indexOf("机密") > -1) {
					if(!AccessAdminUtil.isGroup("重要用户密级组")){
						buffer.append("用户密级低于产品库密级，无法导入。");
						return buffer.toString();
					}
				} else if(productSecret.indexOf("秘密") > -1) {
					if(!AccessAdminUtil.isGroup("重要用户密级组")
							&& !AccessAdminUtil.isGroup("一般用户密级组")){
						buffer.append("用户密级低于产品库密级，无法导入。");
						return buffer.toString();
					}
					if(fileName.indexOf("机密") > -1) {
						buffer.append("文件密级高于产品库密级，无法导入。");
						return buffer.toString();
					}
				} else if(productSecret.indexOf("内部") > -1) {
					if(!AccessAdminUtil.isGroup("重要用户密级组")
							&& !AccessAdminUtil.isGroup("一般用户密级组")
							&& !AccessAdminUtil.isGroup("内部用户密级组")){
						buffer.append("用户密级低于产品库密级，无法导入。");
						return buffer.toString();
					}
					if(fileName.indexOf("机密") > -1 || fileName.indexOf("秘密") >-1) {
						buffer.append("文件密级高于产品库密级，无法导入。");
						return buffer.toString();
					}
				} else if(productSecret.indexOf("公开") > -1) {
					if(fileName.indexOf("机密") > -1 || fileName.indexOf("秘密") >-1 || fileName.indexOf("内部") >-1) {
						buffer.append("文件密级高于产品库密级，无法导入。");
						return buffer.toString();
					}
				}
			}
		}

        List<String> alltops= new ArrayList<String>();
        List<String> allLinks= new ArrayList<String>();
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
        	String setmark = bean.getSetmark();
        	String ctype = bean.getCtype();
        	String view = bean.getView();
        	String phaseCode = bean.getPhasecode();
        	String secret = bean.getSecret();
        	String cindex =  bean.getCindex();
        	String pindex = bean.getPindex();// 产品代号
            String mindex = bean.getMindex();// 所属型号
        	allLinks.add(parentno+"_"+number);

        	if(number!=null &&!"".equals(number)){
	        	if(parentno!=null &&!"".equals(parentno)&&!"无".equals(parentno)){
	        		processIsDeadCycle(buffer,bean,i,list);
	        	}else{
	        		alltops.add(number);
	        	}
	        	if(alltops.contains(number)&&parentno!=null &&!"".equals(parentno)&&!"无".equals(parentno)){
	        		buffer.append("第").append(i+2).append("行是顶级节点不能有父件编号<br>");
	        	}
        	}

        	if(number.indexOf(",")!=-1){
        		buffer.append("第").append(i+2).append("行内“编号”不能含有“,”<br>");
        	}

        	String link = number+"_"+parentno;
        	if(allLinks.contains(link)){
        		buffer.append("子件编号为 ").append(number).append(" 的零部件与父件编号为 ").append(parentno).append(" 的零部件在系统中出现循环使用关系！").append("<br>");
        	}

        	processIsNull(buffer,number,"编号",i);
        	processIsAllUpper(buffer,number,"编号",i);
        	processNum(buffer,number,"编号",i);
        	processCanGBKEncode(buffer,number,"编号",i);
			processCanEnEncode(buffer,number,"编号",i);


        	processIsNull(buffer,name,"名称",i);
        	processNum(buffer,name,"名称",i);
        	processCanGBKEncode(buffer,name,"名称",i);

        	processIsNull(buffer,parentno,"父件编号",i);
        	processNum(buffer,parentno,"父件编号",i);
        	processIsAllUpper(buffer,parentno,"父件编号",i);

        	processIsNull(buffer,amount,"使用数量",i);
        	processIsNum(buffer,amount,"使用数量",i);


        	processIsNull(buffer,productno,"产品名称",i);

        	if(filefolder==null||filefolder.equals(""))
        		buffer.append("第").append(i+2).append("行“位置”不能为空").append("<br>");
        	if(enditem==null||enditem.equals(""))
        		buffer.append("第").append(i+2).append("行“是否成品”不能为空").append("<br>");
        	if(defaultunit==null||defaultunit.equals(""))
        		buffer.append("第").append(i+2).append("行“单位”不能为空").append("<br>");

        	if(setmark==null||setmark.equals(""))
        		buffer.append("第").append(i+2).append("行“成套件标识”不能为空").append("<br>");
        	if(ctype==null||ctype.equals("")){
        		buffer.append("第").append(i+2).append("行“零部件分类”不能为空").append("<br>");
        	}else{
        		if(!Constants.ctypes.contains(ctype)){
        			buffer.append("第").append(i+2).append("行“零部件分类”只有为：自制件,标准件,外购件,外配套件,外协件,辅助材料,带料委外件,不带料委外件,元器件,主要材料").append("<br>");
        		}
        	}

        	if("自制件".equals(ctype)){
        		if(keycomponent==null||keycomponent.equals("")){
        			buffer.append("第").append(i+2).append("行为自制件，“关重件”不能为空").append("<br>");
        		}else{
        			if(!Constants.keycomponents.contains(keycomponent)){
            			buffer.append("第").append(i+2).append("行“关重件”只能为：N/G/Z，且不能存在空格！").append("<br>");
            		}
        		}
        		if(secret==null||secret.equals("")){
        			buffer.append("第").append(i+2).append("行为自制件，“密级”不能为空").append("<br>");
        		}else {
					if(productSecret != null){
						if(productSecret.indexOf("秘密") > -1) {
							if(secret.indexOf("机密") > -1) {
								buffer.append("EBOM密级高于产品库密级，无法导入。");
								return buffer.toString();
							}
						} else if(productSecret.indexOf("内部") > -1) {
							if(secret.indexOf("机密") > -1 || secret.indexOf("秘密") >-1) {
								buffer.append("EBOM密级高于产品库密级，无法导入。");
								return buffer.toString();
							}
						} else if(productSecret.indexOf("公开") > -1) {
							if(secret.indexOf("机密") > -1 || secret.indexOf("秘密") >-1 || secret.indexOf("内部") >-1) {
								buffer.append("EBOM密级高于产品库密级，无法导入。");
								return buffer.toString();
							}
						}
					}
				}

        		if(phaseCode!=null){
        			phaseCode = phaseCode.replaceAll(" ", "");
        			phaseCode = phaseCode.replaceAll("　", "");
        		}
        		if(phaseCode==null||phaseCode.equals("")){
        			buffer.append("第").append(i+2).append("行为自制件，“当前阶段”不能为空").append("<br>");
        		}else{
        			if(!Constants.phases.contains(phaseCode)){
            			buffer.append("第").append(i+2).append("行“当前阶段”只能为：Y,M,M1,M2,C,C1,C2,C3,S,S1,S2,S3,Z,Z1,Z2,Z3,D,D1,D2,D3,G,P,N,A,MC,CS,B，且不能存在空格！").append("<br>");
            		}
        		}
        		if(cindex==null||cindex.equals("")){
        			buffer.append("第").append(i+2).append("行为自制件，“图号”不能为空").append("<br>");
        		}
        		if(pindex==null||pindex.equals("")){
        			buffer.append("第").append(i+2).append("行为自制件，“产品代号”不能为空").append("<br>");
        		}
        		if(mindex==null||mindex.equals("")){
        			buffer.append("第").append(i+2).append("行为自制件，“所属型号”不能为空").append("<br>");
        		}

        	}
        	//判断模板中输入的产品名称与选择的入口产品是否一致
            WTContainer conref = cb.getContainer();
            String confString = conref.getName();
            if(!confString.equals(productno))
            	buffer.append("第").append(i+2).append("行“产品名称”").append(productno).append("与数据导入的入口产品").append(confString).append("不一致！")
            			.append("<br>");
            //默认视图选择为Design
//        	if(!view.equals("Design")){
//        		buffer.append("第").append(i+3).append("行“视图”种类默认需为Design").append("<br>");
//        	}
            //判断枚举类型值
 //       	if(!(parttype.equals("可分")||parttype.equals("不可分")||parttype.equals("组件"))){
 //       		buffer.append("第").append(i+3).append("行“装配模式”必须为“可分；不可分；组件”中一类").append("<br>");
 //       	}
        	if(filefolder!=null&&!filefolder.equals("")&&!(filefolder.substring(0, 15).equals("/Default/01设计文件"))){
        		buffer.append("第").append(i+2).append("行“位置”填写不规范，应以“/Default/01设计文件”为起始").append("<br>");
        	}
            if (filefolder != null && !filefolder.equals("") && filefolder.contains("\\")) {
                buffer.append("第").append(i + 2).append("行“位置”填写不规范，应以“/”分隔文件夹目录").append("<br>");
            }
        }
        return buffer.toString();
    }

    private static void processCanGBKEncode(StringBuffer buffer, String value, String label, int i) {
    	if(value!=null&&!"".equals(value)){
    		if(!java.nio.charset.Charset.forName("gbk").newEncoder().canEncode(value)){
    			buffer.append("第").append(i+2).append("行“"+label+"”的存在特殊符号，请使用GBK支持的字符！").append("<br>");
    		}
    	}


	}
	private static void processCanEnEncode(StringBuffer buffer, String value, String label, int i) {
		if(value!=null&&!"".equals(value)){
			if(value.contains("（")||value.contains("）")){
				buffer.append("第").append(i+2).append("行“"+label+"”的不允许有中文括号！").append("<br>");
			}else if(value.endsWith("/")){
				buffer.append("第").append(i+2).append("行“"+label+"”的末尾不允许有/").append("<br>");
			}
		}

	}

	private static void processIsDeadCycle(StringBuffer buffer,
			ProductObjectBean bean, int i,
			List<ProductObjectBean> list) throws WTException {
    	String number = bean.getNumber();
    	String parentNum = bean.getParentNumber();
    	if(number!=null &&!"".equals(number)&&parentNum!=null &&!"".equals(parentNum)){
    		WTPart p1 = CSCPart.getPart(number);
        	WTPart p2 = CSCPart.getPart(parentNum);
        	if(p1!=null  && p2!=null){
        		boolean isUsed = checkUsedLinked(p2,number);
        		if(isUsed) {
            		buffer.append("子件编号为 ").append(number).append(" 的零部件与父件编号为 ").append(parentNum).append(" 的零部件在系统中出现循环使用关系！").append("<br>");
            	}else{
            		isUsed = checkUsesLinked(p1,parentNum);
            		if(isUsed) {
                		buffer.append("子件编号为 ").append(number).append(" 的零部件与父件编号为 ").append(parentNum).append(" 的零部件在系统中出现循环使用关系！").append("<br>");
            		}
            	}
        	}
    	}


	}

	public static void processIsNull( StringBuffer buffer,String value,String label,int i){
    	if(value==null||value.equals("")){
        	buffer.append("第").append(i+2).append("行“"+label+"”不能为空").append("<br>");
    	}
    }
    public static void processIsAllUpper( StringBuffer buffer,String value,String label,int i){
    	if(value!=null&&!"".equals(value)){
    		if(isAcronym(value)){
    			buffer.append("第").append(i+2).append("行“"+label+"”不能有小写字符").append("<br>");
    		}
    	}
    }
    public static void processNum( StringBuffer buffer,String value,String label,int i){
    	if(value!=null&&!"".equals(value)){
    		if(value.length()>60){
    			buffer.append("第").append(i+2).append("行“"+label+"”的字符长度不能大于60").append("<br>");
    		}
    	}
    }

    public static void processIsNum( StringBuffer buffer,String value,String label,int i){
    	if(value!=null&&!"".equals(value)){
    		boolean isNum = value.matches("[0-9]+");
    		if(!isNum){
    			buffer.append("第").append(i+2).append("行“"+label+"”必须为数字").append("<br>");
    		}
    	}
    }
	public static boolean isAcronym(String word) {
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if (Character.isLowerCase(c)) {
				return true;
			}
		}
		return false;
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
    private static Folder getFolder(String path, WTContainer con) throws WTException {
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


}
