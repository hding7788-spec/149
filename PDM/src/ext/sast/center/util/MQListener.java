/**
 *
 */
package ext.sast.center.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import ext.sast.center.util.RestMessageQueue;



public class MQListener implements ServletContextListener {
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
//		log.info("MQ Worker Starting...");
//		RestMessageQueue.startRestQueue();
//		log.info("MQ Worker Start OK!");
		System.out.println("启动MQ消息");
		for(int i=1;i<=2;i++){
			MQWorker work = new MQWorker();
			System.out.println("线程号为："+work.getId());
			work.start();
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("华丽的线程分割线----------------------"+work.getId()+"!!!!");
		}
	}
}