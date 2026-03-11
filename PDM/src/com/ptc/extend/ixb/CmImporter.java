package com.ptc.extend.ixb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

import wt.content.Streamed;
import wt.enterprise.RevisionControlled;
import wt.facade.ixb.IxbDocument;
import wt.fc.Persistable;
import wt.fc.collections.WTHashSet;
import wt.inf.container.WTContainerRef;
import wt.org.WTUser;
import wt.util.WTException;
import wt.vc.Iterated;

public interface CmImporter {

    public abstract boolean isValid();

    public abstract void putInExistedHashtable(String s, Object obj);

    public abstract void putInNewCreatedHashtable(String s, Object obj);
    
    public abstract void putIntExistedNumberObject(String s,RevisionControlled revisoin);

    public abstract void putInMissingObjectSet(String s, String s1, String s2,
            String s3);

    public abstract void putInMissingMasterObjectSet(String s, String s1);

    public abstract void putInProperlyReceivedObjectSet(Persistable persistable);

    public abstract void removeFromProperlyReceivedObjectSet(
            Persistable persistable);

    public abstract boolean isProperlyReceived(Persistable persistable);

    public abstract WTHashSet getDocHashSet();

    public abstract WTHashSet getPartHashSet();

    public abstract WTHashSet getEPMDocHashSet();

    public abstract HashSet getMissingObjectSet();

    public abstract Hashtable getExistObjects();
    public abstract HashMap<String, RevisionControlled> getExistedNumberObject();

    public abstract Hashtable getNewObjects();

    public abstract ArrayList getAllObjects();

    public abstract WTContainerRef getWTContainerRef() throws WTException;
    public abstract void setWTContainerRef(WTContainerRef containerRef) throws WTException;

    public abstract WTContainerRef getWTContainerRef(Class kass, String name) throws WTException;

    public abstract WTUser getOperator();

    public abstract ArrayList getAllFilesInJar() throws WTException;

    public abstract ArrayList getAllXmlFilesInJar() throws WTException;

    public abstract ArrayList getAllWTDocumentInJar() throws WTException;

    public abstract ArrayList getAllWTPartInJar() throws WTException;

    public abstract ArrayList getAllEPMDocumentInJar() throws WTException;

    public abstract ArrayList getAllTopObjectXmlFileInJar() throws WTException;

    public abstract ArrayList getXmlDocumentsUnderDirInJar(String s)
            throws WTException;

    public abstract IxbDocument getIxbDocumentFromJar(String s)
            throws WTException;

    public abstract boolean checkFileExistInJar(String s) throws WTException;

    public abstract InputStream getContentAsInputStream(String s)
            throws WTException;

    public abstract InputStream getXMLEntryAsInputStream(String s)
            throws WTException;

    public abstract Streamed getContentAsStreamed(String s) throws WTException;

    public abstract void logger(Object obj);

    public abstract void processException(Exception exception)
            throws WTException;

    public abstract boolean isLoopTest();

    public abstract String getLoopTestPrefix();

    public abstract boolean isChangeLifecycle();

    public abstract String getLifecycleTemplateName();

    public abstract String getLifecycleStateName();

    public abstract void doOperationAfterStore(Iterated iterated,
            HashMap hashmap);

    public abstract String adjustViewName(String s);

    public abstract boolean isChangeFolder();

    public abstract String getLocalImportFolder();

    public abstract String getRemoteURL();

    public abstract void pubImportedObject(Object obj, Object obj1);
    
    public abstract void putNumberImplementAdvise(String number, String implement);

    public abstract HashMap<String, String> getNumberImplementAdvise();
}
