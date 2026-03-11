package com.glaway.mpm.pbombuilder.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.util.CmTaskInfo;
import com.glaway.mpm.pbombuilder.util.PviewTask;
import com.ptc.pview.pvapi.SelectionObserver;
import com.ptc.pview.pvkapp.AsyncEventCB;
import com.ptc.pview.pvkapp.InstanceVisitor;
import com.ptc.pview.pvkapp.InstanceVisitorEvents;
import com.ptc.pview.pvkapp.InstanceVisitorImpl;
import com.ptc.pview.pvkapp.Kernel;
import com.ptc.pview.pvkapp.PropertiesVisitorEvents;
import com.ptc.pview.pvkapp.Property;
import com.ptc.pview.pvkapp.ViewStateVisitor;
import com.ptc.pview.pvkapp.ViewStateVisitorEvents;
import com.ptc.pview.pvkapp.ViewStateVisitorImpl;
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
import com.ptc.pview.utils.dom.StringHolder;

public class CmPVActor extends ManagedObject implements ActionListener {
   private final static CmLogger log = CmLogger.getLogger(CmPVActor.class.getName()); 
   private MessageQueue     queue;
   private Timer            event_timer;
   private final static int LISTENER_DELAY = 10;

   public CmPVActor() {
      super();
      queue = new MessageQueue();

      ManageSelf(queue, "PVActor");

      try {
         event_timer = new Timer(LISTENER_DELAY, this);
         event_timer.start();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public String GetObjectClass() {
      return "ext::cobi::pview";
   }

   public Kernel getKernel() {
      try {
         return (Kernel) GetDistinguishedObject(Kernel.CLASS_NAME, "pvkernel", 20000);
      } catch (InvalidActorException e) {
         e.printStackTrace();
      } catch (ConnectionLostException e) {
         e.printStackTrace();
      } catch (MessageProtocolException e) {
         e.printStackTrace();
      } catch (ActorShutdownException e) {
         e.printStackTrace();
      }
      return null;
   }

   public void shutdown() {
      try {

         if (event_timer != null) {
            event_timer.stop();
            event_timer = null;
         }

         if (queue != null) {
            queue.ShutdownQueue();
            queue = null;
         }

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void actionPerformed(ActionEvent e) {
      try {
         if (queue.IsInService()) {
            Message m = queue.PollMessage();
            if (m != null) {
               queue.ForwardMessageToHandler(m);
            }
         } else {
            queue.ForwardMessageToHandler(null);
         }
      } catch (MessageProtocolException ex) {
         ex.printStackTrace();
      } catch (InvalidActorException ex) {
         ex.printStackTrace();
      } catch (ConnectionLostException ex) {
         ex.printStackTrace();
      } catch (ActorShutdownException ex) {
         ex.printStackTrace();
      }
   }

   class MyAsyncEvent extends com.ptc.pview.pvkapp.AsyncEventCB {
      private String reason;
      MyAsyncEvent(String reason) {
         this.reason = reason;
      }

      public void OnProgress(long progress) {
         log.info("In OnProgress " + " - " + progress);
      }

      public void OnComplete(long status) {
         if (reason != null && reason.endsWith(".finishAnimFrame")) {
            try {
               PviewTask.sendTask(CmTaskInfo.newCmTaskInfo(reason, this, null), null);
            } catch (CmTaskException e) {
               e.printStackTrace();
            }
         }
         else if ("ZoomAll".equalsIgnoreCase(reason) || "ZoomSelected".equalsIgnoreCase(reason)) {
        	 //PviewTask.postTask("mainframe.setStatus", reason + (status == 0 ? " �ɹ�" : " ʧ��"));
        	 //log.info("status value-------" + status);
        	 String success = (status == 0 ? " 成功" : " 失败");
        	 //log.info("reason-------" + reason + success);
        	 PviewTask.postTask("mainframe.setStatus", reason + success);
         }
      }
   }

   public AsyncEventCB getAsyncEvent(String reason) {
      AsyncEventCB as = new MyAsyncEvent(reason);
      ManageObject(as.GetAsyncEventIf());
      return as;
   }

   public SelectionObserver getSelectionObserver(SelectionObserver so) {
      ManageObject(so);
      return so;
   }

   public ProtocolHandler GetProtocolHandler(ProtocolHandlerEvents phe, String protocols, RemoteIf rif) {
      ProtocolHandler ph = new ProtocolHandler(phe, protocols, rif);
      ManageObject(ph);
      return ph;
   }
   
   public ViewStateVisitor getViewStateVisitor(ViewStateVisitorEvents vsve)
   {
     ViewStateVisitorImpl vsvimpl = new ViewStateVisitorImpl();
     vsvimpl.SetEventHandler(vsve);
     ManageObject(vsvimpl);
     return vsvimpl;
   }
   
   public InstanceVisitor getInstanceVisitor(InstanceVisitorEvents ive) {
	     InstanceVisitorImpl iv = new InstanceVisitorImpl();
	     iv.SetEventHandler(ive);
	     ManageObject(iv);
	     return iv;
	   }
   
   public CmPropertiesVisitorImpl getPropertiesVisitor() {
	   CmPropertiesVisitorImpl visitor = new CmPropertiesVisitorImpl();
	   visitor.SetEventHandler(new MyPropertiesVisitorEvents(visitor));
	   ManageObject(visitor);
	   return visitor;
   }
   
   class MyPropertiesVisitorEvents extends PropertiesVisitorEvents {
	private CmPropertiesVisitorImpl visitor;
	   
	public MyPropertiesVisitorEvents(CmPropertiesVisitorImpl visitor) {
		this.visitor = visitor;
	}

	@Override
	public boolean Visit(Property prop) {
		StringHolder holder = new StringHolder();
		try {
			if(prop.GetValue(holder)) {
				String[] property = new String[3];
				property[0] = prop.GetName();
				property[1] = prop.GetGroup();
				property[2] = holder.value;
				
				visitor.getProperties().add(property);
			}
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
   }
}
