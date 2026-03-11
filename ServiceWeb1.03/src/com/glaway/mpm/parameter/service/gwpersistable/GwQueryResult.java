package com.glaway.mpm.parameter.service.gwpersistable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * 自定义的搜索结果集<br>
 *
 * @author 龙秀川
 */
@SuppressWarnings("unchecked")
public class GwQueryResult implements Iterator, Serializable {
    private static final long serialVersionUID = 5779942097833105524L;

    private int currPos;

    private boolean canRemove;

    private Vector dataVector;

    /**
     * 缺省无参构造函数
     *
     */
    public GwQueryResult() {
        this(new Vector());
    }

    /**
     * 根据指定的数据集构建
     *
     * @param dataVector
     *            搜索结果集包含的数据集
     */
    public GwQueryResult(Vector dataVector) {
        this.dataVector = dataVector == null ? new Vector() : dataVector;
        currPos = 0;
        canRemove = false;
    }

    /**
     * 返回是否还有数据
     *
     * @return true表示还有数据，false表示没有数据
     */
    @Override
	public boolean hasNext() {
        return currPos < dataVector.size();
    }

    /**
     * 返回当前位置的数据对象，同时指定移向下一个位置
     *
     * @return 当前位置的数据
     */
    @Override
	public Object next() {
        if (!hasNext())
            throw new NoSuchElementException();
        canRemove = true;
        return dataVector.get(currPos++);
    }

    /**
     * 移除当前位置所对应的数据对象
     *
     */
    @Override
	public void remove() {
        if (!canRemove)
            throw new IllegalStateException();
        canRemove = false;
        currPos--;
        dataVector.remove(currPos);
    }

    /**
     * 重置对象，使之返回没有调用过next()时的状态
     *
     */
    public void reset() {
        canRemove = false;
        currPos = 0;
    }

    /**
     * 清空结果集中的所有数据
     *
     */
    public void clear() {
        reset();
        dataVector.clear();
    }

    /**
     * 获取结果集的Vector表示
     *
     * @return 结果集的Vector表示
     */
    public Vector getVector() {
        return dataVector;
    }

    /**
     * 获取结果集的对象数据表示
     *
     * @param a
     *            指定类型的数组
     * @return 结果集的对象数据表示
     */
    public Object[] toArray(Object[] a) {
        return dataVector.toArray(a);
    }

    /**
     * 追加单个对象到结果集中
     *
     * @param obj
     *            待增加到结果集中的对象
     */
    public void append(Object obj) {
        dataVector.add(obj);
    }

    /**
     * 追加所有collection中的所有数据对象到结果集中
     *
     * @param collection
     *            数据对象集
     */
    public void append(Collection collection) {
        dataVector.addAll(collection);
    }

    /**
     * 追加另一个CmQueryResult的所有数据对象到当前结果集中
     *
     * @param qr
     *            CmQueryResult结果集
     */
    public void append(GwQueryResult qr) {
        dataVector.addAll(qr.getVector());
    }

    /**
     * 返回当前结果集包含的数据个数
     *
     * @return 当前结果集包含的数据个数
     */
    public int size() {
        return dataVector.size();
    }
}
