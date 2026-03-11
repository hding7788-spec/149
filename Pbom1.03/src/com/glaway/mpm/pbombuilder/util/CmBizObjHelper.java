package com.glaway.mpm.pbombuilder.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMDescribeLink;
import wt.fc.BinaryLink;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.ObjectToObjectLink;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.CollectionContainsDeletedException;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeamManaged;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.IterationInfo;
import wt.vc.IterationState;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.vc.Versioned;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.IteratedDescribeLink;
import wt.vc.struct.IteratedReferenceLink;
import wt.vc.struct.IteratedUsageLink;

import com.ptc.core.task.TaskResult;
import com.ptc.netmarkets.model.NmConsoleOpenException;
import com.ptc.windchill.classproxy.ConsoleClassProxy;

public class CmBizObjHelper implements RemoteAccess, Serializable {
   private static final long    serialVersionUID = 3241268719415661629L;
   private static final boolean VERBOSE          = true;

   public static WTContainer findWTContainer(String containerName) {
      if (containerName == null)
         return null;

      try {
         QuerySpec qs = new QuerySpec(ContainerTeamManaged.class);
         qs.appendWhere(new SearchCondition(ContainerTeamManaged.class, WTContainer.NAME, SearchCondition.EQUAL, containerName), new int[]{0});
         QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
         if (qr.hasMoreElements())
            return (WTContainer) qr.nextElement();
      } catch (Exception e) {
      }
      return null;
   }

   public static void findParts(String[] numbers, String[] versions, Map parts) throws Exception {
      findBizObjs(WTPart.class, WTPart.NUMBER, numbers, versions, parts);
   }

   public static void findEPMs(String[] numbers, String[] versions, Map epms) throws Exception {
      findBizObjs(EPMDocument.class, EPMDocument.NUMBER, numbers, versions, epms);
   }

   public static void findDocs(String[] numbers, String[] versions, Map docs) throws Exception {
      findBizObjs(WTDocument.class, WTDocument.NUMBER, numbers, versions, docs);
   }

   public static WTPart findPart(String number, String version) throws Exception {
      return (WTPart) findBizObj(WTPart.class, WTPartMaster.class, WTPart.NUMBER, WTPartMaster.NUMBER, number, version);
   }

   public static EPMDocument findEPM(String number, String version) throws Exception {
      return (EPMDocument) findBizObj(EPMDocument.class, EPMDocumentMaster.class, EPMDocument.NUMBER, EPMDocumentMaster.NUMBER, number, version);
   }

   public static WTDocument findDoc(String number, String version) throws Exception {
      return (WTDocument) findBizObj(WTDocument.class, WTDocumentMaster.class, WTDocument.NUMBER, WTDocumentMaster.NUMBER, number, version);
   }

   public static WTPartMaster findPartMaster(String number) throws Exception {
      return (WTPartMaster) findMaster(WTPartMaster.class, WTPartMaster.NUMBER, number);
   }

   public static EPMDocumentMaster findEPMMaster(String number) throws Exception {
      return (EPMDocumentMaster) findMaster(EPMDocumentMaster.class, EPMDocumentMaster.NUMBER, number);
   }

   public static WTDocumentMaster findDocMaster(String number) throws Exception {
      return (WTDocumentMaster) findMaster(WTDocumentMaster.class, WTDocumentMaster.NUMBER, number);
   }

