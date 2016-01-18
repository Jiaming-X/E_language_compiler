/* *** This file is given as part of the programming assignment. *** */

public class Parser {


    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    private void scan() {
	tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
	this.scanner = scanner;
	scan();
	program();
	if( tok.kind != TK.EOF )
	    parse_error("junk after logical end of program");
    }

    private void program() {
	block();
    }

    private void block(){
	declaration_list();
	statement_list();
    }

    private void declaration_list() {
	// below checks whether tok is in first set of declaration.
	// here, that's easy since there's only one token kind in the set.
	// in other places, though, there might be more.
	// so, you might want to write a general function to handle that.
	while( is(TK.DECLARE) ) {
	    declaration();
	}
    }

    private void declaration() {
	mustbe(TK.DECLARE);
	mustbe(TK.ID);
	while( is(TK.COMMA) ) {
	    scan();
	    mustbe(TK.ID);
	}
    }
//// Modification start here
    private void statement_list() {
    	while(is( TK.PRINT ) | is( TK.DO ) | is( TK.IF ) | is( TK.TILDE ) | is(TK.ID)){
    		statement();
    	}
    }

////Added functions
    private void statement(){
    	if( is( TK.PRINT ) ){ // !
    		print_funct();
    	}else if( is( TK.DO ) ){ // '<'
    		do_funct();
    	}else if( is( TK.IF ) ){ // '['
    		if_funct();
    	}else{
    		assignment();
    	}
    }

    private void do_funct(){
    	mustbe(TK.DO);
    	guarded_command();
    	mustbe(TK.ENDDO);
    }
    private void if_funct(){
    	mustbe(TK.IF);
    	guarded_command();
    	while(is(TK.ELSEIF)){
    		scan();
    		guarded_command();
    	}
    	if(is(TK.ELSE)){
    		scan();
    		block();
    	}
    	mustbe(TK.ENDIF);
    }

    private void guarded_command(){
    	expr();
    	mustbe(TK.THEN);
    	block();
    }
    private void assignment(){
    	ref_id();
    	//if(!is(TK.EOF)){
    	mustbe(TK.ASSIGN);
    	//}
    	expr();
    }
    private void print_funct(){
    	mustbe(TK.PRINT);
    	expr();
    }
    private void expr(){
    	term();
    	while(ifaddop()){
    		addop();
    		term();
    	}
    }
    private void ref_id(){
    	if(is(TK.TILDE)){
    		mustbe(TK.TILDE);
    		if(is(TK.NUM)){
    			mustbe(TK.NUM);
    		}
    	}
    //	if(!is(TK.EOF)){
    	mustbe(TK.ID);    		
    //	}
    }

    private void term(){
    	factor();
    	while(ifmultop()){
    		multop();
    		factor();
    	}
    }
    private void factor(){
    	if(is(TK.LPAREN)){// (
     		mustbe(TK.LPAREN);
    		expr();
     		mustbe(TK.RPAREN);    		   		
    	}else if(is(TK.NUM)){
    		mustbe(TK.NUM);   
    	}else{
    		ref_id();	
    	}
    }
    private boolean ifaddop(){
    	return (is(TK.PLUS) | is(TK.MINUS) );
    }
    private boolean ifmultop(){
    	return (is(TK.TIMES) | is(TK.DIVIDE) );
    }
    private void addop(){
    	if(is(TK.PLUS) ){
    		mustbe(TK.PLUS);
    	}else{
    		mustbe(TK.MINUS);
    	}
    }
    private void multop(){
    	if(is(TK.TIMES) ){
    		mustbe(TK.TIMES);
    	}else{
    		mustbe(TK.DIVIDE);
    	}
    }



////Added functions


    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
	if( tok.kind != tk ) {
	    System.err.println( "mustbe: want " + tk + ", got " +
				    tok);
	    parse_error( "missing token (mustbe)" );
	}
	scan();
    }

    private void parse_error(String msg) {
	System.err.println( "can't parse: line "
			    + tok.lineNumber + " " + msg );
	System.exit(1);
    }

}
