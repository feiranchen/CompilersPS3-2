import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CuContext {
	Map<String,CuClass> mClasses;
	//kind context theta
	List<String> mKind;
	//function context is a map with name and type scheme pairs
	Map<String,CuTypeScheme> mFunctions;
	//mVariable are the immutable variables
	Map<String,CuType> mVariables;
	//mMutVariables are the mutable variables
	Map<String,CuType> mMutVariables;
	final static CuContext Empty=new CuContext();
	
	public CuContext () {
		this.mClasses = new HashMap<String,CuClass>();
		this.mKind = new ArrayList<String>();
		this.mFunctions = new HashMap<String,CuTypeScheme>();
		//mVariable are the immutable variables
		this.mVariables = new HashMap<String,CuType>();
		//mMutVariables are the mutable variables
		this.mMutVariables = new HashMap<String,CuType>();
	}
	//copy constructor
	public CuContext (CuContext context) {
		this.mClasses = new HashMap<String,CuClass>(context.mClasses);
		this.mKind = new ArrayList<String>(context.mKind);
		this.mFunctions = new HashMap<String,CuTypeScheme>(context.mFunctions);
		//mVariable are the immutable variables
		this.mVariables = new HashMap<String,CuType>(context.mVariables);
		//mMutVariables are the mutable variables
		this.mMutVariables = new HashMap<String,CuType>(context.mMutVariables);
	}


	public void updateKind(String name){ mKind.add(name); }
	public CuType getVariable(String name) { 
		CuType temp = mVariables.get(name);
		if (temp.isClassOrInterface())
		{
			if (!temp.parents.contains(mClasses.get(temp.id).superType))
				temp.parentType.add(mClasses.get(temp.id).superType);
		}
		return temp;
	}
	public CuType getMutVariable(String name) { return mMutVariables.get(name); }
	public CuTypeScheme getFunction(String name) { return mFunctions.get(name); }
	public void updateType(String name, CuType value){ mVariables.put(name, value);}
	public void updateMutType(String name, CuType value){ mMutVariables.put(name, value);}
	public void updateFunction(String name, CuTypeScheme value){ mFunctions.put(name, value);}
	public void updateClass(String name, CuClass value){ this.mClasses.put(name, value);}
	public boolean inVar (String name) {return this.mVariables.containsKey(name); }
	public boolean inMutVar (String name) {return this.mMutVariables.containsKey(name); }

	public List<String> getKindList() { return mKind; }
	
	
	//this function merges the mutable variables to immutable variables
	//this function is created because a lot of times we don't need to distinguish immutable variables
	//and mutable variables, so we just use the immutable variables
    public void mergeVariable () {
    	this.mVariables.putAll(this.mMutVariables);
    	this.mMutVariables = new HashMap<String,CuType>();
    }
    //weakens the mutable type contexts to make it consistent with context
    public void weakenMutType (CuContext context) {
    	Map<String,CuType> new_mMutVariables = new HashMap<String,CuType>();
    	for (String key : this.mMutVariables.keySet()) {
    		//only keeps the mutable variables which exist in both
    		if (context.inMutVar(key)) {
	    		CuType t1 = this.getMutVariable(key);
	    		CuType t2 = context.getMutVariable(key);
	    		System.out.println("t1 is " + t1.id + " t2 is " + t2.id);
	    		System.out.println("t1 type is is " + t1.type.id + " t2 type is " + t2.type.id);
	    		CuType tCom = CuType.commonParent(t1, t2);
	    		System.out.println("tCom is " + tCom);
	    		new_mMutVariables.put(key, tCom);
    		}
    	}
    	this.mMutVariables = new_mMutVariables;
    }
    //check if this type parameter is in kind context
    public boolean hasVTypePara(String name) {
    	return this.mKind.contains(name);
    }
    public void updateKc(List<String> kc) {
    	this.mKind.addAll(kc);
    }
    public void updateMutType(Map<String,CuType> mutVar){ mMutVariables.putAll(mutVar);}
    
    
    @SuppressWarnings("serial")
	public void init(){
    	//=====Class init==========
    	List<String> tempforIterInit=new ArrayList<String>();
    	tempforIterInit.add("E");
		this.mClasses.put("Iterable", new VIterable(tempforIterInit));
		this.mClasses.put("Boolean", new VBoolean());
		this.mClasses.put("Integer", new VInteger());
		this.mClasses.put("Character", new VCharacter());
		this.mClasses.put("String", new VString());
		
		//=====Function init=======
		//character
		
		//tempclassmapForChar.put(new Vv("unicode"),new VClass("Character",new ArrayList<CuType>());
		//CuFun chararcterFun= new Function(new Vv("character"),
		//public TypeScheme(List<String> kc, Map<String, CuType> tc , CuType t){
		//VClass(String s, List<CuType> pt, Boolean intf)
		HashMap<String, CuType> arg = new LinkedHashMap<String, CuType>();
		arg.put("unicode",CuType.integer);
		TypeScheme chararcterFunTemp = new TypeScheme(new ArrayList<String>(), arg, CuType.character); 
		mFunctions.put("character", chararcterFunTemp);
		//string
		arg = new LinkedHashMap<String, CuType>();
		arg.put("characters",new Iter(CuType.character));
		TypeScheme stringFunTemp = new TypeScheme(new ArrayList<String>(), arg, CuType.string); 
		mFunctions.put("string", stringFunTemp);
		
		//=====Type init===========
		mVariables.put("input",CuType.iterable(new ArrayList<CuType>(){{add(CuType.string);}}));
	}
}

    /*
class ClsContextEle {
	String keyword;
	//class/interface names
	String name;
	//kind contexts
	List<String> kc;
	CuType superType;
	//function context
	Map<String,CuTypeScheme> mFunctions;
	
	public ClsContextEle () {
		this.kc = new ArrayList<String>();
		this.superType = CuType.top;
		this.mFunctions = new HashMap<String, CuTypeScheme>();
	}
}
	*/
