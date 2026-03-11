/**
 *
 */
package ext;

import org.json.JSONArray;
import org.json.JSONObject;

import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.pom.Transaction;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.wvs.VisualizationHelperFactory;

/**
 * @author cfire
 *
 */
public class TestRep {
	public static void test() {

		Representable obj ;
		try {
			  Transaction tx = null;
			  tx = new Transaction();
	            tx.start();
			ReferenceFactory rf = new ReferenceFactory();
			obj = (Representable)rf.getReference("OR:wt.epm.EPMDocument:1105857").getObject();

			//obj = (Representable)rf.getReference("OR:wt.part.WTPart:1105842").getObject();
			boolean isOk = VisualizationHelperFactory.HELPER.loadRepresentation("C:\\test\\rep\\", getRefFromObject(obj), false,
	                "default", "导入可视化", true,false, false);
			if(isOk){
				Representation rep = (Representation) VisualizationHelperFactory.HELPER.getRepresentation(obj, "default");
				System.out.println(rep);
			}else{
				System.out.println(isOk);
			}
			tx.commit();
            tx = null;
		} catch (WTRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static  String getRefFromObject(Persistable persistable) {
        try {
            ReferenceFactory referencefactory = new ReferenceFactory();
            return referencefactory.getReferenceString(ObjectReference.newObjectReference(persistable.getPersistInfo()
                    .getObjectIdentifier()));
        } catch (Exception exception) {
        }
        return null;
    }
	public static void main(String[] args) {
		 JSONObject datas = new JSONObject();
         JSONObject doc = new JSONObject();
         JSONObject part = new JSONObject();
         JSONArray parts = new JSONArray();
         parts.put(part);
         doc.put("billid","");
         doc.put("dept","");
         doc.put("graphcode","");
         doc.put("graphname","");
         doc.put("billdate","");
         doc.put("operator","");
         doc.put("pdmid","");
         doc.put("processId","");
         doc.put("memo","");

         part.put("partcode","");
         part.put("partname","");

         datas.put("data",doc);
         doc.put("bodys",parts);

         System.out.println(datas.toString().replaceAll("\"", "'"));
	}
}
