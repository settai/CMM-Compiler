import java.io.IOException;
import java.util.*;

class Variable{
    protected String identifiant;
    protected String type; //int int[] int[][]
    private int value=0;
    
    
    Variable(String identifiant, String type){
        this.identifiant = identifiant;
        this.type = type;
    }
   
    static int hashcode(String str)
	{
		int c=0;
			 for(int i=0;i<str.length();i++){
					 c+= (str.charAt(i)*Math.pow(37,i))%300;
			 }
			 return c%300;
	 }
    @Override
    public String toString() {
        return "int"+" "+identifiant;
    }


    public int getValue() {
        return value;
    }

    /**
     * @return the identifiant
     */
    public String getIdentifiant() {
        return identifiant;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;


    }
}

class Tableau extends Variable {
    protected int size1;

    Tableau(String identifiant, String type, int size1){
        super(identifiant,type);
        this.size1 = size1;
    }
    @Override
    public String toString() {
        return super.toString()+"["+size1+"]";
    }
    public int getSize1(){
        return size1;
    }
   
}

class TableauDouble extends Tableau{
    private int size2;
    TableauDouble(String identifiant, String type, int size1, int size2){
        super(identifiant, type, size1);
        this.size2 = size2;
    }
    @Override
    public String toString() {
        return super.toString()+"["+size2+"]";
    }
    public int getSize2(){
        return size2;
    }
}

class Fonction{
    private String identifiant;
    private String returnedType;
    private int nbrParms; //int int[] int[][]

    Fonction(String identifiant, String returnedType, int parms){
        this.identifiant = identifiant;
        this.returnedType = returnedType;
        this.nbrParms = parms;
    }
    static void parcourir(Fonction list[])
    {
        for(int i=0;i<300;i++)
            if(list[i]!=null)
                 System.out.println("Fonction:" + list[i]);

    }
    public int getNbrParms() {
        return nbrParms;
    }
    static int hashcode(String str)
	{
		int c=0;
			 for(int i=0;i<str.length();i++){
			    c+= (str.charAt(i)*Math.pow(37,i))%300;
			 }
			 return c%300;
	}
    public String getIdentifiant() {
        return identifiant;
    }


    public String getReturnedType() {
        return returnedType;
    }
    
    @Override
    public String toString() {
        String str="";
        for(int i=0;i<nbrParms;i++){
            str+="int";
            if(i<nbrParms-1)
                str+=", ";
        }
        return returnedType+" "+identifiant+"("+str+")";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof String)
            return identifiant.equals(obj);
        else
            return super.equals(obj);
    }


}

public class Syntaxique extends Lexical{
    public Variable variablesGlobales[];
    public Variable variablesLocales[];
    public Fonction fonctions[];

    private TUniteLexical ULCourant;
    private int errorNum =0; // syntaxiques
    private int errorSemanticNum =0; // syntaxiques
    private int realLineNumber=1;

    private String identifCourant;
    private String typeCourant;
    private int size1Courant, size2Courant;
    private boolean global;
    private int nbrParmsCourant;
    private String fonctionCourant="";
    private String expressionType="";
    private String expressionTypeAncien="";

    private String SyntaxicErrors="";
    private String SemanticErrors="";
    private String structure;

    public void addSemanticError(String error){
        this.SemanticErrors+="\n"+error;
    }
    public void addSyntaxicError(String error){
        this.SyntaxicErrors+="\n"+error;
    }

    public String arrayToString(Variable var[]){
        String str = "[";
        int j=0;
        
        for(j=0;j<300;j++){
            if(var[j]!=null){
                str += var[j];
                break;
            }
        }  

        for(int i=j+1;i<300;i++){
            if(var[i]!=null)
                str += ","+var[i];
        }    
        return str+"]";  
    }

    public String arrayToString(Fonction var[]){
        String str = "[";
        int j=0;
        
        for(j=0;j<300;j++){
            if(var[j]!=null){
                str += var[j];
                break;
            }
        }  

        for(int i=j+1;i<300;i++){
            if(var[i]!=null)
                str += ","+var[i];
        }    
        return str+"]";   
    }

    public String getSemanticErrors() {
        return SemanticErrors;
    }

    public String getSyntaxicErrors() {
        return SyntaxicErrors;
    }

    public String getStructure() {
        return structure;
    }

    //Cette fonction doit être remplacé par la fonction de hachage (c'est linéaire mnt)
    public Variable getVariable(String str){   
        
        //vérifie les varibales locales
        int pos = hashcode(str);
        if(variablesLocales[pos]!=null)
            {
                if(variablesLocales[pos].getIdentifiant() == str)
                    return variablesLocales[pos];
            }


        //vérifier les variables globales
        if(variablesGlobales[pos]!=null)
            {
                if(variablesGlobales[pos].getIdentifiant() == str)
                    return variablesGlobales[pos];
            }

        
            return null; 

    }


    public Fonction estFonctionDeclare(String str){    
        //vérifier les fonctions déclarées
        int pos = hashcode(str);
        if(fonctions[pos]!=null)
           {
                if(fonctions[pos].equals(str))
                    return fonctions[pos];
           }

        return null; 
    }

