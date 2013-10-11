import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class ParserErrorListener extends BaseErrorListener
{ 
	Boolean debug = false;
	ParserErrorListener (boolean d) {
		super();
		debug = d;
	}
	
	@Override public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, 
			int line, int charPositionInLine, String msg, RecognitionException e)
	{
		if(debug) {
			Helper.P("syntaxError:");
			List<String> stack = ((Parser)recognizer).getRuleInvocationStack(); 
			Collections.reverse(stack);
			System.err.println("rule stack: "+stack);
			System.err.println("line "+line+":"+charPositionInLine+" at "+ offendingSymbol+": "+msg);
		}
		throw new NoSuchTypeException();
	}
/*
	@Override
	public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex,
			int stopIndex, boolean exact, java.util.BitSet ambigAlts,
			ATNConfigSet configs) {
		if(debug) {
			CuMethod.P("reportAmbiguity");
			super.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs);
		}
		throwException();		
	}

	@Override
	public void reportAttemptingFullContext(Parser recognizer, DFA dfa,
			int startIndex, int stopIndex, java.util.BitSet conflictingAlts,
			ATNConfigSet configs) {
		if(debug) {
			CuMethod.P("reportAttemptingFullContext");
			super.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex,
					conflictingAlts, configs);
		}
		throwException();
	}

	@Override
	public void reportContextSensitivity(Parser recognizer, DFA dfa,
			int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
		if(debug) {
			CuMethod.P("reportContextSensitivity");
			super.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex,
					prediction, configs);
		}
		throwException();
	}
*/
}