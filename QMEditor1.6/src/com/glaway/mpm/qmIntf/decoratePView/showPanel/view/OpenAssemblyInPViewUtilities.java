package com.glaway.mpm.qmIntf.decoratePView.showPanel.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.Timer;

import com.ptc.pview.pvapi.SelectionObserver;
import com.ptc.pview.pvapi.ShapeSceneObserver;
import com.ptc.pview.pvapi.TreeObserver;
import com.ptc.pview.pvapi.ViewObserver;
import com.ptc.pview.pvkapp.AnnoSetTableEntry;
import com.ptc.pview.pvkapp.AnnotationSaveEvent;
import com.ptc.pview.pvkapp.AsyncEvent;
import com.ptc.pview.pvkapp.AsyncEventCB;
import com.ptc.pview.pvkapp.ComponentInstance;
import com.ptc.pview.pvkapp.Instance;
import com.ptc.pview.pvkapp.Kernel;
import com.ptc.pview.pvkapp.MenuItemEvent;
import com.ptc.pview.pvkapp.ShapeInstance;
import com.ptc.pview.pvkapp.ShapeScene;
import com.ptc.pview.pvkapp.ShapeView;
import com.ptc.pview.pvkapp.ViewableSourceVisitorImpl;
import com.ptc.pview.pvloader.ProtocolHandler;
import com.ptc.pview.pvloader.ProtocolHandlerEvents;
import com.ptc.pview.pvloader.RemoteIf;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.ManagedObject;
import com.ptc.pview.utils.dom.Message;
import com.ptc.pview.utils.dom.MessageProtocolException;
import com.ptc.pview.utils.dom.MessageQueue;
import com.ptc.pview.utils.dp.PVTracer;

    /**
      * MyActor class which extends ManagedObject
      * An object of this class is the starting point
      * for using the PV Java APIs
      */
class MyActor extends ManagedObject{

    public MyActor(){
        super();
        queue = new MessageQueue();
        ManageSelf(queue, "Actor");
    }

    private Timer msgTimer;

    /**
     *  This class is required for ProductView message processing.
     */
    class CheckForMessages implements ActionListener{
        public void actionPerformed(ActionEvent event){
            try{
                if(queue.IsInService()){
                    Message m = queue.PollMessage();
                    if(m != null){
                        queue.ForwardMessageToHandler(m);
                    }
                } else {
                    queue.ForwardMessageToHandler(null);
                }
            } catch(InvalidActorException e){
                System.out.println("InvalidActorException");
                msgTimer.stop();
            } catch(ConnectionLostException e){
                System.out.println("ConnectionLostException");
                msgTimer.stop();
            } catch(MessageProtocolException e){
                System.out.println("MessageProtocolException");
                msgTimer.stop();
            } catch(ActorShutdownException e){
                System.out.println("ActorShutdownException");
                msgTimer.stop();
            }
        }
    };

    public TreeObserver getTreeObserver(){
        TreeObserver treeObserver = new MyTreeObserver();
        ManageObject(treeObserver);
        return treeObserver;
    }

    public SelectionObserver getSelectionObserver(){
        SelectionObserver so = new MySelectionObserver();
        ManageObject(so);
        return so;
    }

    public AsyncEventCB getAsyncEvent(String use){
        AsyncEventCB as = new MyAsyncEvent(use);
        ManageObject (as.GetAsyncEventIf());
        return as;
    }

    public boolean manageObject(com.ptc.pview.utils.dom.ManagedObject managedObject){
        return ManageObject(managedObject);
    }

    public AnnotationSaveEvent getAnnotationSaveEvent(){
        AnnotationSaveEvent ev = new MyAnnotationSaveEvent();
        ManageObject (ev.GetAnnotationSaveEventIf());
        return ev;
    }

    public ViewableSourceVisitorImpl getViewableSourceVisitorImpl(){
        MyViewableSourceVisitorEvents vse = new MyViewableSourceVisitorEvents();
        ViewableSourceVisitorImpl     vsv = new ViewableSourceVisitorImpl();
        vsv.SetEventHandler(vse);
        ManageObject (vsv);
        return vsv;
    }


