/**
 * {@code PLLexer} is the lexer of the PL.
 * No parser rules should ever appear in this file.
 */
lexer grammar PLLexerPart;

/*
 * ----------------------------------------------------------------------------
 * PART 1: Predefined Functions & Actions
 * ----------------------------------------------------------------------------
 */

// expressions functions
MEM : 'mem';
NEARBY : 'nearby';
AHEAD : 'ahead';
RANDOM : 'random';
SMELL : 'smell';

// actions
WAIT : 'wait' ;
FORWARD : 'forward';
BACKWARD : 'backward';
LEFT : 'left';
RIGHT : 'right';
EAT : 'eat';
ATTACK : 'attack';
GROW : 'grow';
BUD : 'bud';
MATE : 'mate';
TAG : 'tag';
SERVE : 'serve';

/*
 * ----------------------------------------------------------------------------
 * PART 2: Parentheses & Separators
 * ----------------------------------------------------------------------------
 */

LPAREN : '(';
RPAREN : ')';

LBRACE : '{';
RBRACE : '}';

LBRACKET : '[';
RBRACKET : ']';

// SEPARATORS

SEMICOLON : ';';
ARROW : '-->';

/*
 * ----------------------------------------------------------------------------
 * PART 3: Operators
 * ----------------------------------------------------------------------------
 */

ASSIGN : ':=';

MUL : '*';
DIV : '/';
MOD : 'mod';

PLUS : '+';
MINUS : '-';

EQ : '=';
NE : '!=';
LT : '<';
LE : '<=';
GT : '>';
GE : '>=';

AND : 'and';
OR : 'or';

/*
 * ----------------------------------------------------------------------------
 * PART 5: Literals
 * ----------------------------------------------------------------------------
 */

IntLiteral : ('0' | '1'..'9' '0'..'9'*);

/*
 * ----------------------------------------------------------------------------
 * PART 6: Comments
 * ----------------------------------------------------------------------------
 */

COMMENT : '/*' .*? '*/' -> channel(HIDDEN); // match anything between /* and */
WS : [ \r\t\u000C\n]+ -> channel(HIDDEN); // white space
LINE_COMMENT : '//' ~[\r\n]* '\r'? '\n' -> channel(HIDDEN);
