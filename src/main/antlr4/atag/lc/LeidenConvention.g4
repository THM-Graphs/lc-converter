grammar LeidenConvention;

/* lexer rules */

EMPHASISED_START  : '[ru[';
EMPHASISED_END    : ']ru]';
EXPANDED_START : '(';
EXPANDED_END   : ')';
NONLINEAR_START : '[sl[';
NONLINEAR_END   : ']sl]';
MARGINNOTE_START : '[m[';
MARGINNOTE_END   : ']m]';
INTERNENTION_START : '[c[';
INTERNENTION_END   : ']c]';
INRASURA_START : '[ra[';
INRASURA_END   : ']ra]';
DELETED_START : '[del[';
DELETED_END   : ']del]';
UNREADABLE_START : '[ln[';
UNREADABLE_END   : ']ln]';
REPEATED_START : '{';
REPEATED_END   : '}';
INTERVENTION_START : '<';
INTERVENTION_END   : '>';
ADDITION_START : '[rm[';
ADDITION_END   : ']rm]';
HEAD_START : '[t[';
HEAD_END   : ']t]';
TRANSPOSITION_START : '[$[';
TRANSPOSITION_END   : ']$]';
COMMENT:  '§§';

GAP : '[lacuna]';
COLUMN : '|||';
LINEBREAK : '|';

WORD :  [A-Za-z0-9]+ ;
//WORD : ('a'..'z'|'A'..'Z'|'0'..'9') +;
//NEWLINE             : ('\r'? '\n' | '\r')+ ;
WHITESPACE          : (' ' | '\t') ;
SEPARATOR           : '.' | ',' |'?' | '!' | '-' | ':' | ';' | '*' | '>';


//SPECIAL : '[' | ']' | '(' | ')' | '{' | '}' | '<' | '>' | '|' | '.' | ',' |'?' | '!' | '#' ;
NEWLINE             : ('\r'? '\n' | '\r')+ ;
//WHITESPACE          : (' ' | '\t') ;
//ANY: . ;

/* parser rules */


doc     : comment? blocks EOF ;
comment: COMMENT WHITESPACE (WORD | WHITESPACE | SEPARATOR)+ WHITESPACE COMMENT NEWLINE;
blocks  : block+ ;
//block      : ( WORD | WHITESPACE | SEPARATOR | LINEBREAK | NEWLINE | page | emphasised | expanded | nonlinear | marginnote | internention | inrasura | deleted | unreadable | repeated | intervention | addition | head | GAP  )+ ;
block      : ( annotation | COLUMN | WORD | WHITESPACE | LINEBREAK | SEPARATOR | page | GAP | NEWLINE)+ ;

annotation : emphasised | expanded | nonlinear | marginnote | internention | inrasura | deleted | unreadable | repeated | intervention | addition | head | transposition;

emphasised    : EMPHASISED_START block EMPHASISED_END  ;
expanded   : EXPANDED_START block EXPANDED_END ;
nonlinear: NONLINEAR_START block NONLINEAR_END ;
marginnote  : MARGINNOTE_START block MARGINNOTE_END ;
internention   : INTERNENTION_START block INTERNENTION_END ;
inrasura   : INRASURA_START block INRASURA_END ;
deleted    : DELETED_START block DELETED_END ;
unreadable : UNREADABLE_START block UNREADABLE_END ;
repeated   : REPEATED_START block REPEATED_END ;
intervention        : INTERVENTION_START block INTERVENTION_END ;
addition: ADDITION_START block ADDITION_END ;
head   : HEAD_START block HEAD_END ;
page    : '#' WORD '#';
transposition: TRANSPOSITION_START block TRANSPOSITION_END;
