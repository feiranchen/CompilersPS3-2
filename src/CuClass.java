import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public abstract class CuClass {
	protected String text = "";
	String name;
	CuType            superType = new Top();	
	List<String>       kindCtxt = null;
	Map<String,CuTypeScheme> mFunctions = new HashMap<String,CuTypeScheme>();
	
	HashMap<String, CuFun>  funList = new HashMap<String, CuFun>();

	public void add(List<CuExpr> s) {}
	public void addSuper(CuType t) {}
	public void add(CuStat s) {}
	public void add(String v, CuTypeScheme ts, CuStat s) {}
	public void add(String v_name, CuTypeScheme ts) {}
	public void add(CuVvc v_name, CuTypeScheme ts) {}
	public boolean isInterface() {return false; }
	public CuClass calculateType(CuContext context) throws NoSuchTypeException { return this;}

	@Override public String toString() {
		return text;
	}
}

class Cls extends CuClass {
	Map<String, CuType> fieldTypes=new LinkedHashMap<String,CuType>();
	
	List<CuType> appliedTypePara=new ArrayList<CuType>();
	List<CuStat> classStatement = new ArrayList<CuStat>();
	//private static final Exception NoSuchTypeExpression() = null;
	
	List<CuExpr>        superArg;

	public Cls(String clsintf, List<String> kc, LinkedHashMap<String, CuType> tc) {
		super.name=clsintf;
		super.kindCtxt=kc;
		this.fieldTypes=tc;
	}

	@Override public void add (CuStat s) {
		classStatement.add(s);}

	//TODO: grab all the methods here
	@Override public void addSuper (CuType tt) {
		super.superType = tt;}
	
	@Override public void add(String v, CuTypeScheme ts, CuStat s) {
		super.funList.put(v,new Function(v,ts,s));
		super.mFunctions.put(v, ts);
	}
	
	@Override public CuClass calculateType(CuContext context) throws NoSuchTypeException { 
		context.updateClass(name, this);
		if (superType instanceof VClass){
			Map<String, CuFun> superfunLst= context.mClasses.get(superType.id).funList;
			//also need to update the function context
			List<CuType> cur_types = new ArrayList<CuType>();
			for (String str : super.kindCtxt) {
				cur_types.add(new VTypePara(str));
			}
			CuTypeScheme temp_ts = new TypeScheme(super.kindCtxt, this.fieldTypes , new VClass(super.name, cur_types));
			context.updateFunction(super.name, temp_ts);
			for (CuStat s :classStatement) {s.calculateType(context);}
			for (Entry<String, CuType> e: fieldTypes.entrySet()) {e.getValue().calculateType(context);}
			
			for (Map.Entry<String, CuFun> e : superfunLst.entrySet()){
				//check signature if already exists
				if (funList.containsKey(e.getKey())){
					//check signature
					e.getValue().ts.calculateType(context);
					if (!e.getValue().ts.equals(context.mFunctions.containsKey(e.getKey()))){
						throw new NoSuchTypeException();}
				}else{//add method if it doesn't already exist
					context.updateFunction(e.getKey(),e.getValue().ts);
					funList.put(e.getKey(), e.getValue());
				}
			}
			for (CuStat s :classStatement) {
				if (!s.calculateType(context).b
						||s.calculateType(context).tau.isSubtypeOf(s.calculateType(context).tau)) 
					throw new NoSuchTypeException();}
			
			
		}
		else if(superType instanceof VTypeInter) {
			for (CuType t:superType.parentType){
				Map<String, CuFun> superfunLst= context.mClasses.get(t.id).funList;
				for (Map.Entry<String, CuFun> e : superfunLst.entrySet()){
					//check signature if already exists
					if (context.mFunctions.containsKey(e.getKey())){
						//check signature
						e.getValue().ts.calculateType(context);
						if (!e.getValue().ts.equals(context.mFunctions.containsKey(e.getKey()))){
							throw new NoSuchTypeException();}
					}else{//add method if it doesn't already exist
						context.updateFunction(e.getKey(),e.getValue().ts);
						funList.put(e.getKey(), e.getValue());
					}
				}
			}
		}
		return this;
	}
	

