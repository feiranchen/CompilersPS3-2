all:
	export PATH="/Users/apple/Documents/eclipseWorkspace/CompilersPS3/src":"${PATH}"
	export CLASSPATH=".:/usr/local/lib/antlr-4.1-complete.jar:${CLASSPATH}"
	alias antlr4="java -jar /usr/local/lib/antlr-4.1-complete.jar"
	alias grun="java org.antlr.v4.runtime.misc.TestRig"
	[[ -s ~/.bashrc ]] && source ~/.bashrc
	java -jar /usr/local/lib/antlr-4.1-complete.jar *.g4 -no-listener
	javac *.java
	jar cvfm TypeCheck.jar MANIFEST.MF *.class *.java *.g4

test:
	bash TestCases TypeCheck.jar

clean:
	rm *.class
	rm *.jar
	rm *.tokens

old_test:
	java -jar TypeCheck.jar tests/tc_test1.in
	java -jar TypeCheck.jar tests/tc_test2.in
