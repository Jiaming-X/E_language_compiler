/* *** This file is given as part of the programming assignment. *** */

///////////////////////////////////////////////////////////////////////////////
/* *** This file is given as part of the programming assignment. *** */
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
                    result = i;
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
                result = pos;
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
    private int scopelevel;
    private Table aTable; 
    private Scan scanner; 

    private void scan() {
	tok = scanner.scan();
    }

    Parser(Scan scanner) {  // Constructor

	this.scanner = scanner;
    aTable = new Table();
    scopelevel = 0;

    System.out.println("#include<stdio.h>\n\nint main(){\n");

	scan();
	program();

    System.out.println("\nreturn 0;\n}");    

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
    int PreOneRedef = 0;
	mustbe(TK.DECLARE);

    if(is(TK.ID)){
        if(aTable.findVariable(0, tok.string ) >= 0){

                System.err.println( "redeclaration of variable " + tok.string); 
                PreOneRedef = 1;           
        }else{
            aTable.addVariable( tok.string );
        System.out.print("int " + tok.string + Integer.toString(scopelevel));            
        }// table!!!!
        // Convert into C
    }

	mustbe(TK.ID);
	while( is(TK.COMMA) ) {
	    scan();

        if(is(TK.ID)){
            if(aTable.findVariable(0, tok.string ) >= 0){
                System.err.println( "redeclaration of variable " + tok.string);
            }else{
                aTable.addVariable( tok.string );

                if(PreOneRedef == 1){
                    System.out.print("int " + tok.string + Integer.toString(scopelevel) ); 
                }else{
                    System.out.print(", " + tok.string + Integer.toString(scopelevel) ); 
                }              
            }// table!!!!
        }
	    mustbe(TK.ID);
	}
    System.out.print(";\n");
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
        System.out.print("while");
    	mustbe(TK.DO);
        scopelevel++;        
        aTable.addTable();  // table!!!!

    	guarded_command();

    	mustbe(TK.ENDDO);
        aTable.removeTable();  /// table!!!!
        scopelevel--;
    }
    private void if_funct(){
        System.out.print("if");
    	mustbe(TK.IF);

        scopelevel++;
        aTable.addTable();  // table!!!!
    	guarded_command();
    	while(is(TK.ELSEIF)){
        System.out.print("else if");
    		scan();
    		guarded_command();
    	}
    	if(is(TK.ELSE)){
            System.out.print("else{");
    		scan();
    		block();
        System.out.print("}");    
    	}

    	mustbe(TK.ENDIF);

        aTable.removeTable();  /// table!!!!
        scopelevel--;
    }

    private void guarded_command(){
        System.out.print("( ");
    	expr();
        System.out.print(" <= 0 ){\n ");
    	mustbe(TK.THEN);
    	block();
        System.out.print(" }\n ");
    }
    private void assignment(){
    	ref_id();
        System.out.print(" = ");
    	mustbe(TK.ASSIGN);
    	expr();
        System.out.print(";\n ");  
    }
    private void print_funct(){
        System.out.print("printf(\"%d\\n\", ");
    	mustbe(TK.PRINT);
    	expr();
        System.out.print( " );\n" );
        //aTable.printTable();
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
               // System.out.print("1. find " + find);
            }else{
                find = aTable.findVariable(tok.string );
               // System.out.print("2. find " + find);
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

                System.out.print( tok.string + Integer.toString( find ) );

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
            System.out.print(" ( ");
     		mustbe(TK.LPAREN);
    		expr();
            System.out.print(" ) ");
     		mustbe(TK.RPAREN);    		   		
    	}else if(is(TK.NUM)){
            System.out.print( tok.string);
    		mustbe(TK.NUM);   // ??????
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
            System.out.print(" + ");           
    		mustbe(TK.PLUS);
    	}else{
            System.out.print(" - ");
    		mustbe(TK.MINUS);
    	}
    }
    private void multop(){
    	if(is(TK.TIMES) ){
            System.out.print(" * ");
    		mustbe(TK.TIMES);
    	}else{
            System.out.print(" / ");            
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
