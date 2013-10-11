lexer grammar CubexLexer2;

STRING : ('"'(~[\t\r\n])*'"');
IF : 'if';
ELSE : 'else';
WHILE : 'while';
FOR : 'for';
IN : 'in';
RETURN : 'return';
INTERFACE : 'interface';
EXTENDS : 'extends';
FUN : 'fun';
CLASS : 'class';
SUPER : 'super';
THING : 'Thing';
NOTHING : 'Nothing';
TRUE : 'true';
FALSE : 'false';
CLSINTF : [A-Z][a-zA-Z0-9_]+;
TPARA : [A-Z];
VAR : [a-z][a-zA-Z0-9_]*;
INTEGER : [0-9]+;

LBRACKET : '[';
RBRACKET : ']';
COLON : ':';
EQUAL : '=';
LPAREN : '(';
RPAREN : ')';
COMMA : ',';
SEMICOLON : ';';
LBRACE : '{';
RBRACE : '}';
STAR : '*';
SLASH : '/';
PERCENT : '%';
PLUS : '+';
DASH : '-';
LANGLE : '<';
RANGLE : '>';
BANG : '!';
AMPERSAND : '&';
PIPE : '|';
DOT : '.';
APPEND : '++';
THR : '..';
LTHR : '<.';
RTHR : '.<';
LRTHR : '<<';
ONW : '...';
LONW : '<..';
LTE : '<=';
GTE : '>=';
EQEQUAL : '==';
INEQUAL : '!=';
ASSIGN : ':='; 

COMMENTS : '`'(COMMENTS | .)*? '\'' -> skip;
COMMENTS_POND : '#'~[\r\n]*?[\r\n] -> skip;
SPACE : [ \t\r\n]+ -> skip;

ERROR : .+? {System.out.println("lexer error"); System.exit(-1);};