    public MenuItemEvent getMenuItemEvent(){
        MenuItemEvent ev = new MyMenuItemEvent();
        ManageObject (ev.GetMenuItemIf());
        return ev;
    }

    public ShapeSceneObserver getShapeSceneObserver(ShapeScene scene){
        ShapeSceneObserver sceneObserver = new MySceneObserver(scene);
        ManageObject(sceneObserver);
        return sceneObserver;
    }

    public ProtocolHandler GetProtocolHandler(ProtocolHandlerEvents phe,
            String  protocols, RemoteIf rif){
        ProtocolHandler ph = new ProtocolHandler(phe, protocols, rif);
        ManageObject (ph);
        return ph;
    }

    public Kernel getKernel(){
        try{
            return (Kernel)GetDistinguishedObject(Kernel.CLASS_NAME, "pvkernel");
        } catch (Throwable x){
            System.out.println("Exception doing GetDistinguishedObject()");
        }
        return null;
    }

    public void listenForEvents(){
        // Start a timer to process ProductView messages
        CheckForMessages cfm = new CheckForMessages();
        msgTimer = new Timer(10,cfm);
        msgTimer.start();
    }

    public String GetObjectClass() {
        return "pvexamples::pvexamplesutilities::MyActor";
    }
    private MessageQueue queue;
};

//Inherit from the ViewableSourceVisitorEvents class

class MyViewableSourceVisitorEvents extends  com.ptc.pview.pvkapp.ViewableSourceVisitorEvents {
    public MyViewableSourceVisitorEvents(){
        super();
    }

    public boolean Visit(com.ptc.pview.pvkapp.ViewableSource viewableSource){
        System.out.println(" - Visited - ");
        return true;
    }
}

class MyAsyncEvent extends com.ptc.pview.pvkapp.AsyncEventCB{
    long finishTime, startTime = -1, timeTaken;
    MyAsyncEvent(String reason){
        m_description =reason;
    }

    public void OnProgress(long progress){
        System.out.println("In OnProgress " + m_description +" - " + progress);
    }

    public void OnComplete( long status){
        System.out.println("- Inside OnComplete(), pvExampleUtilities.java " +
                            m_description +" "+status +
                            "  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        if(startTime != -1){
            finishTime = System.currentTimeMillis();
            timeTaken = finishTime - startTime;
            System.out.println("- Time taken to load structure: " + timeTaken + " ms");
        }
    }

    private String m_description;
}

class MyMenuItemEvent extends com.ptc.pview.pvkapp.MenuItemEvent {
    MyMenuItemEvent(){
        super("Customized Button",0);
    }

    public void OnDoCommand(){
        System.out.println("- MyMenuItemEvent.OnDoCommand(), " +
                            "pvExamplesUtilities.java");
    }
}

//TreeObserver class which when set up observes a Tree object
//Call backs from this class are called in response to changes
//in the Tree being observed
class MyTreeObserver extends TreeObserver {
    protected void OnBeginUpdate(){
        System.out.println("MyTreeObserver.OnBeginUpdate()");
    }

    protected void OnEndUpdate(){
        System.out.println("MyTreeObserver.OnEndUpdate()");
    }

    protected void OnInstanceCreate(Instance instance, Instance parent, String name){
        System.out.println("MyTreeObserver.OnInstanceCreate() " + name);
    }
    protected void OnInstanceRemove( Instance instance, Instance parent){
        System.out.println("MyTreeObserver.OnInstanceRemove()");
    }

    protected void OnInstanceName(Instance instance){}
    protected void OnInstanceLocation(Instance instance){}

    public String GetObjectClass() {
        return "pvapps::javatestapp::MyTreeObserver";
    }
}



