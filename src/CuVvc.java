
public abstract class CuVvc {
	final static protected String TOP = "Thing";
	final static protected String BOTTOM = "Nothing";
	final static protected String ITERABLE = "Iterable";
	final static protected String BOOLEAN = "Boolean";
	final static protected String INTEGER = "Integer";
	final static protected String CHARACTER = "Character";
	final static protected String STRING = "String";
	protected String text = "";
	@Override public String toString() {
		return text;
	}
	public boolean isIterable() { return false;}
	public boolean equals(CuVvc that) { return text.equals(that.text); }
}

class Vc extends CuVvc{
	public Vc (String s) {
		text = s;
	}
	@Override public boolean isIterable() {
		if(text.equals(ITERABLE)) return true;
		return false;
	}
}

class Vv extends CuVvc{
	public Vv (String s){
		text=s;
	}
}

class Vvc extends CuVvc{
	public Vvc(String s){
		text=s;
	}
}