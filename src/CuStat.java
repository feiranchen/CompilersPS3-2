import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class CuStat {
	protected String text = "";
	@Override public String toString() {
		return text;
	}
	public void add (CuStat st){}
	public HReturn calculateType(CuContext context) throws NoSuchTypeException {
		
		HReturn re = new HReturn();
		return re;
	}
}

class AssignStat extends CuStat{
	private CuVvc var;
	private CuExpr ee;
	public AssignStat (CuVvc t, CuExpr e) {
		var = t;
		ee = e;
		super.text = var.toString() + " := " + ee.toString() + " ;";
	}
	
	public HReturn calculateType(CuContext context) throws NoSuchTypeException {
		System.out.println("In assig start");
		System.out.println("var="+var.toString() + " expr="+ ee.toString());
		//check var is in immutable, type check fails
		if (context.inVar(var.toString())) {
			throw new NoSuchTypeException();
		}
		//whenever we calculate expr type, we use a temporary context with merged mutable and
		//immutable variables
		CuContext tcontext = new CuContext (context);
		tcontext.mergeVariable();
		System.out.println("In assig stat, before expr check");
		CuType exprType = ee.calculateType(tcontext);
		System.out.println("In assig stat, after expr check");
		context.updateMutType(var.toString(), exprType);
		HReturn re = new HReturn();
		re.b = false;
		re.tau = CuType.bottom;
		System.out.println("In assignment statement end");
		return re;
	}
}

class ForStat extends CuStat{
	private CuVvc var;
	private CuExpr e;
	private CuStat s1;
	public ForStat(CuVvc v, CuExpr ee, CuStat ss) {
		var = v;
		e = ee;
		s1 = ss;
		super.text = "for ( " + var + " in " + e.toString() + " ) " + s1.toString();
	}
	public HReturn calculateType(CuContext context) throws NoSuchTypeException {
		//whenever we calculate expr type, we use a temporary context with merged mutable and
		//immutable variables
		System.out.println("in for stat, begin");
		CuContext tcontext = new CuContext (context);
		tcontext.mergeVariable();		
    	//check whether e is an iterable of tao
    	CuType eType = e.calculateType(tcontext);
 		Boolean flag = eType.isIterable();
    	if (flag != true) {
    		throw new NoSuchTypeException();
    	}
    	//var can't appear in mutable or immutable variables
    	if (context.inMutVar(this.var.toString()) || context.inVar(this.var.toString())) {
    		throw new NoSuchTypeException();
    	}
    	CuType iter_type = eType.getArgument();
    	System.out.println("variable type is " + iter_type.id);
    	CuContext s_context = new CuContext(context);
    	s_context.updateMutType(this.var.toString(), iter_type);
    	HReturn re = s1.calculateType(s_context);
    	System.out.println("return type is " + re.tau.id);
    	
    	//type weakening to make it type check
    	context.weakenMutType(s_context);
		
		re.b = false;
		System.out.println("in for stat, end");
		return re;
	}
}

class IfStat extends CuStat{
	private CuExpr e;
	private CuStat s1=null;
	private CuStat s2=null;
	public IfStat (CuExpr ex, CuStat st) {
		this.e = ex;
		this.s1 = st;
		super.text = "if ( " + e.toString() + " ) " + s1.toString();
	}

    @Override public void add (CuStat st) {
    	s2 = st;
    	super.text += " else " + s2.toString();
    }
    
	public HReturn calculateType(CuContext context) throws NoSuchTypeException {
		//whenever we calculate expr type, we use a temporary context with merged mutable and
		//immutable variables
		CuContext tcontext = new CuContext (context);
		tcontext.mergeVariable();		
    	//check whether e is boolean
    	CuType eType = e.calculateType(tcontext);
    	if (!eType.isBoolean()) {
    		throw new NoSuchTypeException();
    	}
    	CuContext temp_context1 = new CuContext (context);
    	
		HReturn re1 = s1.calculateType(temp_context1);
		//if we don't have s2, temp_context2 will simply be context
		CuContext temp_context2 = new CuContext (context);
		//default is false and bottom
		HReturn re2 = new HReturn();
		if (s2 != null) {
			re2 = s2.calculateType(temp_context2);		
		}
		temp_context1.weakenMutType(temp_context2);
		//we are passing reference, this is suppose to change
		context = temp_context1;
		
		HReturn re_out = new HReturn();
		if (re1.b==false || re2.b==false) {
			re_out.b = false;
		}
		else {
			re_out.b = true;
		}
		re_out.tau = CuType.commonParent(re1.tau, re2.tau);
		return re_out;
	}

}

class ReturnStat extends CuStat{
	private CuExpr e;
	public ReturnStat (CuExpr ee) {
		e = ee;
		super.text = "return " + e.toString() + " ;";
	}
	public HReturn calculateType(CuContext context) throws NoSuchTypeException {
		System.out.println("in return stat, begin");
		HReturn re = new HReturn();
		re.b = true;
		//whenever we calculate expr type, we use a temporary context with merged mutable and
		//immutable variables
		CuContext tcontext = new CuContext (context);
		tcontext.mergeVariable();	
		re.tau = e.calculateType(tcontext);
		System.out.println("in return stat, end");
		return re;
	}
}

class Stats extends CuStat{
	public ArrayList<CuStat> al = new ArrayList<CuStat>();
	public Stats (List<CuStat> cu) {
		al = (ArrayList<CuStat>) cu;
		text = "{ " + Helper.listFlatten(al) + " }";
	}
	public HReturn calculateType(CuContext context) throws NoSuchTypeException {
		System.out.println("in stats statement, begin");
		System.out.println("number of elements: "+al.size());
		//default is false, bottom
		HReturn re = new HReturn();
		for (CuStat cs : al) {
			System.out.println(cs.toString());
			HReturn temp = cs.calculateType(context);
			if (temp.b==true) {
				re.b = true;
			}
			System.out.println("finished " + cs.toString() + "before common parrent");
			System.out.println("re tau is " + re.tau.id + ", temp tau is " + temp.tau.id);
			re.tau = CuType.commonParent(re.tau, temp.tau);
			System.out.println("finished " + cs.toString() + "after common parrent, common type is " + re.tau.id);
		}
		System.out.println("in stats statement, end");
		return re;
	}
}

class WhileStat extends CuStat{
	private CuExpr e;
	private CuStat s1;
	public WhileStat (CuExpr ex, CuStat st){
		e = ex;
		s1 = st;
		text = "while ( " + e.toString() + " ) " + s1.toString();
	}
    public HReturn calculateType(CuContext context) throws NoSuchTypeException {
		//whenever we calculate expr type, we use a temporary context with merged mutable and
		//immutable variables
		CuContext tcontext = new CuContext (context);
		tcontext.mergeVariable();		
    	//check whether e is boolean
		System.out.println("in while, before checking expr");
		System.out.println(this.e.toString());
    	CuType eType = e.calculateType(tcontext);
    	System.out.println("in while, after checking expr");
    	if (!eType.isBoolean()) {
    		System.out.println("in while, expr is not boolean");
    		System.out.println("in while, expr type is " + eType.id);
    		throw new NoSuchTypeException();
    	} 
    	CuContext s_context = new CuContext(context);
    	HReturn re = s1.calculateType(s_context);   	
    	//type weakening to make it type check
    	context.weakenMutType(s_context);
    	re.b = false;
    	return re;
    }
}

class EmptyBody extends CuStat {
	public EmptyBody(){
		text=" ;";
	}
	public HReturn calculateType(CuContext context) throws NoSuchTypeException {
		//default is false and bottom
		return new HReturn();
	}
} 