    Syntaxique(String path) throws IOException{
        super(path);
        variablesGlobales = new Variable[300];
        variablesLocales =new Variable[300];
        fonctions = new Fonction[300] ;
    }

    public void motSuivant(){
        try{
            realLineNumber = super.getLineNumber();
            ULCourant = uniteSuivante();
        }
        catch(IOException ignore){
        }
        
    }

    public boolean ULCourantIn(TUniteLexical ... ULs){
        boolean bool=false;
        for(TUniteLexical UL : ULs) {
            if(ULCourant.equals(UL))
                bool = true;
        }
        return bool;
    }

    public boolean skip(TUniteLexical ... ULs){
        boolean found = false;
        do{
            found = ULCourantIn(ULs);
            if(EOF()) return false;
            else if(found == false){
                System.out.println("\tSKIPED");
                motSuivant();
            }
        } while(found == false);
        return found;   
    }

    public int getErrorNum() {
        return errorNum;
    }


    public int getErrorSemanticNum() {
        return errorSemanticNum;
    }

    public int getNumPar(String fonctionCourant){

        int pos = hashcode(fonctionCourant);
        if(fonctions[pos]!=null)
        
        {
            if(fonctions[pos].equals(fonctionCourant))
                return fonctions[pos].getNbrParms();
        }
        return -1;
    }

    

    public void addVariableGlobal(Variable var){
        int pos = hashcode(var.getIdentifiant());
        System.out.println(pos);
        variablesGlobales[pos]=var;
    }

    public void addVariableLocal(Variable var){
        int pos = hashcode(var.getIdentifiant());
        variablesLocales[pos]=var;
    }

    public void addFonction(Fonction f){
        int pos = hashcode(f.getIdentifiant());
        fonctions[pos]=f;
    }

    public boolean EOF() {
        return ULCourant.getUL().equals(TUnite.EOF);
    }

    public void start(){
        try{
            motSuivant();
            boolean test = programme() && EOF();
            if (test){
                SyntaxicErrors = "Nombre d'erreurs syntaxiques : "+ getErrorNum() + SyntaxicErrors ;
                SemanticErrors = "Nombre d'erreurs semantiques : "+ getErrorSemanticNum() + SemanticErrors ;
            }
            else
            {
                SyntaxicErrors += "Erreur de syntaxe a la ligne " + (realLineNumber + 1);
                SemanticErrors = "Erreur de syntaxe a la ligne " + (realLineNumber + 1);
            }
            

            structure = "La structure de Travail :\n\n-Les variables globales :\n" + arrayToString(variablesGlobales) + "\n\n-Les variables locales :\n" + arrayToString(variablesLocales) + "\n\n-Les fonctions :\n" + arrayToString(fonctions);
            }
            catch(Exception ignore){
                ignore.printStackTrace();
            }
    }
    public static void main(String[] args) {
        try{
        Syntaxique syn = new Syntaxique(args[0]);
        syn.motSuivant();
        if (syn.programme() && syn.EOF())
            System.out.println("true");
        else
            System.out.println("false");
        
        System.out.println("Nombre d'erreurs syntaxiques : "+ syn.getErrorNum());
        System.out.println("Nombre d'erreurs semantiques : "+ syn.getErrorSemanticNum());
        System.out.println(syn.arrayToString(syn.variablesGlobales));
        System.out.println(syn.arrayToString(syn.variablesLocales));
        System.out.println(syn.arrayToString(syn.fonctions));
        //Variable.parcourir(syn.variablesGlobales, 'G');
        //Fonction.parcourir(syn.fonctions);
        syn.close();
        }
        catch(Exception ignore){
            ignore.printStackTrace();
        }
    }

    //<Programme> : < declarateurs-global> < liste-fonctions’>  
    public boolean programme() {
        if(declarateurs_global()){
            if(liste_fonctions_prime()){
                return true;
            }
            else{
                errorNum++;
                System.out.println("\nFonction invalide a la ligne "+realLineNumber+"\n"); 
                addSyntaxicError("Fonction invalide a la ligne "+realLineNumber); 
                if(skip(new TUniteLexical(TUnite.MOT_CLE,4), new TUniteLexical(TUnite.MOT_CLE,5), new TUniteLexical(TUnite.MOT_CLE,6))){
                    return liste_fonctions();
                }
                else return false;
                
            }
        }
        else {
            errorNum++;
            System.out.println("\nDeclaration invalide a la ligne "+realLineNumber+"\n"); 
            addSyntaxicError("Declaration invalide a la ligne "+realLineNumber); 
            if(skip(new TUniteLexical(TUnite.MOT_CLE,4), new TUniteLexical(TUnite.MOT_CLE,5), new TUniteLexical(TUnite.MOT_CLE,6))){
                return programme();
            }
            else return false;
        }
    }

    //< declarateurs-global> : int identificateur <declaration-global> | epsilon
    public boolean declarateurs_global() {
        if (ULCourant.estMotCle("int")) {
            typeCourant = "int";
            motSuivant();
            if (ULCourant.estIdentif()) {
                identifCourant = ULCourant.getLexeme();
                global = true;
                variablesLocales =new Variable[300];
                nbrParmsCourant = 0;
                motSuivant();
                if(declaration_global())
                    return true;
                else               
                    return false;
            }
            else return false;

        } else 
            return true;
    }

