import java.util.List;
import java.util.Map;


public abstract class CuTypeScheme {
	protected String text = "";
	List<String>        data_kc;
	Map<String, CuType>  data_tc;
	CuType              data_t;
	@Override public String toString() {
		return text;
	}
	public void calculateType(CuContext context) throws NoSuchTypeException {}
}

class TypeScheme extends CuTypeScheme {
	public TypeScheme(List<String> kc, Map<String, CuType> tc , CuType t){
		super.data_kc=kc;
		super.data_tc=tc;
		super.data_t=t;
		super.text=Helper.printList("<", data_kc, ">", ",")+" "+Helper.printMap("(", data_tc, ")", ",")+" : "+t.toString();
	}
	@Override public void calculateType(CuContext context) throws NoSuchTypeException {
		CuContext temp_context = new CuContext(context);
		temp_context.updateKc(super.data_kc);
		for (String key : super.data_tc.keySet()) {
			CuType value = super.data_tc.get(key);
			value.calculateType(temp_context);
		}
		super.data_t.calculateType(temp_context);
	}
}
