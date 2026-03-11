package ext.casc.nc;

import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.SWXMLUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import ext.casc.nc.bean.GLMaterialQuota;
import ext.casc.nc.bean.GLMaterialQuotaRecord;
import ext.casc.util.DBConn;
import ext.casc.util.Tools;
import ext.sast.common.fc.CmPersistenceHelper;
import org.jdom.Element;
import wt.doc.WTDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class NCMaterialHelper {

    public static  String  structurePath = PropertiesUtil.getTempPath()+ File.separator +"structureMaterial";
    public static boolean structure(WTDocument doc){
       boolean isSuccess = false;
       InputStream inputStream =null;
       try {
           String zipFileName = doc.getNumber()+".zip";
           String xmlFileName = doc.getNumber()+".xml";

           String tempPath = structurePath+File.separator+doc.getNumber();
           WTDocumentUtil.downloadDocumentPrimaryToTemp(doc, tempPath);
           ApacheZipUtil.decompress(tempPath + File.separatorChar + zipFileName, tempPath);

           inputStream = new FileInputStream(tempPath+File.separator+xmlFileName);
           SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
           Element rootElement = xmlUtil.getRootElement();


           if ("technics".equals(rootElement.getName())) {
               Element qmFawTechnicsInfo = rootElement.getChild("QMFawTechnicsInfo");
               structure(doc,qmFawTechnicsInfo);
               record(doc);
           }
           isSuccess = true;
       }catch (Exception e){
           e.printStackTrace();
           return false;
       }finally{
    	   if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
       }
       return isSuccess;
   }

    private static void record(WTDocument doc) {
        try {
            GLMaterialQuotaRecord record = new GLMaterialQuotaRecord();
            record.setKeyId(String.valueOf(doc.getPersistInfo().getObjectIdentifier().getId()));
            record.setDocNumber(doc.getNumber());
            record.setVersion(doc.getVersionIdentifier().getValue()+"."+doc.getIterationIdentifier().getValue());
            CmPersistenceHelper.manager.save(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  static String getStringValue(Object obj){
        if(obj==null){
            return "";
        }else{
            return String.valueOf(obj);
        }

    }
    public static void structure(WTDocument doc ,Element qmFawTechnicsInfo){
    	String gywjbh = doc.getNumber();
        String version = doc.getVersionIdentifier().getValue();
    	String gywjh = getStringValue(qmFawTechnicsInfo.getAttributeValue("pplanNumber"));
        String th = getStringValue(qmFawTechnicsInfo.getAttributeValue("partNumber"));
        String dept = getStringValue(qmFawTechnicsInfo.getAttributeValue("DEPT"));

        clear(gywjbh,version);
        
        String[] ss = getFirstProcess(qmFawTechnicsInfo);

        Element cldeElement = qmFawTechnicsInfo.getChild("CLDE");
        if(cldeElement!=null){
            Element  ycldeElement = cldeElement.getChild("YCLDE");
            if(ycldeElement!=null){
                List<Element> ycldeRecords =  ycldeElement.getChildren("ycldeRecord");
                for (Element ycldeRecord : ycldeRecords) {
                    GLMaterialQuota materialQuota = new GLMaterialQuota();
                    materialQuota.setKeyId(java.util.UUID.randomUUID().toString());
                    materialQuota.setGywjbh(gywjbh);
                    materialQuota.setVersion(version);
                    materialQuota.setGywjh(gywjh);
                    materialQuota.setTh(th);
                    materialQuota.setGywzbm(dept);
                    if(ss.length==2) {
                        materialQuota.setGxnum(ss[0]);
                        materialQuota.setGxname(ss[1]);
                    }

                    materialQuota.setMaterialcode(getStringValue(ycldeRecord.getAttributeValue("chbm")));
                    materialQuota.setXlcc(getStringValue(ycldeRecord.getAttributeValue("xlcc")));
                    String ksjs = ycldeRecord.getAttributeValue("kzjs");
                    if(Tools.isNull(ksjs)){
                        ycldeRecord.getAttributeValue("sjkzjs");
                    }
                    materialQuota.setKzjs(getStringValue(ksjs));
                    materialQuota.setGzdw(getStringValue(ycldeRecord.getAttributeValue("dw")));
                    materialQuota.setDetype("原材料");

                    try {
						CmPersistenceHelper.manager.save(materialQuota);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

                }
            }

            Element  zycldeElement = cldeElement.getChild("ZYCLDE");
            if(zycldeElement!=null){
                List<Element> zycldeRecords =  zycldeElement.getChildren("zycldeRecord");
                for (Element zycldeRecord : zycldeRecords) {
                    GLMaterialQuota materialQuota = new GLMaterialQuota();
                    materialQuota.setKeyId(java.util.UUID.randomUUID().toString());
                    materialQuota.setGywjbh(gywjbh);
                    materialQuota.setVersion(version);
                    materialQuota.setGywjh(gywjh);
                    materialQuota.setTh(th);
                    materialQuota.setGywzbm(dept);

                    if(ss.length==2) {
                        materialQuota.setGxnum(ss[0]);
                        materialQuota.setGxname(ss[1]);
                    }
                    materialQuota.setMaterialcode(getStringValue(zycldeRecord.getAttributeValue("chbm")));
                    materialQuota.setXlcc(getStringValue(zycldeRecord.getAttributeValue("xlcc")));
                    String ksjs = zycldeRecord.getAttributeValue("kzjs");
                    if(Tools.isNull(ksjs)){
                        zycldeRecord.getAttributeValue("sjkzjs");
                    }
                    materialQuota.setKzjs(getStringValue(ksjs));

                    materialQuota.setQtyrequired(getStringValue(zycldeRecord.getAttributeValue("sl")));
                    materialQuota.setGzdw(getStringValue(zycldeRecord.getAttributeValue("dw")));
                    materialQuota.setDetype("主要材料");
                    try {
						CmPersistenceHelper.manager.save(materialQuota);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

                }
            }

            Element  sjycldeElement = cldeElement.getChild("SJYCLDE");
            if(sjycldeElement!=null){
                List<Element> sjycldeRecords =  sjycldeElement.getChildren("sjycldeRecord");
                for (Element sjycldeRecord : sjycldeRecords) {
                    GLMaterialQuota materialQuota = new GLMaterialQuota();
                    materialQuota.setKeyId(java.util.UUID.randomUUID().toString());
                    materialQuota.setGywjbh(gywjbh);
                    materialQuota.setVersion(version);
                    materialQuota.setGywjh(gywjh);
                    materialQuota.setTh(th);
                    materialQuota.setGywzbm(dept);

                    if(ss.length==2) {
                        materialQuota.setGxnum(ss[0]);
                        materialQuota.setGxname(ss[1]);
                    }
                    materialQuota.setMaterialcode(getStringValue(sjycldeRecord.getAttributeValue("chbm")));
                    materialQuota.setXlcc(getStringValue(sjycldeRecord.getAttributeValue("xlcc")));

                    materialQuota.setSjxlcc(getStringValue(sjycldeRecord.getAttributeValue("sjcc")));
                    String ksjs = sjycldeRecord.getAttributeValue("kzjs");
                    if(Tools.isNull(ksjs)){
                        sjycldeRecord.getAttributeValue("sjkzjs");
                    }
                    materialQuota.setKzjs(getStringValue(ksjs));
                    materialQuota.setQtyrequired(getStringValue(sjycldeRecord.getAttributeValue("sjsl")));
                    materialQuota.setGzdw(getStringValue(sjycldeRecord.getAttributeValue("dw")));
                    materialQuota.setDetype("试件原材料");
                    try {
						CmPersistenceHelper.manager.save(materialQuota);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

                }
            }

            Element  yclEElementZYK = cldeElement.getChild("SJZYKYCLDE");
            if(yclEElementZYK!=null){
                List<Element> ycldeRecords =  yclEElementZYK.getChildren("ycldeRecord");
                for (Element ycldeRecord : ycldeRecords) {
                    GLMaterialQuota materialQuota = new GLMaterialQuota();
                    materialQuota.setKeyId(java.util.UUID.randomUUID().toString());
                    materialQuota.setGywjbh(gywjbh);
                    materialQuota.setVersion(version);
                    materialQuota.setGywjh(gywjh);
                    materialQuota.setTh(th);
                    materialQuota.setGywzbm(dept);

                    if(ss.length==2) {
                        materialQuota.setGxnum(ss[0]);
                        materialQuota.setGxname(ss[1]);
                    }
                    materialQuota.setMaterialcode(getStringValue(ycldeRecord.getAttributeValue("sjbm")));
                    materialQuota.setXlcc(getStringValue(ycldeRecord.getAttributeValue("xlcc")));
                    String ksjs = ycldeRecord.getAttributeValue("kzjs");
                    if(Tools.isNull(ksjs)){
                        ycldeRecord.getAttributeValue("sjkzjs");
                    }
                    materialQuota.setKzjs(getStringValue(ksjs));
                    materialQuota.setGzdw(getStringValue(ycldeRecord.getAttributeValue("dw")));
                    materialQuota.setDetype("原材料");
                    try {
						CmPersistenceHelper.manager.save(materialQuota);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

                }
            }


            Element  zycldeElementZYK = cldeElement.getChild("SJZYKZYCLDE");
            if(zycldeElementZYK!=null){
                List<Element> zycldeRecords =  zycldeElementZYK.getChildren("zycldeRecord");
                for (Element zycldeRecord : zycldeRecords) {
                    GLMaterialQuota materialQuota = new GLMaterialQuota();
                    materialQuota.setKeyId(java.util.UUID.randomUUID().toString());
                    materialQuota.setGywjbh(gywjbh);
                    materialQuota.setVersion(version);
                    materialQuota.setGywjh(gywjh);
                    materialQuota.setTh(th);
                    materialQuota.setGywzbm(dept);

                    if(ss.length==2) {
                        materialQuota.setGxnum(ss[0]);
                        materialQuota.setGxname(ss[1]);
                    }
                    materialQuota.setMaterialcode(getStringValue(zycldeRecord.getAttributeValue("sjbm")));
                    materialQuota.setXlcc(getStringValue(zycldeRecord.getAttributeValue("xlcc")));
                    String kzjs = zycldeRecord.getAttributeValue("kzjs");
                    if(Tools.isNull(kzjs)){
                        zycldeRecord.getAttributeValue("sjkzjs");
                    }
                    materialQuota.setKzjs(getStringValue(kzjs));
                    materialQuota.setQtyrequired(getStringValue(zycldeRecord.getAttributeValue("sl")));
                    materialQuota.setGzdw(getStringValue(zycldeRecord.getAttributeValue("dw")));
                    materialQuota.setDetype("主要材料");
                    try {
						CmPersistenceHelper.manager.save(materialQuota);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

                }
            }

            Element  sjycldeElementZYK = cldeElement.getChild("SJZYKSJYCLDE");
            if(sjycldeElementZYK!=null){
                List<Element> sjycldeRecords =  sjycldeElementZYK.getChildren("sjycldeRecord");
                for (Element sjycldeRecord : sjycldeRecords) {
                    GLMaterialQuota materialQuota = new GLMaterialQuota();
                    materialQuota.setKeyId(java.util.UUID.randomUUID().toString());
                    materialQuota.setGywjbh(gywjbh);
                    materialQuota.setVersion(version);
                    materialQuota.setGywjh(gywjh);
                    materialQuota.setTh(th);
                    materialQuota.setGywzbm(dept);

                    if(ss.length==2) {
                        materialQuota.setGxnum(ss[0]);
                        materialQuota.setGxname(ss[1]);
                    }
                    materialQuota.setMaterialcode(getStringValue(sjycldeRecord.getAttributeValue("sjbm")));
                    materialQuota.setXlcc(getStringValue(sjycldeRecord.getAttributeValue("xlcc")));

                    materialQuota.setSjxlcc(getStringValue(sjycldeRecord.getAttributeValue("sjcc")));
                    String kzjs = sjycldeRecord.getAttributeValue("kzjs");
                    if(Tools.isNull(kzjs)){
                        sjycldeRecord.getAttributeValue("sjkzjs");
                    }
                    materialQuota.setKzjs(getStringValue(kzjs));
                    materialQuota.setQtyrequired(getStringValue(sjycldeRecord.getAttributeValue("sjsl")));
                    materialQuota.setGzdw(getStringValue(sjycldeRecord.getAttributeValue("dw")));
                    materialQuota.setDetype("试件原材料");
                    try {
						CmPersistenceHelper.manager.save(materialQuota);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

                }
            }
        }


        Element gydeElement = qmFawTechnicsInfo.getChild("GYDE");
         if(gydeElement!=null){
             Element  zycldeElement = gydeElement.getChild("ZYCLDE");
             if(zycldeElement!=null){
                 List<Element> zycldeRecords =  zycldeElement.getChildren("zycldeRecord");
                 for (Element zycldeRecord : zycldeRecords) {
                     GLMaterialQuota materialQuota = new GLMaterialQuota();
                     materialQuota.setKeyId(java.util.UUID.randomUUID().toString());
                     materialQuota.setGywjbh(gywjbh);
                     materialQuota.setVersion(version);
                     materialQuota.setGywjh(gywjh);
                     materialQuota.setTh(th);
                     materialQuota.setGywzbm(dept);

                     if(ss.length==2) {
                         materialQuota.setGxnum(ss[0]);
                         materialQuota.setGxname(ss[1]);
                     }
                     materialQuota.setMaterialcode(getStringValue(zycldeRecord.getAttributeValue("chbm")));
                     materialQuota.setXlcc(getStringValue(zycldeRecord.getAttributeValue("xlcc")));
                     String kzjs = zycldeRecord.getAttributeValue("kzjs");
                     if(Tools.isNull(kzjs)){
                         zycldeRecord.getAttributeValue("sjkzjs");
                     }
                     materialQuota.setKzjs(getStringValue(kzjs));
                     materialQuota.setQtyrequired(getStringValue(zycldeRecord.getAttributeValue("sl")));
                     materialQuota.setGzdw(getStringValue(zycldeRecord.getAttributeValue("dw")));
                     materialQuota.setDetype("主要材料");
                     try {
 						CmPersistenceHelper.manager.save(materialQuota);
 					} catch (Exception e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}

                 }
             }

             Element  sjycldeElement = gydeElement.getChild("SJYCLDE");
             if(sjycldeElement!=null){
                 List<Element> sjycldeRecords =  sjycldeElement.getChildren("sjycldeRecord");
                 for (Element sjycldeRecord : sjycldeRecords) {
                     GLMaterialQuota materialQuota = new GLMaterialQuota();
                     materialQuota.setKeyId(java.util.UUID.randomUUID().toString());
                     materialQuota.setGywjbh(gywjbh);
                     materialQuota.setVersion(version);
                     materialQuota.setGywjh(gywjh);
                     materialQuota.setTh(th);
                     materialQuota.setGywzbm(dept);

                     if(ss.length==2) {
                         materialQuota.setGxnum(ss[0]);
                         materialQuota.setGxname(ss[1]);
                     }
                     materialQuota.setMaterialcode(getStringValue(sjycldeRecord.getAttributeValue("chbm")));

                     materialQuota.setSjxlcc(getStringValue(sjycldeRecord.getAttributeValue("sjcc")));
                     materialQuota.setXlcc(getStringValue(sjycldeRecord.getAttributeValue("xlcc")));
                     String kzjs = sjycldeRecord.getAttributeValue("kzjs");
                     if(Tools.isNull(kzjs)){
                         sjycldeRecord.getAttributeValue("sjkzjs");
                     }
                     materialQuota.setKzjs(getStringValue(kzjs));
                     materialQuota.setQtyrequired(getStringValue(sjycldeRecord.getAttributeValue("sjsl")));
                     materialQuota.setGzdw(getStringValue(sjycldeRecord.getAttributeValue("dw")));
                     materialQuota.setDetype("试件原材料");
                     try {
 						CmPersistenceHelper.manager.save(materialQuota);
 					} catch (Exception e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}

                 }
             }

             Element  matchpartElement = gydeElement.getChild("MATCHPART");
             if(matchpartElement!=null){
                 List<Element> matchParts =  matchpartElement.getChildren("MatchPart");
                 for (Element matchPart : matchParts) {
                     GLMaterialQuota materialQuota = new GLMaterialQuota();
                     materialQuota.setKeyId(java.util.UUID.randomUUID().toString());
                     materialQuota.setGywjbh(gywjbh);
                     materialQuota.setVersion(version);
                     materialQuota.setGywjh(gywjh);
                     materialQuota.setTh(th);
                     materialQuota.setGywzbm(dept);

                     if(ss.length==2) {
                         materialQuota.setGxnum(ss[0]);
                         materialQuota.setGxname(ss[1]);
                     }

                     materialQuota.setXlcc(getStringValue(matchPart.getAttributeValue("xlcc")));
                     String kzjs = matchPart.getAttributeValue("kzjs");
                     if(Tools.isNull(kzjs)){
                         matchPart.getAttributeValue("sjkzjs");
                     }
                     materialQuota.setMaterialcode(getStringValue(matchPart.getAttributeValue("chbm")));
                     materialQuota.setQtyrequired(getStringValue(matchPart.getAttributeValue("sl")));
                     String dw = getStringValue(matchPart.getAttributeValue("dw2"));
                     if(Tools.isNull(dw)){
                         dw = getStringValue(matchPart.getAttributeValue("dw"));
                     }
                     materialQuota.setGzdw(dw);
                     materialQuota.setDetype("工艺定额匹配");
                 }
             }
             Element  newpartElement = gydeElement.getChild("NEWPART");
             if(newpartElement!=null){
                 List<Element> newParts =  newpartElement.getChildren("NewPart");
                 for (Element newPart : newParts) {
                     GLMaterialQuota materialQuota = new GLMaterialQuota();
                     materialQuota.setKeyId(java.util.UUID.randomUUID().toString());
                     materialQuota.setGywjbh(gywjbh);
                     materialQuota.setVersion(version);
                     materialQuota.setGywjh(gywjh);
                     materialQuota.setTh(th);
                     materialQuota.setGywzbm(dept);

                     if(ss.length==2) {
                         materialQuota.setGxnum(ss[0]);
                         materialQuota.setGxname(ss[1]);
                     }

                     materialQuota.setXlcc(getStringValue(newPart.getAttributeValue("xlcc")));
                     String kzjs = newPart.getAttributeValue("kzjs");
                     if(Tools.isNull(kzjs)){
                         newPart.getAttributeValue("sjkzjs");
                     }
                     materialQuota.setMaterialcode(getStringValue(newPart.getAttributeValue("chbm")));
                     materialQuota.setQtyrequired(getStringValue(newPart.getAttributeValue("sl")));
                     String dw = getStringValue(newPart.getAttributeValue("dw2"));
                     if(Tools.isNull(dw)){
                         dw = getStringValue(newPart.getAttributeValue("dw"));
                     }
                     materialQuota.setGzdw(dw);
                     materialQuota.setDetype("工艺定额新增");
                     try {
 						CmPersistenceHelper.manager.save(materialQuota);
 					} catch (Exception e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}

                 }
             }

             Element  matchpartElementZYK = gydeElement.getChild("SJZYKMATCHPART");
             if(matchpartElementZYK!=null){
                 List<Element> matchParts =  matchpartElementZYK.getChildren("SjzykMatchPart");
                 for (Element matchPart : matchParts) {
                     GLMaterialQuota materialQuota = new GLMaterialQuota();
                     materialQuota.setKeyId(java.util.UUID.randomUUID().toString());
                     materialQuota.setGywjbh(gywjbh);
                     materialQuota.setVersion(version);
                     materialQuota.setGywjh(gywjh);
                     materialQuota.setTh(th);
                     materialQuota.setGywzbm(dept);

                     if(ss.length==2) {
                         materialQuota.setGxnum(ss[0]);
                         materialQuota.setGxname(ss[1]);
                     }
                     materialQuota.setXlcc(getStringValue(matchPart.getAttributeValue("xlcc")));
                     String kzjs = matchPart.getAttributeValue("kzjs");
                     if(Tools.isNull(kzjs)){
                         matchPart.getAttributeValue("sjkzjs");
                     }
                     materialQuota.setMaterialcode(getStringValue(matchPart.getAttributeValue("sjbm")));
                     String sl = getStringValue(matchPart.getAttributeValue("gysl"));
                     if(Tools.isNull(sl)){
                         sl = getStringValue(matchPart.getAttributeValue("sl"));
                     }
                     materialQuota.setQtyrequired(sl);
                     materialQuota.setGzdw(getStringValue(matchPart.getAttributeValue("dw")));
                     materialQuota.setDetype("工艺定额匹配");
                     try {
 						CmPersistenceHelper.manager.save(materialQuota);
 					} catch (Exception e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}

                 }
             }
             Element  newpartElementZYK = gydeElement.getChild("SJZYKNEWPART");
             if(newpartElementZYK!=null){
                 List<Element> newParts =  newpartElementZYK.getChildren("SjzykNewPart");
                 for (Element newPart : newParts) {
                     GLMaterialQuota materialQuota = new GLMaterialQuota();
                     materialQuota.setKeyId(java.util.UUID.randomUUID().toString());
                     materialQuota.setGywjbh(gywjbh);
                     materialQuota.setVersion(version);
                     materialQuota.setGywjh(gywjh);
                     materialQuota.setTh(th);
                     materialQuota.setGywzbm(dept);

                     if(ss.length==2) {
                         materialQuota.setGxnum(ss[0]);
                         materialQuota.setGxname(ss[1]);
                     }
                     materialQuota.setXlcc(getStringValue(newPart.getAttributeValue("xlcc")));
                     String kzjs = newPart.getAttributeValue("kzjs");
                     if(Tools.isNull(kzjs)){
                         newPart.getAttributeValue("sjkzjs");
                     }
                     materialQuota.setMaterialcode(getStringValue(newPart.getAttributeValue("sjbm")));
                     String sl = getStringValue(newPart.getAttributeValue("gysl"));
                     if(Tools.isNull(sl)){
                         sl = getStringValue(newPart.getAttributeValue("sl"));
                     }
                     materialQuota.setQtyrequired(sl);
                     materialQuota.setGzdw(getStringValue(newPart.getAttributeValue("dw")));
                     materialQuota.setDetype("工艺定额新增");
                     try {
 						CmPersistenceHelper.manager.save(materialQuota);
 					} catch (Exception e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}

                 }
             }
         }

    }

    private static String[] getFirstProcess(Element qmFawTechnicsInfo) {
        String[] ss= new String[2];
        for (Element rootChildElement : (List<Element>) qmFawTechnicsInfo.getChildren()) {
            if ("steps".equals(rootChildElement.getName())) {
                Element stepAttrElement = rootChildElement.getChild("QMProcedureInfo");
                String name = stepAttrElement.getAttributeValue("stepName");
                String stepNumber = stepAttrElement.getAttributeValue("stepNumber");
                ss[0]=stepNumber;
                ss[1]=name;
            }

        }
        return ss;
    }

    private static void clear(String gywjbh, String version) {
		DBConn conn = null;
        try {
            conn = new DBConn();
    		String sql = "delete from GLMaterialQuota  where gywjbh='"+gywjbh+"' and "+ " VERSION='"+version+"'";
            conn.executeUpdate(sql);
            conn.commit();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

	}

    public static boolean  hasStructure(WTDocument doc) throws Exception {
        GLMaterialQuotaRecord record = (GLMaterialQuotaRecord) CmPersistenceHelper.manager.find(GLMaterialQuotaRecord.class,doc.getPersistInfo().getObjectIdentifier().getId()+"");
        if(record !=null) return true;
        return false;
    }
}
