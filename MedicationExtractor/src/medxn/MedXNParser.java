package medxn;
import org.apache.uima.resource.ResourceInitializationException;
import org.ohnlp.medxn.cc.MedXNCC;
public class MedXNParser {
	public static void runMedXN() throws ResourceInitializationException {
		MedXNCC med=new MedXNCC();
		med.initialize();
	}
}