class MyUtilsImpl extends com.ptc.pview.utils.dp.UtilsImpl {
    public String getenv(String envName){
        System.out.println("MyImpl.getenv");
        return System.getenv(envName);
    }
    public void load(String name) throws UnsatisfiedLinkError {
        System.out.println("MyImpl.load");
        System.load(name);
    }
    public Preferences Preferences_systemRoot(){
        System.out.println("MyImpl.Preferences_systemRoot");
        return null;
    }
    public Preferences Preferences_userRoot(){
        System.out.println("MyImpl.Preferences_userRoot");
        return null;
    }
    public String GetArchModel(){
        return System.getProperties().getProperty("sun.arch.data.model");
    }
}

class MyAnnotationSaveEvent extends com.ptc.pview.pvkapp.AnnotationSaveEvent {

    public void OnSaveAnnotation( AnnoSetTableEntry annoSetTableEntry, AsyncEvent saveEvent){
        System.out.println("Java In onSaveAnnotation");
    }

    public void OnCreateAnnotation( AnnoSetTableEntry annoSetTableEntry,
                                        AsyncEvent createEvent){
        try{
            System.out.println("Java In onCreateAnnotation "
                    + annoSetTableEntry.GetName()
                    + " disk "
                    + annoSetTableEntry.GetAstDiskFileName()
                    + " thumb "
                    + annoSetTableEntry.GetThumbnailDiskFileName());
            createEvent.Complete();
        } catch (java.lang.Exception e){
        }
    }

    public void OnDeleteAnnotation( AnnoSetTableEntry annoSetTableEntry,
                                        AsyncEvent deleteEvent){
        System.out.println("Java In onDeleteAnnotation");
    }
}

class MyProtocolHandlerEvents extends  com.ptc.pview.pvloader.ProtocolHandlerEvents {
    public void DownloadFile(String url,  String diskfile, String handle) {
        System.out.println("MyProtocolHandlerEvents - DownloadFile\n");
        String fileIn = url.substring(8);
        System.out.println("MyProtocolHandlerEvents - DownloadFile " + fileIn);
        try{
            byte[] buffer = new byte[4096];
            FileInputStream   fin = new FileInputStream(fileIn);
            FileOutputStream fout = new FileOutputStream(diskfile);
            int  bytesRead;
            PVTrace.print("MyProtocolHandlerEvents.DownloadFile("+ fileIn +") 1");

            while ((bytesRead = fin.read(buffer)) >0 ){
                fout.write(buffer, 0, bytesRead);
            }
            fin.close();
            fout.close();

            PVTrace.print("MyProtocolHandlerEvents.DownloadFile("+ fileIn +") 2");
            DownloadComplete(diskfile, handle);
        } catch (java.lang.Exception e){
            System.out.println("Exception copying file");
        }
    }

    public void UploadFile(String url,  String diskfile, String handle){
        System.out.println("MyProtocolHandlerEvents - UploadFile TBD");
    }


    private static PVTracer PVTrace
    = new PVTracer("com.ptc.pview.pvapps.javatestapp","JavaTestApp");
};

    /**
      * This class observes the selections being made
      * Callbacks from this class are called in response to
      *changes in the selection
      */