   @SuppressWarnings("unchecked")
   private static void findBizObjs(Class objClass, String numberField, String[] numbers, String[] versions, Map objs) throws Exception {
      if (numbers.length != versions.length)
         throw new Exception("INTERNAL ERROR: number of obj numbers and " + "number of obj versions should be same!");

      Map<String, String> vers = new HashMap<String, String>();
      for (int i = 0; i < numbers.length; i++) {
         numbers[i] = numbers[i] == null ? null : numbers[i].toUpperCase();
         versions[i] = versions[i] == null ? null : versions[i].toUpperCase();
         vers.put(numbers[i], versions[i]);
      }

      QuerySpec qs = new QuerySpec(objClass);
      qs.appendWhere(new SearchCondition(objClass, numberField, numbers, true), new int[]{0});
      qs = new LatestConfigSpec().appendSearchCriteria(qs);
      QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
      Vector pv = qr.getObjectVectorIfc().getVector();

      // Latest version first
      Collections.sort(pv, new Comparator<Versioned>() {
         public int compare(Versioned o1, Versioned o2) {
            String v1 = o1.getVersionIdentifier().getValue();
            String v2 = o2.getVersionIdentifier().getValue();
            return v2.compareTo(v1);
         }
      });

      for (Iterator it = pv.iterator(); it.hasNext();) {
         Versioned p = (Versioned) it.next();
         String number;
         if (p instanceof WTPart)
            number = ((WTPart) p).getNumber();
         else if (p instanceof EPMDocument)
            number = ((EPMDocument) p).getNumber();
         else
            number = ((WTDocument) p).getNumber();
         String version = p.getVersionIdentifier().getValue();
         String targetVersion = (String) vers.get(number);

         // get the latest version or the correct version
         if (objs.get(number) == null || version.equals(targetVersion))
            objs.put(number, p);
      }
   }

   private static RevisionControlled findBizObj(Class objClass, Class masterClass, String objNumber, String masterNumber, String number, String version) throws Exception {
      if (version != null && !version.equals("")) {
         QuerySpec qs = new QuerySpec(objClass);
         qs.appendWhere(new SearchCondition(objClass, objNumber, SearchCondition.EQUAL, number), new int[]{0});
         qs.appendAnd();
         qs.appendWhere(new SearchCondition(objClass, Versioned.VERSION_IDENTIFIER + "." + VersionIdentifier.VERSIONID, SearchCondition.EQUAL, version), new int[]{0});
         qs = new LatestConfigSpec().appendSearchCriteria(qs);
         QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
         return qr.hasMoreElements() ? (RevisionControlled) qr.nextElement() : null;
      } else {
         Mastered m = findMaster(masterClass, masterNumber, number);
         if (m == null)
            return null;
         QueryResult qr = VersionControlHelper.service.allVersionsOf(m);
         return qr.hasMoreElements() ? (RevisionControlled) qr.nextElement() : null;
      }
   }

   private static Mastered findMaster(Class masterClass, String masterNumber, String number) throws Exception {
      QuerySpec qs = new QuerySpec(masterClass);
      qs.appendWhere(new SearchCondition(masterClass, masterNumber, SearchCondition.EQUAL, number), new int[]{0});
      QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
      if (!qr.hasMoreElements())
         return null;
      return (Mastered) qr.nextElement();
   }

   /**
    * ɾ��ָ�����/�汾���ĵ�,��Ҫɾ�����а汾,ָ��versionΪnull
    * @param number    Ŀ�������
    * @param version   Ŀ�����汾
    */
   public static void deleteDoc(String number, String version) throws Exception {
      WTDocument doc = findDoc(number, version);
      if (doc == null)
         return;
      deleteBizObj(doc, version == null || version.length() == 0);
   }

   /**
    * ɾ��ָ�����/�汾��PART,��Ҫɾ�����а汾,ָ��versionΪnull
    * @param number    Ŀ�������
    * @param version   Ŀ�����汾
    */
   public static void deletePart(String number, String version) throws Exception {
      WTPart part = findPart(number, version);
      if (part == null)
         return;
      deleteBizObj(part, version == null || version.length() == 0);
   }

   /**
    * ɾ��ָ�����/�汾��CAD�ĵ�,��Ҫɾ�����а汾,ָ��versionΪnull
    * @param number    Ŀ�������
    * @param version   Ŀ�����汾
    */
   public static void deleteEPM(String number, String version) throws Exception {
      EPMDocument epm = findEPM(number, version);
      if (epm == null)
         return;
      deleteBizObj(epm, version == null || version.length() == 0);
   }

