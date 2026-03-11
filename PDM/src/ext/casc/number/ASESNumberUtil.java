package ext.casc.number;


import java.io.UnsupportedEncodingException;
import java.util.PropertyResourceBundle;
import java.util.Vector;

import org.apache.log4j.Logger;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.inf.container.WTContainer;
import wt.log4j.LogR;
import wt.pdmlink.PDMLinkProduct;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import ext.casc.util.IBAHelper;

public class ASESNumberUtil {

	 private static final Logger log;

    static {
       try {
          log = LogR.getLogger(ASESNumberUtil.class.getName());
       }
       catch (Exception e) {
          throw new ExceptionInInitializerError(e);
       }
    }
	public ASESNumberUtil() {
    }
    
	public static Vector getContainer(String name) 
		throws WTException
	{
		Vector vector = new Vector();
		QuerySpec queryspec = new QuerySpec (PDMLinkProduct.class);
		SearchCondition sc = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL,  name);
		queryspec.appendSearchCondition(sc);
		QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
		WTContainer wtcontainer = null;
		while (queryresult.hasMoreElements()) {
			Object obj = queryresult.nextElement();
			if(obj instanceof PDMLinkProduct)
			    vector.add((PDMLinkProduct)obj);
		}
		return vector;
	}
	
	public static PDMLinkProduct getPDMLinkProductByName(String name) 
		throws WTException
	{
		PDMLinkProduct product = null;
		QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
		SearchCondition sc = new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL,  name);
		qs.appendSearchCondition(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if(qr.hasMoreElements())
			product = (PDMLinkProduct) qr.nextElement();
		return product;
	}
	
	public static String getDocAbbreviate(WTObject obj)
    {
        String jh="";
        try{
            String types=IBAHelper.getSoftType(obj);
            String doctype=IBAHelper.getIBAStringValue(obj,"SUBTYPE" );
            String type=types.substring(types.lastIndexOf(".")+1, types.length());
            String key=type+"*"+doctype;
            jh = getStrFromProperties(key,"ext.ases.number.wjjh");       
        }catch(Exception et){
        	System.out.println(et);
        }
        return jh;
    }
	public static String getStrFromProperties(String key,String propertiefile)throws WTException,UnsupportedEncodingException {
        String strinfo="";
        try
        {            
            log.debug(key+"--"+propertiefile);
            PropertyResourceBundle prBundle=(PropertyResourceBundle)PropertyResourceBundle.getBundle(propertiefile);
            byte[] temp =null;
            temp = key.getBytes("GB2312");
            key=new String(temp,"ISO-8859-1");
            temp = prBundle.getString(key).getBytes("ISO-8859-1");
            strinfo=new String(temp,"GB2312");
        }catch(java.util.MissingResourceException mre){
        	System.out.println("Missing ResourceException！请检查");
        }catch(UnsupportedEncodingException uee){
        	System.out.println("编码错误！请检查");
        }catch(Exception e){
        	System.out.println("Key值不存在！请检查");
        }
        return strinfo;
	}
}