	/*
	@Override public String toString() {
		return String.format("class %s %s %s extends %s { %s super ( %s ) ; %s }", 
				clsintf, Helper.printList("<", kc, ">", ","), Helper.printMap("(", tc, ")", ","), superType.toString(), 
				Helper.printList("", classStatement, "", ""), Helper.printList("(", es, ")", ","), Helper.printList("", fun, "", ""));
	}
	*/
	
}

class Intf extends CuClass{
	Map<String, CuType> fieldTypes=new LinkedHashMap<String,CuType>();
	
	List<CuType> appliedTypePara=new ArrayList<CuType>();
	List<CuStat> classStatement = new ArrayList<CuStat>();
	
	public Intf (String iname, List<String> kname){
		
		//for printing
		text = "interface " + name.toString() + " <";
		for (String s : kindCtxt) {
			text += " " + s.toString();
		}
		text += " > extends";
	}
	@Override
	public void addSuper (CuType tt) throws NoSuchTypeException{
		superType = tt;
	}
	@Override public void add(String v, CuTypeScheme ts, CuStat s) {
		funList.put(v,new Function(v,ts,s));
		super.mFunctions.put(v, ts);
	}
	
	@Override public CuClass calculateType(CuContext cTxt) throws NoSuchTypeException {
		cTxt.updateClass(name,this);
		if (superType instanceof VClass){
			Map<String, CuFun> superfunLst= cTxt.mClasses.get(superType.calculateType(cTxt).id).funList;
			for (Map.Entry<String, CuFun> e : superfunLst.entrySet()){
				//check signature if already exists
				if (cTxt.mFunctions.containsKey(e.getKey())){
					//check signature
					e.getValue().ts.calculateType(cTxt);
					if (!e.getValue().ts.equals(cTxt.mFunctions.containsKey(e.getKey()))){
						throw new NoSuchTypeException();}
				}else{//add method if it doesn't already exist
					funList.put(e.getKey(), e.getValue());
				}
			}
		}
		else if(superType instanceof VTypeInter) {
			for (CuType t:superType.parentType){
				Map<String, CuFun> superfunLst= cTxt.mClasses.get(t.calculateType(cTxt).id).funList;
				for (Map.Entry<String, CuFun> e : superfunLst.entrySet()){
					//check signature if already exists
					if (cTxt.mFunctions.containsKey(e.getKey())){
						//check signature
						e.getValue().ts.calculateType(cTxt);
						if (!e.getValue().ts.equals(cTxt.mFunctions.containsKey(e.getKey()))){
							throw new NoSuchTypeException();}
					}else{//add method if it doesn't already exist
						funList.put(e.getKey(), e.getValue());
					}
				}
			}
		}
		else if(superType instanceof VTypePara) {
			if (!cTxt.mKind.contains(superType.calculateType(cTxt).id)){
				throw new NoSuchTypeException();}
		}
		else if(superType instanceof Iter) {
			superType.calculateType(cTxt);
		}	
		else { throw new NoSuchTypeException();}
		return this;
	}

	@Override public boolean isInterface() {return true; }
}


//======Class init=========

class VBoolean extends Cls {
	Boolean v=false;
	public VBoolean() {
		super("Boolean", new ArrayList<String>(), new LinkedHashMap<String, CuType>());
		//if (val instanceof Boolean) { v=val; }
		//else { throw new NoSuchTypeException();}
		//Just one example, we also need to add other methods
		CuTypeScheme ts;
		ts = new TypeScheme(new ArrayList<String>(), new LinkedHashMap<String, CuType>(), CuType.bool);
		super.mFunctions.put("negate", ts);
		HashMap<String, CuType> arg = new LinkedHashMap<String, CuType>();
		arg.put("that", CuType.bool);
		ts = new TypeScheme(new ArrayList<String>(), arg, CuType.bool);
		super.mFunctions.put("and", ts);
		super.mFunctions.put("or", ts);
		super.mFunctions.put("equals", ts);
		
		arg = new LinkedHashMap<String, CuType>();
		arg.put("upper", CuType.bool);
		arg.put("includeLower", CuType.bool);
		arg.put("includeUpper", CuType.bool);
		ts = new TypeScheme(new ArrayList<String>(), arg, new Iter(CuType.bool));
		super.mFunctions.put("through", ts);
		
		arg = new LinkedHashMap<String, CuType>();
		arg.put("inclusive", CuType.bool);
		ts = new TypeScheme(new ArrayList<String>(), arg, new Iter(CuType.bool));
		super.mFunctions.put("onwards", ts);
		
		arg = new LinkedHashMap<String, CuType>();
		arg.put("that", CuType.bool);
		arg.put("strict", CuType.bool);
		ts = new TypeScheme(new ArrayList<String>(), arg, CuType.bool);
		super.mFunctions.put("lessThan", ts);
		
	}
    