   public static void deleteBaseLine(String number) throws Exception {
      Transaction tx = new Transaction();
      try {
         tx.start();
         ManagedBaseline baseline = findBaseline(number);
         if (baseline != null)
            PersistenceHelper.manager.delete(baseline);

         tx.commit();
         tx = null;
      } catch (Exception e) {
         if (tx != null)
            tx.rollback();
         throw e;
      }
   }

   @SuppressWarnings("unchecked")
   private static void deleteBizObj(RevisionControlled obj, boolean deleteAllVersion) throws Exception {
      if (VERBOSE)
         System.out.println("enter deleteBizObj(RevisionControlled obj, boolean deleteAllVersion): obj.name=" + obj.getName() + " & deleteAllVersion=" + deleteAllVersion);
      Transaction tx = new Transaction();
      try {
         tx.start();
         // ��ɾ����ָ������ÿ��Iteration�Ĺ�����ϵ: IteratedDescribeLink
         List<Persistable> objList = new ArrayList<Persistable>();
         Set linkSet = new HashSet();
         QueryResult qrIter;
         if (deleteAllVersion)
            qrIter = VersionControlHelper.service.allIterationsOf(obj.getMaster());
         else
            qrIter = VersionControlHelper.service.iterationsOf(obj);
         while (qrIter.hasMoreElements()) {
            Iterated iterated = (Iterated) qrIter.nextElement();
            objList.add(iterated);
            linkSet.addAll((PersistenceServerHelper.manager.expand(iterated, IteratedDescribeLink.DESCRIBES_ROLE, IteratedDescribeLink.class, false)).getObjectVectorIfc().getVector());
         }

         // ���ֻ��һ���汾����MasterҲ�����Զ�ɾ����Ҫ׼����Master�Ĺ���ɾ��
         // IteratedUsageLink, IteratedReferenceLink
         if (deleteAllVersion || VersionControlHelper.service.allVersionsOf(obj).size() == 1) {
            linkSet.addAll((PersistenceServerHelper.manager.expand(obj.getMaster(), IteratedUsageLink.USED_BY_ROLE, IteratedUsageLink.class, false)).getObjectVectorIfc().getVector());
            linkSet.addAll((PersistenceServerHelper.manager.expand(obj.getMaster(), IteratedReferenceLink.REFERENCED_BY_ROLE, IteratedReferenceLink.class, false)).getObjectVectorIfc().getVector());
         }

         // ɾ�����Link
         for (Iterator it = linkSet.iterator(); it.hasNext();) {
            BinaryLink link = (BinaryLink) it.next();
            if (VERBOSE)
               System.out.println("remove link : link.class=" + link.getClass().getName() + " & getRoleAObjectId=" + link.getRoleAObjectId() + " & getRoleBObjectId=" + link.getRoleBObjectId());
            PersistenceServerHelper.manager.remove(link);
         }

         // ɾ�������
         for (Iterator it = objList.iterator(); it.hasNext();) {
            Iterated iterated = (Iterated) it.next();
            // ���һ���汾�ж��С�汾�Ļ�����Ҫ��״̬����Ϊ�ǿ���״̬���ſ�ɾ��
            iterated.getIterationInfo().setState(IterationState.toIterationState("unctrld"));
            if (VERBOSE)
               System.out.println("remove iterated : iterated.class=" + iterated.getClass().getName() + " & ObjectIdentifier=" + PersistenceHelper.getObjectIdentifier(iterated));
            PersistenceHelper.manager.delete(iterated);
         }

         tx.commit();
         tx = null;

         // ɾ�������
         // removeObjectsBackground(objList);
      } catch (Exception e) {
         if (tx != null)
            tx.rollback();
         throw e;
      }
      if (VERBOSE)
         System.out.println("leave deleteBizObj(RevisionControlled obj, boolean deleteAllVersion): obj.name=" + obj.getName() + " & deleteAllVersion=" + deleteAllVersion);
   }

