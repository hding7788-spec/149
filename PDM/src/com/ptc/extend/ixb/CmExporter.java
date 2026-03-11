package com.ptc.extend.ixb;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import wt.facade.ixb.IxbElement;
import wt.util.WTException;

public interface CmExporter {

    public abstract List getExportedList();

    public abstract void addExportedObject(Object obj);

    public abstract int getNextSequence();

    public abstract void finalizeJar() throws WTException;

    public abstract String storeDocument(IxbElement ixbelement)
            throws WTException;

    public abstract String storeDocumentAsContent(IxbElement ixbelement)
            throws WTException;

    public abstract String storeDocumentAsRole(IxbElement ixbelement, String s,
            String s1) throws WTException;

    public abstract String storeDocumentAsNameInDir(IxbElement ixbelement,
            String s, String s1) throws WTException;

    public abstract String storeDocumentInDir(IxbElement ixbelement, String s)
            throws WTException;

    public abstract String storeDocument(IxbElement ixbelement, String s)
            throws WTException;

    public abstract void reallyStoreContent(InputStream inputstream, String s)
            throws WTException;

    public abstract void logger(Object obj);

    public abstract void processException(Exception exception)
            throws WTException;

    public abstract String getSavePathInJar(Object obj);

    public abstract void storeTypeDefinition(String s);

    public abstract void storeIBADefinition(String s);

    public abstract HashSet getTypeDefinitionSet();

    public abstract HashSet getIBADefinitionSet();
}
