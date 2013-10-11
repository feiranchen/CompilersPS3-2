
public abstract class CuVvt {
	protected String text = "";
	@Override public String toString() {
		return text;
	}
}

class Vvt extends CuVvt{
	String data_s;
	CuType data_t;
	public Vvt(String s,CuType t){
		data_s=s;
		data_t=t;
		text=s+" : "+t.toString();
	}
}