class MySelectionObserver
extends SelectionObserver {
    protected void OnBeginUpdate(){
        System.out.println("MySelectionObserver.OnBeginUpdate()");
    }

    protected void OnEndUpdate(){
        System.out.println("MySelectionObserver.OnEndUpdate()");
    }


    protected void OnInsertItems(Instance[] items, long recurseMask){
        System.out.println("OnInsertItems");
        try{
            System.out.println(  "insertItems length "
                    + items.length
                    + " recurse"
                    + recurseMask);

            for(int i = 0; i < items.length; i++){
                Instance inst = items[i];
                System.out.println("MySelectionObserver.OnInsertItems() Insert  "
                        + inst.GetObjectClass()
                        + " Called "
                        + inst.GetName()
                        + " into  Selection list.");
            }
        } catch(InvalidActorException e){
            System.out.println("OnInsertItems() caught InvalidActorException:-"
                    + e.getMessage()
                    + ".");
            e.printStackTrace();
        } catch(ConnectionLostException e){
            System.out.println("OnInsertItems() caught ConnectionLostException:-"
                    + e.getMessage()
                    + ".");
            e.printStackTrace();
        } catch(MessageProtocolException e){
            System.out.println("OnInsertItems() caught MessageProtocolException:-"
                    + e.getMessage()
                    + ".");
            e.printStackTrace();
        } catch(ActorShutdownException e){
            System.out.println("OnInsertItems() caught ActorShutdownException:-"
                    + e.getMessage()
                    + ".");
            e.printStackTrace();
        }
        System.out.println("Out OnInsertItems");
    }

    protected void OnRemoveItems(Instance[] items, long recurseMask){

        try{

            for(int i = 0; i < items.length; i++){

                Instance inst = (Instance) items[i];

                System.out.println("MySelectionObserver.OnRemoveItems() Remove  "
                        + inst.GetObjectClass()
                        + "Called "
                        + inst.GetName()
                        + " from Selection list.");
            }
        } catch(InvalidActorException e){

            System.out.println("OnRemoveItems() caught InvalidActorException:-"
                    + e.getMessage()
                    + ".");
            e.printStackTrace();
        }
        catch(ConnectionLostException e){

            System.out.println("OnRemoveItems() caught ConnectionLostException:-"
                    + e.getMessage()
                    + ".");
            e.printStackTrace();
        }
        catch(MessageProtocolException e){

            System.out.println("OnRemoveItems() caught MessageProtocolException:-"
                    + e.getMessage()
                    + ".");
            e.printStackTrace();
        }
        catch(ActorShutdownException e){

            System.out.println("OnRemoveItems() caught ActorShutdownException:-"
                    + e.getMessage()
                    + ".");
            e.printStackTrace();
        }
    }

    protected void OnClearSelection(){

        System.out.println("MySelectionObserver.OnClearSelection()");
    }


    public String GetObjectClass() {

        return "pvapps::javatestapp::MySelectionObserver";
    }
};

    /**
      * This class observes a ShapeScene object
      * Call backs from this class are called in response to changes
      * in the ShapeScene being observed
      */
class MySceneObserver extends ShapeSceneObserver{

    boolean airbusExample = false;
    ArrayList visibleList, hiddenList;
    MySceneObserver(ShapeScene scene){

        m_shapeScene= scene;

    }

    protected void OnBeginUpdate(){

        System.out.println("MySceneObserver.OnBeginUpdate()");
    }

    protected void OnEndUpdate(){

        System.out.println("MySceneObserver.OnEndUpdate()");
    }
    protected void OnShapeInstanceCreate(ShapeInstance shapeInstance){

        System.out.println("MySceneObserver.OnShapeInstanceCreate()<---------------------------------------------");
    }
    protected void OnShapeInstanceRemove(ShapeInstance shapeInstance){

        System.out.println("MySceneObserver.OnShapeInstanceRemove()");
    }

    protected void
        OnShapeInstanceVisibility(ShapeInstance shapeInstance, long visible){

        try{

            if(airbusExample){

                System.out.println("- Name: "+shapeInstance.GetInstance().GetName() + "is " +visible );

                System.out.println("MySceneObserver.OnShapeInstanceVisibility()<------------------------------------------------------");

                if(visible == 0){

                    hiddenList.add(shapeInstance.GetInstance());
                    visibleList.remove(shapeInstance.GetInstance());
                }
                else if(visible == 1){

                    visibleList.add(shapeInstance.GetInstance());
                    hiddenList.remove(shapeInstance.GetInstance());
                }

            }

        } catch(Throwable x){

            x.printStackTrace();
        }

    }

    protected void
    OnShapeInstanceHighlight(ShapeInstance shapeInstance, long highlighted){

        System.out.println("MySceneObserver.OnShapeInstanceHighlight()");
    }

    protected void
    OnShapeInstanceLocation(ShapeInstance shapeInstance){

        System.out.println("MySceneObserver.OnShapeInstanceLocatio()");
    }

    protected void OnShapeViewCreate(ShapeView shapeView){

        System.out.println("MySceneObserver.OnShapeViewCreate()");
    }
    protected void OnShapeViewRemove1(ShapeView shapeView){

        System.out.println("MySceneObserver.OnShapeViewRemove()");
    }