   /**
    * ��̨ɾ����󣬲��ܰ���������
    * @param deleteObjList
    * @throws WTException
    */
   public static void removeObjectsBackground(Collection<Persistable> deleteObjList) throws WTException {
      try {
         if (deleteObjList == null || deleteObjList.isEmpty())
            return;

         ArrayList<Object> taskList = new ArrayList<Object>(deleteObjList.size());
         for (Iterator it = deleteObjList.iterator(); it.hasNext();) {
            Persistable p = (Persistable) it.next();
            addTaskList(taskList, p);
         }

         Object deleteTask = ConsoleClassProxy.createDeleteTask();
         Object taskData = ConsoleClassProxy.createTaskData();
         ConsoleClassProxy.setTaskObjects(taskData, taskList);
         ConsoleClassProxy.setTaskData(deleteTask, taskData);

         Object task = ConsoleClassProxy.runTask(deleteTask);
         Method method = TaskResult.class.getMethod("isSuccess", new Class[0]);
         Object ret = method.invoke(task, new Object[0]);
         if ((ret instanceof Boolean) && !((Boolean) ret).booleanValue()) {
            throw new NmConsoleOpenException("");
         }
      } catch (CollectionContainsDeletedException ccde) {
         throw new WTException(ccde);
      } catch (ClassNotFoundException cnfe) {
         throw new WTException(cnfe);
      } catch (NoSuchMethodException nsme) {
         throw new WTException(nsme);
      } catch (InstantiationException ie) {
         throw new WTException(ie);
      } catch (IllegalAccessException iae) {
         throw new WTException(iae);
      } catch (InvocationTargetException ite) {
         throw new WTException(ite);
      }
   }

   private static void addTaskList(ArrayList<Object> deleteObjList, Persistable p) throws WTException {
      try {
         Object taskObject = ConsoleClassProxy.createTaskObject(p);
         ConsoleClassProxy.setObjectOnTaskDataObject(taskObject, p);
         deleteObjList.add(taskObject);
      } catch (NoSuchMethodException nsme) {
         throw new WTException(nsme);
      } catch (ClassNotFoundException cnfe) {
         throw new WTException(cnfe);
      } catch (InstantiationException ie) {
         throw new WTException(ie);
      } catch (IllegalAccessException iae) {
         throw new WTException(iae);
      } catch (InvocationTargetException ite) {
         throw new WTException(ite);
      }
   }

   public static WTPartUsageLink findPartUsageLink(String parentNumber, String version, String childNumber) throws Exception {
      WTPartUsageLink ret = null;

      WTPart parent = findPart(parentNumber, version);
      WTPartMaster childMaster = findPartMaster(childNumber);
      if (parent != null && childMaster != null) {
         QuerySpec qs = new QuerySpec(WTPartUsageLink.class);
         qs.appendWhere(new SearchCondition(WTPartUsageLink.class, ObjectToObjectLink.ROLE_AOBJECT_REF + "." + ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL,
               PersistenceHelper.getObjectIdentifier(parent).getId()), new int[]{0});
         qs.appendAnd();
         qs.appendWhere(new SearchCondition(WTPartUsageLink.class, ObjectToObjectLink.ROLE_BOBJECT_REF + "." + ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL,
               PersistenceHelper.getObjectIdentifier(childMaster).getId()), new int[]{0});

         QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
         ret = qr.hasMoreElements() ? (WTPartUsageLink) qr.nextElement() : null;
      }

      return ret;
   }

