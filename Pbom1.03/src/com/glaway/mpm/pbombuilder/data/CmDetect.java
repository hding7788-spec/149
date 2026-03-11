package com.glaway.mpm.pbombuilder.data;

import java.util.List;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;

/**
 * <br>Created on 2012-11-17
 * 检测拖动过程中源节点和目标节点的可用性
 * @author chenyunlong
 */
public interface CmDetect<T> {
 /**
 *检测源节点的可用性
 * @param snode
 * @return
 */
List<CmTreeNode> detectSource(List<T> snode);
 /**
 *检测目标节点的可用性
 * @param onode
 * @return
 */
boolean detectObject(List<?> onode);
}