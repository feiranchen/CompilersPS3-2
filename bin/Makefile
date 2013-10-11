all:
	[[ -s ~/.bashrc ]] && source ~/.bashrc
	java -jar /usr/local/lib/antlr-4.1-complete.jar *.g4 -no-listener
	javac *.java

test:
	java -jar Lexer.jar simple_test1.in > tokens1.out