   public static EPMDescribeLink findEPMDescribeLink(String partNumber, String partVersion, String epmNumber, String epmVersion) throws Exception {
      EPMDescribeLink ret = null;

      WTPart part = findPart(partNumber, partVersion);
      EPMDocument epm = findEPM(epmNumber, epmVersion);
      if (part != null && epm != null) {
         QuerySpec qs = new QuerySpec(EPMDescribeLink.class);
         qs.appendWhere(new SearchCondition(EPMDescribeLink.class, ObjectToObjectLink.ROLE_AOBJECT_REF + "." + ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL,
               PersistenceHelper.getObjectIdentifier(part).getId()), new int[]{0});
         qs.appendAnd();
         qs.appendWhere(new SearchCondition(EPMDescribeLink.class, ObjectToObjectLink.ROLE_BOBJECT_REF + "." + ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL,
               PersistenceHelper.getObjectIdentifier(epm).getId()), new int[]{0});

         QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
         ret = qr.hasMoreElements() ? (EPMDescribeLink) qr.nextElement() : null;
      }

      return ret;
   }

   public static EPMBuildRule findEPMBuildRule(String partNumber, String partVersion, String epmNumber, String epmVersion) throws Exception {
      EPMBuildRule ret = null;

      WTPart part = findPart(partNumber, partVersion);
      EPMDocument epm = findEPM(epmNumber, epmVersion);
      if (part != null && epm != null) {
         QuerySpec qs = new QuerySpec(EPMBuildRule.class);
         qs.appendWhere(new SearchCondition(EPMBuildRule.class, ObjectToObjectLink.ROLE_AOBJECT_REF + "." + ObjectReference.KEY + "." + IterationInfo.BRANCH_ID, SearchCondition.EQUAL,
               PersistenceHelper.getObjectIdentifier(epm).getId()), new int[]{0});
         qs.appendAnd();
         qs.appendWhere(new SearchCondition(EPMBuildRule.class, ObjectToObjectLink.ROLE_BOBJECT_REF + "." + ObjectReference.KEY + "." + IterationInfo.BRANCH_ID, SearchCondition.EQUAL,
               PersistenceHelper.getObjectIdentifier(part).getId()), new int[]{0});

         QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
         ret = qr.hasMoreElements() ? (EPMBuildRule) qr.nextElement() : null;
      }

      return ret;
   }

   public static EPMBuildHistory findEPMBuildHistory(String partNumber, String partVersion, String epmNumber, String epmVersion) throws Exception {
      EPMBuildHistory ret = null;

      WTPart part = findPart(partNumber, partVersion);
      EPMDocument epm = findEPM(epmNumber, epmVersion);
      if (part != null && epm != null) {
         QuerySpec qs = new QuerySpec(EPMBuildHistory.class);
         qs.appendWhere(new SearchCondition(EPMBuildHistory.class, ObjectToObjectLink.ROLE_AOBJECT_REF + "." + ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL,
               PersistenceHelper.getObjectIdentifier(epm).getId()), new int[]{0});
         qs.appendAnd();
         qs.appendWhere(new SearchCondition(EPMBuildHistory.class, ObjectToObjectLink.ROLE_BOBJECT_REF + "." + ObjectReference.KEY + "." + ObjectIdentifier.ID, SearchCondition.EQUAL,
               PersistenceHelper.getObjectIdentifier(part).getId()), new int[]{0});

         QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
         ret = qr.hasMoreElements() ? (EPMBuildHistory) qr.nextElement() : null;
      }

      return ret;
   }

   public static ManagedBaseline findBaseline(String number) throws Exception {
      ManagedBaseline ret = null;

      if (number != null) {
         QuerySpec qs = new QuerySpec(ManagedBaseline.class);
         qs.appendWhere(new SearchCondition(ManagedBaseline.class, ManagedBaseline.NUMBER, SearchCondition.EQUAL, number, false), new int[]{0});

         QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
         ret = qr.hasMoreElements() ? (ManagedBaseline) qr.nextElement() : null;
      }

      return ret;
   }
}