    //<declaration-global> : <liste-declarateurs> ; < dectarateurs-global> | <fonction> <liste-fonctions>
    public boolean declaration_global() {
        if (ULCourant.getUL().equals(TUnite.VERG) || ULCourant.getUL().equals(TUnite.CROCH_OUV) // si l'unite courant inclut dans le premier de liste-declarateurs
                || ULCourant.getUL().equals(TUnite.POINT_VER)) {
            if (liste_declarateurs()) {
                if (ULCourant.getUL().equals(TUnite.POINT_VER)) {
                    //variable
                    motSuivant();
                    return declarateurs_global();
                } else
                    return false;
            } else
                return false;
        } else if (ULCourant.getUL().equals(TUnite.PARTH_OUV)) {
            return fonction() && liste_fonctions();
        } else
            return false;
    }

    //<liste-declarateurs> : <tableau> <liste-declarateurs’>
    public boolean liste_declarateurs() {
        return tableau() && liste_declarateurs_prime();
    }

    //<liste-declarateurs’> : , identificateur <declarateur> <liste-declarateurs’> | epsilon
    public boolean liste_declarateurs_prime() {
        if (ULCourant.getUL().equals(TUnite.VERG)){
            motSuivant();
            if(ULCourant.estIdentif()){
                identifCourant = ULCourant.getLexeme();
                motSuivant();
                return tableau() && liste_declarateurs_prime();
            }
            else return false;
            
        } else
            return true;
    }

    //<tableau> : [constante] <tableau-double> | epsilon
    public boolean tableau() {
        if (ULCourant.getUL().equals(TUnite.CROCH_OUV)) {
            motSuivant();
            if (ULCourant.getUL().equals(TUnite.ENT)) {
                size1Courant = ULCourant.getAttribute();
                motSuivant();
                if (ULCourant.getUL().equals(TUnite.CROCH_FER)) {
                    motSuivant();
                    return tableau_double();
                } else
                    return false;
            } else
                return false;
        } else{
            Variable checker = getVariable(identifCourant);
            if(checker!=null){
                System.out.println("La variable '"+identifCourant+"' est deja declaree (ligne:"+realLineNumber+")");
                addSemanticError("La variable '"+identifCourant+"' est deja declaree (ligne:"+realLineNumber+")");
                errorSemanticNum++;
            }

            else if(global)
                addVariableGlobal(new Variable(identifCourant, "int"));

            else {
                addVariableLocal(new Variable(identifCourant, "int"));
            }

            return true;
        }
    }

    //<tableau-double> : [constante] | epsilon
    public boolean tableau_double() {
        if (ULCourant.getUL().equals(TUnite.CROCH_OUV)) {
            motSuivant();
            if (ULCourant.getUL().equals(TUnite.ENT)) {
                size2Courant = ULCourant.getAttribute();
                motSuivant();
                if(ULCourant.getUL().equals(TUnite.CROCH_FER)){
                    System.out.println(new TableauDouble(identifCourant,"int[][]",size1Courant,size2Courant));
                    if(global)
                        addVariableGlobal(new TableauDouble(identifCourant,"int[][]",size1Courant,size2Courant));
                    else
                        addVariableLocal(new TableauDouble(identifCourant,"int[][]",size1Courant,size2Courant));
                    motSuivant();
                    return true;
                }
                else 
                    return false;
            } else
                return false;
        } else{
            if(global)
                addVariableGlobal(new Tableau(identifCourant,"int[]",size1Courant));
            else
                addVariableLocal(new Tableau(identifCourant,"int[]",size1Courant));
            return true;
        }
           

    }

