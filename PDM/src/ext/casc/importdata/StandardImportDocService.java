package ext.casc.importdata;

import java.beans.PropertyVetoException;
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
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.doc.CSCDoc;
import ext.casc.part.CSCPart;
import ext.casc.util.IBAUtility;




public class StandardImportDocService extends StandardManager implements ImportDocService {

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

    public static StandardImportDocService newStandardImportDocService()
            throws WTException {
        StandardImportDocService instance = new StandardImportDocService();
        instance.initialize();
        return instance;
    }

    /**
     * ��ȡEXCEL�����ݣ����Ҵ����ĵ������ϴ������ݡ�
     *
     * {@inheritDoc}
     *
     * @throws RemoteException
     */
    @Override
    public String importObjects(NmCommandBean cb, File file, String fileName, List<String> allFileName)
            throws WTException, RemoteException {
    	 System.out.println("------------<<<<>>>"+cb.getContainer().getName());
    	 StringBuffer buffer = new StringBuffer();
        // 获取上传压缩文件的文件列表
          compressZIPData(file, fileName);

        // 获取EXCEL文件
        File xlsFile = new File( tmp_dir +fileName);

        // ��ȡEXCEL������
        List<DocObjectBean> list = readExcel(xlsFile);

        //解析EXCEL
        String returnValue = analyzeData(list, allFileName);

        if (returnValue != null && !"".equals(returnValue)) {
           return returnValue;
        } else {

            Transaction tran = new Transaction();

            try {
                tran.start();
                //初始化类型映射表
                Map<String, String> map = initializeTypeMap();
                int i=0;
                for (DocObjectBean bean : list) {
                	++i;
                    String number = bean.getNumber();
                    String name = bean.getName();
                    String dalei = bean.getDalei();
                    String xiaolei = bean.getXiaolei();
                    String relatedpart = bean.getRelatedpart();
                    String filename = bean.getFilename();
                    String folderpath = bean.getFolderpath();
                    String designcompany = bean.getDesigncompany();
                    String secret = bean.getSecret();
                    String designdepart = bean.getDesignpart();
                    String keycomponent = bean.getKeycomponent();
                    String phasecode = bean.getPhasecode();
                    String filestatus = bean.getFilestatus();
                    String pplanNumber = bean.getPplanNumber();

                    WTDocument doc = CSCDoc.getDoc(number);
                    WTPart part = CSCPart.getPartByNumberAndViewName(relatedpart,"Design");

                    //判断导入的文档是否在系统中存在
                    if(doc!=null){
                    	IBAUtility ibaUtility = new IBAUtility(doc);
                		buffer.append("编号为：").append(number).append("的对象在系统中已经存在！未执行导入，若确定更新，请手动执行").append("<br>");

                		if(dalei.equals("典型工艺") || dalei.equals("通用工艺")|| dalei.equals("国家标准")) {
                         	ibaUtility.setIBAValue("SECRET", secret);
                         	ibaUtility.setIBAValue("PPNUMBER",pplanNumber);
                         }

                		 try {
							doc = (WTDocument) ibaUtility.updateAttributeContainer(doc);
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                         ibaUtility.updateIBAHolder(doc);
                         doc = (WTDocument) PersistenceHelper.manager.refresh(doc);

                         if(part==null){
                         	buffer.append("编号为：").append(number).append("所关联的部件").append(relatedpart).append("在系统中不存在，没有建立关联关系！<br>");
                         }else{
                             boolean createFlag = CSCPart.createPartAssociateDoc(part, doc);
                             if(createFlag){
                             	System.out.println("关联关系建立成功");
                             }
                         }
                    }
                    //若文档不存在，新建文档及关联关系
                    else{
                        WTContainer wtContainer = cb.getContainer();
                        //创建文档
                       	HashMap<String, String> attrmap = new HashMap<String, String>();
                       	HashMap<String, String> IBAmap = new HashMap<String, String>();
                       	attrmap.put("NUMBER", number);
                       	attrmap.put("NAME", name);
                       	attrmap.put("DALEI", dalei);
                       	attrmap.put("RELATEDPART", relatedpart);
                       	attrmap.put("FILEFOLDER", folderpath);
                        IBAmap.put("SUBTYPE", xiaolei);
                        IBAmap.put("COMPANY", designcompany);
                        IBAmap.put("SECRET", secret);
                        IBAmap.put("DEPT", designdepart);
                        IBAmap.put("KEYCOMPONENT", keycomponent);
                        IBAmap.put("PHASE_CODE", phasecode);
                        IBAmap.put("PROCESSSTATUS", filestatus);
                        IBAmap.put("PPNUMBER", pplanNumber);

                        //创建文档类型
                        String type = map.get(dalei);
                        System.out.println("-------<<<文档大类是："+type);
                        System.out.println("-------<<<IBAmap："+IBAmap);
                        System.out.println("-------<<<attrmap："+attrmap);

                        WTDocument document = CSCDoc.createDoc(number, name, attrmap,IBAmap,type,wtContainer);

                        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);

                        // ����Ϊ�־ö���
                        //document = (WTDocument) PersistenceHelper.manager.save(document);

                        FormatContentHolder holder = (FormatContentHolder) ContentHelper.service.getContents(document);
                        holder = (FormatContentHolder) PersistenceHelper.manager.refresh(holder);
                        String path = tmp_dir + "files" + File.separator + filename;
                        ApplicationData data = ApplicationData.newApplicationData(holder);
                        data.setRole(ContentRoleType.PRIMARY);
                        data.setFileName(name);
                        data.setUploadedFromPath(name);
                        data = ContentServerHelper.service.updateContent(holder, data, path);
                        holder = ContentServerHelper.service.updateHolderFormat(holder);
                        PersistenceServerHelper.manager.update(data);

                        SessionServerHelper.manager.setAccessEnforced(flag);

                        //建立部件与文档关联关系
                        if(part==null){
                        	buffer.append("编号为：").append(number).append("所关联的部件").append(relatedpart).append("在系统中不存在，没有建立关联关系！<br>");
                        }else{
                            boolean createFlag = CSCPart.createPartAssociateDoc(part, document);
                            if(createFlag){
                            	System.out.println("关联关系建立成功");
                            }
                        }
                    }
                }

                // �ύ����
                tran.commit();
                tran = null;

            } catch (WTPropertyVetoException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }catch (PropertyVetoException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //
                if (tran != null) {
                    tran.rollback();
                }
            }
            //返回警告信消息
            if(buffer.toString()!=null || !(buffer.toString().equals(""))){
            	buffer.insert(0, '1');
            	return buffer.toString();
            }
        }

        return null;
    }

