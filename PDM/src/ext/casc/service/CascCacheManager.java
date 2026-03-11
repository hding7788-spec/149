package ext.casc.service;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import ext.casc.process.util.ProcessUtil;
import ext.casc.service.bean.StoredCacheBean;

import wt.cache.CacheManager;
import wt.org.WTUser;
import wt.util.WTException;

public class CascCacheManager extends CacheManager {

    private static final long serialVersionUID = 8251899522891254074L;

    public CascCacheManager() throws RemoteException {
        super();
    }
    
    public void initializeGroupAndUsers(){
        try {
            Map<String, List<WTUser>> map = ProcessUtil.getGroupAndUsersInOrgContainer();
            StoredCacheBean bean = new StoredCacheBean();
            bean.setMap(map);
            refreshGongYiZuZhangMap(bean);
        } catch (WTException e) {
            e.printStackTrace();
        }
    }
    
    public void initializeCheJianAndUsers(){
        try {
            Map<String, List<WTUser>> map = ProcessUtil.getCheJianGroupAndUsers();
            StoredCacheBean bean = new StoredCacheBean();
            bean.setMap(map);
            refreshGongYiYuanMap(bean);
        } catch (WTException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<WTUser>> getGroupAndUsers() {
        Object object = getEntry(CascServiceConstants.CACHE_ZUZHANGANDUSERS);
        if (object instanceof StoredCacheBean) {
            StoredCacheBean bean = (StoredCacheBean)object;
            return bean.getMap();
        }
        return null;
    }

    public Map<String, List<WTUser>> getCheJianGroupAndUsers() {
        Object object = getEntry(CascServiceConstants.CACHE_CHEJIANANDUSERS);
        if (object instanceof StoredCacheBean) {
            StoredCacheBean bean = (StoredCacheBean)object;
            return bean.getMap();
        }
        return null;
    }
    
    public void refreshGongYiZuZhangMap(StoredCacheBean bean) {
        putEntry(CascServiceConstants.CACHE_ZUZHANGANDUSERS, bean);
    }

    public void refreshGongYiYuanMap(StoredCacheBean bean) {
        putEntry(CascServiceConstants.CACHE_CHEJIANANDUSERS, bean);
    }

    public Object getEntry(String key) {
        return super.get(key);
    }

    public void putEntry(String key, Object value) {
        super.remove(key);
        super.put(key, value);
    }

    public void removeEntry(String key) {
        super.remove(key);
    }

}