    //<fonction> : ( <liste-parms> ) { <liste-declarations> < liste-instructions>}
    public boolean fonction() {
        if (ULCourant.getUL().equals(TUnite.PARTH_OUV)) {
            motSuivant();
            if (liste_parms()) {
                if (ULCourant.getUL().equals(TUnite.PARTH_FER)){
                    


                    Fonction checker = estFonctionDeclare(identifCourant);
                    if(checker!=null){
                        System.out.println("La fonction '"+identifCourant+"' est deja declaree (ligne:"+realLineNumber+")");
                        addSemanticError("La fonction '"+identifCourant+"' est deja declaree (ligne:"+realLineNumber+")");

                        errorSemanticNum++;
                    }
                    else
                        addFonction(new Fonction(identifCourant, typeCourant, nbrParmsCourant));


                    motSuivant();
                    if (ULCourant.getUL().equals(TUnite.ACC_OUV)) {
                        motSuivant();
                        if (corps_fonction()) { 
                            if (ULCourant.getUL().equals(TUnite.ACC_FER)) {                                                              
                                motSuivant();
                                return true;
                            } else
                                return false;
                        } else
                            return false;

                    } else
                        return false;
                } else
                    return false;
            } else
                return false;
        } else
            return false;
    }

    
    public boolean corps_fonction(){
        if(liste_declarations()){
            if(liste_instructions()){
                return true;
            }
            else if(skip(new TUniteLexical(TUnite.IDENT,-1), new TUniteLexical(TUnite.MOT_CLE,2), new TUniteLexical(TUnite.MOT_CLE,3), new TUniteLexical(TUnite.MOT_CLE,7), 
            new TUniteLexical(TUnite.MOT_CLE,8), new TUniteLexical(TUnite.ACC_OUV,-1)))
            {
                errorNum++;
                System.out.println("\nInstrucion invalide a la ligne "+realLineNumber+"\n");
                addSyntaxicError("Instrucion invalide a la ligne "+realLineNumber);

                return liste_instructions();
            }
            else return false;
        }
        else if(skip(new TUniteLexical(TUnite.MOT_CLE,5),new TUniteLexical(TUnite.IDENT,-1), new TUniteLexical(TUnite.MOT_CLE,2), new TUniteLexical(TUnite.MOT_CLE,3), new TUniteLexical(TUnite.MOT_CLE,7), 
        new TUniteLexical(TUnite.MOT_CLE,8), new TUniteLexical(TUnite.ACC_OUV,-1)))
        {
            errorNum++;
            System.out.println("\nDeclaration invalide a la ligne "+realLineNumber+"\n");
            addSyntaxicError("Declaration invalide a la ligne "+realLineNumber);

            return corps_fonction();
        }
        else return false;
    }

    //<liste-declarations> : <declaration> <liste-declarations> | epsilon
    public boolean liste_declarations(){
        if(ULCourantIn(new TUniteLexical(TUnite.MOT_CLE,5))){
            return declaration() && liste_declarations();
        }
        else return true;
    }

    //<declaration> : int identificateur <liste-declarateurs> ;
    public boolean declaration(){
        if (ULCourant.estMotCle("int")) {
            motSuivant();
            if (ULCourant.estIdentif()) {
                identifCourant = ULCourant.getLexeme();
                global = false;
                motSuivant();
                if(liste_declarateurs())
                {
                    if(ULCourant.getUL().equals(TUnite.POINT_VER))
                    {
                        motSuivant();
                        return true;
                    }
                    else return false;
                }
                else return false;
            } else
                return false;
        } else 
            return false;
    }

    // <prototype> : extern <type> identificateur ( <liste-parms> ) ;
    public boolean prototype() {
        if (ULCourant.estMotCle("extern")) {
            motSuivant();
            if (type()) {
                if (ULCourant.estIdentif()) {
                    identifCourant =ULCourant.getLexeme();
                    variablesLocales =new Variable[300];
                    nbrParmsCourant = 0;
                    motSuivant();
                    if (ULCourant.getUL().equals(TUnite.PARTH_OUV)) {
                        motSuivant();
                        if (liste_parms()) {
                            if (ULCourant.getUL().equals(TUnite.PARTH_FER)) {
                                motSuivant();
                                if (ULCourant.getUL().equals(TUnite.POINT_VER)) {
                                    


                                    Fonction checker = estFonctionDeclare(identifCourant);
                                    if(checker!=null){
                                        System.out.println("La fonction '"+identifCourant+"' est deja declaree (ligne:"+realLineNumber+")");
                                        addSemanticError("La fonction '"+identifCourant+"' est deja declaree (ligne:"+realLineNumber+")");
                                        errorSemanticNum++;
                                    }
                        
                                    else
                                        addFonction(new Fonction(identifCourant, typeCourant, nbrParmsCourant));


                                    motSuivant();
                                    return true;
                                } else 
                                    return false;
                            } else
                                return false;
                        } else
                            return false;
                    } else
                        return false;
                } else
                    return false;
            } else
                return false;
        } else
            return false;
    }

    // <type> : void | int
    public boolean type() {
        if (ULCourant.estMotCle("void") || ULCourant.estMotCle("int")) {
            typeCourant = Lexical.motsCle[ULCourant.getAttribute()]; //le type courant
            motSuivant();
            return true;
        } else
            return false;
    }

    // <liste-parms> : <parm> <liste-parms’> | eps

    public boolean liste_parms() {
        if(parm())
            return liste_parms_prime();
        
        else return true;
    }

    // btw ya errorNum critique here, <list-parms> = ,a,c,b and not a,c,b
    // It should be <liste-parms> : <<liste-parms>,<parm> | <parm>
    // which gives what im coding now xd

    // <parm> : int identificateur 
    public boolean parm() {
        if (ULCourant.estMotCle("int")) {
            motSuivant();
            if (ULCourant.estIdentif()) {
                fonctionCourant = ULCourant.getLexeme();
                addVariableLocal(new Variable(ULCourant.getLexeme(),"int"));
                nbrParmsCourant++;
                motSuivant();
                return true;
            } else
                return false;
        } else
            return false;
    }

    // <liste-parms’> : , <parm> <liste-parms’> | epsilon (A CORRIGER sur PDF)
    public boolean liste_parms_prime() {
        if (ULCourant.getUL().equals(TUnite.VERG)) {
            motSuivant();
            return (parm() && liste_parms_prime());
        }
        return true;
    }

