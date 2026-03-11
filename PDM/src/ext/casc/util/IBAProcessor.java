package ext.casc.util;

import java.util.HashMap;
import java.util.Locale;

import wt.iba.value.IBAHolder;
import wt.util.WTContext;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.LinkedHashMap;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.applicationcontext.implementation.DefaultServiceProvider;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.ptc.core.command.common.bean.entity.PrepareEntityCommand;
import com.ptc.core.foundation.type.server.impl.SoftAttributesHelper;
import com.ptc.core.meta.common.AttributeIdentifier;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.core.meta.common.DataSet;
import com.ptc.core.meta.common.DefinitionIdentifier;
import com.ptc.core.meta.common.DiscreteSet;
import com.ptc.core.meta.common.IdentifierFactory;
import com.ptc.core.meta.common.OperationIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeInstanceIdentifier;
import com.ptc.core.meta.common.impl.WCTypeIdentifier;
import com.ptc.core.meta.container.common.AttributeContainer;
import com.ptc.core.meta.container.common.AttributeContainerSpec;
import com.ptc.core.meta.container.common.AttributeTypeSummary;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.meta.type.common.TypeInstance;
import com.ptc.core.meta.type.runtime.server.PopulatedAttributeContainerFactory;

public class IBAProcessor implements RemoteAccess {

	public static final String IBACONST_LEGAL_VALUE_SET = "LEGAL_VALUE_SET";

	public static final String IBACONST_STRING_LENGTH_SET = "STRING_LENGTH_SET";

	public static final String IBA_IDENTIFIER = "IBA_IDENTIFIER";

	public static final String IBA_ATTRIBUTE_IDENTIFIER = "IBA_ATTRIBUTE_IDENTIFIER";

	public static final String IBA_ATTRIBUTE_TYPE_SUMMARY = "IBA_ATTRIBUTE_TYPE_SUMMARY";

	public static final String IBA_NAME = "IBA_NAME";

	public static final String IBA_VALUE = "IBA_VALUE";

	public static final String IBA_LABEL = "IBA_LABEL";

	public static final String IBA_DATATYPE = "IBA_DATATYPE";

	public static final String IBA_OPTIONS_VECTOR = "IBA_OPTIONS_VECTOR";

	public static final String IBA_REQUIRED = "IBA_REQUIRED";

	public static final String IBA_EDITABLE = "IBA_EDITABLE";

	public static final String IBA_STRING_LENGTH_MIN = "IBA_STRING_LENGTH_MIN";

	public static final String IBA_STRING_LENGTH_MAX = "IBA_STRING_LENGTH_MAX";

	public static final String IBA_FROM_DEFINITION = "IBA_FROM_DEFINITION";

	public static final String IBA_UNDEFINED = "IBA_UNDEFINED";

	public static LinkedHashMap getIBAValues(Object obj) throws Exception {
		if (!RemoteMethodServer.ServerFlag) {
			return (LinkedHashMap) RemoteMethodServer.getDefault().invoke("getIBAValues",
					IBAProcessor.class.getName(), null,
					new Class[] { Object.class }, new Object[] { obj });
		}
		System.out.println("Excute into!");
		LinkedHashMap ibas = new LinkedHashMap();
		if (obj instanceof IBAHolder)
			getIBAValues((IBAHolder) obj, null, ibas);
		else
			getIBAValues(String.valueOf(obj), null, ibas);
		return ibas;
	}

	public static void getIBAValues(IBAHolder ibaHolder, List ibaList,
			LinkedHashMap ibaMap) throws WTException {
		System.out.println("exvute romote!");
		getIBAValuesInternal(ibaHolder, ibaList, ibaMap, true);
	}

	public static void getIBAValues(String typeIdentifier, List ibaList,
			LinkedHashMap ibaMap) throws WTException {
		String protoHeader = WCTypeIdentifier.PROTOCOL
				+ WCTypeIdentifier.PROTOCOL_SEPARATOR;
		if (!typeIdentifier.startsWith(protoHeader))
			typeIdentifier = protoHeader + typeIdentifier;
		// Debug.P("Get type ibas: ", typeIdentifier);
		getIBAValuesInternal(typeIdentifier, ibaList, ibaMap, true);
	}
	
	//to judge whether the input IBA is contained in the IBA list of the doc type
	
