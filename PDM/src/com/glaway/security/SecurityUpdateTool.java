/**
 * 南京国睿信维软件有限公司
 */
package com.glaway.security;

import com.glaway.mpm.constants.DocumentConstants;
import com.glaway.security.bean.SecretObject;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import ext.casc.ixb.ExpImpLogger;
import ext.casc.securitymgr.SecurityLabelDataHelper;
import ext.casc.util.IBAHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import wt.change2.WTChangeOrder2;
import wt.change2._WTChangeOrder2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionMgr;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 类功能：从Excel中读取数据密级，更新对象密级属性
 * 执行命令：windchill com.glaway.security.SecurityUpdateTool -u <用户名> -p <密码> -f <文件名>
 *
 * @author lichenglei
 * @date 2020/6/2
 */
public class SecurityUpdateTool implements RemoteAccess {

    private static Logger LOGGER = Logger.getLogger(SecurityUpdateTool.class);

    private static final String SUFFIX_XLS = ".xls";
    private static final String SUFFIX_XLSX = ".xlsx";

    public static void main(final String[] args) {
        final int argsLength = args.length;
        String userName = null;
        String password = null;
        String fileName = null;

        if (argsLength == 0) {
            printUsage();
        }

        for (int i = 0; i < argsLength; i++) {
            if (args[i].equals("-u")) {
                if (++i < argsLength)
                    userName = new String(args[i]);
            } else if (args[i].equals("-p")) {
                if (++i < argsLength)
                    password = new String(args[i]);
            } else if (args[i].equals("-f")) {
                if (++i < argsLength)
                    fileName = new String(args[i]);
            } else {
                System.out.println("Problem with command line argument: " + args[i] + " " + args[i + 1]);
                for (int j = 0; j < argsLength; j++) {
                    System.out.print(args[j] + " ");
                }
                System.out.println("");
                printUsage();
            }
        }

        //检验文件名
        if (fileName == null || fileName.length() == 0) {
            System.out.println("Please specify a file name.");
            printUsage();
        } else {

        }
        if (userName != null && userName.length() > 0) {
            RemoteMethodServer.getDefault().setUserName(userName);
            if (password != null) {
                RemoteMethodServer.getDefault().setPassword(password);
            }
        }

        try {
            execute(userName, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("----->执行完毕");
    }


    private static void execute(String userName, String fileName) throws IOException, WTException, WTPropertyVetoException {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "doUpdate";
            Class[] types = {String.class, String.class};
            Object[] vals = {userName, fileName};
            RemoteMethodServer rms = RemoteMethodServer.getDefault();
            try {
                rms.invoke(method, SecurityUpdateTool.class.getName(), null, types, vals);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            doUpdate(userName, fileName);
        }
    }

    /**
     * 读取多个Excel文件
     * @param userName
     * @param fileName
     * @throws IOException
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public static void doUpdate(String userName, String fileName) throws IOException, WTException {

        File file = new File(fileName);
        if(null == file) {
            return ;
        }
        File[] files = file.listFiles();
	    if (files != null) {
		    for (File value : files) {
			    Workbook workbook = null;
			    InputStream isteam = null;
			    try {
				    if (value.isDirectory()) {
					    continue;
				    }
				    isteam = SecurityUpdateTool.class.getResourceAsStream(value.getName());
				    isteam = Files.newInputStream(value.toPath());
				    String fileSuffix = value.getName().substring(value.getName().indexOf("."));
				    if (SUFFIX_XLSX.equalsIgnoreCase(fileSuffix)) {
					    workbook = new XSSFWorkbook(isteam);
				    } else if (SUFFIX_XLS.equalsIgnoreCase(fileSuffix)) {
					    workbook = new HSSFWorkbook(isteam);
				    } else {
					    continue;
				    }
				    List<SecretObject> result = readExcel2obj(workbook);
				    updateSecretAttribute(userName, result);
			    } finally {
				    if (isteam != null) {
					    isteam.close();
				    }
			    }
		    }
	    }
    }

    /**
     * 读取Excel数据
     *
     * @param workbook
     * @return
     */
    private static List<SecretObject> readExcel2obj(Workbook workbook) {
        List<SecretObject> result = new ArrayList();
        Sheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();

        for (int r = 1; r <= lastRowNum; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                LOGGER.debug("Row " + r + " is null，Skipped.");
                continue;
            }
            SecretObject so = new SecretObject();
            String number = null;
            //第二列，编号
            Cell cell = row.getCell(1);
            if (cell != null) {
                number = cell.getStringCellValue();
                if (number == null || number.isEmpty()) {
                    LOGGER.debug("Row: " + r + " number is empty，Skipped.");
                    continue;
                }
                so.setNumber(number);
            }

            String secret = null;
            //第三列，密级
            cell = row.getCell(2);
            if (cell != null) {
                secret = cell.getStringCellValue();
                if (secret == null || secret.isEmpty()) {
                    LOGGER.debug("Row: " + r + " ，secret: " + secret + " ,secret attribute is empty，Skipped.");
                    continue;
                }
                so.setMiji(secret);
            }

            String type = null;
            //第四列，类型
            cell = row.getCell(3);
            if (cell != null) {
                type = cell.getStringCellValue();
                if (type == null || type.isEmpty()) {
                    LOGGER.debug("Row: " + r + " ，type: " + type + " ,secret attribute is empty，Skipped.");
                    continue;
                }
                so.setType(type);
            }
            result.add(so);
        }
        return result;
    }


    private static void updateSecretAttribute(final String userName, final List<SecretObject> result) throws WTException {
        initializeUser(userName);

        boolean previous = SessionServerHelper.manager.setAccessEnforced(false);

        ExpImpLogger mijiLog = ExpImpLogger.getInstance();
        for (SecretObject so : result) {
            String number = so.getNumber();
            String secretValue = so.getMiji();
            String type = so.getType();
            mijiLog.log("开始设置类型为->" + type + ",编号为->" + number + "的密级为->" + secretValue);
            if (StringUtils.isEmpty(number) || StringUtils.isEmpty(secretValue) || StringUtils.isEmpty(type)) {
                continue;
            }
            try {

               if(null == SecurityLabelDataHelper.miji.get(secretValue)) {
                   mijiLog.log("number: " + number + " Secret level does not exist: " + secretValue);
                   continue;
               }
                // 文档
                if (StringUtils.isNotEmpty(type)) {
                    type = type.trim();
                    WTObject doc = null;
                    QueryResult queryResult = null;
                    if (type.contains("WTDocument")) {
                        queryResult = getDocumentByNumber(number);
                    } else if (type.contains("Design") || type.contains("Manufacturing")) {
                        queryResult = getPartByNumber(number);
                    } else if ("图纸".equals(type)) {
                        queryResult = getEpmByNumber(number);
                    } else if ("ECN".equals(type)) {
                        doc = getWTChangeOrderByNumber(number);
                        if (doc != null) {
                            IBAHelper.setIBAStringValue(doc, DocumentConstants.IBA_SECRET, secretValue);
                        }else {
                            mijiLog.log("找不到编号为：" +number + "的" + type);
                        }
                    } else if ("工艺规程".equals(type)) {
                        queryResult = getProcessPlanByNumber(number);
                    }
                    if (queryResult != null) {
                        while (queryResult.hasMoreElements()) {
                            doc = (WTDocument) queryResult.nextElement();
                            IBAHelper.setIBAStringValue(doc, DocumentConstants.IBA_SECRET, secretValue);
                        }
                    } else {
                        mijiLog.log("找不到编号为：" +number + "的" + type);
                    }
                    SecurityLabelDataHelper.setSecret(doc);
                }
            } catch (Exception e) {
                mijiLog.log("设置时候出现错误=" + e.getLocalizedMessage());
                e.printStackTrace();
            } finally{
                SessionServerHelper.manager.setAccessEnforced(previous);
            }
        }

    }

    /**
     * 设置执行账户
     *
     * @param userName 执行账户
     */
    private static void initializeUser(final String userName) {
        if (userName != null && !userName.equals("")) {
            try {
                SessionMgr.setPrincipal(userName);
            } catch (WTException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打印提示信息
     */
    private static void printUsage() {
        System.out.println("Usage: windchill com.glaway.security.SecurityUpdateTool [-u user name] [-p user password] [-f xxxx.xlsx]");
        System.exit(1);
    }

    public static QueryResult getDocumentByNumber(String number) throws WTException {
        QuerySpec qs = new QuerySpec(WTDocument.class);
        int[] index = {0};
        SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number);
        qs.appendWhere(sc, index);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qs);
        return qResult;
    }

    public static QueryResult getPartByNumber(String number) throws WTException {
        QuerySpec qSpec = new QuerySpec(WTPart.class);
        int[] index = {0};
        SearchCondition sCondition = new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number);
        qSpec.appendWhere(sCondition, index);
	    return PersistenceHelper.manager.find((StatementSpec) qSpec);
    }

    public static QueryResult getEpmByNumber(String epmNumber) throws WTException {
        QuerySpec qs = new QuerySpec(EPMDocument.class);
        SearchCondition sc = new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, SearchCondition.EQUAL, epmNumber);
        int[] index = {0};
        qs.appendWhere(sc, index);
	    return PersistenceHelper.manager.find((StatementSpec) qs);
    }

    public static QueryResult getProcessPlanByNumber(String number) throws WTException {
        QueryResult qr;
        QuerySpec qs = new QuerySpec(MPMProcessPlan.class);
        qs.appendWhere(new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER, SearchCondition.EQUAL, number, false));
        qr = PersistenceHelper.manager.find((StatementSpec) qs);
        return qr;
    }


    public static WTChangeOrder2 getWTChangeOrderByNumber(String number) throws WTException {
        QuerySpec qs = new QuerySpec(WTChangeOrder2.class);
        //int index[] = { 0 };
        SearchCondition sc = new SearchCondition(WTChangeOrder2.class, _WTChangeOrder2.NUMBER, SearchCondition.EQUAL, number);
        qs.appendWhere(sc);
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qs);
        LatestConfigSpec lcs = new LatestConfigSpec();
        qResult = lcs.process(qResult);
        WTChangeOrder2 request = null;
        if (qResult.hasMoreElements()) {
            request = (WTChangeOrder2) qResult.nextElement();
        }
        return request;
    }
}
