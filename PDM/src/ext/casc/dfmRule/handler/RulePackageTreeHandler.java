package ext.casc.dfmRule.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.util.WTException;

import com.ptc.core.components.beans.TreeHandlerAdapter;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.dfmRule.common.Constants;
import ext.casc.dfmRule.util.GeneralUtil;


/**
 * 文件夹及规则包treetable<br/>
 * <p>
 * 该类的详细描述<br/>
 * </p>
 * @version 1.0.0
 * @since 1.0.0
 */
public class RulePackageTreeHandler extends TreeHandlerAdapter implements RemoteAccess {

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ptc.core.components.beans.TreeHandler#getNodes(java.util.List)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	@Override
	public Map<Object, List> getNodes(List parents) throws WTException {
		Map<Object, List> result = new HashMap<Object, List>();
		NmCommandBean cb = getModelContext().getNmCommandBean();
		Map map = cb.getText();
		for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			List children = new ArrayList();
			if (object instanceof Folder) {
				Folder folder = (Folder) object;
				QueryResult result2 = FolderHelper.service.findFolderContents(folder);
				while (result2.hasMoreElements()) {
					Object object2 = (Object) result2.nextElement();
					if (object2 instanceof Folder) {
						Folder tfolder=(Folder) object2;
						map.put(tfolder.getName(), folder.getType());
						children.add(tfolder);
					} else if (object2 instanceof WTDocument) {
						WTDocument document = (WTDocument) object2;
						document = (WTDocument) GeneralUtil.getLatestObject(document);
						map.put(document.getNumber(), document.getName());
						TypeIdentifier type = TypeIdentifierUtility.getTypeIdentifier(document);
						String typeName = type.getTypename();
						/*
						 * if (typeName.contains("com.cacgg.CSDoc")) {
						 * children.add(document); }
						 */

						//测试期间暂时这样
						if (typeName.contains(Constants.DOC_RULEPACKAGE)) {
//						if(TypeChecker.isRuleCheckReport(document))
							children.add(document);
						}
					}
				}
			}
			result.put(object, children);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ptc.core.components.beans.TreeHandler#getRootNodes()
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	@Override
	public List getRootNodes() throws WTException {
		List roots = new ArrayList();
		WTContainer container = GeneralUtil.getContainerByName(Constants.LIB_GUIZE);
		WTContainerRef containerRef = WTContainerRef.newWTContainerRef(container);
		Folder folder = FolderHelper.service.getFolder("/Default", containerRef);
		QueryResult result2 = FolderHelper.service.findFolderContents(folder);
		roots.addAll(result2.getObjectVector().getVector());
		return roots;
	}

}
