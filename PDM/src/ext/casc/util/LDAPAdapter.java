package ext.casc.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;

import wt.log4j.LogR;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.util.CollationKeyFactory;
import wt.util.SortedEnumeration;
import wt.util.WTException;
import ext.casc.model.DepartmentModel;
import ext.casc.model.MemberModel;

public class LDAPAdapter implements Serializable {
	private static Hashtable env;

	private int timeOut = 30;

	public static DirContext dirContext;

	private static String searchBase;
	private static String userBase;

	private static String org;
	
	public static String ORG = "o";

    private static HashMap nameDeptMap = new HashMap();
    private static HashMap uidGroupMap = new HashMap();
    private static HashMap deptNameCodeMap = new HashMap();
    
    private static Logger logger = LogR.getLogger(LDAPAdapter.class.getName());
    
	public LDAPAdapter() {
		init();
	}

	private void init() {
		if (env == null) {
			logger.debug("init()");
			String providerURL = null;
			String securityAuthentication = null;
			String securityCredentials = null;
			Properties p = new Properties();
			try {
				p.load(LDAPAdapter.class.getClassLoader().getResourceAsStream(
						"ext/conf/iplanet.properties"));
			} catch (FileNotFoundException e) {
				logger.debug("File not found!");
				e.printStackTrace();
			} catch (IOException e) {
				logger.debug("IO error!");
				e.printStackTrace();
			}
			providerURL = p.getProperty("providerURL");
			securityAuthentication = p.getProperty("securityAuthentication");
			securityCredentials = p.getProperty("securityCredentials");
			try {
				org = new String(p.getProperty(ORG).getBytes("iso8859_1"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			searchBase = p.getProperty("searchBase");
			userBase = p.getProperty("userBase");
			env = new Hashtable(8, 0.75f);
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, providerURL);
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, securityAuthentication);
			env.put(Context.SECURITY_CREDENTIALS, securityCredentials);
			try {
				dirContext = new InitialDirContext(env);
				logger.debug("dirContext = " + dirContext.toString());
			} catch (NamingException ne) {
				ne.printStackTrace();
			}
		}
	}

	public NamingEnumeration query(String searchBase, String filter,
			int searchScope) {
        if (dirContext == null)
            return null;
        
		logger.debug("query started..");
		logger.debug("searchBase = " + searchBase);
		logger.debug("filter = " + filter);
		NamingEnumeration results = null;
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(searchScope);
		constraints.setTimeLimit(timeOut);
		try {
			logger.debug("quering...");
			results = dirContext.search(searchBase, filter, constraints);
			logger.debug("results = " + dirContext.toString());
		} 
		catch (NamingException e) {
			e.printStackTrace();
		}
		return results;
	}

	public Collection listDepartment() 
	{
		Collection rs = new ArrayList();
		String _filter = "(&(objectClass=groupOfUniqueNames)(objectClass=top))";
		NamingEnumeration results = query(searchBase, _filter, SearchControls.ONELEVEL_SCOPE);
		if (results != null) 
		{
			DepartmentModel department = null;
			SearchResult result = null;
			Attributes attributes = null;
			Attribute cn = null;
			Attribute description = null;
			while (results.hasMoreElements()) 	{
				try {
					result = (SearchResult) results.next();
					attributes = result.getAttributes();
					
					cn = attributes.get("cn");
					
					description = attributes.get("description");
			
					department = new DepartmentModel();
					department.setDN("cn=" + cn.get() + "," + searchBase);
					department.setCN(cn.get().toString()); //ywu 2009.04.03
					department.setName(description.get().toString());
									
					rs.add(department);
				}
				catch (NamingException ne) {
					logger.info("===== LDAP Department error =====");
					ne.printStackTrace();
				}
			}
		}
		
		Collections.sort((ArrayList)rs, new Comparator() {
            public int compare(Object o1, Object o2) {
                String s1 = ((DepartmentModel) o1).getCN();
                String s2 = ((DepartmentModel) o2).getCN();
                return s1.compareTo(s2);
            }      
        });
		
		return rs;
	}
	
    /**
     * �г��¼����ż�������Ա�嵥
     * @param _searchBase
     * @return
     */
	public Collection listSubDeptAndMember(String _searchBase) {
        ArrayList list = new ArrayList();
        try {
            Collection depts = listSubDepts(_searchBase);
            if (depts != null)
                list.addAll(depts);
            Collection members = listDeptMembers(_searchBase);
            if (members != null)
                list.addAll(members);
            Collections.sort(list, new Comparator() {
                public int compare(Object o1, Object o2) {
                    String s1 = getKeyString(o1);
                    String s2 = getKeyString(o2);
                    return s1.compareTo(s2);
                }
                private String getKeyString(Object o) {
                    String key = null;
                    if (o instanceof DepartmentModel)
                        key = "0" + ((DepartmentModel) o).getCN();
                    else if (o instanceof MemberModel)
                        key = "1" + ((MemberModel) o).getName();
                    if (key == null)
                        key = "2";
                    
                    try {
                        return new String(key.getBytes("gb18030"), "iso-8859-1");
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        return key;
                    }
                }
            });
        }
        catch (NamingException ne) {
            ne.printStackTrace();
        }
        return list;
    }
    
    /**
     * �г��¼������嵥
     * @param _searchBase   ����dn��   
     * @return
     * @throws NamingException
     */
    public Collection listSubDepts(String _searchBase) throws NamingException {
        NamingEnumeration depts = query(_searchBase, "(objectClass=*)", 
                SearchControls.ONELEVEL_SCOPE);
        ArrayList list = new ArrayList();
        while (depts != null && depts.hasMoreElements()) {
            SearchResult sr = (SearchResult) depts.next();
            Attributes deptAttrs = sr.getAttributes();
            if (deptAttrs == null)
                continue;
            Attribute objClass = deptAttrs.get("objectClass");
            if (!objClass.contains("groupOfUniqueNames"))
                continue;

            DepartmentModel departmentModel = new DepartmentModel();
            String cn = (String)deptAttrs.get("cn").get();
            departmentModel.setCN(cn);  //ywu. 2009.04.03
            departmentModel.setDN("cn=" + cn + "," + _searchBase);
            departmentModel.setName((String)deptAttrs.get("description").get());
            list.add(departmentModel);
        }
        return list;
    }
    
    /**
     * �г�������Ա�嵥
     * @param _searchBase   ����dn�ַ�
     * @return
     * @throws NamingException
     */
    public Collection listDeptMembers(String _searchBase) throws NamingException {
    	logger.info("Begin����ϵͳ�е��û�����Ա�嵥>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    	logger.info("��ѯ������" + _searchBase);
        NamingEnumeration depts = query(_searchBase, "(objectClass=*)", 
                SearchControls.OBJECT_SCOPE);
        ArrayList list = new ArrayList();
        if (depts == null || !depts.hasMoreElements())
            return list;

        logger.info("������Ϣ��" + depts);
        
        SearchResult sr = (SearchResult) depts.next();
        Attributes deptAttrs = sr.getAttributes();
        if (deptAttrs == null)
            return list;
        Attribute objClass = deptAttrs.get("objectClass");
        if (!objClass.contains("groupOfUniqueNames"))
            return list;

        logger.info("����" + objClass);
        
        Attribute um = deptAttrs.get("uniqueMember");
        if (um == null)
            return list;
        logger.info("��Ա������" + um.size());
        
        NamingEnumeration members = um.getAll();
        while (members != null && members.hasMoreElements()) {
            String dn = (String) (members.nextElement());
            //logger.debug("dn: ", dn);
            NamingEnumeration mne = query(dn, "(objectClass=*)", 
                    SearchControls.OBJECT_SCOPE);
            if (mne == null || !mne.hasMoreElements())
                continue;
            SearchResult member = (SearchResult) mne.next();
            Attributes attrs = member.getAttributes();
            if (attrs == null)
                continue;
            
            //Attribute name = attrs.get("displayName");
            Attribute name = attrs.get("cn");
            Attribute uid = attrs.get("uid");
            Attribute mail = attrs.get("mail");
            Attribute attrOrg = attrs.get(ORG);
            //logger.debug("org=" + attrOrg);
            if (mail != null && attrOrg != null && org.equals(attrOrg.get().toString())) {
                // logger.debug(mail);
                MemberModel memberModel = new MemberModel();
                memberModel.setDN(dn);
                String dispName = name.get().toString()
                        + " (" + uid.get().toString() + ")";
                //memberModel.setName(name.get().toString());
                memberModel.setName(dispName);
                memberModel.setUid(uid.get().toString());
                memberModel.setProjectReportName(name.get().toString());
                
                logger.info(dispName + "|" + uid.get().toString()+"|"+name.get().toString());
                // logger.debug(memberModel.toString());
                list.add(memberModel);
            }
        }
        logger.info("End����ϵͳ�е��û�����Ա�嵥>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        return list;
    }

    /**
     * �г��¼����ż�������Ա�嵥
     * @param _searchBase   ����dn�ַ�
     * @return
     */
    public Collection listSubdepartmentAndMember(String _searchBase) {
        return listSubDeptAndMember(_searchBase);
        //return listSubdepartmentAndMember0(_searchBase);
    }
    
	public Collection listSubdepartmentAndMember0(String _searchBase) {
        Vector rs = new Vector();
        String _filter = "(objectClass=*)";
        // = searchBase;
        NamingEnumeration department = query(_searchBase, _filter,
                SearchControls.SUBTREE_SCOPE);
        
        if (department != null) {
            SearchResult resultOfDepartment = null;
            Attributes attributesOfDepartment = null;
            Attribute uniqueMember = null;
            DepartmentModel departmentModel = null;
            MemberModel memberModel = null;
            int count = 0;
            try {
                while (department.hasMoreElements()) {
                    count++;
//                  logger.debug("count = " + count);
                    resultOfDepartment = (SearchResult) department.next();
                    attributesOfDepartment = resultOfDepartment.getAttributes();
//                  System.err.println("attributesOfDepartment="
//                          + attributesOfDepartment);
                    if (attributesOfDepartment != null) {
                        if(count == 1)
                        uniqueMember = attributesOfDepartment
                                .get("uniqueMember");
                        else
                            uniqueMember = null;
//                      logger.debug(uniqueMember);
                        if (uniqueMember != null) {
//                          System.err.println("member");
//                          System.err.println("cn = "
//                                  + ((String) uniqueMember.getAll().next()));
                            //����Member����
                            NamingEnumeration members = uniqueMember.getAll();
                            NamingEnumeration member = null;
                            while(members != null && members.hasMoreElements()){
                                String dn = (String) (members.nextElement());
                                member = query(dn, _filter,
                                        SearchControls.OBJECT_SCOPE);
                                if (member != null) {
                                    SearchResult resultOfMember = null;
                                    Attributes attributesOfMember = null;
                                    Attribute objectClass = null;
                                    Attribute name = null;
                                    Attribute mail = null;
                                    Attribute attrOrg = null;
                                    if (member.hasMoreElements()) {
                                        resultOfMember = (SearchResult) member
                                                .next();
                                        attributesOfMember = resultOfMember
                                                .getAttributes();
                                        objectClass = attributesOfMember
                                                .get("objectClass");
                                        //name = attributesOfMember.get("displayName");
                                        name = attributesOfMember.get("cn");
                                        Attribute uid = attributesOfMember.get("uid");
                                        mail = attributesOfMember.get("mail");
                                        attrOrg = attributesOfMember.get(ORG);
//                                      logger.debug("org=" + org);
                                        //logger.debug("attrOrg.get() = " + attrOrg.get().toString());
                                        if (mail != null && attrOrg != null && org.equals(attrOrg.get().toString())) {
//                                          logger.debug(mail);
                                            memberModel = new MemberModel();
                                            memberModel.setDN(dn);
                                            /**
                                             * todo Use description
                                             * attribute instead of
                                             * cn!!!
                                             */
                                            String dispName = name.get().toString()
                                                    + "(" + uid.get().toString() + ")";
                                            //memberModel.setName(name.get().toString());
                                            memberModel.setName(dispName);
                                            // logger.debug(memberModel.toString());
                                            rs.add(memberModel);
                                        }
                                    }
                                }
                            
                            }

                        } else {
                            Attribute objectClass = attributesOfDepartment
                                    .get("objectClass");
                            if (objectClass.contains("groupOfUniqueNames")) {
                                departmentModel = new DepartmentModel();
                                String cn = (String)attributesOfDepartment.get("cn").get();
                                departmentModel.setCN(cn);
                                departmentModel.setDN("cn=" + cn + "," + _searchBase);
                                /**
                                 * todo Use description
                                 * attribute instead of cn!!!
                                 */
                                departmentModel.setName((String)attributesOfDepartment.get("description").get());
                                // logger.debug(departmentModel.toString());
                                rs.add(departmentModel);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        SortedEnumeration se = new SortedEnumeration(rs.elements(), new CollationKeyFactory() {
            public String getCollationString(Object o) {
                String key = null;
                if (o instanceof DepartmentModel)
                    key = ((DepartmentModel) o).getName();
                else if (o instanceof MemberModel)
                    key = ((MemberModel) o).getName();

                if (key == null)
                    key = "";
                
                try {
                    return new String(key.getBytes("GB18030"));
                }
                catch (Exception e) {
                    return key;
                }
            }
        });
        
        rs = new Vector();
        while (se.hasMoreElements())
            rs.add(se.nextElement());
        
        return rs;
	}
	  
      public boolean containMember(String _searchbase, String uid)
      {
        String dept = null;
        String _filter = "(objectclass=*)";
        boolean flag = false; 
              NamingEnumeration subdepartments = query(_searchbase, _filter, SearchControls.SUBTREE_SCOPE);
          
              if (subdepartments != null)
        {
          SearchResult resultOfDepartment = null;
          Attributes attributesOfDepartment = null;
          Attribute uniqueMember = null;
          DepartmentModel departmentModel = null;
          MemberModel memberModel = null;
     try
          {
            while (subdepartments.hasMoreElements())
            {
              resultOfDepartment = (SearchResult) subdepartments.next();
              attributesOfDepartment = resultOfDepartment.getAttributes();
              if (attributesOfDepartment != null)
              {
                uniqueMember = attributesOfDepartment.get("uniqueMember");
                //logger.info(attributesOfDepartment.get("cn"));
                            if (uniqueMember != null)
                {
                  NamingEnumeration members = uniqueMember.getAll();
                  if (members != null)
                  {
                    NamingEnumeration member = null;
                    String dn = null;
                    while (members.hasMoreElements())
                    {
                      dn = (String) (members.nextElement());
                      if (dn.indexOf(uid)>0)
                      {   
                          flag = true;
                          break;
                      }   
                     }
                        
                    }
                  }
                }
                //logger.info("flag="+flag);
                if (flag)
                   break;
            }
          }
          
          catch (NamingException ne)
          {
            ne.printStackTrace();
          }
         
          }
        return flag;
        
      }

    /**
     * ��ȡ�û�uid�Ĳ������,�Ľ��㷨(ԭ�㷨��ΪgetDepartment0)
     * ������Ϊtree�ͣ���Ҫ����û����ڵ���һ�㲿�ţ���˸�Ϊԭ������getBottomDept
     * @param uid   �û���½ID
     * @return
     */
    public static String getDepartment(String uid) {
        LDAPAdapter adapter = new LDAPAdapter();
        return adapter.getBottomDept(uid);
    }
    
    public static String getTopDepartment(String uid) {
        LDAPAdapter adapter = new LDAPAdapter();
        return adapter.getTopDeptAfterInst14(uid);
        
    }
    
    private String getTopDeptAfterInst14(String uid) {
    	  if (dirContext == null)
              return "";
          String deptName = "";
          
          String filterUser = "(&(objectClass=groupOfUniqueNames)(uniqueMember=uid=" + uid + ",*))";
          SearchControls scUser = new SearchControls();
          scUser.setSearchScope(SearchControls.SUBTREE_SCOPE);
          try {
              NamingEnumeration ne = dirContext.search(searchBase, filterUser, scUser);
              while (ne.hasMore()) {
                  SearchResult sr = (SearchResult) ne.next();
                  String name = sr.getName();
                  logger.info("name="+name);
                  //e.g. cn=14010300,cn=140103,cn=1401, take cn=140103
                  if(name.indexOf(",cn=1401")> 0 && name.lastIndexOf(",cn=1401") > name.indexOf(",cn=1401"))
                  {                  
                      name = name.substring(name.indexOf(",cn=1401")+1);                  
                      logger.info("name2="+name);
                  }
                  
                  scUser.setSearchScope(SearchControls.OBJECT_SCOPE);
                  NamingEnumeration ne1 = dirContext.search(name + "," + searchBase, "objectClass=groupOfUniqueNames", scUser);
                  if (ne1.hasMore()) {
                      sr = (SearchResult) ne1.next();
                      Attributes as = sr.getAttributes();
                      if (as != null)
                          deptName = (String) as.get("description").get();
                  }
                  
                  if (deptName != null)
                      break;
              }
          }
          catch (Exception e) {
              e.printStackTrace();
          }
          
          if (deptName == null)
              deptName = "";
          
          synchronized(nameDeptMap) {
              nameDeptMap.put(uid, deptName);
          }
          
          return deptName;
    }
    
    
    public static Vector getDeptMembers(String deptName) throws NamingException, WTException
    {    	
    	Vector userVec = new Vector();
    	LDAPAdapter adapter = new LDAPAdapter();
    	String deptCode = adapter.getDeptCode(deptName);
    	
    	WTGroup group = OrganizationServicesHelper.manager.getGroup(deptCode);
    	if(group!=null)
    	{
    		String groupDN = group.getDn();
//    		logger.info("group "+group.getName()+": "+groupDN);
	    	Collection collection = adapter.listDeptMembers(groupDN);
	    	Iterator it = collection.iterator();
	    	while(it.hasNext())
	    	{
	    		MemberModel model = (MemberModel)it.next();
//	    		logger.info("get user "+model.getName());
	    		userVec.add(model.getUid());
	    	}
    	}
    	return userVec;    	
    }
    
	public static String getDepartment0(String uid )
	{
		String getdept = "";
    String getdescription = null;
    String dept = "";
	 try 
	 {	 
	 	LDAPAdapter ldap = new LDAPAdapter();
    Collection departments = ldap.listDepartment();
    Collection members = null;
    DepartmentModel department = null;


    for (Iterator iterator = departments.iterator(); iterator.hasNext();)
    {
      department = (DepartmentModel) iterator.next();
      //logger.info(department.getDN());
      getdept= department.getName();
    //  getdeptdc = department.getDescription();
      if (ldap.containMember(department.getDN(),uid))
       {
       	//logger.info("dept ok");
       dept = getdept;
       break;
      }
    }
   }
   catch (Exception ne)
      {
        ne.printStackTrace();
        return dept;
      }
    
	    return dept;
	}
    
    public String getBottomDept(String uid) {
        String deptName = null;
        synchronized(uidGroupMap) {
            deptName = (String) uidGroupMap.get(uid);
        }
        if (deptName != null)
            return deptName;
        
        HashMap nameCodeMap = new HashMap();
        deptName = getBottomDept(uid, null, nameCodeMap);
        if (deptName == null)
            deptName = "";
        
        synchronized(uidGroupMap) {
            uidGroupMap.put(uid, deptName);
        }
        
        Object deptCode = nameCodeMap.get(deptName);
        synchronized(deptNameCodeMap) {
            deptNameCodeMap.put(deptName, deptCode);
        }
        return deptName;
    }
    
    public String getDeptCode(String deptName) {
        synchronized(deptNameCodeMap) {
            String deptCode = (String) deptNameCodeMap.get(deptName);
            return deptCode == null ? "" : deptCode;
        }
    }
    
    /**
     * ��ȡ�û����ڵĵײ㲿��(С��)
     * @param uid           �û���½id
     * @param deptList      ���ڵĲ���/С���б�
     * @param nameCodeMap   KEYΪ������ƣ�VALΪ���Ŵ�ŵ�HashMap
     * @return              С�����
     */
    private String getBottomDept(String uid, List deptList, HashMap nameCodeMap) {
        if (dirContext == null)
            return "";
        
        String deptName = null;
        try {
            String filter = "(&(objectClass=groupOfUniqueNames)(uniqueMember=uid=" + uid + ",*))";
            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration ne = dirContext.search(searchBase, filter, sc);
            String cn0 = "";
            int cnLen0 = cn0.length();
            while (ne.hasMoreElements()) {
                SearchResult sr = (SearchResult) ne.next();
                Attributes as = sr.getAttributes();
                String cn = sr.getName(); 
                if (as != null) {
                    String thisName = (String) as.get("description").get(); 
                    if (cn.length() > cnLen0) {
                        deptName = thisName; 
                        cn0 = cn;
                        cnLen0 = cn.length();
                    }
                    if (deptList != null)
                        deptList.add(thisName);
                    if (nameCodeMap != null)
                        nameCodeMap.put(thisName, (String) as.get("cn").get());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return deptName == null ? "" : deptName;
    }
    
    /**
     * ��ȡ�û����ڵĶ��㲿��
     * @param uid   �û���¼id
     * @return      �������
     */
    public String getTopDept(String uid) {
        if (dirContext == null)
            return "";
        
        String deptName = null;
        synchronized(nameDeptMap) {
            deptName = (String) nameDeptMap.get(uid);
            if (deptName != null)
                return deptName;       
        }
        
        String filterUser = "(&(objectClass=groupOfUniqueNames)(uniqueMember=uid=" + uid + ",*))";
        SearchControls scUser = new SearchControls();
        scUser.setSearchScope(SearchControls.SUBTREE_SCOPE);
        try {
            NamingEnumeration ne = dirContext.search(searchBase, filterUser, scUser);
            while (ne.hasMore()) {
                SearchResult sr = (SearchResult) ne.next();
                String name = sr.getName();
                if (name.indexOf(",") >= 0)
                    name = name.substring(name.lastIndexOf(",") + 1);
                scUser.setSearchScope(SearchControls.OBJECT_SCOPE);
                NamingEnumeration ne1 = dirContext.search(name + "," + searchBase, "objectClass=groupOfUniqueNames", scUser);
                if (ne1.hasMore()) {
                    sr = (SearchResult) ne1.next();
                    Attributes as = sr.getAttributes();
                    if (as != null)
                        deptName = (String) as.get("description").get();
                }
                
                if (deptName != null)
                    break;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        if (deptName == null)
            deptName = "";
        
        synchronized(nameDeptMap) {
            nameDeptMap.put(uid, deptName);
        }
        
        return deptName;
    }
    
    
	public void getSubDeptFromName(String dept){
		NamingEnumeration ne = query(searchBase, "(&(objectclass=groupOfUniqueNames)(description="+dept+"))", SearchControls.SUBTREE_SCOPE);
		while (ne.hasMoreElements()) {
			SearchResult elem = (SearchResult) ne.nextElement();
			try {
				String s = elem.getName();
				logger.info(s+"," + searchBase);
				recurseDeptFromBase(s+"," + searchBase, (String)elem.getAttributes().get("cn").get());
				logger.info(elem.getName());
				logger.info(elem.getAttributes());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void recurseDeptFromBase(String base,String excludeCN){
		NamingEnumeration ne  = query(base, "(objectclass=groupOfUniqueNames)", SearchControls.SUBTREE_SCOPE);
		while (ne.hasMoreElements()) {
			try {
			SearchResult elem = (SearchResult) ne.nextElement();
			String s = elem.getName();
			
			String ss = (String)elem.getAttributes().get("cn").get();
				if(!excludeCN.equals(ss)){
					logger.info(elem.getAttributes().get("uniqueMember").getAll().next());
						recurseDeptFromBase(s+"," + base,ss);
				}
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}

    
	public static void main(String[] args) {
		
		LDAPAdapter ldap = new LDAPAdapter();
		Collection departments = ldap.listDepartment();
		Collection members = null;
		DepartmentModel department = null;
		for (Iterator iterator = departments.iterator(); iterator.hasNext();) {
			department = (DepartmentModel) iterator.next();
			members = ldap.listSubdepartmentAndMember(department.getDN());
			for (Iterator iter = members.iterator(); iter.hasNext();) {

			}
		}
	}

	public static void main2(String[] args) {
		String _searchBase = "cn=Type Administrators,ou=people,cn=Windchill_8.0,cn=Application Services,o=jacky";
		String _filter = "(objectclass=*)";
		LDAPAdapter test = new LDAPAdapter();
		NamingEnumeration results = test.query(_searchBase, _filter,
				SearchControls.OBJECT_SCOPE);
		StringBuffer sb = new StringBuffer();
		sb.append("<Objects>\r\n");
		if (results != null) {
			SearchResult result = null;
			Attributes attributes = null;
			Attribute uniqueMember = null;
			try {
				if (results.hasMoreElements()) {
					result = (SearchResult) results.next();
					attributes = result.getAttributes();
					uniqueMember = attributes.get("uniqueMember");
					if (uniqueMember != null) {
						NamingEnumeration members = uniqueMember.getAll();
						String dn = null;
						while (members.hasMoreElements()) {
							dn = (String) (members.nextElement());
							sb.append("<Object dn=\"" + dn + "\"/>\r\n");
						}
					}
				}
			} catch (NamingException ne) {
				ne.printStackTrace();
			}
		}
		sb.append("</Objects>");
		logger.debug(sb);
	}
	
//yin modify for get user attr --2008/11/24 begin
    /**
     * ��ȡ�û��İ�ȫ����
     * @param uid   �û���¼id
     * @return      ��ȫ����
     */
    public String getUserAttr0(String uid,String attrName) {
        if (dirContext == null)
            return "";
        
        String returnAttrName = null;

        
        String filterUser = "(uid=" + uid + ")";
        SearchControls scUser = new SearchControls();
        scUser.setSearchScope(SearchControls.SUBTREE_SCOPE);
        try {
        	
        	String searchUserBase = "ou=people,dc=pmserver,dc=nriet,dc=com";
        	searchUserBase = userBase;
//        	logger.info("searchUserBase is = " + searchUserBase);
            NamingEnumeration ne = dirContext.search(searchUserBase, filterUser, scUser);
            while (ne.hasMore()) {
                SearchResult sr = (SearchResult) ne.next();

                Attributes as = sr.getAttributes();
                
                if (as != null)
                {
                	Attribute attr = as.get(attrName);
                	if(attr!=null)
                	{
                		//ywu: remove null pointer exception. 
//                		returnAttrName = (String)attr.get();
                		
                		Object obj = attr.get();
                		if(obj !=null)
                			returnAttrName = (String)obj;
                	}
                		             	
                }
                
                if (returnAttrName != null)
                    break;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        if (returnAttrName == null)
        	returnAttrName = "";

        
        return returnAttrName;
    }
	
    /**
     * ��ȡ�û�uid�İ�ȫ����
     * @param uid   �û���½ID
     * @return
     */
    public static String getUserAttr(String uid,String attrName) {
        LDAPAdapter adapter = new LDAPAdapter();
        return adapter.getUserAttr0(uid,attrName);
    }
//yin modify for get user attr --2008/11/24 end
    
    public Vector getSubDeptMembersFromName(String dept){
		Vector v = new Vector();
		NamingEnumeration ne = query(searchBase, "(&(objectclass=groupOfUniqueNames)(description="+dept+"))", SearchControls.SUBTREE_SCOPE);
		while (ne.hasMoreElements()) {
			SearchResult elem = (SearchResult) ne.nextElement();
			try {
				String s = elem.getName();
				Attribute ar = elem.getAttributes().get("uniqueMember");
				if(ar!= null){
					NamingEnumeration n = ar.getAll();
					while (n.hasMoreElements()) {
						String suid = (String) n.nextElement();
						NamingEnumeration uu = query(suid, "(objectclass=*)", SearchControls.SUBTREE_SCOPE);
						while (uu.hasMoreElements()) {
							SearchResult elem3 = (SearchResult) uu.nextElement();
							Attribute Auid = elem3.getAttributes().get("uid");
							if(Auid != null){
								if(!v.contains(Auid.get())){
									v.add((String)Auid.get());
								}
							}
						}
					}
				}
				
				recurseDeptFromBase(s+"," + searchBase, (String)elem.getAttributes().get("cn").get(),v);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return v;
	}
	
	public void recurseDeptFromBase(String base,String excludeCN,Vector v){
		NamingEnumeration ne  = query(base, "(objectclass=groupOfUniqueNames)", SearchControls.SUBTREE_SCOPE);
		while (ne.hasMoreElements()) {
			try {
			SearchResult elem = (SearchResult) ne.nextElement();
			String s = elem.getName();
			
			String ss = (String)elem.getAttributes().get("cn").get();
			if(!excludeCN.equals(ss)){
				Attribute ar = elem.getAttributes().get("uniqueMember");
				if(ar!= null){
					NamingEnumeration n = ar.getAll();
					while (n.hasMoreElements()) {
						String suid = (String) n.nextElement();
						NamingEnumeration uu = query(suid, "(objectclass=*)", SearchControls.SUBTREE_SCOPE);
						while (uu.hasMoreElements()) {
							SearchResult elem3 = (SearchResult) uu.nextElement();
							Attribute Auid = elem3.getAttributes().get("uid");
							if(Auid != null){
								if(!v.contains(Auid.get())){
									v.add((String)Auid.get());
								}
							}
						}
					}
				}
					recurseDeptFromBase(s+"," + base,ss,v);
				}
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getUserIDFromName(String name){
		NamingEnumeration ne  = query(userBase, "(&(objectclass=*)(displayName="+name+"))", SearchControls.SUBTREE_SCOPE);
		String id = null;
		while (ne.hasMoreElements()) {
			try {
			SearchResult elem = (SearchResult) ne.nextElement();
			String s = elem.getName();
			
			id = (String)elem.getAttributes().get("uid").get();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return id;
	}
}