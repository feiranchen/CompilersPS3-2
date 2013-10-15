import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.*;

public class Cubex {
	public static void main (String args[]) throws IOException {
		getParser(args[0]);
	}
	
	protected static void getParser(String fn) throws IOException {
		CubexLexer2 lexer = new CubexLexer2(new ANTLRFileStream(fn));
		
		//bound inputs to a variable input, put in context
		CuContext context=new CuContext();
		context.init();
		
		
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CubexParser2 parser = new CubexParser2(tokens);
		// altering anltr error messages
		parser.removeErrorListeners();
		parser.addErrorListener(new ParserErrorListener(false)); //prevent printing debugging messages
		
		String coreLang = null;
		try {
			parser.program().p.calculateType(context);
		} catch (Exception e) {
			System.out.print("reject");
			System.exit(-2);
		}
		System.out.print("accept");
	}
}
