package com.glaway.mpm.util;

import wt.fc.PersistenceHelper;
import wt.pom.PersistenceException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.NonLatestCheckoutException;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

public class WorkInProcessUtil {
	/**
	 * 出库
	 *
	 * @author qianlong
	 * @date 2012-10-22
	 * @param workable
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @return Workable
	 *
	 */
	public static Workable checkout(Workable workable) throws WTException, WTPropertyVetoException {
		Workable workingCopy = null;

		if (!WorkInProgressHelper.isCheckedOut(workable)) {
			WorkInProgressHelper.service.checkout(workable, WorkInProgressHelper.service.getCheckoutFolder(), "");
			workingCopy = WorkInProgressHelper.service.workingCopyOf(workable);
		} else {
			if (!WorkInProgressHelper.isWorkingCopy(workable)) {
				workingCopy = WorkInProgressHelper.service.workingCopyOf(workable);
			} else {
				workingCopy = workable;
			}
		}

		return workingCopy;
	}

	/**
	 * 入库
	 *
	 * @author qianlong
	 *@date 2012-9-17
	 * @param workable
	 * @return
	 * @throws Exception
	 */
	public static Workable checkin(Workable workable) throws WTPropertyVetoException, WTException {

		workable = (Workable) PersistenceHelper.manager.refresh(workable);
		if (WorkInProgressHelper.isCheckedOut(workable)) {
			WorkInProgressHelper.service.checkin(workable, "");
		}

		return workable;
	}

	public static Workable checkin(Workable workable,String back) throws WTPropertyVetoException, WTException {

		workable = (Workable) PersistenceHelper.manager.refresh(workable);
		if (WorkInProgressHelper.isCheckedOut(workable)) {
			WorkInProgressHelper.service.checkin(workable, back);
		}

		return workable;
	}


	/**
	 * 如果出库了就获取出库后的对象
	 *
	 * @author qianlong
	 * @date 2012-10-18
	 * @param workable
	 * @return
	 * @throws Exception
	 * @return Workable
	 *
	 */
	public static Workable getWorkCopyIfCheckout(Workable workable) throws WTPropertyVetoException, WTException {
		Workable workingCopy = null;

		if (!WorkInProgressHelper.isCheckedOut(workable)) {
			WorkInProgressHelper.service.checkout(workable, WorkInProgressHelper.service.getCheckoutFolder(), "");
			workingCopy = WorkInProgressHelper.service.workingCopyOf(workable);
		}
		return workingCopy;
	}

	/**
	 * 升版序
	 *
	 * @author qianlong
	 * @date 2013-6-5
	 * @param workable
	 * @return
	 * @throws WTException
	 * @throws PersistenceException
	 * @throws WTPropertyVetoException
	 * @throws WorkInProgressException
	 * @throws NonLatestCheckoutException
	 *
	 */
	public static Workable updateIteration(Workable workable) throws NonLatestCheckoutException,
			WorkInProgressException, WTPropertyVetoException, PersistenceException, WTException {

		if (!WorkInProgressHelper.isCheckedOut(workable)) {
			WorkInProgressHelper.service.checkout(workable, WorkInProgressHelper.service.getCheckoutFolder(), "");
			workable = WorkInProgressHelper.service.workingCopyOf(workable);
		} else {
			if (!WorkInProgressHelper.isWorkingCopy(workable)) {
				workable = WorkInProgressHelper.service.workingCopyOf(workable);
			}
		}

		return WorkInProgressHelper.service.checkin(workable, "");
	}

}