    /*
     * <expression> : ( <expression> ) <expression’> | -<expression><expression’> |
     * identificateur <expression’’> | <constante> <expression’>
     */

    public boolean expression() {
        if (ULCourant.getUL().equals(TUnite.PARTH_OUV)) {
            motSuivant();
            if (expression()) {
                if (ULCourant.getUL().equals(TUnite.PARTH_FER)) {
                    motSuivant();
                    return (expression_prime());
                } else
                    return false;
            } else
                return false;
        }


        else if (ULCourant.estBinOp("-")) {
            motSuivant();
            return (expression() && expression_prime());

        }

        else if (ULCourant.estIdentif()) {

            identifCourant = ULCourant.getLexeme();
            
            Fonction foncCourante;
            Variable identifiantCourant;

            TUniteLexical UniteCourante = ULCourant;
            motSuivant();

            boolean isFonction = ULCourant.getUL().equals(TUnite.PARTH_OUV);
            if (isFonction)
            {
                foncCourante = estFonctionDeclare(identifiants[UniteCourante.getAttribute()]); 
                if(foncCourante==null)
                {
                    errorSemanticNum++;
                    System.out.println("'"+identifiants[UniteCourante.getAttribute()]+"' est non declare! (ligne:"+realLineNumber+")");
                    addSemanticError("'"+identifiants[UniteCourante.getAttribute()]+"' est non declare! (ligne:"+realLineNumber+")");

                }
                else
                {
                    if(expressionType.equals("")) 
                        expressionType=foncCourante.getReturnedType();
    
                    else if(!expressionType.equals(foncCourante.getReturnedType()))
                    {
                        errorSemanticNum++;
                        System.out.println("Non homogenite des types dans l'operation (ligne:"+realLineNumber+")");
                        addSemanticError("Non homogenite des types dans l'operation (ligne:"+realLineNumber+")");

                    }
                }                   
            }
            else
            {
                identifiantCourant = getVariable(identifiants[UniteCourante.getAttribute()]);                
                if(identifiantCourant==null)
                {
                    errorSemanticNum++;
                    System.out.println("'"+identifiants[UniteCourante.getAttribute()]+"' est non declare! (ligne:"+realLineNumber+")");
                    addSemanticError("'"+identifiants[UniteCourante.getAttribute()]+"' est non declare! (ligne:"+realLineNumber+")");

                }
                else
                {   
                    expressionType=identifiantCourant.getType();
                }

            }
                     
            return (expression_second());
        }

        else if (ULCourant.getUL().equals(TUnite.ENT)) {

            motSuivant();

            if (!ULCourant.getUL().equals(TUnite.CROCH_FER))
                expressionType="int";

            return (expression_prime());
        }

        else
            return false;
    }

    // <expression’> : <binary-op> <expression><expression’> | episilon
    public boolean expression_prime() {
        if (ULCourant.getUL().equals(TUnite.BIN_OP)) {

            if(expressionTypeAncien!="")
                if(!expressionTypeAncien.equals(expressionType)){
                    errorSemanticNum++;
                    System.out.println("Non homogenite des types dans l'operation (ligne:"+realLineNumber+")");
                    addSemanticError("Non homogenite des types dans l'operation (ligne:"+realLineNumber+")");

                }
            expressionTypeAncien=expressionType;

            motSuivant();
            return (expression() && expression_prime());
        }
        return true;
    }

    // <expression’’> : <variable’> <expression’> | (<liste-expressions>)<expression’>
    public boolean expression_second() {

        if (ULCourant.getUL().equals(TUnite.PARTH_OUV)) {
            nbrParmsCourant=0;
            motSuivant();
            if (liste_expressions()) {
                if (ULCourant.getUL().equals(TUnite.PARTH_FER)) {

                    if(getNumPar(identifCourant)!=-1 && nbrParmsCourant != getNumPar(identifCourant)){
                        System.out.println("Le nombre de parametre invalide");
                        addSemanticError("Le nombre de parametre invalide (ligne:"+realLineNumber+")");
                        errorSemanticNum++;
                    }
                    motSuivant();
                    return expression_prime();
                } else
                    return false;

            } else
                return false;
        }

        else if (variable_prime()) {
            return expression_prime();
        }   
        else return false;
    }

    // <liste-expressions> :<expression> <liste-expressions’> | epsilon
    public boolean liste_expressions() {
        //if in premier ( - ident ent
        if(ULCourantIn(new TUniteLexical(TUnite.PARTH_OUV, -1), new TUniteLexical(TUnite.IDENT, -1), new TUniteLexical(TUnite.BIN_OP, 1), new TUniteLexical(TUnite.ENT, -1)))
            return expression() && liste_expressions_prime();
        else 
            return true;
    }

    // <liste-expressions’> : , <expression> <liste-expressions’> | epsilon
    public boolean liste_expressions_prime() {
        nbrParmsCourant++;
        if (ULCourant.getUL().equals(TUnite.VERG)) {
            motSuivant();
            return (expression() && liste_expressions_prime());
        }
        return true;
    }