    /**
     *
     *
     */
    public List<String> compressZIPData(File zipFile, String fileName) {
        // ��ѹѹ����ʱ������ո�Ŀ¼�µ������ļ�
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

        File tempFile = new File(tmp_dir + fileName);
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            inBuff = new BufferedInputStream(new FileInputStream(zipFile));

            // �½��ļ��������������л���
            outBuff = new BufferedOutputStream(new FileOutputStream(tempFile));

            // ��������
            byte[] b = new byte[BUFFER * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // ˢ�´˻���������
            outBuff.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // �ر���
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
        // ��ѹ
        if (tempFile != null && tempFile.length() > 0 && fileName.endsWith(".zip")) {
            try {
                // ָ��ѹ��������ļ���ѹ��Ŀ¼
                String dir = tmp_dir + "files" + File.separator;
                allFileName = unzip(tmp_dir + fileName, dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return allFileName;
    }

    /**
     * ��ѹ�ļ���ָ��Ŀ¼
     *
     * @param zipFile
     *            ��Ҫ��ѹ��ѹ�����ļ�
     * @param dest
     *            ѹ�����ŵ�Ŀ¼·��
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

            // �������ļ���ѹ��filesĿ¼��
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
     * ��ȡEXCEL���
     *
     * @param xlsFile
     *            EXCEL����ļ�
     * @return List<DocObjectBean>
     */
    private static List<DocObjectBean> readExcel(File xlsFile) {
      	System.out.println("开始解析EXCEL:");
        List<DocObjectBean> list = new ArrayList<DocObjectBean>();
        try {
            InputStream is = new FileInputStream(xlsFile);
            HSSFWorkbook wb = new HSSFWorkbook(is);
            HSSFSheet sheet = wb.getSheetAt(0);

            int n = sheet.getLastRowNum() - sheet.getFirstRowNum();
            System.out.println("------行数:"+n);
            HSSFRow row = null;

            DocObjectBean bean = null;
            if (n > 1) {
                for (int i = 2; i<=n; i++) {
                    row = sheet.getRow(i);

                    bean = new DocObjectBean();
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
                    // 文档大类
                    if(row.getCell(2)==null){
                    	bean.setDalei("");
                    }else if (row.getCell(2).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setDalei(row.getCell(2).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(2).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(2).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(2).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setDalei(temp);
                    }

                    // 文档小类
                   if(row.getCell(3)==null){
                   	bean.setXiaolei("");
                   }else if (row.getCell(3).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setXiaolei(row.getCell(3).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(3).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(3).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(3).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setXiaolei(temp);
                    }
                    System.out.println("-----parentno:"+bean.getXiaolei());
                    //关联部件
                    if(row.getCell(4)==null){
                    	bean.setRelatedpart("");
                    }else if (row.getCell(4).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setRelatedpart(row.getCell(4).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(4).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(4).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(4).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setRelatedpart(temp);
                    }
                    //物理文件名称
                    if(row.getCell(5)==null){
                    	bean.setFilename("");
                    }else if (row.getCell(5).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setFilename(row.getCell(5).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(5).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(5).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(5).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setFilename(temp);
                    }
                    // PDM文件路径
                    if(row.getCell(6)==null){
                    	bean.setFolderpath("");
                    }else if (row.getCell(6).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setFolderpath(row.getCell(6).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(6).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setFolderpath(row.getCell(6).getNumericCellValue() + "");
                    }
                    // 设计单位
                    if(row.getCell(7)==null){
                    	bean.setDesigncompany("");
                    }else if (row.getCell(7).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setDesigncompany(row.getCell(7).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(7).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(7).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(7).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setDesigncompany(temp);
                    }
                    //文档密级
                    if(row.getCell(8)==null){
                    	bean.setSecret("");
                    }else if (row.getCell(8).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setSecret(row.getCell(8).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(8).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setSecret(row.getCell(8).getNumericCellValue() + "");
                    }
                    //编制部门
                    if(row.getCell(9)==null){
                    	bean.setDesignpart("");
                    }else if (row.getCell(9).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setDesignpart(row.getCell(9).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(9).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        row.getCell(9).setCellType(HSSFCell.CELL_TYPE_STRING);
                        String temp = row.getCell(9).getStringCellValue().trim();
                        if (temp.indexOf(".")>-1) {
                            temp = String.valueOf(new Double(temp));
                        }
                        bean.setDesignpart(temp);
                    }
                    //关重件标识
                    if(row.getCell(10)==null){
                    	bean.setKeycomponent("");
                    }else if (row.getCell(10).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setKeycomponent(row.getCell(10).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(10).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setKeycomponent(row.getCell(10).getNumericCellValue() + "");
                    }
                    //阶段标识
                    if(row.getCell(11)==null){
                    	bean.setPhasecode("");
                    }else if (row.getCell(11).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setPhasecode(row.getCell(11).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(11).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setPhasecode(row.getCell(11).getNumericCellValue() + "");
                    }
                    //文件状态
                    if(row.getCell(12)==null){
                    	bean.setFilestatus("");
                    }else if (row.getCell(12).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setFilestatus(row.getCell(12).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(12).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setFilestatus(row.getCell(12).getNumericCellValue() + "");
                    }

                    //工艺文件编号
                    if(row.getCell(13)==null){
                    	bean.setPplanNumber("");
                    }else if (row.getCell(13).getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        bean.setPplanNumber(row.getCell(13).getRichStringCellValue().getString().trim());
                    } else if (row.getCell(13).getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        bean.setPplanNumber(row.getCell(13).getNumericCellValue() + "");
                    }

                    list.add(bean);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static String analyzeData(List<DocObjectBean> list, List<String> allFileName) throws WTException,
            RemoteException {
        StringBuffer buffer = new StringBuffer();
        int j = 0;
        for(String s:allFileName){

        		System.out.println("引入文件名称：〈〈〈"+allFileName.get(j));
        		++j;

        }
        for (int i = 0; i < list.size(); i++) {
            DocObjectBean bean = list.get(i);
            String number = bean.getNumber();
            String name = bean.getName();
            String dalei = bean.getDalei();
            String xiaolei = bean.getXiaolei();
            String relatedpart = bean.getRelatedpart();
            String filename = bean.getFilename();
            String folderpath = bean.getFolderpath();
            String designcompany = bean.getDesigncompany();
            String secret = bean.getSecret();
            String designdepart = bean.getDesignpart();
            String keycomponent = bean.getKeycomponent();
            String phasecode = bean.getPhasecode();
            String filestatu = bean.getFilestatus();
            String pplanNumber = bean.getPplanNumber();

        	if(number.equals("")&&!(dalei.equals("工艺附图")||dalei.equals("数控程序")||dalei.equals("设计方案预审单")))
        		buffer.append("第").append(i+3).append("行“编号”不能为空(工艺附图/数控程序/设计反案预审单可为空)").append("<br>");
        	if(name==null||name.equals(""))
        		buffer.append("第").append(i+3).append("行“名称”不能为空").append("<br>");
        	if(dalei==null||dalei.equals(""))
        		buffer.append("第").append(i+3).append("行“文档大类”不能为空").append("<br>");
        	if(folderpath==null||folderpath.equals(""))
        		buffer.append("第").append(i+3).append("行“PDM系统存储文件夹路径”不能为空").append("<br>");
        	if((designcompany==null||designcompany.equals(""))&&!(dalei.equals("典型工艺")||dalei.equals("通用工艺")))
        		buffer.append("第").append(i+3).append("行“设计单位”不能为空").append("<br>");
        	if(filename==null||filename.equals(""))
        		buffer.append("第").append(i+3).append("行“物理文件名称”不能为空").append("<br>");
        	if(!folderpath.equals("")&&!(folderpath.substring(0, 8).equals("/Default"))){
        		buffer.append("第").append(i+3).append("行“位置”填写不规范，应以“/Default”为起始").append("<br>");
        	}
        	if(!folderpath.equals("")&&folderpath.contains("\\")){
        		buffer.append("第").append(i+3).append("行“位置”填写不规范，应以“/”分隔文件夹目录").append("<br>");
            }
        	if(!filename.equals("")&&!allFileName.contains(filename)){
        		buffer.append("第").append(i+3).append("行“物理文件名称”中指定的文件").append(filename).append("在上传的压缩包中不存在").append("<br>");
        	}
            //判断其他必填项
			if(dalei.equals("工艺规程")||dalei.equals("工艺技术通知单")
					||dalei.equals("技术通知单")||dalei.equals("临时工艺文件")
					||dalei.equals("图样")||dalei.equals("文字或表格类设计文件")){
				if(secret.equals("")){
	        		buffer.append("第").append(i+3).append("行“密级”不应为空").append("<br>");
				}
				if(phasecode.equals("")){
					buffer.append("第").append(i+3).append("行“阶段标记”不应为空").append("<br>");
				}
				if(dalei.equals("工艺规程")){
					if(designdepart.equals("")){
						buffer.append("第").append(i+3).append("行“编制部门”不应为空").append("<br>");
					}
					if(keycomponent.equals("")){
						buffer.append("第").append(i+3).append("行“关重件标识”不应为空").append("<br>");
					}
					if(filestatu.equals("")){
						buffer.append("第").append(i+3).append("行“文件状态”不应为空").append("<br>");
					}
				}
			}
			if(dalei.equals("软件文档")||dalei.equals("图样")||dalei.equals("文字或表格类设计文件")||dalei.equals("研试文件")||dalei.equals("质量报告")){
				if (xiaolei.equals("")) {
					buffer.append("第").append(i+3).append("行“文档小类”不应为空").append("<br>");

				}
			}

			if(dalei.equals("典型工艺")||dalei.equals("通用工艺")||dalei.equals("国家标准")) {
				if(secret == null || secret.equals("")){
	        		buffer.append("第").append(i+3).append("行“密级”不应为空").append("<br>");
				}

				if(pplanNumber==null||pplanNumber.equals("")) {
        			buffer.append("第").append(i+3).append("行“工艺文件编号”不能为空").append("<br>");
        		}
			}
        }

        return buffer.toString();
    }

    /**
     *
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

    private static Map<String, String> initializeTypeMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("工艺附图", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_DRAWING");
        map.put("工艺规程", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN");
        map.put("工艺技术通知单", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_NOTICE");
        map.put("工艺技术协议", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.TECHNOLOGY_AGREEMENT");
        map.put("临时工艺文件", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.TEMP_PROCESS_DOC");
        map.put("数控程序", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.149.NC_CODE");
        map.put("外来工艺文件", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.EXTERNAL_PROCESS_DOC");

        map.put("设计方案预审单", "wt.doc.WTDocument|casc.sast.149.PREVIEWFORM");

        map.put("技术通知单", "wt.doc.WTDocument|casc.sast.149.DISIGN_DOC|casc.sast.149.TECHNOTICE_DOC");
        map.put("软件文档", "wt.doc.WTDocument|casc.sast.149.DESIGN_DOC|casc.sast.149.SOFTWARE_DOC");
        map.put("图样", "wt.doc.WTDocument|casc.sast.149.DESIGN_DOC|casc.sast.149.DRAWING_DOC");
        map.put("文字或表格类设计文件", "wt.doc.WTDocument|casc.sast.149.DESIGN_DOC|casc.sast.149.WRITINGORFORM_DOC");

        map.put("研试文件", "wt.doc.WTDocument|casc.sast.149.RESEARCH_DOC");

        map.put("质量报告", "wt.doc.WTDocument|casc.sast.149.QA_REPORT");

        map.put("典型工艺", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.DX_PROCESS_DOC");
        map.put("通用工艺", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.TY_PROCESS_DOC");
        map.put("国家标准", "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.GUOJIABIAOZHUN");

        return map;
    }
}
