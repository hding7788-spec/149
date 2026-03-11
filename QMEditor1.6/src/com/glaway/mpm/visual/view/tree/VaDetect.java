/**
 * <br>Created on 2011-3-17
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree;

import java.util.List;

/**
 * <br>Created on 2011-3-17
 * ����϶������Դ�ڵ��Ŀ��ڵ�Ŀ�����
 * @author Alex.Huang - ����
 */
public interface VaDetect<T> {
 /**
 *���Դ�ڵ�Ŀ�����
 * @param snode
 * @return
 */
List<VaTreeNode> detectSource(List<T> snode);
 /**
 *���Ŀ��ڵ�Ŀ�����
 * @param onode
 * @return
 */
boolean detectObject(List<?> onode);
}