    /*
     * <condition> : !(<condition>) <condition’> | (<condition >) <condition’> |
     * <expression><binary-comp><expression><condition’>
     */

    public boolean condition() {
        if (ULCourant.getUL().equals(TUnite.NEG)) {
            motSuivant();
            if (ULCourant.getUL().equals(TUnite.PARTH_OUV)) {
                motSuivant();
                if (condition()) {
                    if (ULCourant.getUL().equals(TUnite.PARTH_FER)) {
                        motSuivant();
                        return (condition_prime());
                    } else
                        return false;
                } else
                    return false;
            } else
                return false;
        }

        else if (ULCourant.getUL().equals(TUnite.PARTH_OUV)) {
            motSuivant();
            if (condition()) {
                //motSuivant();
                if (ULCourant.getUL().equals(TUnite.PARTH_FER)) {
                    motSuivant();
                    return condition_prime();
                } else
                    return false;
            } else
                return false;

        }

        else if (expression()) {
            if (ULCourant.getUL().equals(TUnite.BIN_COMP)) {
                motSuivant();
                return (expression() && condition_prime());
            }
            else return false;
        }

        else return false;
    }

    // <condition’> : <condition><binary-rel><condition’> | epsilon
    public boolean condition_prime() {
        if (ULCourant.getUL().equals(TUnite.DOUBLE_AND) || ULCourant.getUL().equals(TUnite.DOUBLE_OR)) {
            motSuivant();
            if (condition()) {
                return condition_prime();
            } else
                return false;
        }
        return true;
    }

    // <parms> : int identificateur
    public boolean parms() {
        if (ULCourant.estMotCle("int")) {
            motSuivant();
            if (ULCourant.getUL().equals(TUnite.IDENT)) {
            motSuivant();
                return true;
            } else 
                return false;
        } else 
            return false;
    }

    // <liste-fonctions> : int identificateur <fonction> <liste-fonctions> | <liste-fonctions’>
    public boolean liste_fonctions() {
        if (ULCourant.estMotCle("int")) {
            typeCourant = "int";
            motSuivant();
            if (ULCourant.getUL().equals(TUnite.IDENT)) {
                identifCourant = ULCourant.getLexeme();
                nbrParmsCourant=0;
                variablesLocales =new Variable[300];
                motSuivant();
                return (fonction() && liste_fonctions());
            }
            else return false;
        }
        //else if (ULCourant.estMotCle("void") || ULCourant.estMotCle("extern") || ULCourant.getUL().equals(TUnite.EOF)) {
        else
            return liste_fonctions_prime();

        /*} else
            return false;*/
    }

    // <liste-fonctions’> : void identificateur <fonction> <liste-fonctions> | <prototype> <liste-fonctions> | eps
    public boolean liste_fonctions_prime() {
        if (ULCourant.estMotCle("void")) {
            typeCourant = "void";
            motSuivant();
            if (ULCourant.getUL() == TUnite.IDENT) {
                identifCourant = ULCourant.getLexeme();
                variablesLocales =new Variable[300];
                nbrParmsCourant = 0;
                motSuivant();
                return fonction() && liste_fonctions();
            }
            else return false;
                
        }
        else if (ULCourant.estMotCle("extern")) {
            return prototype() && liste_fonctions();
        } 
        else return true;
    }

    //<liste-instructions> : <liste-instructions'>  
    public boolean liste_instructions() {
            return (liste_instructions_prime());
        }

    //<liste-instructions'> : < instruction > <liste-instructions’>| epsilon 
    public boolean liste_instructions_prime() {
        //premier dans {for,while,if,return,'{'} 
        if(ULCourant.estIdentif()||ULCourantIn(new TUniteLexical(TUnite.MOT_CLE,2), new TUniteLexical(TUnite.MOT_CLE,3), new TUniteLexical(TUnite.MOT_CLE,7), new TUniteLexical(TUnite.MOT_CLE,8))||ULCourant.getUL().equals(TUnite.ACC_OUV)) {
            return (instruction() && liste_instructions_prime());
        }
        return true;
    }

