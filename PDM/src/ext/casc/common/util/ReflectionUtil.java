package ext.casc.common.util;

import org.apache.log4j.Logger;
import wt.log4j.LogR;
import wt.method.RemoteMethodServer;
import wt.util.WTException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;

public class ReflectionUtil {
    private static Logger logger = LogR.getLogger(ReflectionUtil.class.getName());
    public static Object invokeOnMethodServer(String methodName,Class cls,Object instance,Class[] argTypes,Object[] argValues) throws WTException{
        Object retValue = null;
        RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
        if(hasReturnValue(methodName,cls,argTypes)) {
            try{
                 retValue = methodServer.invoke(methodName, cls.getName(), null, argTypes, argValues);
             }catch (RemoteException e){
                logger.error(e);
                throw new WTException(e);
             }catch (InvocationTargetException e){
                logger.error(e);
                throw new WTException(e);
            }

        }else{
            try{
                 methodServer.invoke(methodName, cls.getName(), null, argTypes, argValues);
            }catch (RemoteException e){
                logger.error(e);
                throw new WTException(e);
            }catch (InvocationTargetException e){
                logger.error(e);
                throw new WTException(e);
            }
        }
        
        return retValue;
    }

    private static boolean hasReturnValue(String methodName, Class cls, Class[] argTypes)throws WTException{
        boolean hasReturnValue = true;
        try {
            Method  method = cls.getMethod(methodName,argTypes);
            Class returnType = method.getReturnType();
            if(void.class.equals(returnType)){
                hasReturnValue = false;
            }
        } catch (NoSuchMethodException e) {
            logger.error(e);
            throw new WTException(e);
        }
        return hasReturnValue;
    }
}