	public static boolean isIBAContained(String iba, Map IBAMap) throws WTException {
		boolean contained = false;
		try {			
			Iterator itr = IBAMap.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry entry = (Map.Entry) itr.next();
				Object key = entry.getKey();
				if (iba.equals((String) key)) {
					contained = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contained;
	}

	/**
	 * the realization of getting IBA attributes
	 * 
	 * @param obj
	 *            IBAHolder object or typeIdentifer string
	 * @param ibaList *
	 * @param ibaMap *
	 * @throws WTException
	 */
	static TypeInstance getIBAValuesInternal(Object obj, List ibaList,
			LinkedHashMap ibaMap, boolean returnOpts) throws WTException {
		System.out.println("excute internal!");
		TypeInstanceIdentifier tii = null;
		Locale locale = WTContext.getContext().getLocale();
		boolean forTypedObj = false;

		// get TypeInstanceIdentifier
		if (obj instanceof IBAHolder) { // obj is IBAHolder(Typed) object
			tii = TypeIdentifierUtility.getTypeInstanceIdentifier(obj);
			forTypedObj = true;
		} else { // obj is TypeIdentifier string, e.g.
			// WTTYPE|wt.doc.WTDocument|...
			IdentifierFactory idFactory = (IdentifierFactory) DefaultServiceProvider
					.getService(
							com.ptc.core.meta.common.IdentifierFactory.class,
							"default");
			TypeIdentifier ti = (TypeIdentifier) idFactory.get((String) obj);
			tii = ti.newTypeInstanceIdentifier();
		}
		// get TypeInstance
		TypeInstance typeInstance = null;
		try {
			if (false) {
				PopulatedAttributeContainerFactory pacFactory = (PopulatedAttributeContainerFactory) DefaultServiceProvider
						.getService(PopulatedAttributeContainerFactory.class,
								"virtual");
				AttributeContainer ac = pacFactory.getAttributeContainer(null,
						(TypeIdentifier) tii.getDefinitionIdentifier());

				if (ac == null) {
					if (obj instanceof String)
						throw new WTException("Undefined SoftType: " + obj);
					else
						throw new WTException("Undefined SoftType: " + tii);
				}

				AttributeContainerSpec acSpec = new AttributeContainerSpec();
				IdentifierFactory idFact = (IdentifierFactory) DefaultServiceProvider
						.getService(
								com.ptc.core.meta.common.IdentifierFactory.class,
								"logical");
				AttributeTypeIdentifier ati1 = (AttributeTypeIdentifier) idFact
						.get("ALL_SOFT_SCHEMA_ATTRIBUTES", tii
								.getDefinitionIdentifier());
				acSpec.putEntry(ati1, true, true);
				AttributeTypeIdentifier ati2 = (AttributeTypeIdentifier) idFact
						.get("ALL_SOFT_ATTRIBUTES", tii
								.getDefinitionIdentifier());
				acSpec.putEntry(ati2, true, true);
				AttributeTypeIdentifier ati3 = (AttributeTypeIdentifier) idFact
						.get("ALL_SOFT_CLASSIFICATION_ATTRIBUTES", tii
								.getDefinitionIdentifier());
				acSpec.putEntry(ati3, true, true);
				if (tii.isInitialized())
					acSpec
							.setNextOperation(OperationIdentifier
									.newOperationIdentifier("STDOP|com.ptc.windchill.update"));
				else
					acSpec
							.setNextOperation(OperationIdentifier
									.newOperationIdentifier("STDOP|com.ptc.windchill.create"));
				PrepareEntityCommand peCmd = new PrepareEntityCommand();
				peCmd.setLocale(locale);
				peCmd.setFilter(acSpec);
				peCmd.setSource(tii);
				peCmd = (PrepareEntityCommand) peCmd.execute();
				typeInstance = peCmd.getResult();
				Set set = (Set) typeInstance.getSingle(ati3);
				if (set != null) {
					for (Iterator iterator = set.iterator(); iterator.hasNext(); typeInstance
							.purge((AttributeTypeIdentifier) iterator.next()))
						;
				}
				typeInstance.purge(ati1);
				typeInstance.purge(ati2);
				typeInstance.purge(ati3);
				AttributeTypeIdentifier ati[] = typeInstance
						.getAttributeTypeIdentifiers();
				for (int j = 0; j < ati.length; j++)
					if (ati[j].getContext() instanceof AttributeTypeIdentifier)
						typeInstance.purge(ati[j]);
			} else {
				typeInstance = SoftAttributesHelper.getSoftSchemaTypeInstance(
						tii, null, locale);
			}
		} catch (WTPropertyVetoException wtpropertyvetoexception) {
			throw new WTException(
					wtpropertyvetoexception,
					"SoftAttributesHelper.getSoftSchemaTypeInstance(): "
							+ "Exception encountered when trying to create a type instance");
		} catch (UnsupportedOperationException unsupportedoperationexception) {
			throw new WTException(
					unsupportedoperationexception,
					"SoftAttributesHelper.getSoftSchemaTypeInstance(): "
							+ "Exception encountered when trying to create a type instance");
		}

		// add the undefined attributes toIBAHolder
		if (forTypedObj) {
			// TypeInstanceUtility.populateMissingTypeContent(typeInstance,
			// null);
		}
		
		// get IBA attributes one by one
		AttributeIdentifier[] ais = typeInstance.getAttributeIdentifiers();
		for (int i = 0; ais != null && i < ais.length; i++) {
			DefinitionIdentifier di = ais[i].getDefinitionIdentifier();
			AttributeTypeIdentifier ati = (AttributeTypeIdentifier) di;
			AttributeTypeSummary ats = typeInstance
					.getAttributeTypeSummary(ati);

			String ibaIdentifier = ais[i].toExternalForm();
			String name = ati.getAttributeName();
			ati.getWithTailContext();
			String value = String.valueOf(typeInstance.get(ais[i]));
			String dataType = ats.getDataType();
			String label = ats.getLabel();
			Boolean required = ats.isRequired() ? new Boolean(true) : null;
			Boolean editable = ats.isEditable() ? new Boolean(true) : null;

			int min = ats.getMinStringLength();
			int max = ats.getMaxStringLength();
			Integer minStringLength = min == 0 ? null : new Integer(min);
			Integer maxStringLength = max == 0 ? null : new Integer(max);

			LinkedHashMap ibaInfo = new LinkedHashMap();
			ibaInfo.put(IBA_IDENTIFIER, ibaIdentifier);
			ibaInfo.put(IBA_ATTRIBUTE_IDENTIFIER, ais[i]);
			ibaInfo.put(IBA_ATTRIBUTE_TYPE_SUMMARY, ats);
			ibaInfo.put(IBA_NAME, name);
			ibaInfo.put(IBA_VALUE, value);
			ibaInfo.put(IBA_LABEL, label);
			ibaInfo.put(IBA_DATATYPE, dataType);
			ibaInfo.put(IBA_REQUIRED, required);
			ibaInfo.put(IBA_EDITABLE, editable);
			ibaInfo.put(IBA_STRING_LENGTH_MIN, minStringLength);
			ibaInfo.put(IBA_STRING_LENGTH_MAX, maxStringLength);
			
			if (returnOpts) {
				Vector options = null;
				DataSet dsVal = ats.getLegalValueSet();
				if (dsVal != null && dsVal instanceof DiscreteSet) {
					Object[] eles = ((DiscreteSet) dsVal).getElements();
					options = new Vector();
					for (int j = 0; eles != null && j < eles.length; j++) {
						options.add(String.valueOf(eles[j]));
					}
				}
				ibaInfo.put(IBA_OPTIONS_VECTOR, options);
			}

			if (ibaList != null) {
				ibaList.add(ibaInfo);
			}
			if (ibaMap != null) {
				ibaMap.put(name, ibaInfo);
			}
		}

		return typeInstance;
	}
	
	
	public static Boolean isModeledIBA(String attrName){
		if ("number".equalsIgnoreCase(attrName)
				|| "name".equalsIgnoreCase(attrName)
				|| "title".equalsIgnoreCase(attrName)
				|| "description".equalsIgnoreCase(attrName)
				|| "folder.id".equalsIgnoreCase(attrName)
				|| "lifeCycle.id".equalsIgnoreCase(attrName)
				|| "teamTemplate.id".equalsIgnoreCase(attrName)) {
			return true;
		}else{
			return false;
		}
	}

}