	public boolean calculateType() { return v; }
}

class VInteger extends Cls {
	Integer v=0;
	public VInteger() {
		super("Integer", new ArrayList<String>(), new LinkedHashMap<String, CuType>());
		//if (val instanceof Integer) { v=val; }
		//else { throw new NoSuchTypeException();}
		CuTypeScheme ts;
		ts = new TypeScheme(new ArrayList<String>(), new LinkedHashMap<String, CuType>(), CuType.integer);
		super.mFunctions.put("negative", ts);
		
		HashMap<String, CuType> arg = new LinkedHashMap<String, CuType>();
		arg.put("operator", CuType.integer);
		ts = new TypeScheme(new ArrayList<String>(), arg, CuType.integer);
		super.mFunctions.put("times", ts);
		super.mFunctions.put("plus", ts);
		super.mFunctions.put("minus", ts);
		ts = new TypeScheme(new ArrayList<String>(), arg, CuType.bool);
		super.mFunctions.put("equals", ts);
		ts = new TypeScheme(new ArrayList<String>(), arg, new Iter(CuType.integer));
		super.mFunctions.put("divide", ts);
		super.mFunctions.put("modulo", ts);		
		
		arg = new LinkedHashMap<String, CuType>();
		arg.put("upper", CuType.integer);
		arg.put("includeLower", CuType.bool);
		arg.put("includeUpper", CuType.bool);
		ts = new TypeScheme(new ArrayList<String>(), arg, new Iter(CuType.integer));
		super.mFunctions.put("through", ts);
		
		arg = new LinkedHashMap<String, CuType>();
		arg.put("inclusive", CuType.bool);
		ts = new TypeScheme(new ArrayList<String>(), arg, new Iter(CuType.integer));
		super.mFunctions.put("onwards", ts);
		
		arg = new LinkedHashMap<String, CuType>();
		arg.put("that", CuType.integer);
		arg.put("strict", CuType.bool);
		ts = new TypeScheme(new ArrayList<String>(), arg, CuType.bool);
		super.mFunctions.put("lessThan", ts);
	}
	public VInteger calculateType() { return this; }
}

class VCharacter extends Cls {
	Character c;
	public VCharacter() {
		super("Character", new ArrayList<String>(), new LinkedHashMap<String, CuType>());
		//if (val instanceof Character) { c=val; }
		//else { throw new NoSuchTypeException();}
		CuTypeScheme ts;
		ts = new TypeScheme(new ArrayList<String>(), new LinkedHashMap<String, CuType>(), CuType.integer);
		super.mFunctions.put("unicode", ts);
		
		HashMap<String, CuType> arg = new LinkedHashMap<String, CuType>();
		arg.put("that", CuType.character);
		ts = new TypeScheme(new ArrayList<String>(), arg, CuType.bool);
		super.mFunctions.put("equals", ts);
	}
	public VCharacter calculateType() { return this; }
}

class VString extends Cls {
	String v="";
	public VString() {
		super("String", new ArrayList<String>(), new LinkedHashMap<String, CuType>());
		this.addSuper(new Iter(CuType.character));
		//if (val instanceof String) { v=val; }
		//else { throw new NoSuchTypeException();}
		HashMap<String, CuType> arg = new LinkedHashMap<String, CuType>();
		arg.put("that", CuType.string);
		CuTypeScheme ts = new TypeScheme(new ArrayList<String>(), arg, CuType.bool);
		super.mFunctions.put("equals", ts);
	}

	public VString calculateType() { return this; }
}


class VIterable extends Cls {
	List<CuType> v;
	public VIterable(List<String> kc) {
		super("Iterable", kc, new LinkedHashMap<String, CuType>());
		//if (val instanceof List<CuType>) { v=val; }
		//else { throw new NoSuchTypeException();}
	}
	public VIterable calculateType() { return this; }
}

