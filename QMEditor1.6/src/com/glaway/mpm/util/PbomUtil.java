package com.glaway.mpm.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWPartTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.view.XWTreeObject;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.PBomIntf;

public class PbomUtil {
    private static VaLogger logger = VaLogger.getLogger();
    public static String[] HARDPART_ARRAY = {"AL7.820", "AL7.821", "AL7.825",
            "AL7.826", "AL7.347"};

    public static final Long DESIGNOID = PBomIntf.getDesignOid();
    public static String XML_ADDED = "xmlAdded";

    /**
     * 组装成最新的Pbom XML
     *
     * @param oid
     * @return
     */
    public static byte[] generatePbomBytes(String[] oids) {
        WorkSpaceUtil.deleteXMLDirectory();
        List<String> mainPartNumbers = new ArrayList<String>();
        byte[] returnBytes = null;
        try {
            Document topDocument = null;
            Element topElement = null;
            for (String temp : oids) {
                byte[] bytes = PBomIntf.getPBomXML(temp);
                if (bytes != null) {
                    Document document = XmlUtil.getDocument(bytes);
                    if (topDocument == null) {
                        topDocument = (Document) document.clone();
                    }
                    Element rootElement = document.getRootElement();
                    String isLargeDecorate = rootElement.attributeValue("bigassemble");
                    if (topElement == null) {
                        topElement = (Element) rootElement.clone();
                        if (topElement.element("parts") != null) {
                            topElement.element("parts").clearContent();
                        } else {
                            topElement.addElement("parts");
                        }
                    }

                    Element partRootElement = rootElement.element("parts");
                    Element partElement = partRootElement.element("QMPartInfo");
                    mainPartNumbers.add(partElement.attributeValue("partNumber"));
                    if ("1".equals(com.glaway.mpm.EditorConfig.startType)) {
                        isLargeDecorate = "true";
                    }
                    partElement = parsePartElement(partElement, true, "true".equals(isLargeDecorate));
                    partElement.setParent(null);
                    topElement.element("parts").add(partElement);
                } else {
                    SwingUtil.showMessageDialog("该零件的PBOM结构不存在，请联系主任工艺师从该零件启动PBOM编辑器并保存关闭后，然后重试。", "提示", 2);
                    System.exit(0);
                }
            }
            if (topDocument != null) {
                topDocument.setRootElement(topElement);
                returnBytes = XmlUtil.generateDocumentByteArray(topDocument);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnBytes;
    }

    /**
     * 解析零件
     *
     * @param partElement
     * @param isRoot
     * @return
     */
    private static Element parsePartElement(Element partElement,
                                            boolean isRoot, boolean isLargeDecorate) {
        String oid = partElement.attributeValue("oid");
        String partNumber = partElement.attributeValue("partNumber");
        try {
            if (!isLargeDecorate) {
                byte[] bytes = PBomIntf.getPBomXML(oid);
                if (bytes != null) {
                    Document document = XmlUtil.getDocument(bytes);
                    XmlUtil.writeDocument(document, WorkSpaceUtil.XML_HOME
                            + partNumber + ".xml");
                    String count = partElement.attributeValue("count");
                    String useCount = partElement.attributeValue("useCount");
                    String occPath = partElement.attributeValue("occpath");
                    String occId = partElement.attributeValue("occId");
                    String middleIndex = partElement
                            .attributeValue("middleIndex");
                    partElement = BomXMLUtil.getXmlTopPart(BomXMLUtil
                            .getProduct(document));
                    if (isRoot) {
                        partElement.setAttributeValue(XML_ADDED, "false");
                    } else {
                        partElement.setAttributeValue(XML_ADDED, "true");
                        partElement.setAttributeValue("occpath", occPath);
                        partElement.setAttributeValue("occId", occId);
                        partElement.setAttributeValue("useCount", useCount);
                        partElement.setAttributeValue("count", count);
                        partElement.setAttributeValue("middleIndex",
                                middleIndex);
                    }
                    // if (!isRoot) {
                    // String newOccPath = partElement
                    // .attributeValue("occpath");
                    // String newPath = "";
                    // String[] occPathArray = newOccPath.split(",");
                    // if (occPathArray != null && occPathArray.length != 0)
                    // {
                    // for (String temp : occPathArray) {
                    // if (temp != null && temp.startsWith("-1")) {
                    // newPath += "," + occPath
                    // + temp.substring(2);
                    // }
                    // }
                    // if (newPath.length() > 0) {
                    // newPath = newPath.substring(1);
                    // }
                    // }
                    // XmlUtility.setAttributeValue(partElement, "occpath",
                    // newOccPath);
                    //
                    // String newOccId =
                    // partElement.attributeValue("occId");
                    // String newId = "";
                    // String[] occIdArray = newOccId.split(",");
                    // if (occIdArray != null && occIdArray.length != 0) {
                    // for (String temp : occIdArray) {
                    // if (temp != null && temp.startsWith("-1")) {
                    // newId += "," + occId + temp.substring(2);
                    // }
                    // }
                    // if (newId.length() > 0) {
                    // newId = newId.substring(1);
                    // }
                    // }
                    // XmlUtility.setAttributeValue(partElement, "occId",
                    // newId);
                    //
                    // XmlUtility.setAttributeValue(partElement,
                    // "middleIndex", middleIndex);
                    // partElement = synchronizedSubPartOccpath(partElement,
                    // occPath, occId);
                    // }
                } else {
                    partElement.setAttributeValue(XML_ADDED, "false");
                }
            }

            // 同步零件信息
            // partElement = synchronizedElementInfo(partElement);

            Element children = partElement.element("childs");
            if (children != null) {
                List<Element> childPartElements = children
                        .elements("QMPartInfo");
                List<Element> returnElements = new ArrayList<Element>();
                if (childPartElements != null && childPartElements.size() != 0) {
                    for (Element temp : childPartElements) {
                        // String tempPartNumber =
                        // temp.attributeValue("partNumber");
                        // Element tempELement = cacheMap.get(tempPartNumber);
                        // Element tempELement = null;
                        // if (tempELement != null) {
                        // returnElements.add(tempELement);
                        // } else {
                        returnElements.add(parsePartElement(temp, false,
                                isLargeDecorate));
                        // }
                    }

                    children.clearContent();

                    if (returnElements != null && returnElements.size() != 0) {
                        for (Element temp : returnElements) {
                            temp.setParent(null);
                            children.add(temp);
                        }
                    }
                }
            }

            // cacheMap.put(partNumber, partElement);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return partElement;

    }

    /**
     * 打开PBOM编辑器
     *
     * @param topPartOid
     * @param topPartNumber
     */
    public static void startPBOM(String topPartOid, String topPartNumber) {
        CommonUtil.openURL(getUrl(topPartOid));
    }

    /**
     * 获取零件url
     *
     * @param oid
     * @return
     */
    public static String getUrl(String oid) {
        return PBomIntf.getPbomUrl(oid);
    }

    /**
     * 获取树上oid节点的子零件的oid
     *
     * @param tree
     * @param oid
     * @return
     */
    public static List<Element> getSubPartOids(JTree tree, String oid) {
        List<Element> subParts = new ArrayList<Element>();
        XWTreeNode partNode = getPart((XWTreeNode) tree.getModel().getRoot(),
                oid);
        if (partNode != null) {
            Enumeration<XWTreeNode> children = partNode.children();
            while (children.hasMoreElements()) {
                XWTreeNode childNode = children.nextElement();
                XWTreeObject xo = childNode.getObject();
                if (xo instanceof XWPartTreeObject) {
                    XWPartTreeObject object = (XWPartTreeObject) xo;
                    subParts.add(object.getTreeCellData());
                }

            }
        }
        return subParts;
    }

    /**
     * 根据oid找到pbom树上的part节点
     *
     * @param partNode
     * @param oid
     * @return
     */
    private static XWTreeNode getPart(XWTreeNode partNode, String oid) {
        if (partNode != null) {
            Enumeration<XWTreeNode> children = partNode.children();
            while (children.hasMoreElements()) {
                XWTreeNode childNode = children.nextElement();
                XWTreeObject xo = childNode.getObject();
                if (xo instanceof XWPartTreeObject) {
                    XWPartTreeObject object = (XWPartTreeObject) xo;
                    Element element = object.getTreeCellData();
                    if (oid.equals(element.attributeValue("oid"))) {
                        return childNode;
                    } else {
                        XWTreeNode node = getPart(childNode, oid);
                        if (node != null) {
                            return node;
                        }
                    }
                }

            }
        }
        return null;
    }

    /**
     * 删除参装件
     *
     * @param totalOccId
     * @param occId
     * @return
     */
    public static boolean addPart(Element partRootElement,
                                  List<Element> partsElements, Map<String, String> map) {
        String partNumber = map.get("partNumber");
        String occId = map.get("occId");
        String occpath = map.get("occpath");
        // if (!"".equals(parts)) {
        // parts += "\n";
        // }
        // parts += partNumber;
        // Element part = BomXMLUtil.generatePartData(map, null);
        // partRootElement.add(part);

//		if (partsElements != null && partsElements.size() != 0) {
//			for (Element partElement : partsElements) {
//				if (partElement.attributeValue("partNumber").equals(partNumber)) {
//					String totalOccId = JavaUtil.convertNull(partElement.attributeValue("occId"));
//					String totalOccpath = JavaUtil.convertNull(partElement.attributeValue("occpath"));
//					try {
//						totalOccId += "," + occId;
//						totalOccpath += "," + occpath;
//
//						String useCount = partElement.attributeValue("useCount");
//						int count;
//						if (useCount == null || "".equals(useCount.trim())) {
//							count = 0;
//						} else {
//							count = Integer.valueOf(useCount);
//						}
//						partElement.setAttributeValue("useCount", String.valueOf(count + 1));
//						partElement.setAttributeValue("occId", totalOccId);
//						partElement.setAttributeValue("occpath", totalOccpath);
//						return false;
//					} catch (Exception e) {
//
//					}
//				}
//			}
//		}

        Element part = BomXMLUtil.generatePartData(map, null);
        partRootElement.add(part);
        return true;
    }

    /**
     * 删除参装件
     *
     * @param totalOccId
     * @param occId
     * @return
     */
    public static boolean deletePart(List<Element> partsElements,
                                     String partNumber, String occId, String occpath) {
        boolean flag = false;
        if (partsElements != null && partsElements.size() != 0) {
            for (Element partElement : partsElements) {
                if (partElement.attributeValue("partNumber").equals(partNumber) && occId.equals(partElement.attributeValue("occId"))) {
                    partsElements.remove(partElement);
                    flag = true;
//					String totalOccId = partElement.attributeValue("occId");
//					String totalOccpath = partElement.attributeValue("occpath");
//					try {
//						String returnOccId = PbomUtil.isOccIdExist(totalOccId,
//								occId);
//						String returnOccpath = PbomUtil.isOccIdExist(
//								totalOccpath, occpath);
//						if (returnOccId != null) {
//							String useCount = partElement
//									.attributeValue("useCount");
//							int count;
//							if (useCount == null || "".equals(useCount.trim())) {
//								count = 1;
//							} else {
//								count = Integer.valueOf(useCount);
//							}
//							if (count > 1) {
//								partElement.setAttributeValue("useCount",
//										String.valueOf(count - 1));
//								partElement.setAttributeValue("occId",
//										returnOccId);
//								partElement.setAttributeValue("occpath",
//										returnOccpath);
//							} else {
//								partsElements.remove(partElement);
//								flag = true;
//							}
//						}
//						break;
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
                }
            }
        }
        return flag;
    }

    /**
     * 判断OccId在汇总参装件中是否存在
     *
     * @param totalOccId
     * @param occId
     * @return
     */
    public static String isOccIdExist(String totalOccId, String occId) {
        String returnOccId = null;
        boolean flag = false;
        if (totalOccId != null) {
            String[] array = totalOccId.split(",");
            if (array != null && array.length != 0) {
                returnOccId = "";
                for (String temp : array) {
                    if (temp.equals(occId)) {
                        flag = true;
                    } else {
                        returnOccId += "," + temp;
                    }
                }
            }
        }
        if (!flag) {
            returnOccId = null;
        }
        if (returnOccId != null && returnOccId.length() >= 1) {
            returnOccId = returnOccId.substring(1);
        }
        return returnOccId;
    }

    /**
     * 是否是印制板的零件
     *
     * @param partNumber
     * @return
     */
    public static boolean isHardPart(String partNumber) {
        // boolean flag = false;
        // if (partNumber != null) {
        // for (String temp : HARDPART_ARRAY) {
        // if (partNumber.startsWith(temp)) {
        // flag = true;
        // break;
        // }
        // }
        // }
        // return flag;
        return true;
    }

    /**
     * 是否是印制板的零件
     *
     * @param partNumber
     * @return
     */
    public static boolean isHardPartHeader(String partNumber) {
        boolean flag = false;
        if (partNumber != null) {
            for (String temp : HARDPART_ARRAY) {
                if (partNumber.startsWith(temp)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 取消参装删除java bean储存 add by zhuhao 2017.12.28
     *
     * @param partsElements
     * @param partNumber
     * @param occID
     */
    public static void deletePartbyOccId(List<Element> partsElements, String partNumber, String occID) {
        if (partsElements != null && partsElements.size() != 0) {
            for (int i = 0; i < partsElements.size(); i++) {
                String number = partsElements.get(i).attributeValue("partNumber");
                String id = partsElements.get(i).attributeValue("occId");
                if (number.equals(partNumber) && id.equals(occID)) {
                    partsElements.remove(partsElements.get(i));
                }
            }
        }
    }

}
