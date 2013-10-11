import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class Helper {
	protected static <T> String printList(String l, List<T> es, String r, String i) {
		if (es == null) return " "+l + " " + r+" ";
		String s = " "+l + " ";
		for (T e : es) {
			s += e.toString() + " "+i + " ";
		}
		int j = s.lastIndexOf(i+" ");
		if (j > 1) s = s.substring(0, j);
		s +=( r+" " );
		return s;
	}
	
	protected static <T, K> String printMap(String l, Map<T,K> es, String r, String i) {
		if (es == null) return " "+l + " " + r+" ";
		String s = " "+l + " ";
		for (Entry<T, K> e : es.entrySet()) {
			s += e.getKey().toString()+" : "+e.getValue().toString() + " "+i + " ";
		}
		int j = s.lastIndexOf(i+" ");
		if (j > 1) s = s.substring(0, j);
		s +=( r+" " );
		return s;
	}
	
	protected static String listFlatten(List<CuStat> cs) {
		if(cs == null) return " ";
		String s=" ";
		if (cs == null || cs.size() == 0) return s;
		for (CuStat t : cs) {
			if (t instanceof Stats) s+=listFlatten((ArrayList<CuStat>) ((Stats)t).al);
			else s+= t.toString() + " ";
		}
		return s;
	}
	
	protected static void P(String s) {
		System.out.println(s);
	}
	/* thoughts coming, to implement later on */
	protected static void ToDo(String comment){
	}
}
