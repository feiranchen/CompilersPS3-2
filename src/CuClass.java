import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

//Yinglei worked on the desugared program (core language), in parsing, we should
//put any implementation in interface to class, and this is done in parsing, probably haven't done it yet
public abstract class CuClass {
	protected String text = "";
	String name;
	CuType            superType = new Top();
	List<CuExpr>        superArg;
	List<CuStat> classStatement = new ArrayList<CuStat>();
	HashMap<String, CuFun>  funList = new HashMap<String, CuFun>();
	List<String>       kindPara = null;
	Map<String, CuType> fieldTypes=new LinkedHashMap<String,CuType>();
	
	List<CuType> appliedTypePara=new ArrayList<CuType>();
	public void add(List<CuExpr> s) {}
	public void addSuper(CuType t) {}
	public void add(CuStat s) {}
	public void addFun(String v, CuTypeScheme ts, CuStat s) {}
	public void add(String v_name, CuTypeScheme ts) {}
	public void add(CuVvc v_name, CuTypeScheme ts) {}
	public boolean isInterface() {return false; }
	public CuClass calculateType(CuContext context) throws NoSuchTypeException { return this;}

	@Override public String toString() {
		return text;
	}
}

class Cls extends CuClass {
	public Cls(String clsintf, List<String> kc, LinkedHashMap<String, CuType> tc) {
		name=clsintf;
		kindPara=kc;
		fieldTypes=tc;
	}

	@Override public void add (CuStat s) {
		classStatement.add(s);}

	//TODO: grab all the methods here
	@Override public void addSuper (CuType tt) {
		superType = tt;}
	
	@Override public void addFun(String v, CuTypeScheme ts, CuStat s) {
		funList.put(v,new Function(v,ts,s));
	}
	
	@Override public CuClass calculateType(CuContext context) throws NoSuchTypeException { 
		context.updateClass(name, this);
		if (superType instanceof VClass){
			Map<String, CuFun> superfunLst= context.mClasses.get(superType.id).funList;
			context.updateType(name, new VClass(name, new ArrayList<CuType>()));
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
	
	public Intf (String iname, List<String> kname){
		
		//for printing
		text = "interface " + name.toString() + " <";
		for (String s : kindPara) {
			text += " " + s.toString();
		}
		text += " > extends";
	}
	@Override
	public void addSuper (CuType tt) throws NoSuchTypeException{
		superType = tt;
	}
	@Override public void addFun(String v, CuTypeScheme ts, CuStat s) {
		funList.put(v,new Function(v,ts,s));
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
		funList = new HashMap<String, CuFun>();
		funList.put("negate", new Function("negate",
				new TypeScheme(new ArrayList<String>(), new LinkedHashMap<String,CuType>(), CuType.bool)));
		funList.put("and", new Function("and",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>(){{put("that",CuType.bool);}}, 
						CuType.bool)));
		funList.put("or", new Function("or",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>(){{put("that",CuType.bool);}}, 
						CuType.bool)));
		funList.put("through", new Function("through",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>()
						{{put("upper",CuType.bool);
						  put("includeLower",CuType.bool);
						  put("includeUpper",CuType.bool);}}, 
						CuType.iterable(CuType.bool))));
		funList.put("onwards", new Function("onwards",
				new TypeScheme(new ArrayList<String>(),
						new LinkedHashMap<String,CuType>(){{put("inclusive",CuType.bool);}}, 
						CuType.iterable(CuType.bool))));
		funList.put("lessThan", new Function("lessThan",
				new TypeScheme(new ArrayList<String>(),
						new LinkedHashMap<String,CuType>(){{
							put("that",CuType.bool);
							put("strict",CuType.bool);}}, 
						CuType.bool)));
		funList.put("equals", new Function("equals",
				new TypeScheme(new ArrayList<String>(),
						new LinkedHashMap<String,CuType>(){{put("that",CuType.bool);}}, 
						CuType.bool)));
		
