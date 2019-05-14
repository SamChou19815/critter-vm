grammar PL;

import PLLexerPart;

program : oneRule+;
oneRule : condition ARROW command SEMICOLON;
command : update* updateOrAction;
updateOrAction : update # UpdateCommand | action # ActionCommand;
update : MEM LBRACKET expr RBRACKET ASSIGN expr;
action
    : WAIT # WaitAction
    | FORWARD # ForwardAction
    | BACKWARD # BackwardAction
    | LEFT # LeftAction
    | RIGHT # RightAction
    | EAT # EatAction
    | ATTACK # AttackAction
    | GROW # GrowAction
    | BUD # BudAction
    | MATE # MateAction
    | TAG LBRACKET expr RBRACKET # TagAction
    | SERVE LBRACKET expr RBRACKET # ServeAction
    ;

condition
    : LBRACE condition RBRACE # NestedCondition
    | expr rel expr # ComparisonCondition
    | condition AND condition # AndCondition
    | condition OR condition # OrCondition
    ;
rel : LT | LE | GT | GE | EQ | NE;

expr
    : IntLiteral # IntExpr
    | NEARBY LBRACKET expr RBRACKET # NearByExpr
    | AHEAD LBRACKET expr RBRACKET # AheadExpr
    | RANDOM LBRACKET expr RBRACKET # RandomExpr
    | SMELL # SmellExpr
    | MEM LBRACKET expr RBRACKET # MemExpr
    | LPAREN expr RPAREN # NestedExpr
    | MINUS expr # NegExpr
    | expr mulOp expr # MulOpExpr
    | expr addOp expr # AddOpExpr
    ;
addOp : PLUS | MINUS;
mulOp : MUL | DIV | MOD;
