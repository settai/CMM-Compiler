import java.io.*; 
import java.util.*;

public class Lexical extends LineNumberReader{
	//table des mot-clés
	public final static String motsCle[]= {"else","then","for","while","void","int","extern","return","if"};

	//table des operateurs binaires
	public final static String binOp[]= {"+","-","<<",">>","*","&","|","/"};

	//table des comparateurs binaires
	public final static String binComp[]= {"<",">","<=",">=","==","!="};

	// liste des identifiants
	public static String identifiants[]; 

	//paramètres de l'input
	private char c;
	private int lineNumber = 1;
	private final static char EOF='\u001a';
	//close file reader

	// constructor 
	Lexical(String file) throws IOException {
		super(new FileReader(file));
		identifiants = new String[300];
		c = lc();
	}
	// le point du départ de lexical
	public String getResult() throws IOException{
		TUniteLexical ul;
		String str="";
		while(!(ul=uniteSuivante()).getUL().equals(TUnite.EOF))
			str += "Unite lexical "+ul.getUL()+", Attribut "+ul.getAttribute()+"\n";
		return str;
	}

	public char lc() throws IOException {
		lineNumber = super.getLineNumber()+1;
		int c = read();
		if(c != -1) return (char) c;
		return EOF;
	}

	public int estMotcle(String lexeme){
		return getIndex(lexeme,motsCle);
	}

	public int getIndex(String stringToFind, String[] stringArray){
		int index = -1;
		for (int i=0;i<stringArray.length;i++) {
			if (stringArray[i].equals(stringToFind)) {
			index = i;
			break;
			}
		}
		return index;
	}

	public int getLineNumber(){
		return lineNumber;
	}

	public boolean estBlanc(char c){
		return (c==' ' || c=='\t' || c=='\n' || c=='\r');
	}
	//fonction de hashage
	static int hashcode(String str)
	{
		int c=0;
			 for(int i=0;i<str.length();i++){
					 c+= (str.charAt(i)*Math.pow(37,i))%300;
			 }
			 return c%300;
	 }

	public TUniteLexical uniteSuivante() throws IOException {
		String lexeme="";

		while(estBlanc(c))
		 c=lc();

		switch(c){
			
			case '+':	
				c=lc();
				return new TUniteLexical(TUnite.BIN_OP, getIndex("+",binOp));
			
			case '-':
				c=lc();
				return new TUniteLexical(TUnite.BIN_OP, getIndex("-",binOp));

			case '<':{
				c=lc();
				if(c=='<'){
					c=lc();
					return new TUniteLexical(TUnite.BIN_OP, getIndex("<<",binOp));
				}
				else if(c=='='){
					c=lc();
					return new TUniteLexical(TUnite.BIN_COMP, getIndex("<=",binComp));
				}
				else
					return new TUniteLexical(TUnite.BIN_COMP, getIndex("<",binComp));
			}

			case '>':{
				c=lc();
				if(c=='>'){
					c=lc();
					return new TUniteLexical(TUnite.BIN_OP, getIndex(">>",binOp));
				}
				else if(c=='='){
					c=lc();
					return new TUniteLexical(TUnite.BIN_COMP, getIndex(">=",binComp));
				}
				else
					return new TUniteLexical(TUnite.BIN_COMP, getIndex(">",binComp));
			}

		
			case '*':
				c=lc();
				return new TUniteLexical(TUnite.BIN_OP, getIndex("*",binOp));

			case '&':
				c=lc();
				if(c=='&'){
					c=lc();
					return new TUniteLexical(TUnite.DOUBLE_AND, -1);
				}
				return new TUniteLexical(TUnite.BIN_OP, getIndex("&",binOp));

			case '|':
				c=lc();
				if(c=='|'){
					c=lc();
					return new TUniteLexical(TUnite.DOUBLE_OR, -1);
				}
				return new TUniteLexical(TUnite.BIN_OP, getIndex("|",binOp));	

			case '/':
				c=lc();
				return new TUniteLexical(TUnite.BIN_OP, getIndex("/",binOp));	

			case '=':
				c=lc();
				if(c=='='){
					c=lc();
					return new TUniteLexical(TUnite.BIN_COMP, getIndex("==",binComp));
				}
				return new TUniteLexical(TUnite.AFF,-1);

			case '!':
				c=lc();
				if(c=='='){
					c=lc();
					return new TUniteLexical(TUnite.BIN_COMP, getIndex("!=",binComp));
				}
				return new TUniteLexical(TUnite.NEG, -1);

			case '[':
				c=lc();
				return new TUniteLexical(TUnite.CROCH_OUV, -1);

			case ']':
				c=lc();
				return new TUniteLexical(TUnite.CROCH_FER, -1);

			case '(':
				c=lc();
				return new TUniteLexical(TUnite.PARTH_OUV, -1);

			case ')':
				c=lc();
				return new TUniteLexical(TUnite.PARTH_FER, -1);

			case ',':
				c=lc();
				return new TUniteLexical(TUnite.VERG, -1);

			case '{':
				c=lc();
				return new TUniteLexical(TUnite.ACC_OUV, -1);

			case '}':
				c=lc();
				return new TUniteLexical(TUnite.ACC_FER, -1);

			case ';':
				c=lc();
				return new TUniteLexical(TUnite.POINT_VER, -1);
				
			default:
			{
				if(Character.isDigit(c)){
					while(Character.isDigit(c)){
						lexeme += c; //Aj
						c=lc();
					}
					return new TUniteLexical(TUnite.ENT,Integer.parseInt(lexeme));
				}
				else if(Character.isLetter(c)){
					while(Character.isLetter(c)||Character.isDigit(c)){
						lexeme += c; //Aj
						c=lc();
						//System.out.println(c);
					}
					
					int pos = estMotcle(lexeme);
					if(pos!=-1)
						return new TUniteLexical(TUnite.MOT_CLE, pos);
					
					return ULIdentif(lexeme);
				}
				else if (c==EOF){
					return new TUniteLexical(TUnite.EOF,-1);
				}

				else{
					int cp = c;
					c=lc();
					return new TUniteLexical(TUnite.AUTRE,cp);
				}
			}
		}

		
	}


	public TUniteLexical ULIdentif(String lexeme){
		int pos = hashcode(lexeme);
		if(identifiants[pos] == null){	
			identifiants[pos]=lexeme;
		}
		return new TUniteLexical(TUnite.IDENT,pos);
	}

	public static void main(String[] args) throws IOException {
		
		Lexical lex = new Lexical(args[0]);
		TUniteLexical ul;

		while(!(ul=lex.uniteSuivante()).getUL().equals(TUnite.EOF))
			System.out.println(ul.getUL());
		}
}


