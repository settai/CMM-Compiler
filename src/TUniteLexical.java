enum TUnite{
	BIN_OP,
	BIN_COMP,
	DOUBLE_AND,
	DOUBLE_OR,
	CROCH_OUV,
	CROCH_FER,
	PARTH_OUV,
	PARTH_FER,
	NEG,
	VERG,
	AFF,
	ACC_OUV,
	ACC_FER,
	POINT_VER,
	MOT_CLE,
	ENT,
	IDENT,
	EOF,
	AUTRE;
}

public class TUniteLexical{
	private TUnite UL;
	private int attribut;

	
	TUniteLexical(TUnite UL, int attribut){
		this.UL=UL;
		this.attribut=attribut;
	}
	public TUnite getUL(){
		return UL;
	}

	public int getAttribute(){
		return attribut;
	}
	// return le nom de l'identifiant 
	//dans la case attribut
	public String getLexeme(){
		return Lexical.identifiants[attribut];
	}


	public boolean estMotCle(String cle){
		return UL.equals(TUnite.MOT_CLE) && Lexical.motsCle[attribut].equals(cle);
	}

	public boolean estBinOp(String cle){
		return UL.equals(TUnite.BIN_OP) && Lexical.binOp[attribut].equals(cle); 
	}

	public boolean estBinComp(String cle){
		return UL.equals(TUnite.BIN_COMP) && Lexical.binComp[attribut].equals(cle); 
	}

	public boolean estIdentif(){
		return UL.equals(TUnite.IDENT);
	}

	public boolean equals(TUniteLexical tul) {
		if(tul.getUL().equals(TUnite.IDENT)||tul.getUL().equals(TUnite.ACC_OUV)||tul.getUL().equals(TUnite.ENT))
			return UL.equals(tul.getUL());
		return UL.equals(tul.getUL()) && attribut == tul.getAttribute();
	}
	
}