		//if (val instanceof Boolean) { v=val; }
		//else { throw new NoSuchTypeException();}
	}
	public VBoolean calculateType(CuContext ctxt) { 
		ctxt.updateClass(name, this);
		return this; }
}

class VInteger extends Cls {
	Integer v=0;
	public VInteger() {
		super("Integer", new ArrayList<String>(), new LinkedHashMap<String, CuType>());
		funList = new HashMap<String, CuFun>();

		public Function (String v_input, CuTypeScheme ts_input){
		funList.put("negative", new Function("negative",
				new TypeScheme(new ArrayList<String>(),
						new LinkedHashMap<String,CuType>(),
						CuType.integer)));
		funList.put("times", new Function("times",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>(){{put("factor",CuType.integer);}}, 
						CuType.iterable(CuType.integer))));
		funList.put("divide", new Function("divide",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>(){{put("divisor",CuType.integer);}},  
						CuType.iterable(CuType.integer))));
		funList.put("modulo", new Function("modulo",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>(){{put("modulus",CuType.integer);}},  
						CuType.iterable(CuType.integer))));
		funList.put("plus", new Function("plus",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>(){{put("summand",CuType.integer);}}, 
						CuType.integer)));
		funList.put("minus", new Function("minus",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>(){{put("subtrahend",CuType.integer);}}, 
						CuType.integer)));
		funList.put("through", new Function("through",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>()
						{{put("upper",CuType.integer);
						  put("includeLower",CuType.bool);
						  put("includeUpper",CuType.bool);}},  
						CuType.iterable(CuType.integer))));
		funList.put("onwards", new Function("onwards",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>(){{put("inclusive",CuType.bool);}}, 
						CuType.iterable(CuType.integer))));
		funList.put("lessThan", new Function("lessThan",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>()
						{{put("that",CuType.integer);
						  put("strict",CuType.bool);}}, 
						CuType.bool)));
		funList.put("equals", new Function("equals",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>(){{put("that",CuType.integer);}}, 
						CuType.bool)));
		//if (val instanceof Integer) { v=val; }
		//else { throw new NoSuchTypeException();}
	}
	public VInteger calculateType(CuContext ctxt) { 
		ctxt.updateClass(name, this);
		return this; }
}

class VCharacter extends Cls {
	Character c;
	public VCharacter() {
		super("Character", new ArrayList<String>(), new LinkedHashMap<String, CuType>());
		funList.put("unicode", new Function("unicode",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>(), 
						CuType.integer)));
		funList.put("equals", new Function("equals",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>(){{put("that",CuType.character);}}, 
						CuType.bool)));
		//if (val instanceof Character) { c=val; }
		//else { throw new NoSuchTypeException();}
	}
	public VCharacter calculateType(CuContext ctxt) { return this; }
}

class VString extends Cls {
	String v="";
	public VString() {
		super("String", new ArrayList<String>(), new LinkedHashMap<String, CuType>());
		funList = new HashMap<String, CuFun>();
		funList.put("plus", new Function("plus",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>(){{put("summand",CuType.integer);}}, 
						CuType.integer)));
		funList.put("minus", new Function("minus",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>(){{put("subtrahend",CuType.integer);}}, 
						CuType.integer)));
		//if (val instanceof String) { v=val; }
		//else { throw new NoSuchTypeException();}
	}

	public VString calculateType(CuContext ctxt) { return this; }
}


class VIterable extends Cls {
	List<CuType> v;
	public VIterable(List<String> kc) {
		super("Iterable", kc, new LinkedHashMap<String, CuType>());
		funList = new HashMap<String, CuFun>();
		funList.put("equals", new Function("equals",
				new TypeScheme(new ArrayList<String>(), 
						new LinkedHashMap<String,CuType>(){{put("that",CuType.string);}}, 
						CuType.bool)));
		//if (val instanceof List<CuType>) { v=val; }
		//else { throw new NoSuchTypeException();}
	}
	
	public VIterable calculateType(CuContext ctxt) { return this; }
}