    //<instruction> :  identificateur <instruction’>| <iteration> | <selection> | <saut> | <bloc> 
    public boolean instruction() {       
        if (ULCourant.estIdentif()) 
        {
                TUniteLexical identifiantCourant = ULCourant;

                motSuivant();
                boolean isFonction = (ULCourant.getUL().equals(TUnite.PARTH_OUV));

                if(instruction_prime())
                {
                    if (isFonction) // eg: f(a,b,c);
                    {
                        Fonction fonctionCourante = estFonctionDeclare(identifiants[identifiantCourant.getAttribute()]);
                        if(fonctionCourante==null)
                        {
                            errorSemanticNum++;
                            System.out.println("'"+identifiants[identifiantCourant.getAttribute()]+"' est non declare! (ligne:"+realLineNumber+")");
                            addSemanticError("'"+identifiants[identifiantCourant.getAttribute()]+"' est non declare! (ligne:"+realLineNumber+")");
                        }
                    }

                    else        // ULCourant = EGAL (eg: a=b OR a=f())
                    {
                        
                        if(expressionTypeAncien!="")
                            if(!expressionTypeAncien.equals(expressionType)){
                                errorSemanticNum++;
                                System.out.println("Non homogenite des types lors d'operation (ligne:"+realLineNumber+")");
                                addSemanticError("Non homogenite des types lors d'operation! (ligne:"+realLineNumber+")");
                            }

                        Variable variableCourant = getVariable(identifiants[identifiantCourant.getAttribute()]);
                        if(variableCourant==null) 
                        {
                            errorSemanticNum++;
                            System.out.println("'"+identifiants[identifiantCourant.getAttribute()]+"' est non declare! (ligne:"+realLineNumber+")");
                            addSemanticError("'"+identifiants[identifiantCourant.getAttribute()]+"' est non declare! (ligne:"+realLineNumber+")");
                        }
                        else
                        {
                            if(!variableCourant.getType().equals(expressionType))
                            {
                                errorSemanticNum++;
                                System.out.println("Non homogenite des types lors d'affectation (ligne:"+realLineNumber+")");
                                addSemanticError("Non homogenite des types lors d'affectation (ligne:"+realLineNumber+")");

                            }
                            
                        }
    
                        
                    }

                    expressionType="";
                    return true;
                }
                    
        }    
        return (iteration() || selection() || saut() || bloc()); //hado khassehom ytefer9o
        }
        
    

    //<instruction’> : ( <liste-expressions> ) ;   | <variable’> = <expression>;
    public boolean instruction_prime() {
        if (ULCourant.getUL().equals(TUnite.PARTH_OUV)) {
            motSuivant();
            if (liste_expressions()) {
                if (ULCourant.getUL().equals(TUnite.PARTH_FER)) {
                    if(getNumPar(fonctionCourant)!=-1 && nbrParmsCourant != getNumPar(fonctionCourant)){
                        System.out.println("Le nombre de parametre invalide (ligne:"+realLineNumber+")");
                        addSemanticError("Le nombre de parametre invalide (ligne:"+realLineNumber+")");
                        errorSemanticNum++;
                    }
                    motSuivant();
                    if(ULCourant.getUL().equals(TUnite.POINT_VER)){
                        motSuivant();
                        return true;
                    }
                    else return false;
                }
                return false;
            }
            return false;  
        }

        else if (variable_prime()) {
            if(ULCourant.getUL().equals(TUnite.AFF)){
                motSuivant();
                if(expression()){
                    if(ULCourant.getUL().equals(TUnite.POINT_VER)){
                        //traitement final
                        motSuivant();
                        return true;
                    }
                    else return false;
                }
                else return false;
            }
            else return false;
        }
        return false;

    }

    //<iteration> : for ( < affectation> ; <condition> ; <affectation> ) <instruction> | while( <condition> ) <instruction> 
    public boolean iteration() {   
        if (ULCourant.estMotCle("for")) {
               motSuivant();
               if (ULCourant.getUL().equals(TUnite.PARTH_OUV)) {
                    motSuivant();
                    if (affectation()) {
                         if (ULCourant.getUL().equals(TUnite.POINT_VER)) {
                            motSuivant();
                            if (condition()) {
                                if (ULCourant.getUL().equals(TUnite.POINT_VER)) {
                                    motSuivant();
                                    if (affectation()) {
                                        if (ULCourant.getUL().equals(TUnite.PARTH_FER)) {
                                            motSuivant();
                                            return instruction();
                                        }return false; 
                                    }return false;
                                }return false;
                            }return false;
                        }return false;
                    }return false;
                }return false;
        }

        else if (ULCourant.estMotCle("while")) {
               motSuivant();
               if (ULCourant.getUL().equals(TUnite.PARTH_OUV)) {
                    motSuivant();
                    if (condition()) {
                        if (ULCourant.getUL().equals(TUnite.PARTH_FER)) {
                            motSuivant();
                            return instruction();
                        }return false; 
                    }return false;
                }return false;
        }

        return false;        
    }


    //<selection> :if ( <condition> ) <instruction><selection’>   
    public boolean selection() {
        if (ULCourant.estMotCle("if")) {
            motSuivant();
            if (ULCourant.getUL().equals(TUnite.PARTH_OUV)) {
                motSuivant();
                if (condition()) {
                    if (ULCourant.getUL().equals(TUnite.PARTH_FER)) {
                        motSuivant();
                        if(instruction()){
                            return selection_prime();
                        }return false;
                    }return false; 
                }return false;
            }return false;
        }
    return false;        

    }

    //<selection’> :else <instruction> | epsilon 
    public boolean selection_prime() {
        if (ULCourant.estMotCle("else")) {
                    motSuivant();
                    return instruction();              
                }
        return true;        
    }


    //<saut> :return <saut’> 
    public boolean saut() {
        if (ULCourant.estMotCle("return")) {
            motSuivant();
            return saut_prime();
        }
        return false;        

    }

