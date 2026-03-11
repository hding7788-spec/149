package com.glaway.mpm.util;

import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.org._WTPrincipal;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;

public class ProcessEditorHander {
    public static WTUser getUser(String name) throws WTException {
        QuerySpec qs = new QuerySpec(WTUser.class);
        int index[] = { 0 };
        SearchCondition scCondition = new SearchCondition(WTUser.class, _WTPrincipal.NAME, SearchCondition.EQUAL, name);
        qs.appendWhere(scCondition, index);
        QueryResult result = PersistenceHelper.manager.find((StatementSpec) qs);
        if (result.hasMoreElements()) {
            return (WTUser) result.nextElement();
        }

        return null;
    }
}