    public String GetObjectClass() {
        return "pvapps::javatestapp::MySceneObserver";
    }

    private ShapeScene m_shapeScene;
}
    /**
      * This class observes a View object
      * Call backs from this class are called in response to changes
      * in the View being observed
      */
class MyViewObserver extends ViewObserver {

    MyViewObserver(ShapeView view){

        m_shapeView = view;
    }
    protected void OnBeginUpdate(){

        System.out.println("MyViewObserver.OnBeginUpdate()");
    }

    protected void OnEndUpdate(){

        System.out.println("MyViewObserver.OnEndUpdate()");
    }

    protected void OnStatusMessageChange(){

        try{

            System.out.println("MyViewObserver.OnStatusMessaeChange"
                    + m_shapeView.GetStatusMessage());
        } catch(java.lang.Exception e){

            System.out.println("ListenForEvents() caught java.lang.InteruptedException:-"
                    + e.getMessage()
                    + ".");
            e.printStackTrace();
        }

    }

    public String GetObjectClass() {

        return "pvapps::javatestapp::MyViewObserver";
    }
    private ShapeView m_shapeView;
};

//Utility class
class ComponentMovement {

    ComponentInstance compInst;
    Movement mvmt;
    int steps;
    long intervalBtwSteps;

    ComponentMovement(ComponentInstance cInst, Movement movement,
                                        int numOfSteps, long interval){

        compInst = cInst;
        mvmt = movement;
        steps = numOfSteps;
        intervalBtwSteps = interval;
    }

}
//Utility class
class Movement {

    double delX = 0, delY = 0, delZ = 0, rotX = 0, rotY = 0, rotZ = 0;

    Movement(double dX,double dY,double dZ,double rX,double rY,double rZ) {
        delX = dX;
        delY = dY;
        delZ = dZ;
        rotX = rX;
        rotY = rY;
        rotZ = rZ;
    }

    Movement() {
    }

    void SetTranslation(double dX,double dY,double dZ) {
        delX = dX;
        delY = dY;
        delZ = dZ;
    }
    void SetRotation(double rX,double rY,double rZ) {
        rotX = rX;
        rotY = rY;
        rotZ = rZ;
    }
}


//Utility class
class ComponentMovements {
    ArrayList compMovmtList = new ArrayList();
    long intervalBtwMovement;
    ComponentMovements(ArrayList list, long interval) {
        compMovmtList = list;
        intervalBtwMovement = interval;
    }

    ComponentMovements(long interval) {
        intervalBtwMovement = interval;
    }

    void AddComponentMovement(ComponentMovement compMvmt) {
        compMovmtList.add(compMvmt);
    }

    void RemoveComponentMovement(ComponentMovement compMvmt) {
        compMovmtList.remove(compMvmt);
    }
}

//Utility class
class CompInstOptions {

    ComponentInstance ci;
    ArrayList olList = new ArrayList();
    ArrayList getList() {return olList;}

    void addOLFile(OLFile file) {
        olList.add(file);
    }

    void removeOLFile(OLFile file) {
        olList.remove(file);
    }

    CompInstOptions(ComponentInstance cInst) {
        try {

            ci = cInst;
        } catch (java.lang.Exception ex) {
        }
    }
}

//Utility class
class OLFile {
    String filename;
    String label;
    public String toString() {
        return label;
    }

    String getFilename() {
        return filename;
    }
    String getLabel () {
        return label;
    }

    OLFile(String file, String textLabel) {
        filename = new String(file);
        label = new String(textLabel);
    }
}

//Utility class - This class stores the name, id path and the instance itself
class InstanceListMember {

    Instance instance;
    String name, id;
    boolean visible;

    public String toString() {
        return name;
    }

    String getId () {
        return id;
    }
    Instance getInstance ()  {
        return instance;
    }

    InstanceListMember (Instance i) {
        try {
            instance = i;
            name = i.GetName();
            id = i.GetIDPath();
            visible = true;
        } catch(java.lang.Exception e) {
        }
    }
}
