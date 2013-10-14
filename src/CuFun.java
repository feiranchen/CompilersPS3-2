import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//struct for functions
public abstract class CuFun {
	public String v;
	public CuTypeScheme ts;
	public CuStat funBody = null; //new Stats(new ArrayList<CuStat>());
	//public void add(CuVvc v, CuTypeScheme ts) {}
	//public void add(CuVvc v, CuTypeScheme ts, CuStat s) {}
	//public void add(CuStat s){}
	public abstract CuType calculateType(String v, CuTypeScheme ts, CuStat s);
}

class Function extends CuFun {


	public Function (String v_input, CuTypeScheme ts_input, CuStat s_input){
		super.v = v_input;
		super.ts = ts_input;
		super.funBody=s_input;
	}

	
	//Figure 7: Type checking Returns
	@Override public CuType calculateType(String v, CuTypeScheme ts, CuStat s){
		
		return null;
	}
	
}