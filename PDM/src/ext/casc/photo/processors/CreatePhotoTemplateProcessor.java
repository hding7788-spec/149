package ext.casc.photo.processors;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.glaway.mpm.print.util.MBAUtil;
import com.glaway.mpm.util.WTContainerUtil;
import ext.casc.util.DBConn;
import org.apache.log4j.Logger;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.inf.container.WTContainer;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.log4j.LogR;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartReferenceLink;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.util.FolderUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.factory.dataUtilities.AttributeDataUtilityHelper;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.doc.forms.CreateDocFormProcessor;

import ext.casc.doc.technology.TechnicsTechnologyTreeHander;

public class CreatePhotoTemplateProcessor extends CreateDocFormProcessor{

	@SuppressWarnings("deprecation")
	@Override
	public FormResult doOperation(NmCommandBean clientData, List<ObjectBean> objectBeans) throws WTException {
		boolean enforce = SessionServerHelper.manager
				.setAccessEnforced(false);
		FormResult phaseResult = new FormResult();
		phaseResult = new FormResult();
		phaseResult.setStatus(FormProcessingStatus.SUCCESS);

		Iterator iterator1= objectBeans.iterator();
		do
		{
			if(!iterator1.hasNext())
			{
				break;
			}
			ObjectBean objectbean = (ObjectBean)iterator1.next();

			if(objectbean.getObject() != null && (objectbean.getObject() instanceof Persistable))
			{
				System.out.println("objectbean.getObject() is: " + objectbean.getObject());
				WTDocument wtdoc = (WTDocument)objectbean.getObject();
				try {
					TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference("casc.sast.149.PhotoTemplate");
					wtdoc.setTypeDefinitionReference(tdr);
					String zpyz = ImportPhotoTemplateProcessor.getZPYZSeqNumber("ZPYZ");
					wtdoc.setNumber("ZPYZ"+zpyz);
					LifeCycleState state = LifeCycleState.newLifeCycleState();
					state.setState(State.toState("APPROVED"));
					wtdoc.setState(state);
					WTContainer container = WTContainerUtil.getContainerByName("工艺资源库");
					wtdoc.setContainer(container);
					HashMap comboBox = clientData.getComboBox();
					ArrayList<String> photoTypeList = (ArrayList<String>) comboBox.get("photoType");
					String photoType = "";
					if(photoTypeList.size()>0){
						photoType = photoTypeList.get(0);
					}
					HashMap text = clientData.getText();
					String productNumber = (String) text.get("productNumber");
					String psyq = (String) text.get("psyq");
					String pbzz = (String) text.get("pbzz");
					String psdxType = (String) text.get("psdxType");
					//String PDCJBH = (String) text.get("PDCJBH");
					ArrayList<String> PDCJMCList = (ArrayList<String>) comboBox.get("PDCJMC");
					String PDCJMC = "";
					if(PDCJMCList.size()>0){
						PDCJMC = PDCJMCList.get(0);
					}
					/*Map maps = clientData.getComboBox();
					Set keys = maps.keySet();
					for(Object key:keys){
						if(key.toString().contains("MdlAttr+java.lang.String+WCTYPE|wt.doc.WTDocument|casc.sast.149.PhotoTemplate~MBA")){
							 PDCJMC =(String)((ArrayList)maps.get(key)).get(0);
							 break;
						}
					}*/
					String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(wtdoc);
					if(docType.endsWith("casc.sast.149.PhotoTemplate")){
						String docFolder = "/Default/照片样张库/"+photoType.replaceAll("/","、");
						FolderUtil.setDocFolder(docFolder, wtdoc);
					}
					// 保存为持久对象
					wtdoc = (WTDocument) PersistenceHelper.manager.save(wtdoc);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("productNumber",productNumber);
					map.put("psyq",psyq);
					map.put("pbzz",pbzz);
					map.put("photoType",photoType);
					map.put("psdxType",psdxType);
					map.put("PDCJBH",ext.casc.constants.Constants.photoValueMap.get(PDCJMC));
					map.put("PDCJMC",PDCJMC);
					MBAUtil.setValue(wtdoc,map);
				} catch (WTPropertyVetoException e1) {
					e1.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			SessionServerHelper.manager.setAccessEnforced(enforce);
		} while(true);

		return phaseResult;

	}



}