
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
//// The table class
import java.util.*;
class Table {

    ArrayList<ArrayList<String>> TableList = new ArrayList<ArrayList<String>>();

    Table() {
        addTable();
    }

    void addTable(){
        ArrayList<String> temp = new ArrayList<String>();
        TableList.add( temp );
    }
    void removeTable(){
        int last = TableList.size();
        TableList.remove(last - 1);
    }
    void addVariable(int pos, String astring){
        if( isEmpty()){
            addTable();
        }

        TableList.get(pos).add( astring );
    }
    void addVariable(String astring){
        if( isEmpty()){
            addTable();
        }

        TableList.get( TableList.size() - 1 ).add( astring );
    }
    void removeVariable(int pos1, int pos2){
        TableList.get(pos1).remove(pos2);
    }
    int findVariable(String aString ){
        int result = -1;

        for (int i = 0; i < TableList.size(); i++ ) {
            for(int j = 0; j < TableList.get(i).size(); j++){
               if( aString.equals(TableList.get(i).get(j))) {
                    result = j;
               }
            }
        }
        return result;
    }

    int findVariable(int pos, String aString ){
        // pos = -1, global level; pos = n, n blocks level up
        int result = -1;

        if( pos < 0){
            pos = 0;
        }else{
            pos = TableList.size() - 1 - pos;
        }
        if( pos < 0){
            return -1;          
        }

        for (int i = 0; i < TableList.get(pos).size(); i++ ) {
           if( aString.equals(TableList.get(pos).get(i))) {
                result = i;
           }
        }
        return result;
    }


    boolean isEmpty(){
        return TableList.size() == 0;
    }

    void printTable(  ){

        for (int i = 0; i < TableList.size(); i++ ) {
            for(int j = 0; j < TableList.get(i).size(); j++){
               System.out.println(i + "  " + TableList.get(i).get(j) );
            }
            System.out.println("\n");
        }

        System.out.println( "aString!!!!!!!!!!!!!" );
    }

}


///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////






public class Parser {

    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    private void scan() {
	tok = scanner.scan();
    }

    private Table aTable;

    //System.out.println( "in the main" );

    private Scan scanner;
    Parser(Scan scanner) {
	this.scanner = scanner;

    aTable = new Table();

	scan();
	program();
    //aTable.printTable();
	if( tok.kind != TK.EOF )
	    parse_error("junk after logical end of program");
    }

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////

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

    if(is(TK.ID)){
        if(aTable.findVariable(0, tok.string ) >= 0){
            //System.out.println( " Redeclaration " + tok.string + " line" + tok.lineNumber );
                System.out.println( "redeclaration of variable " + tok.string);            
        }else{
            aTable.addVariable( tok.string );
        }// table!!!!
    }

	mustbe(TK.ID);
	while( is(TK.COMMA) ) {
	    scan();

        if(is(TK.ID)){
            if(aTable.findVariable(0, tok.string ) >= 0){
                System.out.println( "redeclaration of variable " + tok.string);
            }else{
                aTable.addVariable( tok.string );
            }// table!!!!
        }
	    mustbe(TK.ID);
	}
    }

//// Modification start here
    private void statement_list() {
    	while(is( TK.PRINT ) | is( TK.DO ) | is( TK.IF ) | is( TK.TILDE ) | is(TK.ID)){
    		statement();
    	}
    }

////Added functions below
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
        aTable.addTable();  // table!!!!

    	guarded_command();

    	mustbe(TK.ENDDO);
        aTable.removeTable();  /// table!!!!
    }
    private void if_funct(){
    	mustbe(TK.IF);

        aTable.addTable();  // table!!!!

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

        aTable.removeTable();  /// table!!!!
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
        int pos = -1;  // global
        String pos2 = "";
        int find;
        int find2;
        String haveT = "";

    	if(is(TK.TILDE)){
            haveT = tok.string;
    		mustbe(TK.TILDE);
    		if(is(TK.NUM)){
                pos = Integer.parseInt( tok.string );
                pos2 = tok.string;
    			mustbe(TK.NUM);
    		}
    	}
         // table!!!!!
        if( is(TK.ID)){
            if( haveT.equals("~") ){
                find = aTable.findVariable(pos, tok.string );
            }else{
                find = aTable.findVariable(tok.string );
            }
            find2 = aTable.findVariable(tok.string );
            if( find < 0 & find2 >= 0){
                System.err.println( "no such variable " + haveT + pos2 + 
                 tok.string + " on line " + tok.lineNumber ); 
                System.exit(1);
            }
            if( find < 0 & find2 < 0){
                System.err.println(tok.string +" is an undeclared variable on line " + tok.lineNumber ); 
                System.exit(1);
            }
        }

        mustbe(TK.ID);    		
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



////Added functions above


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
