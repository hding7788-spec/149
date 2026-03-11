package ext.casc.gongshidinge.processor;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.glaway.mpm.util.PropertiesUtil;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import ext.casc.access.AccessAdminUtil;
import ext.casc.constants.Constants;
import ext.casc.sop.util.StringUtil;
import ext.casc.util.ExcelFileGenerator;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;
import ext.casc.workflow.WorkflowHelper;
import ext.ptc.ViewWIHelper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTArrayList;
import wt.inf.container.WTContainer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class ExportGongshiDataProcessor implements Serializable {

    private static final long serialVersionUID = 1L;

    public static File export(String oid) throws WTException, IOException {
        String tempPath = PropertiesUtil.getTempPath();
        String tPath = tempPath + File.separator + DateTime.now().toDateStr() + "_" + "工时定额列表.xls";

        ArrayList<String> head = new ArrayList<String>();
        head.add("部件图号");
        head.add("部件名称");
        head.add("工艺文件编号");
        head.add("工艺文件名称");
        head.add("版本");
        head.add("状态");
        head.add("工序号");
        head.add("工序名称");
        head.add("制造单位");
        head.add("准结");
        head.add("单件人工");
        head.add("单件设备");
        head.add("数量");
        head.add("部套人工工时");
        head.add("部套设备工时");

        ReferenceFactory rf = new ReferenceFactory();
        WTPart wtPart = (WTPart) rf.getReference(oid).getObject();
        List<WTPart> datas = new ArrayList();
        Map<String, Double> map = new HashMap<String, Double>();
        List<WTPart> parents = new ArrayList();
        parents.add(wtPart);
        datas.add(wtPart);
        map.put(wtPart.getNumber(), Double.valueOf(1));
        WTContainer parentContainer = wtPart.getContainer();
        getAllChildPartsByPart(datas, parents, parentContainer, map);
        ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();
        WTUser user = (WTUser) SessionHelper.getPrincipal();
        Map<String,String> roleMap = new HashMap<String,String>();
        boolean zhixingjingli = false;
        if(!datas.isEmpty()){
            zhixingjingli = AccessAdminUtil.isGroup("执行经理");
            if(!zhixingjingli){
                roleMap = GenerateGongShiUtil.getGongShiRoleMapByUser(datas.get(0).getContainer(), user);
            }
        }

        MPMProcessPlan plan = null;
        MPMOperationMaster master = null;
        MPMOperation operation = null;
        try {
            for (WTPart part : datas) {
                List allPlan = ViewWIHelper.getProcessPlan(part);
                for (Object object : allPlan) {
                    plan = (MPMProcessPlan) object;
                    WTDocument document = MPMProcessPlanUtil.getWTDocumentByProcessPlan(plan);
                    if(document == null) {
                        continue;
                    }
                    String dept = IBAHelper.getIBAStringValue(document, "DEPT");
                    String roleName = WorkflowHelper.getRoleNameByDept(dept);
                    if(!roleMap.containsKey(roleName) && !zhixingjingli) {
                        continue;
                    }
                    List<MPMOperationUsageLink> links = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(plan);
                    for(MPMOperationUsageLink link : links) {
                        String stepNumber = link.getOperationLabel();
                        if(stepNumber.startsWith("0")){
                            stepNumber = stepNumber.substring(1);
                        }
                        master = (MPMOperationMaster) link.getRoleBObject();
                        operation = ViewWIHelper.getMpmOperation(master.getNumber());
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(part.getNumber());
                        list.add(part.getName());
	                    list.add(document.getNumber());
	                    list.add( document.getName());
	                    list.add( document.getVersionIdentifier().getValue() + "." + document.getIterationIdentifier().getValue());
	                    list.add( document.getState().getState().getDisplay(Locale.CHINA));
	                    list.add( stepNumber);
                        list.add( operation.getName());
                        list.add( StringUtil.object2String(IBAHelper.getIBAStringValue(operation, "workShop")));
                        list.add(StringUtil.object2String(IBAHelper.getIBAStringValue(operation, "ZJGS")));
                        String djgs = StringUtil.object2String(IBAHelper.getIBAStringValue(operation, "DJGS"));
                        String danJianSheBeiGS = StringUtil.object2String(IBAHelper.getIBAStringValue(operation, "DanJianSheBeiGS"));
                        list.add(djgs);
                        list.add(danJianSheBeiGS);
                        list.add(String.valueOf(map.get(part.getNumber())));
                        String butao = "";
                        String butao_sb = "";  //设备布套工时
                        try {
                            if(StrUtil.isNotEmpty(djgs)){
                                double butaoDouble = Double.parseDouble(djgs) * map.get(part.getNumber());
                                butao = String.valueOf(butaoDouble);
                            }

                            if(StrUtil.isNotEmpty(danJianSheBeiGS)){
                                double butao_sbDouble = Double.parseDouble(danJianSheBeiGS) * map.get(part.getNumber());
                                butao_sb = String.valueOf(butao_sbDouble);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        list.add(butao);
                        list.add(butao_sb);
                        lists.add(list);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        ExcelFileGenerator file = new ExcelFileGenerator(head,lists);
        File xls = new File(tPath);
        try {
            file.expordExcel(new FileOutputStream(xls));
        } catch(Exception e) {
            e.printStackTrace();
        }

        return xls;
    }

    public static void getAllChildPartsByPart(List allChildrenList, List parents, WTContainer parentContainer, Map<String, Double> map) throws WTException {
        Persistable[][][] all_children = WTPartHelper.service.getUsesWTParts(new WTArrayList(parents), getDefaultConfigSpec());
        WTContainer childContainer = null;
        for (ListIterator i = parents.listIterator(); i.hasNext(); ) {
            WTPart parent = (WTPart) i.next();
            Persistable[][] branch = all_children[i.previousIndex()];
            if (branch == null) {
                continue;
            }
            List children = new ArrayList(branch.length);

            for (Persistable[] child : branch) {
                Persistable persistable = child[0];
                Persistable per = child[1];
                if (!(per instanceof WTPart) && !(per instanceof WTPartMaster)) {
                    continue;
                }
                WTPart childPart = null;
                if (per instanceof WTPart) {
                    childPart = (WTPart) per;
                    childPart = WCUtil.getLatestPartByView((Master) childPart.getMaster(), "Manufacturing");
                } else if (per instanceof WTPartMaster) {
                    childPart = WCUtil.getLatestPartByView((Master) per, "Manufacturing");
                }

                if (childPart == null) {
                    continue;
                }

                IBAUtility ibaUtility = new IBAUtility(childPart);
                String partType = ibaUtility.getIBAValue("MTYPE");

                if(!children.contains(childPart)) {
                    children.add(childPart);
                }

                //过滤不是自制件、外配套件、带料委外件、不带料委外件类型的零部件
                if (Constants.TYPE_ZIZHIJIAN.equals(partType)
                        || Constants.TYPE_WAIPEITAOJIAN.equals(partType)
                        || Constants.TYPE_DAILIAOWEIWAIJIAN.equals(partType)
                        || Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(partType)) {
                    childContainer = childPart.getContainer();
                    if (!parentContainer.getName().equals(childContainer.getName())) {//过滤掉借用件，即是跟父件不在同一产品库下的零部件
                        continue;
                    }

                    if(persistable instanceof WTPartUsageLink){
                        WTPartUsageLink link = (WTPartUsageLink) persistable;
                        Double amount = link.getQuantity().getAmount();
                        if(map.containsKey(parent.getNumber())){
                            Double parentAmount = map.get(parent.getNumber());
                            double all = amount * parentAmount;
                            if(map.containsKey(childPart.getNumber())){
                                all = all + map.get(childPart.getNumber());
                            }
                            map.put(childPart.getNumber(), all);
                        }
                    }

                    if (!allChildrenList.contains(childPart)) {
                        allChildrenList.add(childPart);
                    }


                }
            }
            getAllChildPartsByPart(allChildrenList, children, parentContainer, map);
        }
    }

    protected static ConfigSpec getDefaultConfigSpec() throws WTException {
        return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
    }

    private static void writeCellValue(XSSFRow row, int col, String value) {
        XSSFCell cell = row.createCell(col, XSSFCell.CELL_TYPE_STRING);
        cell.setCellValue(new XSSFRichTextString(value));
    }

}