    //<saut’> : ; |<expression>; 
    public boolean saut_prime() {
        if (ULCourant.getUL().equals(TUnite.POINT_VER)) {
            motSuivant();
            return true;               
        }
        else if (expression()) {
            if (ULCourant.getUL().equals(TUnite.POINT_VER)) {
                motSuivant();
                return true;               
                }
            return false;               
        }
        return false;        
    }


//<affecation> : <variable> = <expression> 
    public boolean affectation() {

        Variable variableCourant = null;
        if (ULCourant.estIdentif()){
            variableCourant = getVariable(identifiants[ULCourant.getAttribute()]);
            

            if(variableCourant==null)
            {
                errorSemanticNum++;
                System.out.println("'"+identifiants[ULCourant.getAttribute()]+"' est non declare! (ligne:"+realLineNumber+")");
                addSemanticError("'"+identifiants[ULCourant.getAttribute()]+"' est non declare! (ligne:"+realLineNumber+")");
            }

        }
        if (variable()) {
                if(ULCourant.getUL().equals(TUnite.AFF))
                        motSuivant();
                if(expression())
                {
                    if(variableCourant!=null){
                        if(!variableCourant.getType().equals(expressionType))
                        {
                            errorSemanticNum++;
                            System.out.println("Non homogenite lors d'affectation (ligne:"+realLineNumber+")");
                            addSemanticError("Non homogenite lors d'affectation (ligne:"+realLineNumber+")");

                        }
                        
                    }

                    expressionType="";
                    return true;
                }
                else
                    return false;
            }
        return false;
    }

    //<bloc> : { <liste-instructions> }   
    public boolean bloc() {
        if (ULCourant.getUL().equals(TUnite.ACC_OUV)) {
            motSuivant();
            if (liste_instructions()) {
                if (ULCourant.getUL().equals(TUnite.ACC_FER)) {
                    motSuivant();
                    return true;
                }
                return false;                
            }
            return false;           
        }
        return false;
    }

//<variable> :  identificateur <variable’>  
public boolean variable() {
    if (ULCourant.estIdentif()) {
            identifCourant = ULCourant.getLexeme();
            if(getVariable(identifCourant) instanceof Tableau)
                size1Courant = ((Tableau)getVariable(identifCourant)).getSize1();
            if(getVariable(identifCourant) instanceof TableauDouble)
                size2Courant = ((TableauDouble)getVariable(identifCourant)).getSize2();
            motSuivant();       
            return variable_prime();  

    }return false;     
}

//<variable’> :[<expression>] <variable’'> |epsilon  
public boolean variable_prime() {

if (ULCourant.getUL().equals(TUnite.CROCH_OUV)) {
        motSuivant();
        int size1 = -1;
        if(ULCourant.getUL().equals(TUnite.ENT)){
            size1 = ULCourant.getAttribute();
        }
        else if(ULCourant.getUL().equals(TUnite.IDENT)){
            //System.out.println(ULCourant.getLexeme());
            //System.out.println(variablesLocales+"\n"+variablesGlobales);
            //System.out.println(getVariable(ULCourant.getLexeme()));
            size1 = getVariable(ULCourant.getLexeme()).getValue();
        }
        //System.out.println(size1);
        if(size1>=size1Courant){
            errorSemanticNum++;
            System.out.println("Taille max dimension 1 du tableau depassee");
            addSemanticError("Taille max dimension 1 du tableau depassee (ligne:"+realLineNumber+")");

        }
        
        if (expression()) {
            if (ULCourant.getUL().equals(TUnite.CROCH_FER)) {

                if(expressionType.equals("int"))
                {
                    errorSemanticNum++;
                    System.out.println("ce n'est pas un tableau (ligne:"+realLineNumber+")");
                    addSemanticError("ce n'est pas un tableau (ligne:"+realLineNumber+")");

                }
                else if(expressionType.equals("int[]"))
                    expressionType="int";
                else if(expressionType.equals("int[][]"))
                    expressionType="int[]";

                motSuivant();
                return variable_double();
            }
        } 
    }
    return true;
}

//<variable’'> :[<expression>] | epsilon 
public boolean variable_double() {
if (ULCourant.getUL().equals(TUnite.CROCH_OUV)) {
        motSuivant();
        int size2 = -1;
        if(ULCourant.getUL().equals(TUnite.ENT)){
            size2 = ULCourant.getAttribute();
        }
        else if(ULCourant.getUL().equals(TUnite.IDENT)){
            size2 = getVariable(ULCourant.getLexeme()).getValue();
        }
        if(size2>=size2Courant){
            errorSemanticNum++;
            System.out.println("Taille max dimension 2 du tableau depassee (ligne:"+realLineNumber+")");
            addSemanticError("Taille max dimension 2 du tableau depassee (ligne:"+realLineNumber+")");

        }
        if (expression()) {
            if (ULCourant.getUL().equals(TUnite.CROCH_FER)) {

                if(expressionType.equals("int"))
                {
                    errorSemanticNum++;
                    System.out.println("ce n'est pas un tableau (ligne:"+realLineNumber+")");
                    addSemanticError("ce n'est pas un tableau (ligne:"+realLineNumber+")");

                }
                else if(expressionType.equals("int[]"))
                    expressionType="int";
                else if(expressionType.equals("int[][]"))
                    expressionType="int[]";


                motSuivant();
                return true;
            }return false;
        }return false;
    }return true;
}
}

